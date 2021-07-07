package service;

import model.ExpenseDetails;
import model.User;

public interface BalanceService {
    void displayAllBalances();

    void displayUserBalance(User u);

    void addExpense(ExpenseDetails expenseDetails);
}
