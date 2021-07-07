package service;

import model.ExpenseDetails;
import model.ExpenseDueDetails;
import model.ExpenseSplitType;
import model.User;
import repository.BalanceRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * This expense parser should be able to parse the most basic
 * expense input - equal parsing.
 * For other parsing methods, it would lead to enrich the expense details
 * with other relevant methods
 */
public class CLIExpenseParser implements ExpenseParser {

    @Override
    public ExpenseDetails parseExpense(String[] expenseInfo) {
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
        final ExpenseDetails.ExpenseDetailsBuilder expense =
                ExpenseDetails.builder().expenseSplitType(splitType).paid(expenseAddedBy).sharedBetween(participants);

        List<ExpenseDueDetails> expenseDueDetails = new ArrayList<>();
        if (splitType == ExpenseSplitType.EXACT) {
            for (int i = 1; i <= totalUserInTxn; i++) {
                BigDecimal amtDue = new BigDecimal(expenseInfo[fromIndex++]);
                expenseDueDetails.add(new ExpenseDueDetails(participants.get(i - 1), expenseAddedBy, amtDue));
            }
        } else if (splitType == ExpenseSplitType.PERCENT) {
            for (int i = 1; i <= totalUserInTxn; i++) {
                if (participants.get(i - 1) == expenseAddedBy) continue;
                int percentage = Integer.parseInt(expenseInfo[fromIndex++]);
                double multiplicand = percentage / 100.0;
                BigDecimal amtDue = totalAmount.multiply(new BigDecimal(multiplicand));

                expenseDueDetails.add(new ExpenseDueDetails(participants.get(i - 1), expenseAddedBy, amtDue));
            }
        } else if (splitType == ExpenseSplitType.EQUAL) {
            BigDecimal amtDue = totalAmount.divide(BigDecimal.valueOf(totalUserInTxn), RoundingMode.HALF_UP);
            for (User u : participants) {
                if (u == expenseAddedBy) continue;
                expenseDueDetails.add(new ExpenseDueDetails(u, expenseAddedBy, amtDue));
            }
        }
        expense.expenseDueDetails(expenseDueDetails);
        return expense.build();
    }
}
