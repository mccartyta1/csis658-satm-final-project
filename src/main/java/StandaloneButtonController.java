import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.util.HashSet;
import java.util.Set;

public class StandaloneButtonController {
    private Button button;
    private Set<EventHandler<MouseEvent>> inputHandlers;

    public void initialize(Button button) {
        this.button = button;
        inputHandlers = new HashSet<>();
        EventHandler<MouseEvent> baseHandler = event -> {
            inputHandlers.forEach(handler -> handler.handle(event));
        };
        this.button.setOnMouseClicked(baseHandler);
    }

    public void addHandler(EventHandler<MouseEvent> handler) {
        inputHandlers.add(handler);
    }
}
