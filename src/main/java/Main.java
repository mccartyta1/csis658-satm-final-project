import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../resources/root.fxml"));
        primaryStage.setTitle("SATM");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        addListeners(new StateHandler());

    }

    public static void addListeners(StateHandler stateHandler) {
        Controller controller = Controller.getDefault();

        controller.addKeypadListener((event -> {
            int value = KeyPadController.getMatchingValue((Button) event.getSource());
            controller.displayText(stateHandler.handleKeypadEntry(value));
        }));

        controller.addCardSlotListener(event -> controller.displayText(stateHandler.handleCardSlot(!event.isControlDown())));
        controller.addLeftColumnListener(event -> {
            int value = ButtonColumnController.getIndexOfButton((Button) event.getSource());
            controller.displayText(stateHandler.handleLeftColumn(value));
        });

        controller.addEnterListener(event -> {
            controller.displayText(stateHandler.handleEnterKey());
        });

        controller.addCashListener(event -> controller.displayText(stateHandler.handleCashSlot()));
        controller.addDepositListener(event -> controller.displayText(stateHandler.handleDepositSlot()));
        controller.addClearListener(event -> controller.displayText(stateHandler.handleClear()));
        controller.addCancelListener(event -> controller.displayText(stateHandler.handleCancel()));
    }




    public static void main(String[] args) {
        launch(args);
    }
}
