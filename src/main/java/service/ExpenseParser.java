package service;

import model.ExpenseDetails;

public interface ExpenseParser {
    ExpenseDetails parseExpense(String[] expenseInfo);
}
