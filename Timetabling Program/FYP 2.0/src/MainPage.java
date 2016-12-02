import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

//7-4-2016

public class MainPage {
	// Styling:
	private InputValidation iv;
	private String style = "-fx-border-color:red;";
	private String excep = "-fx-border-color:green;";
	private Preferences pref;
	private Database db;
	private DateAndTime d;
	private String[] roomAndModuleTypes;
	private String[] programOfStudies;
	private ConstraintChecker cc;
	private GreedyAlgorithm ga;
	private Label lableTitle;
	private String dbName;
	private Button bModule, bLecturer, bRoom, bSetting, bGenerate, bSA, bGA;
	private BorderPane mainPane;
	private Stage myWindow;
	private Thread thread;
	// SA STUFF:
	private double initTemp, coolingRate;
	// Style:
	private String myStyle;
	private int buttonSize = 120;

	public MainPage(Database db, DateAndTime d, String dbName, Preferences pref, String myStyle) {
		this.myStyle = myStyle;
		System.out.println("Double click stuff...");
		System.out.println("Program of study.length<40");
		iv = new InputValidation(style, excep, "");
		this.pref = pref;
		this.d = d;
		this.dbName = dbName;
		this.db = db;
		cc = new ConstraintChecker(pref.getInt("cw1", 10), pref.getInt("cw2", 7), pref.getInt("cw3", 5),
				pref.getInt("cw4", 3), pref.getInt("cw5", 3), pref.getInt("cw8", 3), pref.getInt("cw9", 3), d,
				db.getLecUnAvail(), pref.getInt("cw5X", 6), pref.getInt("cw10", 3), pref.get("launchSt", "11:00"),
				pref.get("launchSt", "13:00"));
		ArrayList<String> list = new ArrayList<>();
		list.add("TypeA");
		list.add("TypeB");
		try {
			ArrayList<String> myList = (ArrayList<String>) bytes2Object(
					pref.getByteArray("pOfStudies", object2Bytes(list)));
			programOfStudies = new String[myList.size()];
			myList.toArray(programOfStudies);
		} catch (ClassNotFoundException | IOException e) {
		}

		myWindow = new Stage();
		myWindow.initModality(Modality.APPLICATION_MODAL);

		initialize();
		// Title page
		StackPane topLayout = new StackPane();
		topLayout.setAlignment(Pos.CENTER);
		topLayout.getChildren().add(lableTitle);

		// Left panel
		HBox menuLayout = new HBox();
		// menuLayout.getStyleClass().add("vbox");
		menuLayout.setAlignment(Pos.CENTER);
		menuLayout.setSpacing(20);
		menuLayout.setPadding(new Insets(10, 10, 5, 0));
		menuLayout.getChildren().addAll(bLecturer, bModule, bRoom, bSetting, bGenerate, bSA, bGA);

		// Main Page:
		mainPane = new BorderPane();
		myWindow.setTitle("Timetabling System : Amir Nasiri");
		mainPane.setBottom(topLayout);
		// mainPane.setLeft(leftPanel);
		mainPane.setTop(menuLayout);
		mainPane.setCenter(new ProgressB().getProgressBar());
		Thread t = new Thread(runLecturer());
		t.start();

		Scene scene = new Scene(mainPane, 1100, 1000);
		scene.getStylesheets().add(myStyle);
		myWindow.setScene(scene);
		myWindow.setOnCloseRequest(e -> {
			try {
				thread.stop();
			} catch (NullPointerException eq) {

			}
		});
	}

	private void initialize() {
		lableTitle = new Label("Database : " + dbName);
		bModule = new Button("Module");
		bModule.setMinWidth(buttonSize);
		bModule.setOnAction(e -> {
			updateStuff();
			mainPane.setCenter(new ProgressB().getProgressBar());
			thread = new Thread(runModule());
			thread.start();
		});

		bLecturer = new Button("Lecturer");
		bLecturer.setMinWidth(buttonSize);
		bLecturer.setOnAction(e -> {
			updateStuff();
			mainPane.setCenter(new ProgressB().getProgressBar());
			thread = new Thread(runLecturer());
			thread.start();
		});

		bRoom = new Button("Room");
		bRoom.setMinWidth(buttonSize);
		bRoom.setOnAction(e -> {
			updateStuff();
			mainPane.setCenter(new ProgressB().getProgressBar());
			thread = new Thread(runRoom());
			thread.start();
		});

		bSetting = new Button("Setting");
		bSetting.setMinWidth(buttonSize);
		bSetting.setOnAction(e -> {
			updateStuff();
			mainPane.setCenter(new ProgressB().getProgressBar());
			thread = new Thread(runSetting());
			thread.start();
		});
		bGenerate = new Button("Greedy");
		bGenerate.setMinWidth(buttonSize);
		bGenerate.setOnAction(e -> {
			updateStuff();
			mainPane.setCenter(new ProgressB().getProgressBar());
			bGenerate.setDisable(true);
			bLecturer.setDisable(true);
			bModule.setDisable(true);
			bRoom.setDisable(true);
			bSetting.setDisable(true);
			bSA.setDisable(true);
			bGA.setDisable(true);
			thread = new Thread(runTimetable());
			thread.start();

		});
		bSA = new Button("Generate(SA)");
		bSA.setMinWidth(buttonSize + 40);
		bSA.setOnAction(e -> {
			updateStuff();
			mainPane.setCenter(new ProgressB().getProgressBar());
			bGenerate.setDisable(true);
			bLecturer.setDisable(true);
			bModule.setDisable(true);
			bRoom.setDisable(true);
			bSetting.setDisable(true);
			bSA.setDisable(true);
			bGA.setDisable(true);
			thread = new Thread(runSA());
			thread.start();

		});
		bGA = new Button("Generate(GA)");
		bGA.setMinWidth(buttonSize + 40);
		bGA.setOnAction(e -> {
			updateStuff();
			mainPane.setCenter(new ProgressB().getProgressBar());
			bGenerate.setDisable(true);
			bLecturer.setDisable(true);
			bModule.setDisable(true);
			bRoom.setDisable(true);
			bSetting.setDisable(true);
			bSA.setDisable(true);
			bGA.setDisable(true);
			thread = new Thread(runGA());
			thread.start();
		});

	}

	private Task runGA() {
		Task task = new Task() {
			@Override
			protected Object call() throws Exception {
				GA ga = new GA(pref, pref.getInt("population", 100), db, cc, d, pref.getDouble("percentile", 0.3),
						pref.getBoolean("singleOrDouble", true));
				System.out.println(pref.getInt("population", 100) + "  " + pref.getDouble("percentile", 0.3) + " "
						+ pref.getBoolean("singleOrDouble", true));
				ArrayList<Solution> solution = ga.getSolution();

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						new TimeTableGUI(solution, d, db.getRooms(), db, cc.costFunction(solution), cc).generateGUI(0);
						bGenerate.setDisable(false);
						bLecturer.setDisable(false);
						bModule.setDisable(false);
						bRoom.setDisable(false);
						bSetting.setDisable(false);
						bSA.setDisable(false);
						bGA.setDisable(false);
						Thread thread = new Thread(runLecturer());
						thread.start();
					}
				});
				return null;
			}
		};
		return task;
	}

	/**
	 * The simulated annealing process.(DONE)
	 * 
	 * @return
	 */
	private Task runSA() {
		Task task = new Task() {
			@Override
			protected Object call() throws Exception {
				int initialTemperature = pref.getInt("temperature", 1000);
				float coolingRate = pref.getFloat("coolingRate", (float) 0.003);
				int changes = pref.getInt("changes", 1);
				System.out.println("Changes: " + changes);
				boolean greedyValue = pref.getBoolean("greedyValue", true);
				GreedyAlgorithm ga = new GreedyAlgorithm(db, cc, d, greedyValue);
				ArrayList<Solution> solutions = ga.getSolution();
				if (solutions.size() == 0) {
					new ErrorDialog("The Database is inconsistent, please complete addtional information", "Error")
							.generateGUI();
				} else {
					SA sa = new SA(solutions, cc, initialTemperature, coolingRate);
					ArrayList<Solution> annealed = sa.anneal(solutions, changes, initialTemperature, coolingRate);
					System.out.println("CFCF: " + cc.costFunction(solutions) + " CFCFCF: " + cc.costFunction(annealed));
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							new TimeTableGUI(annealed, d, db.getRooms(), db, cc.costFunction(annealed), cc)
									.generateGUI(cc.costFunction(solutions));
							bGenerate.setDisable(false);
							bLecturer.setDisable(false);
							bModule.setDisable(false);
							bRoom.setDisable(false);
							bSetting.setDisable(false);
							bSA.setDisable(false);
							bGA.setDisable(false);
							Thread thread = new Thread(runLecturer());
							thread.start();
						}
					});
				}
				return null;
			}
		};
		return task;
	}

	private Task runTimetable() {
		Task task = new Task() {
			@Override
			protected Object call() throws Exception {
				boolean greedyValue = pref.getBoolean("greedyValue", true);
				ga = new GreedyAlgorithm(db, cc, d, greedyValue);
				ArrayList<Solution> a = ga.getSolution();
				for (String day : d.getDays()) {
					for (int st : d.getTimesOfTheDay()) {
						for (Solution s : a) {
							if (day.equals(s.getDay()) && s.getSt() == st) {
								//System.out.println(s);
							}
						}
					}
				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						new TimeTableGUI(a, d, db.getRooms(), db, cc.costFunction(a), cc).generateGUI(0);
						bGenerate.setDisable(false);
						bLecturer.setDisable(false);
						bModule.setDisable(false);
						bRoom.setDisable(false);
						bSetting.setDisable(false);
						bSA.setDisable(false);
						bGA.setDisable(false);
						Thread thread = new Thread(runLecturer());
						thread.start();
					}
				});
				return null;
			}
		};
		return task;
	}

	private ScrollPane getSettingPane() {
		return new SettingGUI(pref, iv).getGUI();
	}

	/**
	 * This method creates the GUI for the Lecturer button.
	 * 
	 * @return
	 */
	public Pane getLecturerPane() {
		return new LecturerGUI(db, d, iv, myStyle).getGUI();
	}

	public Pane getModulePane() {
		return new ModuleGUI(db, programOfStudies, roomAndModuleTypes, iv, myStyle).getGUI();
	}

	public Pane getRoomPane() {
		return new RoomGUI(db, roomAndModuleTypes, d, iv, myStyle).getGUI();
	}

	public Stage getStage() {
		return myWindow;
	}

	/**
	 * This method runs the GUI thread in a background thread.
	 * 
	 * @return
	 */
	private Task runLecturer() {
		Task task = new Task() {

			Pane pane = getLecturerPane();

			@Override
			protected Object call() throws Exception {

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						mainPane.setCenter(pane);

					}
				});
				return null;
			}
		};
		return task;
	}

	private Task runModule() {
		Task task = new Task() {

			Pane pane = getModulePane();

			@Override
			protected Object call() throws Exception {

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						mainPane.setCenter(pane);

					}
				});
				return null;
			}
		};
		return task;
	}

	private Task runRoom() {
		Task task = new Task() {

			Pane pane = getRoomPane();

			@Override
			protected Object call() throws Exception {

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						mainPane.setCenter(pane);

					}
				});
				return null;
			}
		};
		return task;
	}

	private Task runSetting() {
		Task task = new Task() {

			ScrollPane pane = getSettingPane();

			@Override
			protected Object call() throws Exception {

				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						mainPane.setCenter(pane);

					}
				});
				return null;
			}
		};
		return task;
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

	/**
	 * updates all the variables
	 */
	public void updateStuff() {
		ArrayList<String> list = new ArrayList<>();
		list.add("TypeA");
		list.add("TypeB");
		try {
			ArrayList<String> myList = (ArrayList<String>) bytes2Object(
					pref.getByteArray("pOfStudies", object2Bytes(list)));
			programOfStudies = new String[myList.size()];
			myList.toArray(programOfStudies);
		} catch (ClassNotFoundException | IOException e) {
		}

		ArrayList<String> listTwo = new ArrayList<>();
		listTwo.add("Lecture");
		listTwo.add("Tutorial");
		listTwo.add("Lab");
		try {
			ArrayList<String> myList = (ArrayList<String>) bytes2Object(
					pref.getByteArray("moduleTypes", object2Bytes(listTwo)));
			roomAndModuleTypes = new String[myList.size()];
			myList.toArray(roomAndModuleTypes);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		initTemp = pref.getDouble("initTemp", 1000);
		coolingRate = pref.getDouble("coolingRate", 0.003);
		d.setStartTime(pref.get("st", "09:00"));
		d.setEndTime(pref.get("et", "18:00"));

		System.out.println(pref.getInt("cw1", 2) + "    " + pref.getInt("cw2", 3) + "   " + pref.getInt("cw3", 1)
				+ "   " + pref.getInt("cw4", 4) + "   " + pref.getInt("cw5", 5) + "   " + pref.getInt("cw8", 5) + "   "
				+ pref.getInt("cw9", 6) + "  " + pref.getInt("cw5X", 6));
		cc = new ConstraintChecker(pref.getInt("cw1", 2), pref.getInt("cw2", 3), pref.getInt("cw3", 1),
				pref.getInt("cw4", 4), pref.getInt("cw5", 5), pref.getInt("cw8", 5), pref.getInt("cw9", 6), d,
				db.getLecUnAvail(), pref.getInt("cw5X", 6), pref.getInt("cw10", 3), pref.get("launchSt", "11:00"),
				pref.get("launchEt", "13:00"));
	}

}
