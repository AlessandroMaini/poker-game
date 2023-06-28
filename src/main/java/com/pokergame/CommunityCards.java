package com.pokergame;

/**
 * The community cards or the cards common to all the players.
 *
 * @author Alessandro Maini
 * @version 2023.06.28
 */
public class CommunityCards {
    /** Number of flop cards */
    final public int FLOP = 3;
    /** Index of the turn card */
    final public int TURN = 3;
    /** Index of the river card */
    final public int RIVER = 4;
    public boolean flopShown;
    public boolean turnShown;
    public boolean riverShown;
    Card[] communityCards;

    /**
     * Initialize the community cards with an empty array.
     */
    public CommunityCards() {
        this.communityCards = new Card[5];
        resetAll();
    }

    /**
     * Reset the game phases.
     */
    public void resetAll() {
        setFlopShown(false);
        setTurnShown(false);
        setRiverShown(false);
    }

    public Card[] getCommunityCards() {
        return communityCards;
    }

    public Card getCommunityCardAt(int index) {
        return communityCards[index];
    }

    public void setFlop(Card[] cards) {
        System.arraycopy(cards, 0, communityCards, 0, FLOP);
    }

    public void setTurn(Card card) {
        communityCards[TURN] = card;
    }

    public void setRiver(Card card) {
        communityCards[RIVER] = card;
    }

    public void setFlopShown(boolean flopShown) {
        this.flopShown = flopShown;
    }

    public void setTurnShown(boolean turnShown) {
        this.turnShown = turnShown;
    }

    public void setRiverShown(boolean riverShown) {
        this.riverShown = riverShown;
    }

    public boolean isFlopShown() {
        return flopShown;
    }

    public boolean isTurnShown() {
        return turnShown;
    }

    public boolean isRiverShown() {
        return riverShown;
    }
}
