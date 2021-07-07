package model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ExpenseDueDetails {
    private final User amountOwedBy;
    private final User amountOwedTo;
    private final BigDecimal amount;

    public ExpenseDueDetails(User amountOwedBy, User amountOwedTo, BigDecimal amount) {
        this.amountOwedBy = amountOwedBy;
        this.amountOwedTo = amountOwedTo;
        this.amount = amount;
    }
}
