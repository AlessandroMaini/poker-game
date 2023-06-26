package com.pokergame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class PokerUtilities {
    public static int KINDS_OF_HAND = 10;
    public static int evaluation;
    PlayerHand playerHand;
    CommunityCards communityCards;

    public PokerUtilities(PlayerHand playerHand, CommunityCards communityCards) {
        this.playerHand = playerHand;
        this.communityCards = communityCards;
        this.evaluation = 0;
    }
    public  int evaluate(){
        for(int i = 0; i < KINDS_OF_HAND; i++) {

        }

    }

    public static int highCard(){

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
