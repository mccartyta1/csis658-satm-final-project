import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyPadController {

    private Set<Button> keypadButtons;

    private Set<EventHandler<MouseEvent>> inputHandlers;

    public void initialize(Button keypadOne,
                           Button keypadTwo,
                           Button keypadThree,
                           Button keypadFour,
                           Button keypadFive,
                           Button keypadSix,
                           Button keypadSeven,
                           Button keypadEight,
                           Button keypadNine,
                           Button keypadZero) {
        keypadButtons = Stream.of(keypadOne, keypadTwo, keypadThree, keypadFour, keypadFive,
                keypadSix, keypadSeven, keypadEight, keypadNine, keypadZero)
                .collect(Collectors.toSet());

        inputHandlers = new HashSet<>();
        EventHandler<MouseEvent> baseHandler = event -> {
            inputHandlers.forEach(handler -> handler.handle(event));
        };

        keypadButtons.forEach(button -> button.setOnMouseClicked(baseHandler));
    }

    public void addHandler(EventHandler<MouseEvent> handler) {
        inputHandlers.add(handler);
    }

    public static int getMatchingValue(Button button) {
        return Integer.parseInt(button.getText());
    }
}
