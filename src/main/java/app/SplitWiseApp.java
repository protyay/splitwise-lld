package app;

import exception.InvalidInputException;
import model.ExpenseDetails;
import model.User;
import repository.BalanceRepository;
import service.BalanceService;
import service.BalanceServiceImpl;
import service.CLIExpenseParser;
import service.ExpenseParser;

import java.text.NumberFormat;
import java.util.Scanner;

public class SplitWiseApp {

    public static final String SPACE = " ";
    private static final String EXPENSE = "EXPENSE";
    public static final String SHOW = "SHOW";
    private final BalanceRepository balanceRepository = new BalanceRepository();
    private final ExpenseParser expenseParser = new CLIExpenseParser();
    private final BalanceService balanceService = new BalanceServiceImpl(balanceRepository, System.out, NumberFormat.getCurrencyInstance());

    /**
     * 1. Assume there are always 4 users(u1, u2, u3, u4)
     * 2. Split expense between the system
     * 3. Generate Report for a user with transaction status
     * 4. Add expense into the system.
     * Input -  u1 1000 4 u1 u2 u3 u4 EQUAL
     */
    public static void main(String[] args) {
        SplitWiseApp app = new SplitWiseApp();
        app.parseInput();
    }

    private void parseInput() {
        while (true) {
            Scanner in = new Scanner(System.in);
            final String expenseInput = in.nextLine();
            final String[] expenseInfo = expenseInput.split(SPACE);


            if (expenseInfo[0] == null)
                throw new InvalidInputException("User Id of user who has paid for txn is mandatory");
            else if (expenseInfo[0].equals(EXPENSE)) {
                final ExpenseDetails expenseDetails = expenseParser.parseExpense(expenseInfo);
                this.balanceService.addExpense(expenseDetails);
            } else if (expenseInfo[0].equals(SHOW)) {
                this.executeShowRequest(expenseInfo);
            } else if (expenseInfo[0].equals("Y")) System.exit(1);
        }
    }

    private void executeShowRequest(String[] expenseInfo) {
        if (expenseInfo.length == 1) {
            this.balanceService.displayAllBalances();
        } else if (expenseInfo.length == 2) {
            this.balanceService.displayUserBalance(User.valueOf(expenseInfo[1]));
        }
    }
}
