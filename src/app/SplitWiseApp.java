package app;

import exception.InvalidInputException;
import model.ExpenseDue;
import model.ExpenseSplitType;
import model.User;
import repository.BalanceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SplitWiseApp {

    public static final String SPACE = " ";
    private static final String EXPENSE = "EXPENSE";
    public static final String SHOW = "SHOW";
    private final BalanceRepository balanceRepository = new BalanceRepository();
    private final NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

    /**
     * 1. Assume there are always 4 users(u1, u2, u3, u4)
     * 2. Split expense between the system
     * 3. Generate Report for a user with transaction status
     * 4. Add expense into the system.
     * Input -  u1 1000 4 u1 u2 u3 u4 EQUAL
     */
    public static void main(String[] args) {
        SplitWiseApp app = new SplitWiseApp();
        while (true) {
            Scanner in = new Scanner(System.in);
            final String expenseInput = in.nextLine();
            final String[] expenseInfo = expenseInput.split(SPACE);


            if (expenseInfo[0] == null)
                throw new InvalidInputException("User Id of user who has paid for txn is mandatory");
            else if (expenseInfo[0].equals(EXPENSE)) {
                app.parseExpense(expenseInfo);
            } else if (expenseInfo[0].equals(SHOW)) {
                app.executeShowRequest(expenseInfo);
            } else if (expenseInfo[0].equals("Y")) System.exit(1);
        }
    }

    private void executeShowRequest(String[] expenseInfo) {
        if (expenseInfo.length == 1) {
            Map<User, Map<User, BigDecimal>> allExpenseDues = this.balanceRepository.getAllBalances();
            for (User u : allExpenseDues.keySet()) {
                this.displayBalance(u, allExpenseDues.get(u));
            }
        } else {
            User user = User.valueOf(expenseInfo[1]);
            Map<User, BigDecimal> userExpenseDue = this.balanceRepository.getBalance(user);
            this.displayBalance(user, userExpenseDue);
        }
    }

    private void displayBalance(User u, Map<User, BigDecimal> expenseDues) {
        for (User toUser : expenseDues.keySet()) {
            System.out.format("%s owes %s: %s", u.getName(), toUser.getName(), numberFormat.format(expenseDues.get(toUser).doubleValue()));
            System.out.println();
        }
    }

    private void parseExpense(String[] expenseInfo) {
        int fromIndex = 1;
        // The user who has paid for this txn
        User expenseAddedBy = User.valueOf(expenseInfo[fromIndex++]);
        // Total totalAmount of the txn
        BigDecimal totalAmount = new BigDecimal(expenseInfo[fromIndex++]);

        int totalUserInTxn = Integer.parseInt(expenseInfo[fromIndex++]);

        List<User> participants = new ArrayList<>();

        for (int i = 1; i <= totalUserInTxn; i++) {
            participants.add(User.valueOf(expenseInfo[fromIndex++]));
        }
        ExpenseSplitType splitType = ExpenseSplitType.valueOf(expenseInfo[fromIndex++]);

        // Model the due as a graph
        if (splitType == ExpenseSplitType.EQUAL) {
            // iterate through the participants and build the adjacency list
            BigDecimal due = totalAmount.divide(BigDecimal.valueOf(totalUserInTxn), RoundingMode.HALF_UP);
            for (User u : participants) {
                if (u == expenseAddedBy) continue;
                balanceRepository.addBalance(expenseAddedBy, u, due);
            }
        } else if (splitType == ExpenseSplitType.EXACT) {
            // We need to parse out the exact amount
            for (int i = 1; i <= totalUserInTxn; i++) {
                BigDecimal amtDue = new BigDecimal(expenseInfo[fromIndex++]);
                balanceRepository.addBalance(expenseAddedBy, participants.get(i - 1), amtDue);
            }

        } else if (splitType == ExpenseSplitType.PERCENT) {
            for (int i = 1; i <= totalUserInTxn; i++) {
                if (participants.get(i - 1) == expenseAddedBy) continue;
                int percentage = Integer.parseInt(expenseInfo[fromIndex++]);
                double multiplicand = percentage / 100.0;
                BigDecimal amtDue = totalAmount.multiply(new BigDecimal(multiplicand));

                balanceRepository.addBalance(expenseAddedBy, participants.get(i - 1), amtDue);
            }
        }
    }
}
