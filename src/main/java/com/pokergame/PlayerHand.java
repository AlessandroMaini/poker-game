package com.pokergame;

import java.util.List;

/**
 * Associates each player to 2 cards (his hand).
 *
 * @author Alessandro Maini
 * @version 2023.06.28
 */
public class PlayerHand {
    Player player;
    List<Card> cards;

    /**
     * Initialize a new player hand.
     *
     * @param player is the player object
     * @param cards are the 2 cards
     */
    public PlayerHand(Player player, List<Card> cards) {
        this.player = player;
        this.cards = cards;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
}
