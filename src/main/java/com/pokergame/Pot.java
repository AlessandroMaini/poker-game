package com.pokergame;

import java.util.Arrays;

/**
 * The set of bets made in the game. With also the value of the bet for each player in an array.
 */
public class Pot {
    public long amount;
    public long[] cumulativeBet;

    public Pot(int numPlayers) {
        amount = 0;
        cumulativeBet = new long[numPlayers];
        Arrays.fill(cumulativeBet, 0);
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

    public long getPlayerAmount(int index) {
        return cumulativeBet[index];
    }

    public void addPlayerAmount(int index, long value) {
        cumulativeBet[index] += value;
    }
}
