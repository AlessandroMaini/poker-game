package com.pokergame;

/**
 * A Poker Player with his username and balance.
 *
 * @author Alessandro Maini
 * @version 2023.6.21
 */
public class Player {
    String username;
    long balance;

    /**
     * Initialize a new player.
     *
     * @param username is the player's name
     * default balance is 100.000$
     */
    public Player(String username) {
        this.username = username;
        this.balance = 100000L;
    }

    public Player(Player other) {
        this.username = other.getUsername();
        this.balance = other.getBalance();
    }

    public Player() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
