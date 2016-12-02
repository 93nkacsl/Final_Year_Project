
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//7-4-2016

public class SettingGUI {
	private InputValidation iv;
	private ListView<String> pOfStudiesList;
	private Preferences pref;
	private ArrayList<String> pOfStudies, roomTypes;
	// SA stuff:
	private Button bChangeSASetting;

	private TextField tfInitTemp, tfCoolingRate;

	private RadioButton rbTwo, rbThree, rbFour;

	private String[] times = { "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00",
			"11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00",
			"23:00", "24:00" };
	// The constraint stuff.
	private TextField tfCw1, tfCw2, tfCw3, tfCw4, tfCw5, tfCw5X, tfCw8, tfCw9, tfCw10;
	private Button bSaveCW;

	// The GA stuff:
	private TextField tfPopulation, tfPercentile;
	private RadioButton rbSingle, rbDouble;

	public SettingGUI(Preferences pref, InputValidation iv) {
		this.pref = pref;
		this.iv = iv;
	}

	public ScrollPane getGUI() {
		// Main View:
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(5, 5, 5, 5));
		vbox.setSpacing(5);
		vbox.setAlignment(Pos.TOP_CENTER);
		Label label = new Label("Setting");

		HBox hbOne = new HBox();
		hbOne.setSpacing(10);
		hbOne.setAlignment(Pos.CENTER_LEFT);
		hbOne.setPadding(new Insets(10, 10, 10, 10));
		Label greedyLabel = new Label("Greedy Algorithm:");
		hbOne.getChildren().add(greedyLabel);

		HBox hbOnePointTwo = new HBox();
		hbOnePointTwo.setSpacing(10);
		hbOnePointTwo.setAlignment(Pos.CENTER);
		hbOnePointTwo.setPadding(new Insets(5, 5, 5, 5));
		ChoiceBox startTime = new ChoiceBox<>();
		String st = pref.get("st", "09:00");
		startTime.getSelectionModel().select(st);
		startTime.setItems(FXCollections.observableArrayList(times));
		ChoiceBox endTime = new ChoiceBox<>();
		String et = pref.get("et", "18:00");
		endTime.getSelectionModel().select(et);
		endTime.setItems(FXCollections.observableArrayList(times));
		Button bAddStAndEt = new Button("Change Start time and End time");

		bAddStAndEt.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String st = startTime.getSelectionModel().getSelectedItem() + "";
				String et = endTime.getSelectionModel().getSelectedItem() + "";
				int one = -1;
				int two = -1;
				for (int i = 0; i < times.length; i++) {
					if (st.equals(times[i])) {
						one = i;
					}
					if (et.equals(times[i])) {
						two = i;
					}
				}
				if (one >= two) {
					new ErrorDialog("Incorrect time's set, Please try again.", "Error").generateGUI();
				} else {
					pref.put("st", st);
					pref.put("et", et);
				}

			}
		});
		hbOnePointTwo.getChildren().addAll(new Label("Opening Time: "), startTime, new Label("Closing time: "), endTime,
				bAddStAndEt);
		// Launchtime stuff
		HBox hbOnePointThree = new HBox();
		hbOnePointThree.setSpacing(10);
		hbOnePointThree.setAlignment(Pos.CENTER);
		hbOnePointThree.setPadding(new Insets(5, 5, 5, 5));
		ChoiceBox launchStCombo = new ChoiceBox<>();
		String launchSt = pref.get("launchSt", "11:00");
		launchStCombo.getSelectionModel().select(launchSt);
		launchStCombo.setItems(FXCollections.observableArrayList(times));
		String launchEt = pref.get("launchEt", "13:00");
		ChoiceBox launchEtCombo = new ChoiceBox<>();
		launchEtCombo.getSelectionModel().select(launchEt);
		launchEtCombo.setItems(FXCollections.observableArrayList(times));
		Button saveLaunchTimes = new Button("Save Launch Time Hours.");
		saveLaunchTimes.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String launchmySt = (String) launchStCombo.getSelectionModel().getSelectedItem();
				String launchmyEt = (String) launchEtCombo.getSelectionModel().getSelectedItem();
				int one = -1;
				int two = -1;
				for (int i = 0; i < times.length; i++) {
					if (times[i].equals(launchmySt)) {
						one = i;
					}
					if (times[i].equals(launchmyEt)) {
						two = i;
					}
				}
				if (one == two || one > two) {
					new ErrorDialog("Please select correct times!", "Error").generateGUI();
				} else {
					pref.put("launchSt", launchmySt);
					pref.put("launchEt", launchmyEt);
				}

			}
		});
		hbOnePointThree.getChildren().addAll(new Label("Launch Time Starting Time: "), launchStCombo,
				new Label("Launch Time Ending Time"), launchEtCombo, saveLaunchTimes);

		HBox hbTwo = new HBox();
		setFormat(hbTwo);
		ToggleGroup group = new ToggleGroup();

		RadioButton rTrue = new RadioButton("Random Days");
		rTrue.setOnAction(e -> {
			pref.putBoolean("greedyValue", false);
			System.out.println(pref.getBoolean("greedyValue", true));
		});
		rTrue.setMinWidth(200);
		rTrue.setToggleGroup(group);

		RadioButton rFalse = new RadioButton("Non-Random Days");
		rFalse.setMinWidth(200);
		rFalse.setToggleGroup(group);
		rFalse.setOnAction(e -> {
			pref.putBoolean("greedyValue", true);
			System.out.println(pref.getBoolean("greedyValue", true));
		});
		hbTwo.getChildren().addAll(rTrue, rFalse);

		if (pref.getBoolean("greedyValue", true)) {
			rFalse.setSelected(true);
		} else {
			rTrue.setSelected(true);
		}
		VBox pOfStudiesvb = getPofStudies();
		VBox vbRType = getRoomType();
		VBox vbSASetting = getSASetting();

		VBox vbGASetting = getGASetting();

		vbox.getChildren().addAll(label, getTheCconstraintsGUI(), hbOnePointTwo, hbOnePointThree, pOfStudiesvb, vbRType,
				hbOne, hbTwo, vbSASetting, vbGASetting);
		vbox.getStyleClass().add("vbox");
		ScrollPane scrollPane = new ScrollPane(vbox);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		scrollPane.setStyle("-fx-background-color:transparent;");

		return scrollPane;
	}

	/**
	 * The Genetic Algorithm part of the GUI.
	 * 
	 * @return
	 */
	private VBox getGASetting() {
		VBox vb = new VBox();
		vb.setAlignment(Pos.CENTER);
		vb.setPadding(new Insets(5, 5, 5, 5));
		vb.setSpacing(10);

		HBox hbThree = new HBox();
		hbThree.setSpacing(10);
		hbThree.setAlignment(Pos.CENTER_LEFT);
		hbThree.setPadding(new Insets(10, 10, 10, 10));
		Label gaLabel = new Label("Genetic Algorithm");
		hbThree.getChildren().add(gaLabel);

		HBox hbFour = new HBox();
		hbFour.setSpacing(10);
		hbFour.setAlignment(Pos.CENTER);
		hbFour.setPadding(new Insets(10, 10, 10, 10));

		tfPopulation = new TextField();
		tfPopulation.setPromptText("Population");
		tfPopulation.setText(pref.getInt("population", 100) + "");
		Tooltip tp = new Tooltip();
		tp.setText("Population");
		tfPopulation.setTooltip(tp);
		hbFour.getChildren().add(tfPopulation);

		tfPercentile = new TextField();
		tfPercentile.setPromptText("Percentile");
		tfPercentile.setText(pref.getDouble("percentile", 0.3) + "");
		Tooltip tpTwo = new Tooltip();
		tpTwo.setText("Percentile");
		tfPercentile.setTooltip(tpTwo);
		hbFour.getChildren().add(tfPercentile);

		ToggleGroup tgGroup = new ToggleGroup();
		rbSingle = new RadioButton("Single Crossover");
		rbSingle.setToggleGroup(tgGroup);
		rbDouble = new RadioButton("Double Crossover");
		rbDouble.setToggleGroup(tgGroup);
		if (pref.getBoolean("singleOrDouble", true)) {
			rbSingle.setSelected(true);
		} else {
			rbDouble.setSelected(true);
		}
		rbSingle.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				pref.putBoolean("singleOrDouble", true);
			}
		});
		rbDouble.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				pref.putBoolean("singleOrDouble", false);
			}
		});

		Button bAdd = new Button("Save Changes");
		bAdd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				boolean a = iv.checkIfInputIsADouble(tfPercentile, true);
				boolean b = iv.checkIfInputIsAnInteger(tfPopulation);
				if (a && b) {
					double percentile = Double.parseDouble(tfPercentile.getText());
					int population = Integer.parseInt(tfPopulation.getText());
					// pref.getDouble("percentile", 0.3)
					pref.putDouble("percentile", percentile);
					pref.putInt("population", population);
				}
			}
		});

		hbFour.getChildren().addAll(rbSingle, rbDouble, bAdd);
		vb.getChildren().addAll(hbThree, hbFour);
		return vb;
	}

	/**
	 * The constraint GUI
	 * 
	 * @return
	 */
	public VBox getTheCconstraintsGUI() {
		VBox vb = new VBox();
		vb.setAlignment(Pos.CENTER);
		vb.setPadding(new Insets(5, 5, 5, 5));
		vb.setSpacing(10);
		VBox rowOne = new VBox();
		rowOne.setSpacing(10);
		rowOne.setAlignment(Pos.CENTER);
		rowOne.setPadding(new Insets(10, 10, 10, 10));
		tfCw1 = new TextField();
		int cw1 = pref.getInt("cw1", 3);
		tfCw1.setText(cw1 + "");
		tfCw1.setPromptText("Constraint One");
		Tooltip tpcw1 = new Tooltip();
		tpcw1.setText("Two (compulsary) TA's of thesame program of study should not clash.");
		tfCw1.setTooltip(tpcw1);

		tfCw2 = new TextField();
		int cw2 = pref.getInt("cw2", 3);
		tfCw2.setText(cw2 + "");
		tfCw2.setPromptText("Constraint Two");
		Tooltip tpTwo = new Tooltip();
		tpTwo.setText("A compulsary TA must not clash with a non-compulsary TA of thesame program of study.");
		tfCw2.setTooltip(tpTwo);

		tfCw3 = new TextField();
		int cw3 = pref.getInt("cw3", 3);
		tfCw3.setText(cw3 + "");
		tfCw3.setPromptText("Constraint Three");
		Tooltip tpThree = new Tooltip();
		tpThree.setText("Two (non-compulsary) TA's of thesame program of study must not clash.");
		tfCw3.setTooltip(tpThree);

		tfCw4 = new TextField();
		int cw4 = pref.getInt("cw4", pref.getInt("cw4", 3));
		tfCw4.setText(cw4 + "");
		tfCw4.setPromptText("Constraint Four");
		Tooltip tpFour = new Tooltip();
		tpFour.setText("A module should not clash with its tutorial.");
		tfCw4.setTooltip(tpFour);

		tfCw8 = new TextField();
		int cw8 = pref.getInt("cw8", 3);
		tfCw8.setText(cw8 + "");
		tfCw8.setPromptText("Constraint Eight");
		Tooltip tpEight = new Tooltip("A Lecturer must not have a TA when he/she is unavaliable.");
		tfCw8.setTooltip(tpEight);

		tfCw9 = new TextField();
		int cw9 = pref.getInt("cw9", 3);
		tfCw9.setText("" + cw9);
		tfCw9.setPromptText("Constraint Nine");
		Tooltip tpNine = new Tooltip("A (compulsary) module must not clash with a (compulsary) tutorial.");
		tfCw9.setTooltip(tpNine);

		// rowOne.getChildren().addAll(tfCw1, tfCw2, tfCw3, tfCw4, tfCw8,
		// tfCw9);
		rowOne.getChildren().add(setUpTheConstraintRow("Constraint Weight One", tfCw1, "Constraint Weight Two", tfCw2));
		rowOne.getChildren()
				.add(setUpTheConstraintRow("Constraint Weight Three", tfCw3, "Constraint Weight Four", tfCw4));
		rowOne.getChildren()
				.add(setUpTheConstraintRow("Constraint Weight Eight", tfCw8, "Constraint Weight Nine", tfCw9));

		tfCw5 = new TextField();
		int cw5 = pref.getInt("cw5", 3);
		tfCw5.setText("" + cw5);
		tfCw5.setPromptText("Constraint five");
		Tooltip tpFive = new Tooltip();
		tpFive.setText("A program of study must not have more than 'x' hours of TA in thesame day.");
		tfCw5.setTooltip(tpFive);

		tfCw5X = new TextField();
		int x = pref.getInt("cw5X", 6);
		tfCw5X.setText(x + "");
		tfCw5X.setPromptText("'x'");
		Tooltip tpX = new Tooltip();
		tpX.setText("The 'x' number of hours for constraint five");
		tfCw5X.setTooltip(tpX);

		tfCw10 = new TextField();
		int cw10 = pref.getInt("cw10", 3);
		tfCw10.setText(cw10 + "");
		tfCw10.setPromptText("cw10");
		Tooltip tp10 = new Tooltip();
		tp10.setText("The launch time constraint.");
		tfCw10.setTooltip(tp10);

		bSaveCW = new Button("Save constraint values");
		bSaveCW.setOnAction(e -> cwSave());

		VBox rowTwo = new VBox();
		rowTwo.setSpacing(10);
		rowTwo.setAlignment(Pos.CENTER);
		rowTwo.setPadding(new Insets(10, 10, 10, 10));
		// rowTwo.getChildren().addAll(tfCw5, tfCw5X, tfCw10);
		rowTwo.getChildren()
				.addAll(setUpTheConstraintRow("Constraint Weight Five", tfCw5, "Number Of Hours 'x'", tfCw5X));
		HBox lastRowCw = new HBox();
		lastRowCw.setSpacing(10);
		lastRowCw.setAlignment(Pos.CENTER);
		lastRowCw.setPadding(new Insets(10, 10, 10, 10));
		lastRowCw.getChildren().addAll(new Label("Constraint Weight Ten"), tfCw10);
		vb.getChildren().addAll(rowOne, rowTwo, lastRowCw, bSaveCW);
		return vb;
	}

	private HBox setUpTheConstraintRow(String name, TextField tf, String nameTwo, TextField tf2) {
		HBox row = new HBox();
		row.setSpacing(10);
		row.setAlignment(Pos.CENTER);
		row.setPadding(new Insets(10, 10, 10, 10));
		row.getChildren().addAll(new Label(name), tf, new Label(nameTwo), tf2);
		return row;
	}

	private void cwSave() {
		boolean a = iv.checkIfInputIsAnInteger(tfCw1);
		boolean b = iv.checkIfInputIsAnInteger(tfCw2);
		boolean c = iv.checkIfInputIsAnInteger(tfCw3);
		boolean d = iv.checkIfInputIsAnInteger(tfCw4);
		boolean e = iv.checkIfInputIsAnInteger(tfCw5);
		boolean f = iv.checkIfInputIsAnInteger(tfCw5X);
		boolean g = iv.checkIfInputIsAnInteger(tfCw8);
		boolean h = iv.checkIfInputIsAnInteger(tfCw9);
		boolean i = iv.checkIfInputIsAnInteger(tfCw10);
		if (a && b && c && d && e && f && g && h && i) {
			int cw1 = Integer.parseInt(tfCw1.getText());
			int cw2 = Integer.parseInt(tfCw2.getText());
			int cw3 = Integer.parseInt(tfCw3.getText());
			int cw4 = Integer.parseInt(tfCw4.getText());
			int cw5 = Integer.parseInt(tfCw5.getText());
			int cw5X = Integer.parseInt(tfCw5X.getText());
			int cw8 = Integer.parseInt(tfCw8.getText());
			int cw9 = Integer.parseInt(tfCw9.getText());
			int cw10 = Integer.parseInt(tfCw10.getText());
			pref.putInt("cw1", cw1);
			pref.putInt("cw2", cw2);
			pref.putInt("cw3", cw3);
			pref.putInt("cw4", cw4);
			pref.putInt("cw5", cw5);
			pref.putInt("cw5X", cw5X);
			pref.putInt("cw8", cw8);
			pref.putInt("cw9", cw9);
			pref.putInt("cw10", cw10);
		}
	}

	public VBox getSASetting() {
		VBox vb = new VBox();
		vb.setAlignment(Pos.CENTER);
		vb.setPadding(new Insets(5, 5, 5, 5));
		vb.setSpacing(10);

		HBox hbThree = new HBox();
		hbThree.setSpacing(10);
		hbThree.setAlignment(Pos.CENTER_LEFT);
		hbThree.setPadding(new Insets(10, 10, 10, 10));
		Label saLabel = new Label("Simulated Annealing");
		hbThree.getChildren().add(saLabel);

		HBox hbFour = new HBox();
		hbFour.setSpacing(10);
		hbFour.setAlignment(Pos.CENTER);
		hbFour.setPadding(new Insets(10, 10, 10, 10));

		double temperature = pref.getDouble("initTemp", 1000);
		double coolingRate = pref.getDouble("coolingRate", 0.003);

		tfInitTemp = new TextField();
		tfInitTemp.setPromptText("Temperature");
		tfInitTemp.setText(temperature + "");
		tfCoolingRate = new TextField();
		tfCoolingRate.setPromptText("Cooling Rate");
		tfCoolingRate.setText(coolingRate + "");
		bChangeSASetting = new Button("Change SA setting");

		bChangeSASetting.setOnAction(e -> bChangeSAButton());
		hbFour.getChildren().addAll(new Label("Initial Temperature"), tfInitTemp, new Label("Colling Rate: "),
				tfCoolingRate, bChangeSASetting);

		HBox hbFive = new HBox();
		hbFive.setSpacing(10);
		hbFive.setAlignment(Pos.CENTER);
		hbFive.setPadding(new Insets(10, 10, 10, 10));

		ToggleGroup tgGroup = new ToggleGroup();
		rbTwo = new RadioButton("2");

		rbTwo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				pref.putInt("changes", 1);
			}
		});
		rbTwo.setToggleGroup(tgGroup);

		rbThree = new RadioButton("3");
		rbThree.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				pref.putInt("changes", 2);
			}
		});
		rbThree.setToggleGroup(tgGroup);
		rbFour = new RadioButton("4");
		rbFour.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				pref.putInt("changes", 3);
			}
		});
		int selected = pref.getInt("changes", 1);
		if (selected == 1) {
			rbTwo.setSelected(true);
		} else if (selected == 2) {
			rbThree.setSelected(true);
		} else {
			rbFour.setSelected(true);
		}
		rbFour.setToggleGroup(tgGroup);
		hbFive.getChildren().addAll(new Label("Neighbour changes: "), rbTwo, rbThree, rbFour);

		vb.getChildren().addAll(hbThree, hbFour, hbFive);
		return vb;
	}

	/**
	 * The Action listener for the changButton.
	 */
	private void bChangeSAButton() {
		iv.checkFocus(tfCoolingRate);
		iv.checkFocus(tfInitTemp);
		boolean a = iv.checkIfInputIsADouble(tfCoolingRate, true);
		boolean b = iv.checkIfInputIsADouble(tfInitTemp, false);
		if (a && b) {
			double initTemp = Double.parseDouble(tfInitTemp.getText());
			double coolingRate = Double.parseDouble(tfCoolingRate.getText());
			pref.putDouble("initTemp", initTemp);
			pref.putFloat("coolingRate", (float) coolingRate);
		}

	}

	public VBox getRoomType() {
		Button bAdd, bRemove;
		VBox vb = new VBox();
		vb.setAlignment(Pos.CENTER);
		vb.setPadding(new Insets(5, 5, 5, 5));
		vb.setSpacing(10);
		HBox hbTitle = new HBox();
		hbTitle.setAlignment(Pos.CENTER_LEFT);
		hbTitle.getChildren().add(new Label("Room & Module Types: "));
		// The ListView :
		ListView<String> roomTypesLV = new ListView<String>();
		roomTypesLV.setMinHeight(100);
		ArrayList<String> defaultList = new ArrayList<>();
		defaultList.add("Lecture");
		defaultList.add("Lab");
		defaultList.add("Tutorial");
		try {
			roomTypes = (ArrayList<String>) bytes2Object(pref.getByteArray("moduleTypes", object2Bytes(defaultList)));
			roomTypesLV.setItems(FXCollections.observableArrayList(roomTypes));
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER);
		hb.setSpacing(10);
		TextField tfInput = new TextField();
		tfInput.setPromptText("Module/Room Type");
		bAdd = new Button("+");
		bAdd.setOnAction(e -> {
			String input = tfInput.getText();
			if (input.length() == 0) {
				new ErrorDialog("Type must have a value", "Error").generateGUI();
				;
			} else {
				if (roomTypesLV.getItems().contains(input)) {
					new ErrorDialog("A type of thesame name already exists!", "Error").generateGUI();
				} else {
					// ArrayList<String> myItems
					// =bytes2Object(pref.get("moduleTypes", defaultList));
					roomTypes.add(input);
					try {
						pref.putByteArray("moduleTypes", object2Bytes(roomTypes));
						roomTypesLV.setItems(FXCollections.observableList(roomTypes));

					} catch (Exception e1) {

					}
				}
			}
		});
		bRemove = new Button("-");
		bRemove.setOnAction(e -> {
			if (roomTypesLV.getSelectionModel().isEmpty()) {
				new ErrorDialog("Please select a row!", "Error").generateGUI();
			} else {
				String input = roomTypesLV.getSelectionModel().getSelectedItem();
				roomTypes.remove(input);
				try {
					pref.putByteArray("moduleTypes", object2Bytes(roomTypes));
				} catch (Exception e1) {
				}
				roomTypesLV.setItems(FXCollections.observableList(roomTypes));
			}
		});

		hb.getChildren().addAll(tfInput, bAdd, bRemove);

		vb.getChildren().addAll(hbTitle, roomTypesLV, hb);
		return vb;
	}

	/**
	 * This method sets up the pOfStudies part of the gui.
	 * 
	 * @return
	 */
	private VBox getPofStudies() {
		Button bAdd, bRemove;
		VBox vb = new VBox();
		vb.setAlignment(Pos.CENTER);
		vb.setPadding(new Insets(5, 5, 5, 5));
		vb.setSpacing(10);
		HBox hbTitle = new HBox();
		hbTitle.setAlignment(Pos.CENTER_LEFT);
		hbTitle.getChildren().add(new Label("Program Of Studies: "));
		pOfStudiesList = new ListView<String>();
		pOfStudiesList.setMinHeight(100);
		pOfStudiesList.setPrefWidth(200);
		// Default list of pOfStudies.
		ArrayList<String> list = new ArrayList<>();
		list.add("TypeA");
		list.add("TypeB");
		try {
			pOfStudies = (ArrayList<String>) bytes2Object(pref.getByteArray("pOfStudies", object2Bytes(list)));
			pOfStudiesList.setItems(FXCollections.observableArrayList(pOfStudies));

		} catch (ClassNotFoundException e) {

		} catch (IOException e) {

		}
		VBox posVB = new VBox();
		posVB.setPadding(new Insets(5, 5, 5, 5));
		posVB.setSpacing(5);

		TextField tfInput = new TextField();
		tfInput.setPromptText("Program Of Study");

		bRemove = new Button("-");
		bAdd = new Button("+");
		bAdd.setOnAction(e -> {
			String input = tfInput.getText();
			if (input.length() == 0) {
				new ErrorDialog("Program of study must have a name!", "Error").generateGUI();
			} else {
				if (pOfStudiesList.getItems().contains(input)) {
					new ErrorDialog("A program of study t", "Error").generateGUI();
				} else {
					try {
						pOfStudies.add(input);
						pref.putByteArray("pOfStudies", object2Bytes(pOfStudies));
						pOfStudiesList.setItems(FXCollections.observableList(pOfStudies));
						tfInput.setText("");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		bRemove.setOnAction(e -> {
			if (pOfStudiesList.getSelectionModel().isEmpty()) {
				new ErrorDialog("Please select a row!", "Error").generateGUI();
			} else {
				tfInput.setText("");
				String selected = pOfStudiesList.getSelectionModel().getSelectedItem().toString();
				pOfStudies.remove(selected);
				try {
					pref.putByteArray("pOfStudies", object2Bytes(pOfStudies));
					pOfStudiesList.setItems(FXCollections.observableList(pOfStudies));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER);
		hb.setSpacing(10);
		hb.getChildren().addAll(tfInput, bAdd, bRemove);
		posVB.getChildren().addAll(pOfStudiesList, hb);
		vb.getChildren().addAll(hbTitle, posVB);
		return vb;
	}

	public void setFormat(HBox hbOne) {
		hbOne.setSpacing(10);
		hbOne.setPadding(new Insets(10, 10, 10, 10));
		hbOne.setAlignment(Pos.CENTER);
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

}
