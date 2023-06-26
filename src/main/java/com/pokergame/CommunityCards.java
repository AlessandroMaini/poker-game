package com.pokergame;

public class CommunityCards {
    final public int FLOP = 3;
    final public int TURN = 3;
    final public int RIVER = 4;

    int phase;
    Card[] communityCards;

    public CommunityCards() {
        this.communityCards = new Card[5];
        phase = 0;
    }

    public Card[] getCommunityCards() {
        return communityCards;
    }

    public Card getCommunityCardAt(int index) {
        return communityCards[index];
    }

    public void setFlop(Card[] cards) {
        System.arraycopy(cards, 0, communityCards, 0, FLOP);
        phase = FLOP;
    }

    public void setTurn(Card card) {
        communityCards[TURN] = card;
        phase = TURN;
    }

    public void setRiver(Card card) {
        communityCards[RIVER] = card;
        phase = RIVER;
    }
}