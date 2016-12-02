
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

//7-4-2016

public class LecturerGUI {
	private Database db;
	private TableView<Lecturer> table;
	private Button bAdd, bRemove, bClear, bAddLecturerUnavail;
	private TextField tfFName, tfLName;
	private DateAndTime d;
	// Style:
	private String myStyle;
	private InputValidation iv;
	// private String myStyle;

	/**
	 * Done and Checked.
	 * 
	 * @param db
	 * @param d
	 * @param iv
	 */
	public LecturerGUI(Database db, DateAndTime d, InputValidation iv, String myStyle) {
		this.db = db;
		this.d = d;
		this.iv = iv;
		this.myStyle = myStyle;
	}

	public Pane getGUI() {
		// The top layout:
		StackPane topLayout = new StackPane();
		topLayout.setPadding(new Insets(5, 5, 5, 5));

		topLayout.setAlignment(Pos.CENTER);
		Label title = new Label("Lecturer");
		topLayout.getChildren().add(title);
		// Center layout
		table = new TableView<Lecturer>();
		table.setItems(loadTheData());
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setRowFactory(tv -> {
			TableRow<Lecturer> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					Lecturer lecturer = row.getItem();
					tfFName.setText(lecturer.getFName());
					tfLName.setText(lecturer.getLName());
				}
			});
			return row;
		});

		TableColumn<Lecturer, Integer> colId = new TableColumn<Lecturer, Integer>("Id");
		colId.setMinWidth(50);
		colId.setCellValueFactory(new PropertyValueFactory<>("Id"));

		TableColumn<Lecturer, String> colFName = new TableColumn<Lecturer, String>("First Name");
		colFName.setMinWidth(100);
		colFName.setCellValueFactory(new PropertyValueFactory<>("fName"));
		colFName.setCellFactory(TextFieldTableCell.forTableColumn());

		TableColumn<Lecturer, String> colLname = new TableColumn<Lecturer, String>("Last Name");
		colLname.setMinWidth(100);
		colLname.setCellValueFactory(new PropertyValueFactory<>("lName"));
		colLname.setCellFactory(TextFieldTableCell.forTableColumn());

		table.getColumns().addAll(colId, colFName, colLname);

		// Add Modify or remove
		bAdd = new Button("Add");
		bAdd.setMinWidth(100);
		bAdd.setOnAction(e -> bAddClicked());
		bRemove = new Button("Remove");
		bRemove.setMinWidth(100);
		bRemove.setOnAction(e -> bRemoveClicked());
		bClear = new Button("Clear");
		bClear.setMinWidth(100);
		bClear.setOnAction(e -> bClearClicked());
		bAddLecturerUnavail = new Button("Excluded Lecturer Times");
		bAddLecturerUnavail.setMinWidth(100);
		bAddLecturerUnavail.setOnAction(e -> bAddLecturerUnavailClicked());
		tfFName = new TextField();
		iv.checkFocus(tfFName);
		tfFName.setPromptText("First Name");
		tfFName.setMinWidth(100);
		tfFName.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					if (mouseEvent.getClickCount() == 1) {
						tfFName.setStyle("");
					}
				}
			}
		});
		tfLName = new TextField();
		iv.checkFocus(tfLName);
		tfLName.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					if (mouseEvent.getClickCount() == 1) {
						tfLName.setStyle("");
					}
				}
			}
		});

		tfLName.setMinWidth(100);
		tfLName.setPromptText("Last Name");
		// Bottom Pane:
		VBox bottomPane = new VBox();
		bottomPane.setAlignment(Pos.CENTER);
		bottomPane.setPadding(new Insets(5, 5, 5, 5));
		bottomPane.setSpacing(5);

		HBox bottomTopPane = new HBox();

		bottomTopPane.setAlignment(Pos.CENTER);
		bottomTopPane.setPadding(new Insets(5, 5, 5, 5));
		bottomTopPane.setSpacing(15);
		Label labelOne = new Label("First Name");
		labelOne.setMinWidth(70);
		Label labelTwo = new Label("Last Name");
		labelTwo.setMinWidth(70);

		bottomTopPane.getChildren().addAll(labelOne, tfFName, labelTwo, tfLName);
		HBox bottomBottomPane = new HBox();
		bottomBottomPane.setAlignment(Pos.CENTER);
		bottomBottomPane.setPadding(new Insets(5, 5, 5, 5));
		bottomBottomPane.setSpacing(5);
		bottomBottomPane.getChildren().addAll(bAdd, bRemove, bClear);
		bottomPane.getChildren().addAll(bottomTopPane, bottomBottomPane, bAddLecturerUnavail);
		BorderPane mainPain = new BorderPane();
		mainPain.setTop(topLayout);
		mainPain.setCenter(table);
		mainPain.setBottom(bottomPane);
		mainPain.setPadding(new Insets(5, 5, 5, 5));
		return mainPain;
	}

	private void bAddLecturerUnavailClicked() {
		if (!table.getSelectionModel().isEmpty()) {
			Lecturer lec = table.getSelectionModel().getSelectedItem();
			new LecturerUnavailGUI(db, lec.getId(), d, iv, myStyle).generateGUI();
		} else {
			new ErrorDialog("A row must be selected!", "Error").generateGUI();
		}
	}

	private void bRemoveClicked() {
		try {
			Lecturer lec = table.getSelectionModel().getSelectedItem();
			db.removeLecturer(lec.getId());
			table.setItems(loadTheData());

		} catch (NullPointerException e) {
			new ErrorDialog("No Row has been selected, please select one!", "Error!").generateGUI();
			System.out.println("Error:bRemoveLecturerClicked");
		}
	}

	private void bAddClicked() {
		String fName = tfFName.getText();
		String lName = tfLName.getText();
		iv.checkTFName(tfFName, 20);
		iv.checkTFName(tfLName, 20);
		iv.containsUnderScore(tfFName);
		iv.containsUnderScore(tfLName);
		if (iv.checkTFName(tfFName, 20) && iv.containsUnderScore(tfFName) && iv.checkTFName(tfLName, 20)
				&& iv.containsUnderScore(tfLName)) {
			db.addLecturer(fName, lName, "both");
			tfFName.setText("");
			tfLName.setText("");
			table.setItems(loadTheData());
		}

	}

	/**
	 * This method has to be modified in the future.
	 * 
	 * @param tf
	 * @return
	 */
	public boolean checkInputText(TextField tf) {
		if (tf.getText().length() == 0) {
			tf.setStyle("-fx-border-color:red;");
			return false;
		} else
			tf.setStyle("");
		return true;
	}

	/**
	 * The modified button is clicked.
	 */
	private void bClearClicked() {
		tfFName.setText("");
		tfLName.setText("");
		tfFName.setStyle("");
		tfLName.setStyle("");
	}

	/**
	 * Gets the list of the lecturers from the database.
	 * 
	 * @return
	 */
	private ObservableList<Lecturer> loadTheData() {
		ObservableList<Lecturer> myData = FXCollections.observableArrayList();
		for (Lecturer lecturer : db.getLecturers()) {
			myData.add(lecturer);
		}
		return myData;
	}

}
