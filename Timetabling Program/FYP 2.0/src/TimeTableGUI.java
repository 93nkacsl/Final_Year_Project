import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

//7-4-2016

public class TimeTableGUI {
	private ArrayList<Solution> theSolution;
	private DateAndTime d;
	private Database db;
	private int rowSpan;
	private ArrayList<Room> rooms;
	private int costFunction;
	private ConstraintChecker cc;

	public TimeTableGUI(ArrayList<Solution> theSolution, DateAndTime d, ArrayList<Room> rooms, Database db,
			int costFunction, ConstraintChecker cc) {
		this.costFunction = costFunction;
		this.cc = cc;

		this.theSolution = theSolution;
		fillTheSolutionUp(theSolution);
		this.db = db;
		this.d = d;
		this.rooms = rooms;
		rowSpan = rooms.size();
	}

	void fillTheSolutionUp(ArrayList<Solution> theSolution) {

	}

	public void generateGUI(int cf) {
		Stage stage = new Stage();
		BorderPane bPane = new BorderPane();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Timetable");

		String tableAndStuff = getTheTable();
		String style = getStyle();
		final WebView browser = new WebView();
		final WebEngine webEngine = browser.getEngine();
		String content = "<!DOCTYPE html> \n" + "<html> \n<head style= width:\"100%\" height = \"100%\"> \n" + style
				+ "</head> \n " + "<body bgcolor=\"#1d1d1d\" text = \"#d8d8d8\">  \n <table>" + tableAndStuff
				+ "\n</table> \n</body> \n </html>";
		// System.out.println(content);

		webEngine.loadContent(content);
		bPane.setCenter(browser);
		// Bottom Pane:
		VBox vb = new VBox();
		HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER);
		hb.setPadding(new Insets(5, 5, 5, 5));
		hb.setSpacing(10);
		HBox hbTwo = new HBox();
		hbTwo.setAlignment(Pos.CENTER);
		hbTwo.setPadding(new Insets(5, 5, 5, 5));
		hbTwo.setSpacing(10);
		if (cf == 0) {
			hb.getChildren().addAll(new Label("Cost Function: "), new Label(costFunction + ""));
		} else {
			hb.getChildren().addAll(new Label("Greedy Cost Function: "), new Label(cf + ""),
					new Label("SA Cost function: "), new Label(costFunction + ""));
		}
		String bPaneContent = "Total Number of TA's: " + db.getTeaches().size() + "       " + "Number of TA's: "
				+ getNonZeroSolutionSize(theSolution.size());

		hbTwo.getChildren().add(new Label(bPaneContent));

		HBox hbThree = new HBox();
		hbThree.setAlignment(Pos.CENTER);
		hbThree.setSpacing(10);

		hbThree.getChildren()
				.addAll(new Label(cc.getConstraintValue(1, theSolution)), new Label(
						cc.getConstraintValue(2, theSolution)), new Label(cc.getConstraintValue(3, theSolution)),
				new Label(cc.getConstraintValue(4, theSolution)), new Label(cc.getConstraintValue(5, theSolution)),
				new Label(cc.getConstraintValue(8, theSolution)), new Label(cc.getConstraintValue(9, theSolution)),
				new Label(cc.getConstraintValue(10, theSolution)));

		vb.getChildren().addAll(hb, hbTwo, hbThree);

		bPane.setBottom(vb);
		Scene scene = new Scene(bPane);
		stage.setScene(scene);
		scene.getStylesheets().add("normal.css");

		stage.showAndWait();

	}

	private int getNonZeroSolutionSize(int size) {
		int i = 0;
		for (Solution s : theSolution) {
			if (s.getMId() == 0) {
				i++;
			}
		}
		System.out.println("i:   " + i);
		return theSolution.size() - i;
	}

	private String getStyle() {
		String rValue = "<style> \n" + "table, th, td{ \n" + "border-style:solid; border-width:2px;"
				+ "border-color:#d8d8d8; " + "border-collapse: collapse;\n" + "border-collapse: collapse;\n" + "}\n"
				+ "th,td{ \n" + "padding:5px; \n" + " width: 200px}\n" + "th{ text-align :left}\n" + "</style>";

		return rValue;
	}

	private String getTheTable() {
		// The header:
		String rValue = "";
		rValue = "<th>Days</th>";
		for (int x = 0; x < d.getTimesOfTheDay().length - 1; x++) {
			String tt = d.getTimesOfTheDay()[x] + ":00";
			rValue += "<th>" + tt + "</th> \n";
		}
		rValue = "<tr> \n" + rValue + "</tr> \n";
		// The Header end!
		for (String day : d.getDays()) {
			rValue += "<tr>";
			rValue += "          <td rowspan=\"" + rowSpan + "\">" + day + "</td>\n";

			for (int st : d.getTimesOfTheDay()) {
				for (Solution s : theSolution) {
					if (s.getMId() == 0 && s.getLId() == 0 && s.getDay().equals(day) && s.getSt() == st
							&& s.getRId() == rooms.get(0).getId()) {
						rValue += "          <td colspan=\"" + s.getDuration() + "\">" + getRoomBookedDescription(s)
								+ "</td>\n";
					} else if (s.getSt() == st && s.getDay().equals(day) && s.getRId() == rooms.get(0).getId()) {
						rValue += "          <td colspan=\"" + s.getDuration() + "\">" + getSolutionFormatted(s)
								+ "</td>\n";
					}
				}
			}
			rValue += "</tr>";
			for (int i = 1; i < rooms.size(); i++) {
				rValue += "<tr>";
				for (int st : d.getTimesOfTheDay()) {
					for (Solution s : theSolution) {
						if (s.getMId() == 0 && s.getLId() == 0 && s.getSt() == st && s.getDay().equals(day)
								&& rooms.get(i).getId() == s.getRId()) {
							rValue += "          <td colspan=\"" + s.getDuration() + "\">" + getRoomBookedDescription(s)
									+ "</td>\n";
							// System.out.println(s);
						} else if (s.getSt() == st && s.getDay().equals(day) && rooms.get(i).getId() == s.getRId()) {
							rValue += "<td colspan= \"" + s.getDuration() + "\">" + getSolutionFormatted(s) + "</td>";
						}

					}
				}
				rValue += "</tr>";
			}
		}
		return rValue;
	}

	private String getRoomBookedDescription(Solution s) {
		ArrayList<Date> rBooked = db.getRoomBooked(s.getRId());
		String rB = "";
		for (Date date : rBooked) {
			if (s.getDay().equals(date.getDay()) && s.getSt() == date.getStartTime()) {
				rB = date.getDescription();
			}
		}
		if (rB.length() == 0) {
			rB = "N/A";
		}
		return rB;
	}

	/**
	 * This method returns a box of a solution within the timetable.
	 * 
	 * @param s
	 * @return
	 */
	private String getSolutionFormatted(Solution s) {

		String content = "";
		content += "MId: (" + s.getMId() + "), LId: (" + s.getLId() + ") </br>";
		content += "rId: " + s.getRId() + "</br>";
		content += "Level: " + s.getLevel();
		return content;
	}

}
