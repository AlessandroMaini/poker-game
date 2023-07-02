package com.pokergame;

import java.util.ArrayList;
import java.util.List;

/**
 * The community cards or the cards common to all the players.
 *
 * @author Alessandro Maini
 * @version 2023.06.28
 */
public class CommunityCards {
    /** Index of the turn card */
    final public static int TURN = 3;
    /** Index of the river card */
    final public static int RIVER = 4;
    public boolean flopShown;
    public boolean turnShown;
    public boolean riverShown;
    List<Card> communityCards;

    /**
     * Initialize the community cards with an empty list.
     */
    public CommunityCards() {
        this.communityCards = new ArrayList<>();
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

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public Card getCommunityCardAt(int index) {
        return communityCards.get(index);
    }

    public void setFlop(List<Card> cards) {
        communityCards.addAll(cards);
    }

    public void setTurn(Card card) {
        communityCards.add(card);
    }

    public void setRiver(Card card) {
        communityCards.add(card);
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

    public boolean isNotFlopShown() {
        return !flopShown;
    }

    public boolean isTurnShown() {
        return turnShown;
    }

    public boolean isRiverShown() {
        return riverShown;
    }
}
