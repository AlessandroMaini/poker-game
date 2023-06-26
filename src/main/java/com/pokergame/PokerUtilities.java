package com.pokergame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class PokerUtilities {
    public static int KINDS_OF_HAND = 10;
    public static int evaluation;
    CardInformation[] availableCards;

    static int availableCardsNumber;

    public PokerUtilities(PlayerHand playerHand, CommunityCards communityCards) {
        this.evaluation = 0;

        availableCardsNumber = 2 + communityCards.phase;

    }

    public void setAvailableCards(PlayerHand playerHand, CommunityCards communityCards) {
        availableCards = new CardInformation[availableCardsNumber];
        for(int i = 0; i < 2; i++){
            availableCards[i] = new CardInformation(playerHand.getCardAt(i), i, false);
        }

        for(int i = 2; i < availableCardsNumber; i++){
            availableCards[i] = new CardInformation(playerHand.getCardAt(i), i, true);
        }
    }

    public  int evaluate(){

    }

    public static int highCard(){
        a
    }
    public static int pair(){

    }
    public static int twoPair(){

    }
    public static int threeOfAKind(){

    }
    public static int straight(){

    }
    public static int flush(){

    }
    public static int fullHouse(){

    }
    public static int fourOfAKind(){

    }
    public static int straightFlush(){

    }
    public static int royalFlush(){

    }
    public int maxPH(){
        Card[] cards = playerHand.getCards();

    }
}
