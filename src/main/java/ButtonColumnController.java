import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ButtonColumnController {
    private List<Button> buttons;

    private Set<EventHandler<MouseEvent>> inputHandlers;

    public void initialize(Button first, Button second, Button third, Button fourth) {
        buttons = Stream.of(first, second, third, fourth)
                .collect(Collectors.toList());

        inputHandlers = new HashSet<>();
        EventHandler<MouseEvent> baseHandler = event -> {
            inputHandlers.forEach(handler -> handler.handle(event));
        };

        buttons.forEach(button -> button.setOnMouseClicked(baseHandler));
    }

    public static int getIndexOfButton(Button button) {
        // This is a terrible implementation
        // But it's also a fun one. In actual practice, I'd either store the info in the button, or add an accessor through Controller.
        String id = button.getId();
        String signalLetters = id.substring(id.length() - 2);
        switch (signalLetters) {
            case "ne":
                return 1;
            case "wo":
                return 2;
            case "ee":
                return 3;
            case "ur":
                return 4;
            default:
                return 0;
        }
    }

    public void addHandler(EventHandler<MouseEvent> handler) {
        inputHandlers.add(handler);
    }
}
