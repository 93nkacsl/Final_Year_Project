import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

//7-4-2016

public class RoomBookedGUI {
	private InputValidation iv;
	private int rId;
	private Database db;
	private DateAndTime d;
	private TextField tfDuration, tfDescription;
	private ChoiceBox<String> comboDay, comboTime;
	private Button bAdd, bRemove, bClear;
	private TableView<Date> table;
	private String myStyle;

	public RoomBookedGUI(int rId, Database db, DateAndTime d, InputValidation iv, String myStyle) {
		this.rId = rId;
		this.d = d;
		this.db = db;
		this.iv = iv;
		this.myStyle = myStyle;
	}

	public void generateGUI() {
		// Top Layout:
		StackPane topLayout = new StackPane();
		topLayout.setAlignment(Pos.CENTER);
		Label title = new Label("Room Booking:");
		topLayout.getChildren().add(title);

		// Center Layout

		table = new TableView<Date>();
		table.setItems(loadTheData());
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		table.setRowFactory(tv -> {
			TableRow<Date> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Date myRow = row.getItem();
					tfDuration.setText(myRow.getDuration() + "");
					comboDay.getSelectionModel().select(myRow.getDay());
					comboTime.getSelectionModel().select(myRow.getStartTime() + ":00");
					tfDescription.setText(myRow.getDescription());
				}
			});
			return row;
		});

		// db.addLecUnavail(lId, day, time, duration);
		TableColumn<Date, Integer> colId = new TableColumn<Date, Integer>("Id");
		colId.setMinWidth(100);
		colId.setCellValueFactory(new PropertyValueFactory<>("id"));

		TableColumn<Date, String> colDay = new TableColumn<Date, String>("Day");
		colDay.setMinWidth(100);
		colDay.setCellValueFactory(new PropertyValueFactory<>("day"));

		TableColumn<Date, String> colTime = new TableColumn<Date, String>("Time");
		colTime.setMinWidth(100);
		colTime.setCellValueFactory(new PropertyValueFactory<>("time"));

		TableColumn<Date, Integer> colDuration = new TableColumn<Date, Integer>("Duration");
		colDuration.setMinWidth(100);
		colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));

		TableColumn<Date, String> colDescription = new TableColumn<Date, String>("Description");
		colDescription.setMinWidth(100);
		colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

		table.getColumns().addAll(colId, colDay, colTime, colDuration, colDescription);
		// colId.setCellFactory(TextFieldTableCell.forTableColumn());

		// Bottom Pane:
		VBox bottomPane = new VBox();
		bottomPane.setAlignment(Pos.CENTER);
		bottomPane.setPadding(new Insets(5, 5, 5, 5));
		bottomPane.setSpacing(5);

		HBox bottomTopPane = new HBox();
		bottomTopPane.setAlignment(Pos.CENTER);
		bottomTopPane.setPadding(new Insets(5, 5, 5, 5));
		bottomTopPane.setSpacing(5);
		tfDuration = new TextField();
		tfDuration.setPromptText("Duration");
		tfDescription = new TextField();
		tfDescription.setPromptText("Description");
		comboDay = new ChoiceBox<>();
		comboDay.getItems().addAll(d.getDays());
		comboTime = new ChoiceBox<>();
		comboTime.getItems().addAll(getTimes(d.getTimesOfTheDay()));
		bottomTopPane.getChildren().addAll(new Label("Day"), comboDay, new Label("Time"), comboTime,
				new Label("Duration"), tfDuration, new Label("Description"), tfDescription);
		bottomTopPane.setMinWidth(750);
		HBox bottomBottomPane = new HBox();
		bottomBottomPane.setAlignment(Pos.CENTER);
		bottomBottomPane.setPadding(new Insets(5, 5, 5, 5));
		bottomBottomPane.setSpacing(5);

		bAdd = new Button("Add");
		bAdd.setMinWidth(100);
		bAdd.setOnAction(e -> bAddClicked());
		bRemove = new Button("Remove");
		bRemove.setOnAction(e -> bRemoveClicked());
		bRemove.setMinWidth(100);
		bClear = new Button("Clear");
		bClear.setMinWidth(100);
		bClear.setOnAction(e -> bClearClicked());
		bottomBottomPane.getChildren().addAll(bAdd, bRemove, bClear);

		bottomPane.getChildren().addAll(bottomTopPane, bottomBottomPane);

		BorderPane pane = new BorderPane();
		// pane.getStyleClass().add("vbox");
		pane.setTop(topLayout);
		topLayout.getStyleClass().add("vbox");
		pane.setCenter(table);
		pane.setBottom(bottomPane);
		Stage stage = new Stage();
		stage.setWidth(900);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Booking");
		Scene scene = new Scene(pane);
		scene.getStylesheets().add(myStyle);
		stage.setScene(scene);
		iv.checkFocus(tfDuration);
		iv.checkFocus(tfDescription);
		iv.checkFocus(comboDay);
		iv.checkFocus(comboTime);
		iv.setTFStyleOnClick(tfDuration);
		iv.setTFStyleOnClick(comboDay);
		iv.setTFStyleOnClick(comboTime);
		iv.setTFStyleOnClick(tfDescription);
		stage.showAndWait();
	}

	private void bClearClicked() {
		comboDay.getSelectionModel().clearSelection();
		comboTime.getSelectionModel().clearSelection();
		tfDuration.setText("");
		tfDescription.setText("");
	}

	private void bRemoveClicked() {
		try {
			Date date = table.getSelectionModel().getSelectedItem();
			db.removeRoomBooked(rId, date.getTime(), date.getDay(), date.getDuration());
			table.setItems(loadTheData());
		} catch (Exception e) {

		}
	}

	private void bAddClicked() {
		boolean a = iv.checkIfInputIsAnInteger(tfDuration);
		boolean b = iv.checkModuleInput(tfDescription, 100);
		boolean c = iv.checkIfCheckBoxIsNotSelected(comboDay);
		boolean e = iv.checkIfCheckBoxIsNotSelected(comboTime);
		if (a && b && c && e) {

			String day = comboDay.getSelectionModel().getSelectedItem();
			String time = comboTime.getSelectionModel().getSelectedItem();
			int duration = Integer.parseInt(tfDuration.getText());
			int t = Integer.parseInt(time.split(":")[0]);
			if (t + duration > d.getFinishingTime()) {
				new ErrorDialog("Please check the time and duration of the booking!", "Error").generateGUI();
			} else {
				boolean f = true;
				ArrayList<Date> bookedRooms = db.getRoomBooked(rId);
				for (Date rb : bookedRooms) {
					int rbSt = rb.getStartTime();
					int rbEt = rbSt + rb.getDuration();
					int inputSt = Integer.parseInt(time.split(":")[0]);
					int inputEt = inputSt + duration;
					if (checkTwoEventsClash(rbSt, rbEt, inputSt, inputEt)) {
						f = false;
						break;
					}
				}
				if (!f) {
					new ErrorDialog("This room is already booked in that time!", "Error").generateGUI();
				} else {
					db.addRoomBooked(rId, time, day, duration, tfDescription.getText().replace("-", "_"));
					table.setItems(loadTheData());
				}

			}
		}
	}

	private String[] getTimes(int[] timesOfTheDay) {
		String[] times = new String[timesOfTheDay.length];
		for (int i = 0; i < timesOfTheDay.length; i++) {
			times[i] = timesOfTheDay[i] + ":00";
		}
		return times;
	}

	private ObservableList<Date> loadTheData() {
		ObservableList<Date> myData = FXCollections.observableArrayList();
		for (Date date : db.getRoomBooked(rId)) {
			myData.add(date);
		}
		return myData;
	}

	/**
	 * Returns true if two events clash.
	 * 
	 * @param stOne
	 * @param etOne
	 * @param stTwo
	 * @param etTwo
	 * @return
	 */
	public boolean checkTwoEventsClash(int stOne, int etOne, int stTwo, int etTwo) {
		if ((stOne < etTwo) && (etOne > stTwo)) {
			return true;
		} else
			return false;
	}
}
