import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

//7-4-2016

public class ErrorDialog {
	private String message, title;

	public ErrorDialog(String message, String title) {
		this.message = message;
		this.title = title;
	}

	public void generateGUI() {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle(title);
		Label myMessage = new Label(message);
		Button button = new Button("Ok");
		// button.setPrefWidth(150);
		button.setOnAction(e -> stage.close());
		VBox vb = new VBox();
		vb.setPadding(new Insets(5, 5, 5, 5));
		vb.setSpacing(25);
		vb.getChildren().addAll(myMessage, button);
		vb.setAlignment(Pos.TOP_CENTER);
		Scene scene = new Scene(vb, 400, 150);
		scene.getStylesheets().add("normal.css");
		stage.setScene(scene);
		stage.showAndWait();
	}

}
