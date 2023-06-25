package com.pokergame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A deck of cards.
 *
 * @author Alessandro Maini
 * @version 2023.6.23
 */
public class Deck {
    List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            Card card = new Card(i / 13, i % 13);
            this.cards.add(card);
        }
    }

    /**
     * Shuffles the deck of cards.
     */
    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    /**
     * Draws the first card of the deck.
     *
     * @return the card
     */
    public Card drawCard() {
        return this.cards.remove(0);
    }
}
