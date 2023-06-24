package com.pokergame;

public class CommunityCards {
    final public int FLOP = 3;
    final public int TURN = 3;
    final public int RIVER = 4;
    Card[] communityCards;

    public CommunityCards() {
        this.communityCards = new Card[5];
    }

    public Card[] getCommunityCards() {
        return communityCards;
    }

    public Card getCommunityCardAt(int index) {
        return communityCards[index];
    }

    public void setFlop(Card[] cards) {
        for (int i = 0; i < FLOP; i++)
            communityCards[i] = cards[i];
    }

    public void setTurn(Card card) {
        communityCards[TURN] = card;
    }

    public void setRiver(Card card) {
        communityCards[RIVER] = card;
    }
}
