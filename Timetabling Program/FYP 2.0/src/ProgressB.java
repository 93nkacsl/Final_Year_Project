import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

//7-4-2016

public class ProgressB {

	public ProgressB() {

	}

	/**
	 * Reference:
	 * https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/progress.
	 * htm
	 * 
	 * @return
	 */
	public VBox getProgressBar() {
		VBox vb = new VBox();
		vb.setAlignment(Pos.CENTER);
		vb.setPadding(new Insets(10, 10, 10, 10));
		ProgressIndicator progressIndicator = new ProgressIndicator();
		progressIndicator.setProgress(-1.0f);

		vb.getChildren().addAll(progressIndicator);
		return vb;

	}

}
