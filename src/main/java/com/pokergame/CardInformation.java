package com.pokergame;

public class CardInformation extends Card{
    public static int position;
    public static boolean community;

    public CardInformation(Card card, int position, boolean community) {
        super(card.suit, card.value);
        this.community = community;
        this.position = position;
    }
}
