import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//7-4-2016

public class DBGUI extends Application {

	private Preferences pref;
	private String dbName;
	private ListView dbList;
	private ArrayList<String> databases;
	private TextField tfInput;
	private Button bAddDB, bRemoveDB;
	private DateAndTime d;
	private Permutation permutation;
	private Database db;

	// DB
	private Connection myConnection;
	private Statement myStatement;

	// Style
	private String myStyle = "normal.css";

	public DBGUI() {
		pref = Preferences.userNodeForPackage(DBGUI.class);
		String st = pref.get("st", "09:00");
		String et = pref.get("et", "18:00");
		d = new DateAndTime(st, et);
		permutation = new Permutation(d.getDays());
		databases = new ArrayList<>();
		try {
			databases = (ArrayList<String>) bytes2Object(pref.getByteArray("databases", object2Bytes(databases)));
		} catch (Exception e) {
		}

	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Timetabling System:Amir Nasiri");
		BorderPane borderPane = new BorderPane();
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(new Insets(10, 10, 10, 10));
		vbox.setSpacing(20);
		// ListView
		dbList = new ListView<>();
		dbList.setMinSize(350, 350);
		dbList.setItems(FXCollections.observableArrayList(databases));
		dbList.setPrefHeight(300);
		dbList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					if (mouseEvent.getClickCount() == 2) {
						dbName = dbList.getSelectionModel().getSelectedItem() + "";
						new Thread(getTask()).start();
					}
				}
			}
		});

		// HB CENTER
		HBox hb = new HBox();
		hb.setSpacing(10);
		hb.setAlignment(Pos.CENTER);
		hb.setPadding(new Insets(5, 5, 5, 5));
		tfInput = new TextField();
		tfInput.setPromptText("Database Name");
		bAddDB = new Button("+");
		bAddDB.setOnAction(e -> {
			String input = tfInput.getText();
			if (input.length() == 0) {
				new ErrorDialog("The Database must have a name!", "Error").generateGUI();
			} else {
				if (dbList.getItems().contains(input)) {
					new ErrorDialog("A database with thesame name alreadt exists!", "Error").generateGUI();
				} else {
					try {
						myConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "amir");
						myStatement = myConnection.createStatement();
						myStatement.execute("CREATE DATABASE " + input + ";");
						databases.add(input);
						dbList.setItems(FXCollections.observableArrayList(databases));
						tfInput.setText("");
					} catch (Exception e2) {
						new ErrorDialog("A database with name: " + input
								+ " cannot be created, Please use a different name and try again!", "Error")
										.generateGUI();
					}

					try {
						pref.putByteArray("databases", object2Bytes(databases));
					} catch (Exception e1) {
					}
				}
			}
		});
		bRemoveDB = new Button("-");
		bRemoveDB.setOnAction(e -> {
			if (dbList.getSelectionModel().isEmpty()) {
				new ErrorDialog("Please select a row and try again!", "Error").generateGUI();
			} else {

				databases.remove(dbList.getSelectionModel().getSelectedItem());
				try {
					pref.putByteArray("databases", object2Bytes(databases));
					Database myDB = new Database(dbList.getSelectionModel().getSelectedItem() + "",
							permutation.permutate());
					myDB.getStatement().execute("DROP SCHEMA " + myDB.getDBName());

				} catch (Exception e1) {
				}
				dbList.setItems(FXCollections.observableArrayList(databases));

			}
		});
		hb.getChildren().addAll(tfInput, bAddDB, bRemoveDB);
		vbox.setPadding(new Insets(20, 20, 20, 20));
		vbox.getChildren().addAll(dbList, hb);
		borderPane.setCenter(vbox);
		Scene scene = new Scene(borderPane);
		scene.getStylesheets().add(myStyle);
		// stage.getIcons().add(new Image("file:icon.png"));
		// stage.getIcons().add(new Image("/Desktop/FYP/city-buildings.png"));
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Reference: http://www.ibm.com/developerworks/library/j-prefapi/
	 * 
	 * @param o
	 * @return
	 * @throws IOException
	 */
	private byte[] object2Bytes(Object o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		return baos.toByteArray();
	}

	/**
	 * Reference: http://www.ibm.com/developerworks/library/j-prefapi/
	 * 
	 * @param raw
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Object bytes2Object(byte raw[]) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(raw);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Object o = ois.readObject();
		return o;
	}

	public Task getTask() {

		Task task = new Task() {

			@Override
			protected Object call() throws Exception {
				db = new Database(dbList.getSelectionModel().getSelectedItem() + "", permutation.permutate());
				// print(db);
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						try {
							MainPage mp = new MainPage(db, d, dbName, pref, myStyle);
							mp.getStage().showAndWait();
						} catch (Exception e) {
							new ErrorDialog("Please Check your MYSQLWorkbench details!", "Error").generateGUI();
						}

					}
				});
				return null;
			}
		};
		return task;

	}

	// private void print(Database db) {
	// System.out.println();
	// System.out.println();
	// System.out.println("Lecturers: " + db.getLecturers().size());
	// System.out.println("Modules: " + db.getModules().size());
	// System.out.println("Tutorials: " + db.getACs().size());
	// System.out.println("Rooms: " + db.getRooms().size());
	// System.out.println("Lecturer Unavail: " + db.getLecUnAvail().size());
	// System.out.println("Room Excluded: " + db.getLecUnAvail().size());
	// System.out.println();
	// System.out.println();
	// }

}
