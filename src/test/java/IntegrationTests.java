import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;

public class IntegrationTests extends ApplicationTest {

    private StateHandler handler;
    private FxRobot robot;

    /**
     * Will be called before each test method.
     */
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../resources/root.fxml"));
        stage.setTitle("SATM");
        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
        handler = new StateHandler();
        Main.addListeners(handler);
        robot = new FxRobot();
    }

    //UC1
    @Test
    public void presentValidATMCard() {
        assertDisplay("Welcome!\n\nLava Lamp Creditors of Crediting Union\nEST. 1645\n\nPlease insert your ATM card.");
        clickOn("#cardSlot");
        assertDisplay("\n\nPlease enter your PIN\n\n_ _ _ _ ");
    }

    //UC2
    @Test
    public void presentInvalidATMCard() {
        assertDisplay("Welcome!\n\nLava Lamp Creditors of Crediting Union\nEST. 1645\n\nPlease insert your ATM card.");
        press(KeyCode.CONTROL);
        clickOn("#cardSlot");
        release(KeyCode.CONTROL);
        assertDisplay("\n\nInvalid Card.\nWe'll be keeping it.");
    }

    //UC3
    // Could simulate attempts prior to as well
    @Test
    public void entersValidPin() {
        handler.setState(1); // handle pin state
        clickOn("#keypadOne");
        assertDisplay("\n\nPlease enter your PIN\n\n1 _ _ _ ");

        clickOn("#keypadTwo");
        assertDisplay("\n\nPlease enter your PIN\n\n1 2 _ _ ");

        clickOn("#keypadThree");
        assertDisplay("\n\nPlease enter your PIN\n\n1 2 3 _ ");

        clickOn("#keypadFour");
        assertDisplay("Select Transaction.\n\n\n\n< Balance\n\n\n\n< Deposit\n\n\n\n< Withdrawal\n");

    }

    //UC4
    // Note output events are incorrect for screen displayed
    // Note preconditions assumes two incorrect inputs prior to, but why not just simulate it all?
    @Test
    public void entersInvalidPinThrice() {
        handler.setState(1); // handle pin state
        for (int i = 0; i < 3; i++) {
            String prompt = "\n\nPlease enter your PIN\n\n";
            prompt = i > 0 ? "\n\nIncorrect PIN." + prompt : prompt;

            clickOn("#keypadOne");
            assertDisplay(prompt + "1 _ _ _ ");

            clickOn("#keypadOne");
            assertDisplay(prompt + "1 1 _ _ ");

            clickOn("#keypadOne");
            assertDisplay(prompt + "1 1 1 _ ");

            clickOn("#keypadOne");
            if (i != 2)
                assertDisplay("\n\nIncorrect PIN.\n\nPlease enter your PIN\n\n_ _ _ _ ");
        }
        assertDisplay("\n\nInvalid Card.\nWe'll be keeping it.");
    }

    //UC5
    // UC5 suggests they go back to selecting transactions, so we simulate that here too.
    @Test
    public void checkBalanceAndNotExit() {
        handler.setState(4);
        clickOn("#cardSlot");
        assertDisplay("Select Transaction.\n\n\n\n< Balance\n\n\n\n< Deposit\n\n\n\n< Withdrawal\n");
        clickOn("#leftTwo");
        assertDisplay("\n\nWe're printing your balance. Another transaction?\n\n\n\n< Yes\n\n\n\n< No\n\n\n\n");
        // You'd verify the ATM printed the paper here, or attempted to as you'd likely stub it
        clickOn("#leftTwo");
        assertDisplay("Select Transaction.\n\n\n\n< Balance\n\n\n\n< Deposit\n\n\n\n< Withdrawal\n");

    }

    //UC6
    @Test
    public void checkDepositAndExit() {
        handler.setState(4);
        clickOn("#cardSlot");
        assertDisplay("Select Transaction.\n\n\n\n< Balance\n\n\n\n< Deposit\n\n\n\n< Withdrawal\n");
        clickOn("#leftThree");
        assertDisplay("\n\nPlease insert deposit into deposit slot.\n\n");
        clickOn("#depositSlot");
        assertDisplay("\n\nWe're printing your balance. Another transaction?\n\n\n\n< Yes\n\n\n\n< No\n\n\n\n");
        // You'd verify the ATM printed the paper here, or attempted to as you'd likely stub it
        clickOn("#leftThree");
        assertDisplay("\n\nPlease take your receipt and card, and exit the premises.\n\n");
        clickOn("#cardSlot");
        assertDisplay("Welcome!\nLava Lamp Creditors of Crediting Union\nEST. 1645\nPlease insert your ATM card.");
    }

    //UC7
    // account balance updated seems odd in the post-conditions
    @Test
    public void depostSlotJammed() {
        handler.setState(4);
        handler.jamDeposit();
        clickOn("#cardSlot");
        assertDisplay("Select Transaction.\n\n\n\n< Balance\n\n\n\n< Deposit\n\n\n\n< Withdrawal\n");
        clickOn("#leftThree");
        assertDisplay("\n\nNo Deposits right now. Another transaction?\n\n\n\n< Yes\n\n\n\n< No\n\n\n\n");
        clickOn("#leftThree");
        assertDisplay("\n\nPlease take your receipt and card, and exit the premises.\n\n");
        clickOn("#cardSlot");
        assertDisplay("Welcome!\nLava Lamp Creditors of Crediting Union\nEST. 1645\nPlease insert your ATM card.");
    }


    //UC8
    @Test
    public void withdrawNormal() {
        handler.setState(4);
        clickOn("#cardSlot");
        assertDisplay("Select Transaction.\n\n\n\n< Balance\n\n\n\n< Deposit\n\n\n\n< Withdrawal\n");
        clickOn("#leftFour");
        String prompt = "\n\nEnter an amount. \n Balance must be in multiples of 10.\n\n >";
        assertDisplay(prompt + "0");
        clickOn("#keypadOne");
        assertDisplay(prompt + "1");
        clickOn("#keypadZero");
        assertDisplay(prompt + "10");
        clickOn("#keypadZero");
        assertDisplay(prompt + "100");
        clickOn("#enter");
        assertDisplay("\n\nYour balance is being updated.\nCash is being dispensed.\n\n");
        clickOn("#cashDispenser");
        assertDisplay("\n\nWe're printing your balance. Another transaction?\n\n\n\n< Yes\n\n\n\n< No\n\n\n\n");
        // You'd verify the ATM printed the paper here, or attempted to as you'd likely stub it
        clickOn("#leftThree");
        assertDisplay("\n\nPlease take your receipt and card, and exit the premises.\n\n");
        clickOn("#cardSlot");
        assertDisplay("Welcome!\nLava Lamp Creditors of Crediting Union\nEST. 1645\nPlease insert your ATM card.");
    }


    //UC9
    // Incorrect case, adjusted it to the $10 it should be from specs
    // In this test case we can see an adjustment for Screen 9, as in UC9 and specs, Screen 9 has no correct handling.
    // So instead, it accepts a new amount.
    @Test
    public void withdrawNotInTens() {
        handler.setState(4);
        clickOn("#cardSlot");
        assertDisplay("Select Transaction.\n\n\n\n< Balance\n\n\n\n< Deposit\n\n\n\n< Withdrawal\n");
        clickOn("#leftFour");
        String prompt = "\n\nEnter an amount. \n Balance must be in multiples of 10.\n\n >";
        assertDisplay(prompt + "0");
        clickOn("#keypadOne");
        assertDisplay(prompt + "1");
        clickOn("#keypadZero");
        assertDisplay(prompt + "10");
        clickOn("#keypadOne");
        assertDisplay(prompt + "101");
        clickOn("#enter");
        String notTensPrompt = "\n\nWe only distribute in multiples of 10.\n\nPlease enter a new amount \n\n >";
        assertDisplay(notTensPrompt + "0");
        clickOn("#keypadOne");
        assertDisplay(notTensPrompt + "1");
        clickOn("#keypadZero");
        assertDisplay(notTensPrompt + "10");
        clickOn("#enter");
        assertDisplay("\n\nYour balance is being updated.\nCash is being dispensed.\n\n");
        clickOn("#cashDispenser");
        assertDisplay("\n\nWe're printing your balance. Another transaction?\n\n\n\n< Yes\n\n\n\n< No\n\n\n\n");
        // You'd verify the ATM printed the paper here, or attempted to as you'd likely stub it
        clickOn("#leftThree");
        assertDisplay("\n\nPlease take your receipt and card, and exit the premises.\n\n");
        clickOn("#cardSlot");
        assertDisplay("Welcome!\nLava Lamp Creditors of Crediting Union\nEST. 1645\nPlease insert your ATM card.");
    }

    //UC10
    // As in UC9, added handling, as exiting to Screen 1 from here makes no logical sense.
    @Test
    public void withdrawMoreThanBalance() {
        handler.setState(4);
        clickOn("#cardSlot");
        assertDisplay("Select Transaction.\n\n\n\n< Balance\n\n\n\n< Deposit\n\n\n\n< Withdrawal\n");
        clickOn("#leftFour");
        String prompt = "\n\nEnter an amount. \n Balance must be in multiples of 10.\n\n >";
        assertDisplay(prompt + "0");
        clickOn("#keypadFive");
        assertDisplay(prompt + "5");
        clickOn("#keypadZero");
        assertDisplay(prompt + "50");
        clickOn("#keypadZero");
        assertDisplay(prompt + "500");
        clickOn("#enter");
        String notTensPrompt = "\n\nYou're broke. \n Please enter a new amount.\n\n >";
        assertDisplay(notTensPrompt + "0");
        clickOn("#keypadOne");
        assertDisplay(notTensPrompt + "1");
        clickOn("#keypadZero");
        assertDisplay(notTensPrompt + "10");
        clickOn("#enter");
        assertDisplay("\n\nYour balance is being updated.\nCash is being dispensed.\n\n");
        clickOn("#cashDispenser");
        assertDisplay("\n\nWe're printing your balance. Another transaction?\n\n\n\n< Yes\n\n\n\n< No\n\n\n\n");
        // You'd verify the ATM printed the paper here, or attempted to as you'd likely stub it
        clickOn("#leftThree");
        assertDisplay("\n\nPlease take your receipt and card, and exit the premises.\n\n");
        clickOn("#cardSlot");
        assertDisplay("Welcome!\nLava Lamp Creditors of Crediting Union\nEST. 1645\nPlease insert your ATM card.");
    }


    //UC11
    // As in UC9 and UC10, there is a slight change here, as printing their balance makes no sense.
    @Test
    public void withdrawalOverLimit() {
        handler.setState(4);
        clickOn("#cardSlot");
        assertDisplay("Select Transaction.\n\n\n\n< Balance\n\n\n\n< Deposit\n\n\n\n< Withdrawal\n");
        clickOn("#leftFour");
        String prompt = "\n\nEnter an amount. \n Balance must be in multiples of 10.\n\n >";
        assertDisplay(prompt + "0");
        clickOn("#keypadSix");
        assertDisplay(prompt + "6");
        clickOn("#keypadFive");
        assertDisplay(prompt + "65");
        clickOn("#keypadZero");
        assertDisplay(prompt + "650");
        clickOn("#enter");
        assertDisplay("\n\nNo Withdrawals right now. Another transaction?\n\n\n\n< Yes\n\n\n\n< No\n\n\n\n");
        // You'd verify the ATM printed the paper here, or attempted to as you'd likely stub it
        clickOn("#leftThree");
        assertDisplay("\n\nPlease take your receipt and card, and exit the premises.\n\n");
        clickOn("#cardSlot");
        assertDisplay("Welcome!\nLava Lamp Creditors of Crediting Union\nEST. 1645\nPlease insert your ATM card.");
    }


    // Some Custom tests to fill in gaps
    // Test Withdrawal Jam
    @Test
    public void withdrawalJammed() {
        handler.setState(4);
        handler.jamDispenser();
        clickOn("#cardSlot");
        assertDisplay("Select Transaction.\n\n\n\n< Balance\n\n\n\n< Deposit\n\n\n\n< Withdrawal\n");
        clickOn("#leftFour");
        assertDisplay("\n\nNo Withdrawals right now. Another transaction?\n\n\n\n< Yes\n\n\n\n< No\n\n\n\n");
        clickOn("#leftThree");
        assertDisplay("\n\nPlease take your receipt and card, and exit the premises.\n\n");
        clickOn("#cardSlot");
        assertDisplay("Welcome!\nLava Lamp Creditors of Crediting Union\nEST. 1645\nPlease insert your ATM card.");
    }

    // Test Clearing Pin
    @Test
    public void entersValidPinFollowingClear() {
        handler.setState(1); // handle pin state
        clickOn("#keypadSix");
        assertDisplay("\n\nPlease enter your PIN\n\n6 _ _ _ ");

        clickOn("#keypadSeven");
        assertDisplay("\n\nPlease enter your PIN\n\n6 7 _ _ ");

        clickOn("#keypadEight");
        assertDisplay("\n\nPlease enter your PIN\n\n6 7 8 _ ");

        clickOn("#clear");
        assertDisplay("\n\nPlease enter your PIN\n\n_ _ _ _ ");

        clickOn("#keypadOne");
        assertDisplay("\n\nPlease enter your PIN\n\n1 _ _ _ ");

        clickOn("#keypadTwo");
        assertDisplay("\n\nPlease enter your PIN\n\n1 2 _ _ ");

        clickOn("#keypadThree");
        assertDisplay("\n\nPlease enter your PIN\n\n1 2 3 _ ");

        clickOn("#keypadFour");
        assertDisplay("Select Transaction.\n\n\n\n< Balance\n\n\n\n< Deposit\n\n\n\n< Withdrawal\n");
    }

    // Test Canceling
    @Test
    public void cancelFromJammedWithdrawal() {
        handler.setState(4);
        handler.jamDispenser();
        clickOn("#cardSlot");
        assertDisplay("Select Transaction.\n\n\n\n< Balance\n\n\n\n< Deposit\n\n\n\n< Withdrawal\n");
        clickOn("#leftFour");
        assertDisplay("\n\nNo Withdrawals right now. Another transaction?\n\n\n\n< Yes\n\n\n\n< No\n\n\n\n");
        clickOn("#cancel");
        assertDisplay("\n\nPlease take your receipt and card, and exit the premises.\n\n");
        clickOn("#cardSlot");
        assertDisplay("Welcome!\nLava Lamp Creditors of Crediting Union\nEST. 1645\nPlease insert your ATM card.");
    }

    private void assertDisplay(String text) {
        Assertions.assertThat(robot.lookup("#textArea").queryAs(TextInputControl.class)).hasText(text);
    }
}