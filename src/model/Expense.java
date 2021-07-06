package model;

import java.util.List;

public class Expense {

    private User paid;
    private List<User> sharedBetween;
    private ExpenseSplitType expenseSplitType;
}
