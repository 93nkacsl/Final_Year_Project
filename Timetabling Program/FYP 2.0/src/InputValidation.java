import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

//7-4-2016

/**
 * This method will contain a set of methods that will be used to check the
 * inputs of the text fields and other widgets.
 * 
 * @author amirnasiri
 *
 */
public class InputValidation {
	private String styleNoInput, styleIncorrect, dStyle;

	public InputValidation(String styleNoInput, String styleIncorrect, String dStyles) {
		this.styleNoInput = styleNoInput;
		this.styleIncorrect = styleIncorrect;
		this.dStyle = dStyles;
	}

	public String getNoInputStyle() {
		return styleNoInput;
	}

	/**
	 * The input must not equal 0, larger than 20 characters and must not
	 * contain an integer. Used in the lecturer class for the FName and LName
	 * text fields.
	 * 
	 * @param tfInput
	 * @return
	 */
	public boolean checkTFName(TextField tfInput, int length) {
		boolean t = false;
		String input = tfInput.getText();
		if (input.length() == 0) {
			tfInput.setStyle(styleNoInput);
		} else if (input.length() > length) {
			tfInput.setStyle(styleIncorrect);
		} else {
			if (input.matches(".*\\d.*")) {
				tfInput.setStyle(styleIncorrect);
			} else {
				t = true;
			}
		}
		return t;
	}

	public boolean checkModuleInput(TextField tfInput, int length) {
		boolean t = false;
		String input = tfInput.getText();
		if (input.length() == 0) {
			tfInput.setStyle(styleNoInput);
		} else if (input.length() > length) {
			tfInput.setStyle(styleIncorrect);
		} else {
			t = true;
		}
		return t;
	}

	/**
	 * The text field must not contain '-'. Used in the lecturerGUI.class for
	 * the fName and LName.
	 * 
	 * @param tf
	 * @return
	 */
	public boolean containsUnderScore(TextField tf) {
		String input = tf.getText();
		if (input.contains("-")) {
			tf.setStyle(styleIncorrect);
			return false;
		} else
			return true;
	}

	public boolean checkIfCheckBoxIsNotSelected(ChoiceBox cb) {
		if (cb.getSelectionModel().isEmpty()) {
			cb.setStyle(styleNoInput);
			return false;
		} else
			return true;
	}

	/**
	 * When a node is clicked on it will make its style to what it was by
	 * default.
	 * 
	 * @param tf
	 */
	public void setTFStyleOnClick(Node tf) {
		tf.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					if (mouseEvent.getClickCount() == 1) {
						tf.setStyle("");
					}
				}
			}
		});
	}

	/**
	 * This method checks if the input is an integer.
	 * 
	 * @param tf
	 * @return
	 */
	public boolean checkIfInputIsAnInteger(TextField tf) {
		boolean rValue = true;
		String input = tf.getText();
		if (input.length() == 0) {
			tf.setStyle(styleNoInput);
			rValue = false;
		} else {
			try {
				int a = Integer.parseInt(input);
				if (a < 0) {
					rValue = false;
					tf.setStyle(styleIncorrect);
				}
			} catch (NumberFormatException e) {
				tf.setStyle(styleIncorrect);
				rValue = false;
			}

		}
		return rValue;
	}

	/**
	 * This method checks whether the tf is on focus, it changes the style if it
	 * is.
	 * 
	 * @param tf
	 */
	public void checkFocus(Node tf) {
		tf.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					tf.setStyle(dStyle);
				}
			}
		});
	}

	/**
	 * This method checks if the input is an integer above 0. and smaller than 1
	 * if bool is true.
	 * 
	 * @param tf
	 * @return
	 */
	public boolean checkIfInputIsADouble(TextField tf, boolean bol) {
		boolean rValue = true;
		String input = tf.getText();
		if (input.length() == 0) {
			tf.setStyle(styleNoInput);
			rValue = false;
		} else {
			try {
				double a = Double.parseDouble(input);
				if (a < 0) {
					rValue = false;
					tf.setStyle(styleIncorrect);
				}
				if (bol) {
					if (a > 1) {
						rValue = false;
						tf.setStyle(styleIncorrect);
					}
				}
			} catch (NumberFormatException e) {
				tf.setStyle(styleIncorrect);
				rValue = false;
			}

		}

		return rValue;
	}

	/**
	 * This method checks whether an answer is smaller than the 'size'.
	 * 
	 * @param input
	 * @param size
	 * @return
	 */
	public boolean checkInputSize(String input, int size) {
		if (input.length() <= size) {
			return true;
		} else
			return false;
	}

}
