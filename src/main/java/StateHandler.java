public class StateHandler {
    private int state = 0;
    private static final int START_STATE = 0;
    private static final int WAIT_FOR_PIN_STATE = 1;
    private static final int INCORRECT_PIN_STATE = 2;
    private static final int INVALID_CARD_STATE = 3;
    private static final int SELECT_TRANSACTION_STATE = 4;
    private static final int DISPLAY_BALANCE_STATE = 5;
    private static final int ENTER_AMOUNT_STATE = 6;
    private static final int NOT_ENOUGH_MONEY_STATE = 7;
    private static final int ONLY_TENS_STATE = 8;
    private static final int NO_WITHDRAWALS_STATE = 9;
    private static final int GIVING_CASH_STATE = 10;
    private static final int NO_DEPOSITS_STATE = 11;
    private static final int INSERT_DEPOSIT_STATE = 12;
    private static final int ANOTHER_TRANSACTION_STATE = 13;
    private static final int THANK_YOU_STATE = 14;

    private static final String START_STRING = "Welcome!\nLava Lamp Creditors of Crediting Union\nEST. 1645\nPlease insert your ATM card.";
    private static final String ENTER_PIN_STRING = "\n\nPlease enter your PIN\n\n";
    private static final String INCORRECT_PIN_STRING = "\n\nIncorrect PIN.\n\nPlease enter your PIN\n\n";
    private static final String INVALID_CARD_STRING = "\n\nInvalid Card.\nWe'll be keeping it.";
    private static final String SELECT_TRANSACTION_STRING = "Select Transaction.\n\n\n\n< Balance\n\n\n\n< Deposit\n\n\n\n< Withdrawal\n";
    private static final String BALANCE_STRING = "\n\nBalance is: \n\n";
    private static final String ENTER_AMOUNT_STRING = "\n\nEnter an amount. \n Balance must be in multiples of 10.\n\n >";
    private static final String NOT_ENOUGH_MONEY_STRING = "\n\nYou're broke. \n Please enter a new amount.\n\n >";
    private static final String ONLY_TENS_STRING = "\n\nWe only distribute in multiples of 10.\n\nPlease enter a new amount \n\n >";
    private static final String NO_WITHDRAWALS_STRING = "\n\nNo Withdrawals right now. Another transaction?";
    private static final String NO_DEPOSITS_STRING = "\n\nNo Deposits right now. Another transaction?";
    private static final String CASH_DISPENSE_STRING = "\n\nYour balance is being updated.\nCash is being dispensed.\n\n";
    private static final String INSERT_DEPOSIT_STRING = "\n\nPlease insert deposit into deposit slot.\n\n";
    private static final String ANOTHER_TRANSACTION_STRING = "\n\nWe're printing your balance. Another transaction?";
    private static final String THANK_YOU_STRING = "\n\nPlease take your receipt and card, and exit the premises.\n\n";

    private static final String YES_NO_APPENDAGE = "\n\n\n\n< Yes\n\n\n\n< No\n\n\n\n";

    private static final int BALANCE_INDEX = 2;
    private static final int DEPOSIT_INDEX = 3;
    private static final int WITHDRAWAL_INDEX = 4;
    private static final int YES_INDEX = 2;
    private static final int NO_INDEX = 3;

    private static final int WITHDRAWAL_LIMIT = 600;

    // dummy info
    private static final int STARTING_ACCOUNT_BALANCE = 400;
    private static final int[] DUMMY_PIN = new int[] { 1, 2, 3, 4 };

    // tracked info
    private int[] pin = new int[] { -1, -1, -1, -1 };
    private int pinIndex = 0;
    private int enteredAmount = 0;
    private int pinTries = 0;
    private int accountBalance = STARTING_ACCOUNT_BALANCE;
    private boolean jammedDeposit = false;
    private boolean jammedDispenser = false;

    public String handleCardSlot(boolean validSwipe) {
        switch (state) {
            case START_STATE:
                if (validSwipe)
                    state = WAIT_FOR_PIN_STATE;
                else
                    state = INVALID_CARD_STATE;
                break;
            case THANK_YOU_STATE:
                state = START_STATE;
                break;
        }
        return getTextForState();
    }

    public String handleCashSlot() {
        switch (state) {
            case GIVING_CASH_STATE:
                state = ANOTHER_TRANSACTION_STATE;
        }
        return getTextForState();
    }

    public String handleDepositSlot() {
        switch(state) {
            case INSERT_DEPOSIT_STATE:
                accountBalance += 100;
                state = ANOTHER_TRANSACTION_STATE;
        }

        return getTextForState();
    }

    public String handleClear() {
        switch (state) {
            case WAIT_FOR_PIN_STATE:
            case INCORRECT_PIN_STATE:
                pin = new int[] { -1, -1, -1, -1 };
                pinIndex = 0;
                break;
            case ENTER_AMOUNT_STATE:
            case NOT_ENOUGH_MONEY_STATE:
            case ONLY_TENS_STATE:
                enteredAmount = 0;

        }

        return getTextForState();
    }

    public String handleKeypadEntry(int key) {
        switch (state) {
            case WAIT_FOR_PIN_STATE:
            case INCORRECT_PIN_STATE:
                handlePinEntry(key);
                validatePin();
                break;
            case ENTER_AMOUNT_STATE:
            case NOT_ENOUGH_MONEY_STATE:
            case ONLY_TENS_STATE:
                handleAmountEntry(key);
        }

        return getTextForState();
    }

    private void validatePin() {
        if (pinIndex != 0) // not fully entered yet
            return;

        if (pin[0] == DUMMY_PIN[0] && pin[1] == DUMMY_PIN[1] && pin[2] == DUMMY_PIN[2] && pin[3] == DUMMY_PIN[3]) {
            pinTries = 0;
            state = SELECT_TRANSACTION_STATE; // current auto accept
        } else {
            pinTries++;
            if (pinTries >= 3) {
                pinTries = 0;
                state = INVALID_CARD_STATE;
            } else
                state = INCORRECT_PIN_STATE;
        }

        pin = new int[] { -1, -1, -1, -1 };
    }

    private void handlePinEntry(int pinNumber) {
        // This could also be done by checking the -1 entries instead of a separate index tracking
        // The pin could also be just an int or String as well
        pin[pinIndex] = pinNumber;
        pinIndex++;
        if (pinIndex == 4)
            pinIndex = 0;
    }

    private void handleAmountEntry(int newNumber) {
        // This, amusingly, causes integer overflow pretty fast so this handles that.
        if (enteredAmount < Integer.MAX_VALUE / 10)
            enteredAmount = (enteredAmount * 10) + newNumber;
    }

    public String handleLeftColumn(int index) {
        // handle transaction selection
        if (state == SELECT_TRANSACTION_STATE) {
            switch (index) {
                case BALANCE_INDEX:
                    state = ANOTHER_TRANSACTION_STATE;
                    break;
                case DEPOSIT_INDEX:
                    if (jammedDeposit)
                        state = NO_DEPOSITS_STATE;
                    else
                        state = INSERT_DEPOSIT_STATE;
                    break;
                case WITHDRAWAL_INDEX:
                    if (jammedDispenser)
                        state = NO_WITHDRAWALS_STATE;
                    else
                        state = ENTER_AMOUNT_STATE;
            }
        } else if (state == ANOTHER_TRANSACTION_STATE || state == NO_WITHDRAWALS_STATE || state == NO_DEPOSITS_STATE) {
            switch (index) {
                case YES_INDEX:
                    state = SELECT_TRANSACTION_STATE;
                    break;
                case NO_INDEX:
                    state = THANK_YOU_STATE;
            }
        }
        return getTextForState();
    }

    public String handleEnterKey() {
        switch (state) {
            case ENTER_AMOUNT_STATE:
            case NOT_ENOUGH_MONEY_STATE:
            case ONLY_TENS_STATE:
                attemptWithdrawal();
        }
        return getTextForState();
    }

    private void attemptWithdrawal() {
        if (enteredAmount > WITHDRAWAL_LIMIT)
            state = NO_WITHDRAWALS_STATE;
        else if (enteredAmount % 10 != 0)
            state = ONLY_TENS_STATE;
        else if (enteredAmount > accountBalance)
            state = NOT_ENOUGH_MONEY_STATE;
        else {
            state = GIVING_CASH_STATE;
            accountBalance -= enteredAmount;
        }
        enteredAmount = 0;
    }

    public String getTextForState() {
        switch (state) {
            case START_STATE:
                return START_STRING;
            case WAIT_FOR_PIN_STATE:
                return ENTER_PIN_STRING + getPinAsString();
            case INCORRECT_PIN_STATE:
                return INCORRECT_PIN_STRING + getPinAsString();
            case INVALID_CARD_STATE:
                return INVALID_CARD_STRING;
            case SELECT_TRANSACTION_STATE:
                return SELECT_TRANSACTION_STRING;
            case DISPLAY_BALANCE_STATE:
                return BALANCE_STRING;
            case ENTER_AMOUNT_STATE:
                return ENTER_AMOUNT_STRING + enteredAmount;
            case NOT_ENOUGH_MONEY_STATE:
                return NOT_ENOUGH_MONEY_STRING + enteredAmount;
            case ONLY_TENS_STATE:
                return ONLY_TENS_STRING + enteredAmount;
            case NO_WITHDRAWALS_STATE:
                return NO_WITHDRAWALS_STRING + YES_NO_APPENDAGE;
            case GIVING_CASH_STATE:
                return CASH_DISPENSE_STRING;
            case NO_DEPOSITS_STATE:
                return NO_DEPOSITS_STRING + YES_NO_APPENDAGE;
            case INSERT_DEPOSIT_STATE:
                return INSERT_DEPOSIT_STRING;
            case ANOTHER_TRANSACTION_STATE:
                return ANOTHER_TRANSACTION_STRING + YES_NO_APPENDAGE;
            case THANK_YOU_STATE:
                return THANK_YOU_STRING;
            default:
                return START_STRING;
        }
    }

    public void jamDeposit() {
        jammedDeposit = true;
    }

    public void jamDispenser() {
        jammedDispenser = true;
    }

    public void setState(int state) {
        this.state = state;
    }

    private String getPinAsString() {
        StringBuilder builder = new StringBuilder();
        for (int x : pin) {
            builder.append(x >= 0 ? x : "_");
            builder.append(" ");
        }
        return builder.toString();
    }

    public String handleCancel() {
        // Return card and cancel from current transactions.
        // In reality, this would be far more complex.
        if (state != START_STATE && state != INVALID_CARD_STATE)
            state = THANK_YOU_STATE;
        return getTextForState();
    }
}
