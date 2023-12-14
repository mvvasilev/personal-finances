package dev.mvvasilev.finances.entity;

public class Account {

    private String name;

    private long userId;

    public Account () {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
