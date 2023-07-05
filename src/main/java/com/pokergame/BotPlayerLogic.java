package com.pokergame;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * A static utility class to decide the bot players actions.
 *
 * @author Alessandro Maini
 * @version 2023.07.05
 */
public class BotPlayerLogic {
    public static final int BASE_SCORE = 420000;
    public static RandomGenerator randomGenerator = RandomGenerator.getDefault();

    /**
     * Determine the bot action before the flop.
     *
     * @param hand the bot hand
     * @param bets the list of PlayerBet objects
     * @param index the index of the bot in the players list
     * @param dealer the index of the dealer in the players list
     * @param maxBet the maximum bet in the turn
     * @param minRaise the minimum possible raise
     * @param bluffs a list of boolean to determine if a bot bluffs
     * @param brokePlayer determine if there's a player with void balance
     *
     * @return a string describing the action of the bot
     */
    public static String preFlopBet(PlayerHand hand, List<PlayerBet> bets, int index, int dealer, long maxBet, long minRaise, List<Boolean> bluffs, boolean brokePlayer) {
        if (bluffs.get(index - 1))
            return raisePreFlop(minRaise, brokePlayer) ? "RAISE" : "CALL";
        Card card1 = hand.getCards().get(0);
        Card card2 = hand.getCards().get(1);
        if (card1.getValue() < card2.getValue()) {
            card1 = hand.getCards().get(1);
            card2 = hand.getCards().get(0);
        }
        if (index == dealer)
            return preFlopBetButton(hand.getPlayer().getBalance(), card1, card2, countFold(bets), maxBet - bets.get(index).getBet(), minRaise, brokePlayer);
        else if (index == (dealer + 3) % GameLogic.NUM_PLAYERS)
            return preFlopBetLatePos(hand.getPlayer().getBalance(), card1, card2, countFold(bets), maxBet - bets.get(index).getBet(), minRaise, brokePlayer);
        else
            return preFlopBetEarlyPos(hand.getPlayer().getBalance(), card1, card2, countFold(bets), index == (dealer + 2) % GameLogic.NUM_PLAYERS && maxBet == GameLogic.BLIND, maxBet - bets.get(index).getBet(), minRaise, brokePlayer);
    }

    /**
     * Determine the bot action before the flop when the bot occupy the dealer's position.
     *
     * @param balance the balance of the bot
     * @param c1 the first card in the bots hand
     * @param c2 the second card in the bots hand
     * @param numFold the number of players that folded
     * @param maxBet the amount to call
     * @param minRaise the minimum possible raise
     * @param brokePlayer determine if there's a player with void balance
     *
     * @return a string describing the action of the bot
     */
    public static String preFlopBetButton(long balance, Card c1, Card c2, int numFold, long maxBet, long minRaise, boolean brokePlayer) {
        if (c1.getValue() == 14 || c1.getValue() == c2.getValue() || (c1.getSuit() == c2.getSuit() && (c1.getValue() == 13 || c1.getValue() > 9 && c2.getValue() > 7 || (c1.getValue() - c2.getValue() < 3 && c1.getValue() > 6) || c1.getValue() - c2.getValue() == 1)) || (c1.getSuit() != c2.getSuit() && c1.getValue() > 10 && c2.getValue() > 8)) {
            if (balance < maxBet)
                return "ALL IN";
            else
                return raisePreFlop(minRaise, brokePlayer) ? "RAISE" : "CALL";
        } else
            return fold(numFold, maxBet, balance) ? "FOLD" : "CALL";
    }

    /**
     * Determine the bot action before the flop when the bot occupy a late position in the game.
     *
     * @param balance the balance of the bot
     * @param c1 the first card in the bots hand
     * @param c2 the second card in the bots hand
     * @param numFold the number of players that folded
     * @param maxBet the amount to call
     * @param minRaise the minimum possible raise
     * @param brokePlayer determine if there's a player with void balance
     *
     * @return a string describing the action of the bot
     */
    public static String preFlopBetLatePos(long balance, Card c1, Card c2, int numFold, long maxBet, long minRaise, boolean brokePlayer) {
        if (c1.getValue() == c2.getValue() || (c1.getSuit() == c2.getSuit() && (c1.getValue() == 14 || c1.getValue() == 13 && c2.getValue() > 8 || c1.getValue() > 10 && c2.getValue() > 9)) || (c1.getSuit() != c2.getSuit() && (c1.getValue() == 14 && c2.getValue() > 8 || c1.getValue() == 13 && c2.getValue() > 9 || c1.getValue() == 12 && c2.getValue() == 11))) {
            if (balance < maxBet)
                return "ALL IN";
            else
                return raisePreFlop(minRaise, brokePlayer) ? "RAISE" : "CALL";
        } else
            return fold(numFold, maxBet, balance) ? "FOLD" : "CALL";
    }

    /**
     * Determine the bot action before the flop when the bot occupy an early position in the game.
     *
     * @param balance the balance of the bot
     * @param c1 the first card in the bots hand
     * @param c2 the second card in the bots hand
     * @param numFold the number of players that folded
     * @param check determine if the bot can check
     * @param maxBet the amount to call
     * @param minRaise the minimum possible raise
     * @param brokePlayer determine if there's a player with void balance
     *
     * @return a string describing the action of the bot
     */
    public static String preFlopBetEarlyPos(long balance, Card c1, Card c2, int numFold, boolean check, long maxBet, long minRaise, boolean brokePlayer) {
        if (c1.getValue() == c2.getValue() && c1.getValue() > 6 || (c1.getSuit() == c2.getSuit() && (c1.getValue() == 14 && c2.getValue() > 9 || c1.getValue() == 13 && c2.getValue() == 12)) || (c1.getSuit() != c2.getSuit() && c1.getValue() == 14 && c2.getValue() > 10)) {
            if (balance < maxBet)
                return "ALL IN";
            else
                return raisePreFlop(minRaise, brokePlayer) ? "RAISE" : "CALL";
        } else if (check && randomGenerator.nextInt() % 5 != 0) {
            return "CHECK";
        } else
            return fold(numFold, maxBet, balance) ? "FOLD" : "CALL";
    }

    /**
     * Determine the bot action after the flop, the turn and the river.
     *
     * @param player the bot player
     * @param hand a Hand object containing all the card associated with the bot
     * @param bets the list of PlayerBet objects
     * @param index the index of the bot in the players list
     * @param maxBet the maximum bet in the turn
     * @param minRaise the minimum possible raise
     * @param communityCards the community cards of the game
     * @param bluffs a list of boolean to determine if a bot bluffs
     * @param brokePlayer determine if there's a player with void balance
     *
     * @return a string describing the action of the bot
     */
    public static String flopTurnRiverBet(Player player, Hand hand, List<PlayerBet> bets, int index, long maxBet, long minRaise, CommunityCards communityCards, List<Boolean> bluffs, boolean brokePlayer) {
        int scorePlayerHand;
        if (bluffs.get(index - 1)) {
            if (communityCards.isRiverShown())
                scorePlayerHand = randomGenerator.nextInt(4 * BASE_SCORE, 16 * BASE_SCORE);
            else if (communityCards.isTurnShown())
                scorePlayerHand = randomGenerator.nextInt(2 * BASE_SCORE, 14 * BASE_SCORE);
            else
                scorePlayerHand = randomGenerator.nextInt(BASE_SCORE, 11 * BASE_SCORE);
        } else {
            List<Card> tableCards = new ArrayList<>(communityCards.getCommunityCards());
            if (tableCards.size() < 5)
                tableCards.add(new Card(5, 0));
            if (tableCards.size() < 5)
                tableCards.add(new Card(5, -1));
            //The net score of the bot hand is the score of his best hand minus the score of the community cards only
            scorePlayerHand = hand.getBestHand() - EvaluateHand.valueHand(tableCards.toArray(new Card[5]));
        }
        if ((maxBet - bets.get(index).getBet()) * 100 / player.getBalance() < tolerance(scorePlayerHand))
            return raiseOrCall(player.getBalance(), maxBet - bets.get(index).getBet(), minRaise, scorePlayerHand, brokePlayer);
        else
            return foldOrCall(player.getBalance(), maxBet - bets.get(index).getBet(), maxBet == 0, countFold(bets));
    }

    /**
     * Determine the bet tolerance of the bot based on the score of his best hand.
     *
     * @param score the net score of the bot hand
     *
     * @return a tolerance percentage
     */
    public static int tolerance(int score) {
        score /= BASE_SCORE;
        return switch (score) {
            case 20, 19, 18, 17, 16, 15, 14 -> Integer.MAX_VALUE;
            case 13, 12 -> 100;
            case 11, 10, 9 -> 80;
            case 8, 7 -> 60;
            case 6, 5, 4 -> 40;
            case 3, 2, 1 -> 20;
            default -> 0;
        };
    }

    /**
     * Decide if the bot should raise before the flop.
     *
     * @param minRaise the minimum possible raise
     * @param brokePlayer determine if there's a player with void balance
     *
     * @return true if the bot should raise, otherwise false
     */
    public static boolean raisePreFlop(long minRaise, boolean brokePlayer) {
        if (brokePlayer)
            return false;
        return randomGenerator.nextInt() % (int) Math.pow(2, ((int) (minRaise / GameLogic.BLIND) + 1)) == 0;
    }

    /**
     * Decide if the bot should fold.
     *
     * @param numFold the number of players that folded
     * @param maxBet the amount to call
     * @param balance the balance of the bot
     *
     * @return true if the bot should fold, otherwise false
     */
    public static boolean fold(int numFold, long maxBet, long balance) {
        return randomGenerator.nextInt() % (int) (Math.pow(2, numFold + 1) * (13 - (12.0 / (1.0 + Math.pow(Math.E, -(double) (6 * maxBet) / (double) balance))))) == 0;
    }

    /**
     * Determine if the bot should fold or call or eventually check.
     *
     * @param balance the balance of the bot
     * @param maxBet the amount to call
     * @param check determine if the bot can check
     * @param numFold the number of players that folded
     *
     * @return a string describing the action of the bot
     */
    public static String foldOrCall(long balance, long maxBet, boolean check, int numFold) {
        if (check && randomGenerator.nextInt() % 5 != 0) {
            return "CHECK";
        } else
            return fold(numFold, maxBet, balance) ? "FOLD" : "CALL";
    }

    /**
     * Determine if the bot should raise or call, in the first case determine also the amount to raise.
     *
     * @param balance the balance of the bot
     * @param maxBet the amount to call
     * @param minRaise the minimum possible raise
     * @param score the net score of the bot hand
     * @param brokePlayer determine if there's a player with void balance
     *
     * @return a string describing the action of the bot, raise is represented by a number which is the amount to
     * raise in minRaise units
     */
    public static String raiseOrCall(long balance, long maxBet, long minRaise, int score, boolean brokePlayer) {
        if (brokePlayer)
            return "CALL";
        if (balance < maxBet)
            return "ALL IN";
        for (int i = score / BASE_SCORE; i > 0; i--) {
            if (randomGenerator.nextInt() % (int) Math.pow(2, (int) ((i * (double) minRaise / (double) balance) * 4 + 1)) == 0)
                return Integer.toString(i);
        }
        return "CALL";
    }

    /**
     * Count the number of player that folded.
     *
     * @param bets the list of PlayerBet objects
     *
     * @return the number of player that folded
     */
    public static int countFold(List<PlayerBet> bets) {
        return ((int) bets.stream().filter(PlayerBet::isFolded).count());
    }
}
