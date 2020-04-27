import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class Controller {

    public static final int WELCOME = 1;
    public static int state = WELCOME;

    @FXML
    public TextArea textArea;
    public Button leftOne;
    public Button leftTwo;
    public Button leftThree;
    public Button leftFour;
    public Button rightOne;
    public Button rightTwo;
    public Button rightThree;
    public Button rightFour;
    public Button keypadOne;
    public Button keypadThree;
    public Button keypadTwo;
    public Button keypadFour;
    public Button keypadFive;
    public Button keypadSix;
    public Button keypadSeven;
    public Button keypadEight;
    public Button keypadNine;
    public Button keypadZero;
    public Button cardSlot;
    public Button enter;
    public Button clear;
    public Button cancel;
    public Button receiptSlot;
    public Button cashDispenser;
    public Button depositSlot;


    // receive event
    // need to change display


    private static Controller DEFAULT = null;
    private KeyPadController kpController;
    private DisplayController displayController;

    // default Standalone Button Controllers for basic interaction
    private StandaloneButtonController cardController;
    private StandaloneButtonController receiptController;
    private StandaloneButtonController cashDispenserController;
    private StandaloneButtonController depositController;
    private StandaloneButtonController enterController;
    private StandaloneButtonController clearController;
    private StandaloneButtonController cancelController;


    private ButtonColumnController leftColumnController;
    private ButtonColumnController rightColumnController;

    public Controller() {
        DEFAULT = this;
        kpController = new KeyPadController();
        displayController = new DisplayController();
        cardController = new StandaloneButtonController();
        receiptController = new StandaloneButtonController();
        cashDispenserController = new StandaloneButtonController();
        depositController = new StandaloneButtonController();
        leftColumnController = new ButtonColumnController();
        rightColumnController = new ButtonColumnController();
        enterController = new StandaloneButtonController();
        clearController = new StandaloneButtonController();
        cancelController = new StandaloneButtonController();
    }

    @FXML
    public void initialize() {
        kpController.initialize(keypadOne,
                keypadTwo,
                keypadThree,
                keypadFour,
                keypadFive,
                keypadSix,
                keypadSeven,
                keypadEight,
                keypadNine,
                keypadZero);
        displayController.initialize(textArea);
        cardController.initialize(cardSlot);
        receiptController.initialize(receiptSlot);
        cashDispenserController.initialize(cashDispenser);
        depositController.initialize(depositSlot);
        leftColumnController.initialize(leftOne, leftTwo, leftThree, leftFour);
        rightColumnController.initialize(rightOne, rightTwo, rightThree, rightFour);
        enterController.initialize(enter);
        clearController.initialize(clear);
        cancelController.initialize(cancel);
    }

    public void addKeypadListener(EventHandler<MouseEvent> handler) {
        kpController.addHandler(handler);
    }

    public void addCardSlotListener(EventHandler<MouseEvent> handler) {
        cardController.addHandler(handler);
    }

    public void addCashListener(EventHandler<MouseEvent> handler) {
        cashDispenserController.addHandler(handler);
    }

    public void addDepositListener(EventHandler<MouseEvent> handler) {
        depositController.addHandler(handler);
    }

    public void addLeftColumnListener(EventHandler<MouseEvent> handler) {
        leftColumnController.addHandler(handler);
    }

    public void addEnterListener(EventHandler<MouseEvent> handler) {
        enterController.addHandler(handler);
    }

    public void addClearListener(EventHandler<MouseEvent> handler) {
        clearController.addHandler(handler);
    }

    public void addCancelListener(EventHandler<MouseEvent> handler) {
        cancelController.addHandler(handler);
    }


    public void displayText(String text) {
        displayController.initialize(textArea);
        displayController.setText(text, "right");
    }

    public static Controller getDefault() {
        return DEFAULT;
    }
}
