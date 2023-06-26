package com.pokergame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class PokerUtilities {
    public static int KINDS_OF_HAND = 10;
    public static int evaluation;
    public static CardInformation[] availableCards;

    static  int availableCardsNumber;

    public PokerUtilities(PlayerHand playerHand, CommunityCards communityCards) {
        this.evaluation = 0;

        availableCardsNumber = 2 + communityCards.phase;
        setAvailableCards(playerHand,communityCards);
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

    }
    //if more return highest
    public static CardInformation[] pair(){
        boolean pair = false;
        int pairvalue = 0;
        int pos = -1, value;
        CardInformation[] cardsInAPair = new CardInformation[2];
        cardsInAPair[0] = new CardInformation(availableCards[pos]);

        for(int i = 0; i < availableCardsNumber; i++){
            value = availableCards[i].value;

            for (int j = i + 1; j < availableCardsNumber; j++) {
                if(value == availableCards[j].value){
                    pair = true;
                    if(value > pairvalue)
                        pairvalue = card.value;
                }
            }
        }

    }
    public static int twoPair(int previousPair){
        boolean pair2 = false;
        int pairvalue = 0;
        for(int i = 0; i < availableCardsNumber; i++){
            Card card = new Card(availableCards[i]);

            for (int j = 0; j < availableCardsNumber; j++) {
                if(card.value == availableCards[i].value){
                    pair = true;
                    if(card.value > pairvalue)
                        pairvalue = card.value;
                }
            }
        }
        return pairvalue;
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
