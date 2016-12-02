import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

//7-4-2016

public class TeachesGUI {
	private InputValidation iv;
	private Database db;
	private int mId, duration;
	private TableView<Teaches> table;
	private ChoiceBox<String> comboLec;
	private TextField tfDuration;
	private ArrayList<Lecturer> lecturers;
	private Button bAdd, bRemove, bClear;
	private String style = "-fx-border-color:red;";
	private String excep = "-fx-border-color:green;";
	private String myStyle;

	/**
	 * This class is not finished!
	 * 
	 * @param db
	 * @param mId
	 * @param duration
	 */
	public TeachesGUI(Database db, int mId, int duration, InputValidation iv, String myStyle) {
		this.iv = iv;
		this.db = db;
		this.mId = mId;
		this.duration = duration;
		lecturers = db.getLecturers();
		this.myStyle = myStyle;
	}

	public void generateGUI() {
		Stage stage = new Stage();
		table = new TableView<Teaches>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setItems(loadTheData());
		TableColumn<Teaches, Integer> colMId = new TableColumn<Teaches, Integer>("Module Id");
		colMId.setCellValueFactory(new PropertyValueFactory<>("mId"));
		colMId.setMinWidth(100);
		TableColumn<Teaches, Integer> colLId = new TableColumn<Teaches, Integer>("Lecturer Id");
		colLId.setCellValueFactory(new PropertyValueFactory<>("lId"));
		colLId.setMinWidth(100);
		TableColumn<Teaches, Integer> colDuration = new TableColumn<Teaches, Integer>("Duration");
		colDuration.setCellValueFactory(new PropertyValueFactory<>("numberOfHours"));
		colDuration.setMinWidth(100);
		table.getColumns().addAll(colMId, colLId, colDuration);

		table.setRowFactory(tv -> {
			TableRow<Teaches> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Teaches myRow = row.getItem();

				}
			});
			return row;
		});

		// bottom layout:
		VBox vb = new VBox();
		vb.setPadding(new Insets(5, 5, 5, 5));
		vb.setSpacing(10);
		HBox bottomTopLayout = new HBox();
		bottomTopLayout.setPadding(new Insets(5, 5, 5, 5));
		bottomTopLayout.setSpacing(10);
		bottomTopLayout.setAlignment(Pos.CENTER);

		comboLec = new ChoiceBox<>();
		iv.checkFocus(comboLec);
		comboLec.setItems(setLecturerNames());
		tfDuration = new TextField();
		iv.checkFocus(tfDuration);
		tfDuration.setPromptText("Duration");

		HBox bottomBottomLayout = new HBox();
		bottomBottomLayout.setPadding(new Insets(5, 5, 5, 5));
		bottomBottomLayout.setSpacing(10);
		bottomBottomLayout.setAlignment(Pos.CENTER);

		bAdd = new Button("Add");
		bAdd.setOnAction(e -> addButtonClicked());
		bRemove = new Button("Remove");
		bRemove.setOnAction(e -> RemoveButtonClicked());
		bClear = new Button("Clear");
		bClear.setOnAction(e -> clearButtonClicked());
		bottomBottomLayout.getChildren().addAll(bAdd, bRemove, bClear);
		bottomTopLayout.getChildren().addAll(new Label("Lecturer"), comboLec, new Label("Duration"), tfDuration);
		bottomBottomLayout.setMinWidth(650);
		vb.getChildren().addAll(bottomTopLayout, bottomBottomLayout);

		HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().add(new Label("Assign Lecturer"));

		BorderPane pane = new BorderPane();
		pane.setTop(hb);
		pane.setCenter(table);
		pane.setBottom(vb);
		Scene scene = new Scene(pane);
		scene.getStylesheets().add(myStyle);
		stage.setScene(scene);
		stage.setTitle("Assign Lecturer");
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}

	private void clearButtonClicked() {
		tfDuration.setText("");
		comboLec.getSelectionModel().clearSelection();
	}

	private void RemoveButtonClicked() {
		if (!table.getSelectionModel().isEmpty()) {
			Teaches t = table.getSelectionModel().getSelectedItem();
			db.removeTeaches_Lecturer_Module(t.getMId(), t.getLId());
			table.setItems(loadTheData());
		} else {
			new ErrorDialog("Please select a row", "Error").generateGUI();
		}
	}

	private void addButtonClicked() {
		boolean a = iv.checkIfInputIsAnInteger(tfDuration);
		iv.setTFStyleOnClick(tfDuration);
		iv.checkFocus(tfDuration);
		iv.setTFStyleOnClick(comboLec);
		boolean b = iv.checkIfCheckBoxIsNotSelected(comboLec);
		if (a && b) {
			db.addTeaches_Lecturer_Module(lecturers.get(comboLec.getSelectionModel().getSelectedIndex()).getId(), mId,
					Integer.parseInt(tfDuration.getText()));
			table.setItems(loadTheData());
		}
	}

	private ObservableList<String> setLecturerNames() {
		ObservableList<String> lecturersNames = FXCollections.observableArrayList();
		for (Lecturer lec : lecturers) {
			lecturersNames.add("Id: " + lec.getId() + ", " + lec.getFName() + " , " + lec.getLName());
		}
		return lecturersNames;
	}

	private ObservableList<Teaches> loadTheData() {
		ObservableList<Teaches> myData = FXCollections.observableArrayList();
		for (Teaches teach : db.getTeaches(mId)) {
			myData.add(teach);
		}
		return myData;
	}

}
