package com.pokergame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The set of bets made in the game. With also the value of the bet for each player in an array.
 *
 * @author Alessandro Maini
 * @version 2023.07.02
 */
public class Pot {
    public long amount;
    public List<Long> cumulativeBet;

    /**
     * Initialize a new empty pot and an empty player's bets array.
     *
     * @param numPlayers the number of players
     */
    public Pot(int numPlayers) {
        amount = 0;
        Long[] betArray = new Long[numPlayers];
        Arrays.fill(betArray, 0L);
        cumulativeBet = new ArrayList<>(Arrays.asList(betArray));
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

    public void subAmount(long value) {
        this.amount -= value;
    }

    public long getPlayerAmount(int index) {
        return cumulativeBet.get(index);
    }

    public void addPlayerAmount(int index, long value) {
        cumulativeBet.set(index, cumulativeBet.get(index) + value);
    }

    public void subPlayerAmount(int index, long value) {
        cumulativeBet.set(index, cumulativeBet.get(index) - value);
    }
}
