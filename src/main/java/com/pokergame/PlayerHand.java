package com.pokergame;

/**
 * Associates each player to 2 cards (his hand).
 *
 * @author Alessandro Maini
 * @version 2023.06.28
 */
public class PlayerHand {
    Player player;
    Card[] cards;

    /**
     * Initialize a new player hand.
     *
     * @param player is the player object
     * @param cards are the 2 cards
     */
    public PlayerHand(Player player, Card[] cards) {
        this.player = player;
        this.cards = cards;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Card[] getCards() {
        return cards;
    }

    public void setCards(Card[] cards) {
        this.cards = cards;
    }
}
