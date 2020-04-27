import javafx.scene.control.TextArea;

public class DisplayController {

    private TextArea textArea;

    public void initialize(TextArea textArea) {
        this.textArea = textArea;
    }

    public void setText(String text, String alignment) {
        textArea.setText(text);
        textArea.setStyle("-fx-text-alignment: " + alignment + ";");
    }
}
