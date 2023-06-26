package com.pokergame;

/**
 * A playing card with suit and value.
 *
 * @author Alessandro Maini
 * @version 2023.6.23
 */
public class Card {
    /** 0 = spades, 1 = hearts, 2 = clubs, 3 = diamonds */
    int suit;
    /** 0 = two, ..., 9 = jack, 10 = queen, 11 = king, 12 = ace */
    int value;
    String[] suitNames = {"spades", "hearts", "clubs", "diamonds"};
    String[] valueNames = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace"};

    public Card(int suit, int value) {
        this.suit = suit;
        this.value = value;
    }
    public Card(Card card){
        super();
    }

    public int getSuit() {
        return suit;
    }

    public void setSuit(int suit) {
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getSuitName() {
        return suitNames[this.suit];
    }

    public String getValueName() {
        return valueNames[this.value];
    }

    @Override
    public String toString() {
        return getValueName() + "_of_" + getSuitName();
    }
}
