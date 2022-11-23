package main.springbook.user.domain;

public enum Level {
    BASIC(1,VIP), VIP(2,SUPER), SUPER(3,null);
    private final Level next;
    private final int value;

    Level(int value,Level next) {
        this.value = value;
        this.next = next;
    }
    public int intValue() {
        return value;
    }
    public Level nextLevel() {
        return this.next;
    }
}
