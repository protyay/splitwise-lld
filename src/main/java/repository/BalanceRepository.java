package repository;

import model.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class BalanceRepository {
    private final Map<User, Map<User, BigDecimal>> balanceMap;

    public BalanceRepository() {
        balanceMap = new HashMap<>();
        balanceMap.put(User.U1, new HashMap<>());
        balanceMap.put(User.U2, new HashMap<>());
        balanceMap.put(User.U3, new HashMap<>());
        balanceMap.put(User.U4, new HashMap<>());
    }

    public Map<User, BigDecimal> getBalance(User u) {
        if (this.balanceMap.containsKey(u)) throw new RuntimeException("Invalid User");
        return this.balanceMap.get(u);
    }

    public Map<User, Map<User, BigDecimal>> getAllBalances() {
        return this.balanceMap;
    }

    /**
     * The idea of reconciliation is when we  add an EDGE from U1 to U2
     * We need to make sure that there's no existing edge from U2 to U1 with a lesser weight
     * If it exists, then we re-adjust based on the weight of the graph
     *
     * @param dueOwedTo
     * @param dueOwedBy
     * @param amount
     */
    public void addBalance(User dueOwedTo, User dueOwedBy, BigDecimal amount) {
        amount = amount.setScale(2, RoundingMode.HALF_UP);
        final Map<User, BigDecimal> userDueMap = this.balanceMap.getOrDefault(dueOwedBy, new HashMap<>());
        userDueMap.put(dueOwedTo, userDueMap.getOrDefault(dueOwedTo, BigDecimal.ZERO).add(amount));
        this.balanceMap.put(dueOwedBy, userDueMap);
    }

    public BigDecimal getBalanceOwedBy(User by, User to) {
        BigDecimal balance = BigDecimal.ZERO;
        if (!this.balanceMap.containsKey(by) || !this.balanceMap.get(by).containsKey(to)) return balance;
        return this.balanceMap.get(by).get(to);
    }

    public void removeDues(User from, User to) {
        final Map<User, BigDecimal> userDueMap = this.balanceMap.get(from);
        userDueMap.put(to, BigDecimal.ZERO);
    }

    public void addDue(User from, User to, BigDecimal amount) {
        final Map<User, BigDecimal> userDueMap = this.balanceMap.get(from);
        userDueMap.put(to, userDueMap.getOrDefault(to, BigDecimal.ZERO).add(amount));
    }
}
