package com.pokergame;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class BotPlayerLogic {
    public static final int BASE_SCORE = 420000;
    public static RandomGenerator randomGenerator = RandomGenerator.getDefault();

    public static String preFlopBetButton(long balance, Card c1, Card c2, int numFold, long maxBet, long minRaise) {
        if (c1.getValue() == 14 || c1.getValue() == c2.getValue() || (c1.getSuit() == c2.getSuit() && (c1.getValue() == 13 || c1.getValue() > 9 && c2.getValue() > 7 || (c1.getValue() - c2.getValue() < 3 && c1.getValue() > 6) || c1.getValue() - c2.getValue() == 1)) || (c1.getSuit() != c2.getSuit() && c1.getValue() > 10 && c2.getValue() > 8)) {
            if (balance < maxBet + minRaise)
                return "CALL";
            else
                return raisePreFlop(minRaise) ? "RAISE" : "CALL";
        } else
            return fold(numFold, maxBet, balance) ? "FOLD" : "CALL";
    }

    public static String preFlopBetLatePos(long balance, Card c1, Card c2, int numFold, long maxBet, long minRaise) {
        if (c1.getValue() == c2.getValue() || (c1.getSuit() == c2.getSuit() && (c1.getValue() == 14 || c1.getValue() == 13 && c2.getValue() > 8 || c1.getValue() > 10 && c2.getValue() > 9)) || (c1.getSuit() != c2.getSuit() && (c1.getValue() == 14 && c2.getValue() > 8 || c1.getValue() == 13 && c2.getValue() > 9 || c1.getValue() == 12 && c2.getValue() == 11))) {
            if (balance < maxBet + minRaise)
                return "CALL";
            else
                return raisePreFlop(minRaise) ? "RAISE" : "CALL";
        } else
            return fold(numFold, maxBet, balance) ? "FOLD" : "CALL";
    }

    public static String preFlopBetEarlyPos(long balance, Card c1, Card c2, int numFold, boolean bigBlind, long maxBet, long minRaise) {
        if (c1.getValue() == c2.getValue() && c1.getValue() > 6 || (c1.getSuit() == c2.getSuit() && (c1.getValue() == 14 && c2.getValue() > 9 || c1.getValue() == 13 && c2.getValue() == 12)) || (c1.getSuit() != c2.getSuit() && c1.getValue() == 14 && c2.getValue() > 10)) {
            if (balance < maxBet + minRaise)
                return "CALL";
            else
                return raisePreFlop(minRaise) ? "RAISE" : "CALL";
        } else if (bigBlind)
            return "CHECK";
        else
            return fold(numFold, maxBet, balance) ? "FOLD" : "CALL";
    }

    public static String preFlopBet(PlayerHand hand, List<PlayerBet> bets, int index, int dealer, long maxBet, long minRaise) {
        Card card1 = hand.getCards().get(0);
        Card card2 = hand.getCards().get(1);
        if (card1.getValue() < card2.getValue()) {
            card1 = hand.getCards().get(1);
            card2 = hand.getCards().get(0);
        }
        if (index == dealer)
            return preFlopBetButton(hand.getPlayer().getBalance(), card1, card2, countFold(bets), maxBet - bets.get(index).getBet(), minRaise);
        else if (index == (dealer + 3) % GameLogic.NUM_PLAYERS)
            return preFlopBetLatePos(hand.getPlayer().getBalance(), card1, card2, countFold(bets), maxBet - bets.get(index).getBet(), minRaise);
        else
            return preFlopBetEarlyPos(hand.getPlayer().getBalance(), card1, card2, countFold(bets), index == (dealer + 2) % GameLogic.NUM_PLAYERS && maxBet == GameLogic.BLIND, maxBet - bets.get(index).getBet(), minRaise);
    }

    public static String flopTurnRiverBet(Player player, Hand hand, List<PlayerBet> bets, int index, long maxBet, long minRaise, List<Card> communityCards) {
        List<Card> tableCards = new ArrayList<>(communityCards);
        if (tableCards.size() < 5)
            tableCards.add(new Card(5, 0));
        if (tableCards.size() < 5)
            tableCards.add(new Card(5, -1));
        int scorePlayerHand = hand.getBestHand() - EvaluateHand.valueHand(tableCards.toArray(new Card[5]));
        if ((maxBet - bets.get(index).getBet()) * 100 / player.getBalance() < tolerance(scorePlayerHand))
            return raiseOrCall(player.getBalance(), maxBet, minRaise, scorePlayerHand);
        else
            return foldOrCall(player.getBalance(), maxBet, maxBet == 0, countFold(bets));
    }

    public static int tolerance(int score) {
        score /= BASE_SCORE;
        return switch (score) {
            case 20, 19, 18, 17, 16, 15, 14 -> 100;
            case 13, 12 -> 75;
            case 11, 10, 9 -> 60;
            case 8, 7 -> 50;
            case 6, 5, 4 -> 30;
            case 3, 2, 1 -> 15;
            default -> 0;
        };
    }

    public static boolean raisePreFlop(long minRaise) {
        return randomGenerator.nextInt() % Math.pow(2, ((int) (minRaise / GameLogic.BLIND) + 1)) == 0;
    }

    public static boolean fold(int numFold, long maxBet, long balance) {
        return randomGenerator.nextInt() % (Math.pow(2, numFold + 1) * (13 - (12.0 / (1.0 + Math.pow(Math.E, -(double) ((6 * maxBet) / balance)))))) == 0;
    }

    public static String foldOrCall(long balance, long maxBet, boolean check, int numFold) {
        if (check)
            return "CHECK";
        else
            return fold(numFold, maxBet, balance) ? "FOLD" : "CALL";
    }

    public static String raiseOrCall(long balance, long maxBet, long minRaise, int score) {
        if (balance < maxBet)
            return "ALL IN";
        int rand = randomGenerator.nextInt();
        for (int i = score / BASE_SCORE; i > 0; i--) {
            if (rand % Math.pow(2, (int) (i * minRaise / balance) * 4 + 1) == 0)
                return Integer.toString(i);
        }
        return "CALL";
    }

    public static int countFold(List<PlayerBet> bets) {
        return ((int) bets.stream().filter(PlayerBet::isFolded).count());
    }
}
