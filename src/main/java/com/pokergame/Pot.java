package com.pokergame;

public class Pot {
    public long amount;

    public Pot() {
        amount = 0;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public void addAmount(long value) {
        this.amount += value;
    }
}
