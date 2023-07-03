package com.pokergame;

import java.util.List;
import java.util.random.RandomGenerator;

public class BotPlayerLogic {
    public static RandomGenerator randomGenerator = RandomGenerator.getDefault();

    public static String preFlopBetButton(Card c1, Card c2, List<PlayerBet> bets, long maxBet) {
        if (c1.getValue() == 14 || c1.getValue() == c2.getValue() ||
                (c1.getSuit() == c2.getSuit() && (c1.getValue() == 13 || c1.getValue() > 9 && c2.getValue() > 7 || (c1.getValue() - c2.getValue() < 3 && c1.getValue() > 6) || c1.getValue() - c2.getValue() == 1)) ||
                (c1.getSuit() != c2.getSuit() && c1.getValue() > 10 && c2.getValue() > 8))
            return maxBet > GameLogic.BLIND * 3 ? "CALL" : "RAISE";
        else return fold(countFold(bets)) ? "FOLD" : "CALL";
    }

    public static String preFlopBetLatePos(Card c1, Card c2, List<PlayerBet> bets, long maxBet) {
        if (c1.getValue() == c2.getValue() ||
                (c1.getSuit() == c2.getSuit() && (c1.getValue() == 14 || c1.getValue() == 13 && c2.getValue() > 8 || c1.getValue() > 10 && c2.getValue() > 9)) ||
                (c1.getSuit() != c2.getSuit() && (c1.getValue() == 14 && c2.getValue() > 8 || c1.getValue() == 13 && c2.getValue() > 9 || c1.getValue() == 12 && c2.getValue() == 11)))
            return maxBet > GameLogic.BLIND * 3 ? "CALL" : "RAISE";
        else return fold(countFold(bets)) ? "FOLD" : "CALL";
    }

    public static String preFlopBetEarlyPos(Card c1, Card c2, List<PlayerBet> bets, boolean bigBlind, long maxBet) {
        if (c1.getValue() == c2.getValue() && c1.getValue() > 6 ||
                (c1.getSuit() == c2.getSuit() && (c1.getValue() == 14 && c2.getValue() > 9 || c1.getValue() == 13 && c2.getValue() == 12)) ||
                (c1.getSuit() != c2.getSuit() && c1.getValue() == 14 && c2.getValue() > 10))
            return maxBet > GameLogic.BLIND * 3 ? "CALL" : "RAISE";
        else if (bigBlind)
            return "CHECK";
        else return fold(countFold(bets)) ? "FOLD" : "CALL";
    }

    public static String preFlopBet(PlayerHand hand, List<PlayerBet> bets, int index, int dealer, long maxBet) {
        Card card1 = hand.getCards().get(0);
        Card card2 = hand.getCards().get(1);
        if (card1.getValue() < card2.getValue()) {
            card1 = hand.getCards().get(1);
            card2 = hand.getCards().get(0);
        }
        if (index == dealer)
            return preFlopBetButton(card1, card2, bets, maxBet);
        else if (index == (dealer + 3) % GameLogic.NUM_PLAYERS)
            return preFlopBetLatePos(card1, card2, bets, maxBet);
        else
            return preFlopBetEarlyPos(card1, card2, bets, index == (dealer + 2) % GameLogic.NUM_PLAYERS && maxBet == GameLogic.BLIND, maxBet);
    }

    public static String flopBet() {
        return null;
    }

    public static String turnBet() {
        return null;
    }

    public static String riverBet() {
        return null;
    }

    public static boolean fold(int numFold) {
        return randomGenerator.nextInt() % (Math.pow(2, numFold) * 7) == 0;
    }

    public static int countFold(List<PlayerBet> bets) {
        return ((int) bets.stream().filter(PlayerBet::isFolded).count());
    }
}
