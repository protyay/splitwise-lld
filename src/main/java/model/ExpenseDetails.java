package model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ExpenseDetails {

    private final User paid;
    private final List<User> sharedBetween;
    private final ExpenseSplitType expenseSplitType;
    private final List<ExpenseDueDetails> expenseDueDetails;
}
