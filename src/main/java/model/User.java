package model;

public enum User {
    U1("User1"), U2("User2"), U3("User3"), U4("User4");
    private final String name;

    User(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
