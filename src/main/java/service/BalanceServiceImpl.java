package service;

import model.ExpenseDetails;
import model.ExpenseDueDetails;
import model.User;
import repository.BalanceRepository;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final OutputStream displayStream;
    private final NumberFormat numberFormat;

    public BalanceServiceImpl(BalanceRepository balanceRepository, OutputStream displayStream, NumberFormat numberFormat) {
        this.balanceRepository = balanceRepository;
        this.displayStream = displayStream;
        this.numberFormat = numberFormat;
    }

    @Override
    public void displayAllBalances() {
        final Map<User, Map<User, BigDecimal>> allBalances = this.balanceRepository.getAllBalances();
        for (User u : allBalances.keySet()) {
            displayBalanceForUser(allBalances.get(u), u);
        }
    }

    private void displayBalanceForUser(Map<User, BigDecimal> allBalances, User u) {
        for (User owesTo : allBalances.keySet()) {
            System.out.format("%s owes %s: %s", u.getName(), owesTo, numberFormat.format(allBalances.get(owesTo).doubleValue()));
            System.out.println();
        }
    }
    @Override
    public void displayUserBalance(User u) {
        this.displayBalanceForUser(this.balanceRepository.getBalance(u), u);
    }

    /**
     * Check the existing dues for the users in this transaction. If the user who paid for the current
     * txn has a due greater than what he owes, we can re-adjust the transaction amount instead
     * of adding a new transaction.
     *
     * @param expenseDetails
     */
    @Override
    public void addExpense(ExpenseDetails expenseDetails) {
        final Map<User, Map<User, BigDecimal>> allBalances = this.balanceRepository.getAllBalances();
        List<ExpenseDueDetails> dueDetailsList = expenseDetails.getExpenseDueDetails();
        for (ExpenseDueDetails dues : dueDetailsList) {

            BigDecimal existingDueFromBToA = this.balanceRepository.getBalanceOwedBy(dues.getAmountOwedTo(), dues.getAmountOwedBy());
            BigDecimal dueFromAToB = dues.getAmount();
            if (existingDueFromBToA.compareTo(dueFromAToB) >= 0) {
                this.balanceRepository.updateDues(dues.getAmountOwedTo(), dues.getAmountOwedBy(), existingDueFromBToA.subtract(dueFromAToB));
            } else {
                this.balanceRepository.updateDues(dues.getAmountOwedBy(), dues.getAmountOwedTo(), dueFromAToB.subtract(existingDueFromBToA));
            }
        }
    }
}
