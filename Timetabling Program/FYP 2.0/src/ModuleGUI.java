import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

//7-4-2016

public class ModuleGUI {
	private InputValidation iv;
	private Database db;
	private TableView<Module> table;
	private ChoiceBox<String> comboTypes;
	private TextField tfTA, tfDuration, tfLevel, tfNOfStudents, tfMMId;
	private Button bAdd, bRemove, bMofidyPOfStudies, bClear, bAddLecturer;
	private String[] programOfStudies;
	private String[] roomAndModuleType;
	private String myStyle;

	public ModuleGUI(Database db, String[] programOfStudies, String[] roomAndModuleType, InputValidation iv,
			String myStyle) {
		this.db = db;
		this.programOfStudies = programOfStudies;
		this.roomAndModuleType = roomAndModuleType;
		this.iv = iv;
		this.myStyle = myStyle;
	}

	public Pane getGUI() {
		// The top layout:
		StackPane topLayout = new StackPane();
		topLayout.setAlignment(Pos.CENTER);
		Label title = new Label("Module");
		topLayout.getChildren().add(title);
		topLayout.setPadding(new Insets(5, 5, 5, 5));

		// Center layout:
		table = new TableView<Module>();
		table.setItems(loadData());
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		table.setRowFactory(tv -> {
			TableRow<Module> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					Module module = row.getItem();
					tfDuration.setText(module.getDuration() + "");
					tfLevel.setText(module.getLevel() + "");
					tfMMId.setText(module.getMmId() + "");
					tfNOfStudents.setText(module.getNumberOfStudents() + "");
					tfTA.setText(module.getName());
					if (module.getType().equals("lecture")) {
						comboTypes.getSelectionModel().select(0);
					} else {
						comboTypes.getSelectionModel().select(1);
					}
				}
			});
			return row;
		});

		TableColumn<Module, Integer> colId = new TableColumn<Module, Integer>("Id");
		colId.setMinWidth(50);
		colId.setCellValueFactory(new PropertyValueFactory<>("mId"));

		TableColumn<Module, String> colName = new TableColumn<Module, String>("Teaching Activity");
		colName.setMinWidth(150);
		colName.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<Module, Integer> colDuration = new TableColumn<Module, Integer>("Duration");
		colDuration.setMinWidth(100);
		colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));

		TableColumn<Module, Integer> colLevel = new TableColumn<Module, Integer>("Level");
		colLevel.setMinWidth(100);
		colLevel.setCellValueFactory(new PropertyValueFactory<>("level"));

		TableColumn<Module, String> colType = new TableColumn<Module, String>("Type");
		colType.setMinWidth(100);
		colType.setCellValueFactory(new PropertyValueFactory<>("type"));

		TableColumn<Module, Integer> colNOfStudents = new TableColumn<Module, Integer>("Number of Students");
		colNOfStudents.setMinWidth(150);
		colNOfStudents.setCellValueFactory(new PropertyValueFactory<>("numberOfStudents"));

		TableColumn<Module, Integer> colMMId = new TableColumn<Module, Integer>("MMId");
		colMMId.setMinWidth(100);
		colMMId.setCellValueFactory(new PropertyValueFactory<>("mmId"));

		table.getColumns().addAll(colId, colName, colDuration, colLevel, colType, colNOfStudents, colMMId);

		// Bottom Pane:
		tfDuration = new TextField();
		tfDuration.setMinWidth(100);
		tfDuration.setPromptText("Duration");
		iv.setTFStyleOnClick(tfDuration);
		tfLevel = new TextField();
		iv.setTFStyleOnClick(tfLevel);
		tfLevel.setMinWidth(100);
		tfLevel.setPromptText("Level");
		tfMMId = new TextField();
		iv.setTFStyleOnClick(tfMMId);
		tfMMId.setMinWidth(100);
		tfMMId.setPromptText("MMId");
		tfNOfStudents = new TextField();
		iv.setTFStyleOnClick(tfNOfStudents);
		tfNOfStudents.setMinWidth(100);
		tfNOfStudents.setPromptText("Number of Students");
		tfTA = new TextField();
		iv.setTFStyleOnClick(tfTA);
		tfTA.setMinWidth(100);
		tfTA.setPromptText("Teaching Activity");
		
		comboTypes = new ChoiceBox<>();
		comboTypes.setMinWidth(100);
		iv.setTFStyleOnClick(comboTypes);
		comboTypes.getItems().addAll(roomAndModuleType);
		// Buttons
		bAdd = new Button("Add");
		bAdd.setMinWidth(100);
		bAdd.setOnAction(e -> bAddClicked());
		bRemove = new Button("Remove");
		bRemove.setMinWidth(100);
		bRemove.setOnAction(e -> removeClicked());
		bClear = new Button("Clear");
		bClear.setOnAction(e -> clearClicked());
		bAddLecturer = new Button("Assign Lecturer");
		bAddLecturer.setOnAction(e -> addTeaches(db));

		VBox bottomPane = new VBox();
		bottomPane.setPadding(new Insets(5, 5, 5, 5));
		bottomPane.setSpacing(10);

		HBox topBottomPane = new HBox();
		topBottomPane.setPadding(new Insets(5, 5, 5, 5));
		topBottomPane.setSpacing(10);
		topBottomPane.getChildren().addAll(new Label("TA"), tfTA, new Label("Duration"), tfDuration, new Label("Level"),
				tfLevel);
		HBox topcPane = new HBox();
		topcPane.setAlignment(Pos.CENTER);
		topcPane.setPadding(new Insets(5, 5, 5, 5));
		topcPane.setSpacing(10);
		topcPane.getChildren().addAll(new Label("Type"), comboTypes, new Label("Number of Students"), tfNOfStudents,
				new Label("MMID"), tfMMId);

		topBottomPane.setAlignment(Pos.CENTER);
		HBox bottomBottomPane = new HBox();
		bottomBottomPane.setAlignment(Pos.CENTER);
		bottomBottomPane.setPadding(new Insets(5, 5, 5, 5));
		bottomBottomPane.setSpacing(10);
		bottomBottomPane.getChildren().addAll(bAdd, bRemove, bClear);

		HBox bottomBottomBottomPane = new HBox();
		bottomBottomBottomPane.setAlignment(Pos.CENTER);
		bottomBottomBottomPane.setPadding(new Insets(5, 5, 5, 5));
		bottomBottomBottomPane.setSpacing(10);
		bMofidyPOfStudies = new Button("Modify Program Of Studies");
		bMofidyPOfStudies.setOnAction(e -> programOfStudies());
		bottomBottomBottomPane.getChildren().addAll(bMofidyPOfStudies, bAddLecturer);
		bottomPane.getChildren().addAll(topBottomPane, topcPane, bottomBottomPane, bottomBottomBottomPane);

		// Main Pain:
		BorderPane mainPain = new BorderPane();
		mainPain.setTop(topLayout);
		mainPain.setCenter(table);
		mainPain.setBottom(bottomPane);
		return mainPain;
	}

	private void addTeaches(Database db) {
		if (!table.getSelectionModel().isEmpty()) {
			Module module = table.getSelectionModel().getSelectedItem();
			new TeachesGUI(db, module.getMId(), module.getDuration(), iv, myStyle).generateGUI();
		} else {
			new ErrorDialog("Please select a row.", "Error").generateGUI();
		}
	}

	private void clearClicked() {
		tfDuration.setText("");
		// tfId.setText("");
		tfLevel.setText("");
		tfMMId.setText("");
		tfNOfStudents.setText("");
		tfTA.setText("");
		comboTypes.getSelectionModel().clearSelection();
	}

	/**
	 * This method adds a TA to the database.
	 */
	private void bAddClicked() {
		boolean a = iv.checkModuleInput(tfTA, 60);
		iv.checkFocus(tfTA);
		boolean g = iv.containsUnderScore(tfTA);
		iv.checkFocus(tfDuration);
		boolean b = iv.checkIfInputIsAnInteger(tfDuration);
		iv.checkFocus(tfLevel);
		boolean c = iv.checkIfInputIsAnInteger(tfLevel);
		iv.checkFocus(comboTypes);
		boolean d = iv.checkIfCheckBoxIsNotSelected(comboTypes);
		iv.checkFocus(tfMMId);
		boolean e = iv.checkIfInputIsAnInteger(tfMMId);
		iv.checkFocus(tfNOfStudents);
		boolean f = iv.checkIfInputIsAnInteger(tfNOfStudents);

		if (a && b && c && d && e && f && g) {
			String name = tfTA.getText();
			String type = comboTypes.getSelectionModel().getSelectedItem();
			int duration = Integer.parseInt(tfDuration.getText());
			int level = Integer.parseInt(tfLevel.getText());
			int numberOfStudents = Integer.parseInt(tfNOfStudents.getText());
			int mmId = Integer.parseInt(tfMMId.getText());
			db.addAdditionalClass(name, duration, level, numberOfStudents, type, mmId);
			table.setItems(loadData());
		}
	}

	/**
	 * This method brings up the
	 * 
	 * @return
	 */
	private void programOfStudies() {
		if (!table.getSelectionModel().isEmpty()) {
			Module module = table.getSelectionModel().getSelectedItem();
			new ProgramOfStudyGUI(db, module.getMId(), programOfStudies, iv, myStyle).generateGUI();
		} else {
			new ErrorDialog("A row must be selected", "Error").generateGUI();
		}
	}

	/**
	 * This method loads the content from the database.
	 * 
	 * @return
	 */
	private ObservableList<Module> loadData() {
		ObservableList<Module> myData = FXCollections.observableArrayList();
		for (Module module : db.getALLModules()) {
			myData.add(module);
		}
		return myData;
	}

	private void removeClicked() {
		try {
			Module m = table.getSelectionModel().getSelectedItem();
			db.removeModule(m.getMId());
			table.setItems(loadData());
		} catch (NullPointerException e) {
			new ErrorDialog("No Row has been selected, please select one!", "Error").generateGUI();
			System.out.println("Error:bRemoveModuleClicked");
		}
	}

}
