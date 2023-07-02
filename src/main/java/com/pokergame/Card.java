package com.pokergame;

/**
 * A playing card with suit and value.
 *
 * @author Alessandro Maini
 * @version 2023.06.23
 */
public class Card {
    /** 0 = spades, 1 = hearts, 2 = clubs, 3 = diamonds */
    int suit;
    /** 2 = two, ..., 11 = jack, 12 = queen, 13 = king, 14 = ace */
    int value;
    String[] suitNames = {"spades", "hearts", "clubs", "diamonds"};
    String[] valueNames = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace"};

    /**
     * Initialize a new card object.
     *
     * @param suit is the suit of the card
     * @param value is the value or rank of the card
     */
    public Card(int suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    public int getSuit() {
        return suit;
    }

    public int getValue() {
        return value;
    }

    public String getSuitName() {
        return suitNames[this.suit];
    }

    public String getValueName() {
        return valueNames[this.value - 2];
    }

    @Override
    public String toString() {
        return getValueName() + "_of_" + getSuitName();
    }
}
