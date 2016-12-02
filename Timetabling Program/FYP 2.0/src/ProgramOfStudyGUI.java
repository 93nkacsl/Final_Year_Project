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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

//7-4-2016

public class ProgramOfStudyGUI {
	private InputValidation iv;
	private Database db;
	private int mId;
	private String[] programOfStudies;
	private TableView<ProgramOfStudy> table;
	private Button bAdd, bRemove, bClear;
	private ChoiceBox<String> comboPOfStudies, comboCompulsary;
	private TextField tfId;
	private String style = "-fx-border-color:red;";
	private String excep = "-fx-border-color:green;";
	private String myStyle;

	public ProgramOfStudyGUI(Database db, int mId, String[] programOfStudies, InputValidation iv, String myStyle) {
		this.iv = iv;
		this.db = db;
		this.mId = mId;
		this.programOfStudies = programOfStudies;
		this.myStyle = myStyle;
	}

	public void generateGUI() {
		Stage stage = new Stage();
		stage.setTitle("Program of Study");
		StackPane topLayout = new StackPane();
		topLayout.setAlignment(Pos.CENTER);
		Label title = new Label("Program Of Study");
		topLayout.getChildren().add(title);
		// Center layout:
		table = new TableView<ProgramOfStudy>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setRowFactory(tv -> {
			TableRow<ProgramOfStudy> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					ProgramOfStudy pOfStudy = row.getItem();
					comboCompulsary.getSelectionModel().select(pOfStudy.getCompulsary());
					comboPOfStudies.getSelectionModel().select(pOfStudy.getName());
					tfId.setText(mId + "");
				}
			});
			return row;
		});

		loadData(mId);

		TableColumn<ProgramOfStudy, Integer> colMid = new TableColumn<ProgramOfStudy, Integer>("Id");
		colMid.setMinWidth(150);
		colMid.setCellValueFactory(new PropertyValueFactory<>("mId"));

		TableColumn<ProgramOfStudy, String> colProgramOfStudy = new TableColumn<ProgramOfStudy, String>("Name");
		colProgramOfStudy.setMinWidth(150);
		colProgramOfStudy.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<ProgramOfStudy, String> colCompulsary = new TableColumn<ProgramOfStudy, String>("Compulsary");
		colCompulsary.setMinWidth(150);
		colCompulsary.setCellValueFactory(new PropertyValueFactory<>("compulsary"));
		table.getColumns().addAll(colMid, colProgramOfStudy, colCompulsary);

		VBox bottomPane = new VBox();
		bottomPane.setPadding(new Insets(5, 5, 5, 5));
		bottomPane.setSpacing(10);

		HBox bottomBottomPane = new HBox();
		bottomBottomPane.setAlignment(Pos.CENTER);
		bottomBottomPane.setPadding(new Insets(5, 5, 5, 5));
		bottomBottomPane.setSpacing(10);
		bAdd = new Button("Add");

		bAdd.setMinWidth(100);
		bAdd.setOnAction(e -> bAddClicked());
		bRemove = new Button("Remove");
		bRemove.setMinWidth(100);
		bRemove.setOnAction(e -> bRemoveClicked());
		bClear = new Button("Clear");
		bClear.setMinWidth(100);
		bClear.setOnAction(e -> bClearClicked());
		bottomBottomPane.getChildren().addAll(bAdd, bRemove, bClear);

		HBox bottomTopPane = new HBox();
		bottomTopPane.setSpacing(10);
		bottomTopPane.setAlignment(Pos.CENTER);
		comboPOfStudies = new ChoiceBox<>();
		iv.checkFocus(comboPOfStudies);

		comboPOfStudies.setMinWidth(100);
		comboPOfStudies.getItems().addAll(programOfStudies);
		comboCompulsary = new ChoiceBox<>();
		iv.checkFocus(comboCompulsary);
		iv.setTFStyleOnClick(comboPOfStudies);
		comboCompulsary.setMinWidth(100);
		String[] compulsary = { "yes", "no" };
		comboCompulsary.getItems().addAll(compulsary);
		iv.setTFStyleOnClick(comboCompulsary);
		tfId = new TextField();
		iv.checkFocus(tfId);
		tfId.setMinWidth(40);
		tfId.setPrefWidth(40);
		tfId.setDisable(true);
		bottomTopPane.getChildren().addAll(new Label("ID"), tfId, new Label("Program of Study"), comboPOfStudies,
				new Label("Compulsary"), comboCompulsary);
		bottomPane.getChildren().addAll(bottomTopPane, bottomBottomPane);
		bottomTopPane.setMinWidth(700);
		BorderPane mainPain = new BorderPane();
		mainPain.setTop(topLayout);
		mainPain.setCenter(table);
		mainPain.setBottom(bottomPane);

		Scene scene = new Scene(mainPain);
		scene.getStylesheets().add(myStyle);
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}

	private void bClearClicked() {
		comboPOfStudies.getSelectionModel().clearSelection();
		comboCompulsary.getSelectionModel().clearSelection();
		tfId.setText("");
		table.getSelectionModel().clearSelection();
	}

	private void bRemoveClicked() {
		if (!table.getSelectionModel().isEmpty()) {
			ProgramOfStudy pOfStudy = table.getSelectionModel().getSelectedItem();
			db.removeProgramOfStudy(mId, pOfStudy.getName());
			loadData(mId);
		} else {
			new ErrorDialog("A row must be selected", "Error").generateGUI();
		}
	}

	private void bAddClicked() {
		boolean a = iv.checkIfCheckBoxIsNotSelected(comboCompulsary);
		boolean b = iv.checkIfCheckBoxIsNotSelected(comboPOfStudies);
		if (a && b) {
			db.addProgramOfStudy(comboPOfStudies.getSelectionModel().getSelectedItem(), mId,
					comboCompulsary.getSelectionModel().getSelectedItem());
			comboCompulsary.getSelectionModel().clearSelection();
			comboPOfStudies.getSelectionModel().clearSelection();
			loadData(mId);
		}

	}

	public void loadData(int mId) {
		ObservableList<ProgramOfStudy> myData = FXCollections.observableArrayList();
		for (ProgramOfStudy pOfStudy : db.getProgramOfStudies(mId)) {
			myData.add(pOfStudy);
		}
		table.setItems(myData);
	}

}
