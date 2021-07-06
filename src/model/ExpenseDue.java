package model;

import java.math.BigDecimal;

public class ExpenseDue {
    private final User to;
    private final BigDecimal amount;

    public ExpenseDue(User to, BigDecimal amount) {
        this.to = to;
        this.amount = amount;
    }

    public User getTo() {
        return to;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
