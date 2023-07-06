package com.pokergame;

import java.util.List;

/**
 * Associates each player to 2 cards (his hand).
 *
 * @author Alessandro Maini
 * @version 2023.06.28
 */
public class PlayerHand {
    private final List<Card> cards;

    /**
     * Initialize a new player hand.
     *
     * @param cards are the 2 cards
     */
    public PlayerHand(List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getCards() {
        return cards;
    }
}
