import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

//7-4-2016

public class RoomGUI {
	private InputValidation iv;
	private Database db;
	private TableView<Room> table;
	private String[] rTypes;
	private ArrayList<CheckBox> checkBoxes;
	private Button bAdd, bRemove, bClear, bRoomBooked;
	private TextField tfName, tfCapacity, tfLocation;
	private DateAndTime d;
	private String myStyle;

	public RoomGUI(Database db, String[] rTypes, DateAndTime d, InputValidation iv, String myStyle) {
		this.db = db;
		this.rTypes = rTypes;
		this.d = d;
		this.iv = iv;
		this.myStyle = myStyle;
	}

	public Pane getGUI() {
		// db.addRoom(name, type, capacity, location);
		// The top layout:
		StackPane topLayout = new StackPane();
		topLayout.setAlignment(Pos.CENTER);
		Label title = new Label("Room");
		topLayout.getChildren().add(title);
		topLayout.setPadding(new Insets(5, 5, 5, 5));

		// Center panel
		table = new TableView<Room>();
		table.setItems(loadData());
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setRowFactory(tv -> {
			TableRow<Room> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					Room room = row.getItem();
					tfCapacity.setText(room.getCapacity() + "");
					// tfId.setText(room.getId() + "");
					tfLocation.setText(room.getLocation());
					tfName.setText(room.getName());
					// comboType.getSelectionModel().select(room.getType());

				}
			});
			return row;
		});
		TableColumn<Room, String> nameColumn = new TableColumn<Room, String>("Name");
		nameColumn.setMinWidth(100);
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<Room, String> typeColumn = new TableColumn<Room, String>("Type");
		typeColumn.setMinWidth(100);
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

		TableColumn<Room, Integer> capacityColumn = new TableColumn<Room, Integer>("Capacity");
		capacityColumn.setMinWidth(100);
		capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));

		TableColumn<Room, String> locationColumn = new TableColumn<Room, String>("Location");
		locationColumn.setMinWidth(100);
		locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

		TableColumn<Room, Integer> idColumn = new TableColumn<Room, Integer>("Id");
		idColumn.setMinWidth(100);
		idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

		table.getColumns().addAll(idColumn, nameColumn, typeColumn, capacityColumn, locationColumn);

		HBox topBottomLayout = new HBox();
		topBottomLayout.setAlignment(Pos.CENTER);
		topBottomLayout.setPadding(new Insets(5, 5, 5, 5));
		topBottomLayout.setSpacing(10);
		tfCapacity = new TextField();
		iv.checkFocus(tfCapacity);
		iv.setTFStyleOnClick(tfCapacity);
		tfCapacity.setPromptText("Capacity");
		tfCapacity.setMinWidth(100);
		tfLocation = new TextField();
		iv.setTFStyleOnClick(tfLocation);
		iv.checkFocus(tfLocation);
		tfLocation.setPromptText("Location");
		tfLocation.setMinWidth(100);
		tfName = new TextField();
		iv.checkFocus(tfName);
		iv.setTFStyleOnClick(tfName);
		tfName.setPromptText("Name");
		tfName.setMinWidth(100);

		topBottomLayout.getChildren().addAll(new Label("Room Name"), tfName);
		checkBoxes = new ArrayList<>();
		for (String type : rTypes) {
			CheckBox cb = new CheckBox(type);
			checkBoxes.add(cb);
			iv.setTFStyleOnClick(cb);
			iv.checkFocus(cb);
			topBottomLayout.getChildren().add(cb);
		}

		HBox toptBottomLayout = new HBox();
		toptBottomLayout.setAlignment(Pos.CENTER);
		toptBottomLayout.setPadding(new Insets(5, 5, 5, 5));
		toptBottomLayout.setSpacing(10);

		toptBottomLayout.getChildren().addAll(new Label("Capacity"), tfCapacity, new Label("Location"), tfLocation);

		HBox bottomBottomLayout = new HBox();
		bottomBottomLayout.setAlignment(Pos.CENTER);
		bottomBottomLayout.setPadding(new Insets(5, 5, 5, 5));
		bottomBottomLayout.setSpacing(5);

		// Buttons
		bAdd = new Button("Add");
		bAdd.setMinWidth(100);
		bAdd.setOnAction(e -> bAddClicked());
		bRemove = new Button("Remove");
		bRemove.setMinWidth(100);
		bRemove.setOnAction(e -> bRemoveClicked());
		bClear = new Button("Clear");
		bClear.setMinWidth(100);
		bClear.setOnAction(e -> bClearClicked());
		bRoomBooked = new Button("Book Room (Excluded Times)");
		bRoomBooked.setOnAction(e -> bRoomBookedClicked());

		bottomBottomLayout.getChildren().addAll(bAdd, bRemove, bClear);

		VBox bottomPane = new VBox();
		bottomPane.setAlignment(Pos.CENTER);
		bottomPane.setPadding(new Insets(5, 5, 5, 5));
		bottomPane.setSpacing(5);
		bottomPane.getChildren().addAll(topBottomLayout, toptBottomLayout, bottomBottomLayout, bRoomBooked);

		BorderPane pane = new BorderPane();
		// pane.setAlignment(Pos.CENTER);
		pane.setTop(topLayout);
		pane.setCenter(table);
		pane.setBottom(bottomPane);
		return pane;
	}

	private void bRoomBookedClicked() {
		if (!table.getSelectionModel().isEmpty()) {
			Room room = table.getSelectionModel().getSelectedItem();
			new RoomBookedGUI(room.getId(), db, d, iv, myStyle).generateGUI();
		} else {
			new ErrorDialog("Please select a row!", "Error").generateGUI();
		}
	}

	private void bClearClicked() {
		tfCapacity.setText("");
		// tfId.setText("");
		tfLocation.setText("");
		tfName.setText("");
		// comboType.getSelectionModel().clearSelection();
	}

	private void bRemoveClicked() {
		try {
			Room room = table.getSelectionModel().getSelectedItem();
			db.removeRoom(room.getId());
			table.setItems(loadData());
		} catch (Exception e) {
			new ErrorDialog("Please Select a row!", "Error").generateGUI();
		}
	}

	private void bAddClicked() {
		boolean a = iv.checkModuleInput(tfName, 100);
		boolean b = iv.containsUnderScore(tfName);
		boolean c = iv.checkModuleInput(tfLocation, 100);
		boolean e = iv.containsUnderScore(tfLocation);
		boolean d = iv.checkIfInputIsAnInteger(tfCapacity);
		// if (!comboType.getSelectionModel().isEmpty()) {
		// db.addRoom(, type, capacity, location);
		if (a && b && c && d && e) {
			String name = tfName.getText();
			int capacity = Integer.parseInt(tfCapacity.getText());
			String location = tfLocation.getText();
			String type = "";
			for (CheckBox cbb : checkBoxes) {
				if (cbb.isSelected()) {
					type += cbb.getText() + "-";
				}
			}
			if (type.length() == 0) {
				for (CheckBox cbb : checkBoxes) {
					cbb.setStyle(iv.getNoInputStyle());
				}
			} else {
				type = type.substring(0, type.length() - 1);
				db.addRoom(name, type, capacity, location);
				table.setItems(loadData());
			}
		}
	}

	private ObservableList<Room> loadData() {
		ObservableList<Room> myData = FXCollections.observableArrayList();
		for (Room room : db.getRooms()) {
			myData.add(room);
		}
		return myData;
	}

}
