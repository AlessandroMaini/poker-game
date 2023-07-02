package com.pokergame;

import java.util.Arrays;
import java.util.Comparator;

/**
 * A static class to evaluate a 5 card poker hand.
 *
 * @author Alessandro Maini
 * @version 2023.07.02
 */
public class EvaluateHand {
    public static final int STRAIGHT_FLUSH = 8000000;
    public static final int FOUR_OF_A_KIND = 7000000;
    public static final int FULL_HOUSE = 6000000;
    public static final int FLUSH = 5000000;
    public static final int STRAIGHT = 4000000;
    public static final int THREE_OF_A_KIND = 3000000;
    public static final int TWO_PAIRS = 2000000;
    public static final int ONE_PAIR = 1000000;

    /**
     * Evaluate the 5 card hand.
     *
     * @param h the 5 cards array
     *
     * @return the hand's score
     */
    public static int valueHand(Card[] h) {
        if (isFlush(h) && isStraight(h))
            return valueStraightFlush(h);
        else if (isFourOfAKind(h))
            return valueFourOfAKind(h);
        else if (isFullHouse(h))
            return valueFullHouse(h);
        else if (isFlush(h))
            return valueFlush(h);
        else if (isStraight(h))
            return valueStraight(h);
        else if (isThreeOfAKind(h))
            return valueThreeOfAKind(h);
        else if (isTwoPairs(h))
            return valueTwoPairs(h);
        else if (isOnePair(h))
            return valueOnePair(h);
        else
            return valueHighCard(h);
    }

    /**
     * Evaluate a Straight Flush.
     *
     * @param h the 5 cards array
     *
     * @return the straight flush score
     */
    public static int valueStraightFlush(Card[] h) {
        return STRAIGHT_FLUSH + valueHighCard(h);
    }

    /**
     * Evaluate a Flush.
     *
     * @param h the 5 cards array
     *
     * @return the flush score
     */
    public static int valueFlush(Card[] h) {
        return FLUSH + valueHighCard(h);
    }

    /**
     * Evaluate a Straight.
     *
     * @param h the 5 cards array
     *
     * @return the straight score
     */
    public static int valueStraight(Card[] h) {
        return STRAIGHT + valueHighCard(h);
    }

    /**
     * Evaluate a Four of a Kind.
     *
     * @param h the 5 cards array
     *
     * @return the four of a kind score
     */
    public static int valueFourOfAKind(Card[] h) {
        int val;
        sortByRank(h);
        if (h[0].getValue() == h[3].getValue())
            val = 14 * 14 * 14 * h[2].getValue() + 14 * 14 * h[4].getValue();
        else
            val = 14 * 14 * 14 * h[2].getValue() + 14 * 14 * h[0].getValue();
        return FOUR_OF_A_KIND + val;
    }

    /**
     * Evaluate a Full House.
     *
     * @param h the 5 cards array
     *
     * @return the full house score
     */
    public static int valueFullHouse(Card[] h) {
        int val;
        sortByRank(h);
        if (h[0].getValue() == h[2].getValue() && h[3].getValue() == h[4].getValue())
            val = 14 * 14 * 14 * h[2].getValue() + 14 * 14 * h[4].getValue();
        else
            val = 14 * 14 * 14 * h[2].getValue() + 14 * 14 * h[0].getValue();
        return FULL_HOUSE + val;
    }

    /**
     * Evaluate a Three of a Kind.
     *
     * @param h the 5 cards array
     *
     * @return the three of a kind score
     */
    public static int valueThreeOfAKind(Card[] h) {
        int val;
        sortByRank(h);
        if (h[0].getValue() == h[2].getValue())
            val = 14 * 14 * 14 * h[2].getValue() + 14 * h[3].getValue() + 14 * 14 * h[4].getValue();
        else if (h[1].getValue() == h[3].getValue())
            val = 14 * 14 * 14 * h[2].getValue() + 14 * h[0].getValue() + 14 * 14 * h[4].getValue();
        else
            val = 14 * 14 * 14 * h[2].getValue() + 14 * h[0].getValue() + 14 * 14 * h[1].getValue();
        return THREE_OF_A_KIND + val;
    }

    /**
     * Evaluate Two Pairs.
     *
     * @param h the 5 cards array
     *
     * @return the two pairs score
     */
    public static int valueTwoPairs(Card[] h) {
        int val;
        sortByRank(h);
        if (h[0].getValue() == h[1].getValue() && h[2].getValue() == h[3].getValue())
            val = 14 * 14 * 14 * h[2].getValue() + 14 * 14 * h[0].getValue() + 14 * h[4].getValue();
        else if (h[0].getValue() == h[1].getValue() && h[3].getValue() == h[4].getValue())
            val = 14 * 14 * 14 * h[3].getValue() + 14 * 14 * h[0].getValue() + 14 * h[2].getValue();
        else
            val = 14 * 14 * 14 * h[3].getValue() + 14 * 14 * h[1].getValue() + 14 * h[0].getValue();
        return TWO_PAIRS + val;
    }

    /**
     * Evaluate a One Pair.
     *
     * @param h the 5 cards array
     *
     * @return the one pair score
     */
    public static int valueOnePair(Card[] h) {
        int val;
        sortByRank(h);
        if (h[0].getValue() == h[1].getValue())
            val = 14 * 14 * 14 * h[0].getValue() + h[2].getValue() + 14 * h[3].getValue() + 14 * 14 * h[4].getValue();
        else if (h[1].getValue() == h[2].getValue())
            val = 14 * 14 * 14 * h[1].getValue() + h[0].getValue() + 14 * h[3].getValue() + 14 * 14 * h[4].getValue();
        else if (h[2].getValue() == h[3].getValue())
            val = 14 * 14 * 14 * h[2].getValue() + h[0].getValue() + 14 * h[1].getValue() + 14 * 14 * h[4].getValue();
        else
            val = 14 * 14 * 14 * h[3].getValue() + h[0].getValue() + 14 * h[1].getValue() + 14 * 14 * h[2].getValue();
        return ONE_PAIR + val;
    }

    /**
     * Evaluate a High Card.
     *
     * @param h the 5 cards array
     *
     * @return the high card score
     */
    public static int valueHighCard(Card[] h) {
        int val;
        sortByRank(h);
        val = h[0].getValue() + 14 * h[1].getValue() + 14 * 14 * h[2].getValue() + 14 * 14 * 14 * h[3].getValue() + 14 * 14 * 14 * 14 * h[4].getValue();
        return val;
    }

    /**
     * Determine if the hand is Four of a Kind
     *
     * @param h the 5 cards array
     *
     * @return true if it's four of a kind, otherwise false
     */
    public static boolean isFourOfAKind(Card[] h) {
        boolean a1, a2;
        if (h.length != 5)
            return (false);
        sortByRank(h);
        a1 = h[0].getValue() == h[3].getValue();
        a2 = h[1].getValue() == h[4].getValue();
        return (a1 || a2);
    }

    /**
     * Determine if the hand is Full House
     *
     * @param h the 5 cards array
     *
     * @return true if it's full house, otherwise false
     */
    public static boolean isFullHouse(Card[] h) {
        boolean a1, a2;
        if (h.length != 5)
            return (false);
        sortByRank(h);
        a1 = h[0].getValue() == h[2].getValue() && h[3].getValue() == h[4].getValue();
        a2 = h[0].getValue() == h[1].getValue() && h[2].getValue() == h[4].getValue();
        return (a1 || a2);
    }

    /**
     * Determine if the hand is Three of a Kind
     *
     * @param h the 5 cards array
     *
     * @return true if it's three of a kind, otherwise false
     */
    public static boolean isThreeOfAKind(Card[] h) {
        boolean a1, a2, a3;
        if (h.length != 5)
            return (false);
        if (isFourOfAKind(h) || isFullHouse(h))
            return (false);
        sortByRank(h);
        a1 = h[0].getValue() == h[2].getValue();
        a2 = h[1].getValue() == h[3].getValue();
        a3 = h[2].getValue() == h[4].getValue();
        return (a1 || a2 || a3);
    }

    /**
     * Determine if the hand is Two Pairs
     *
     * @param h the 5 cards array
     *
     * @return true if it's two pairs, otherwise false
     */
    public static boolean isTwoPairs(Card[] h) {
        boolean a1, a2, a3;
        if (h.length != 5)
            return (false);
        if (isFourOfAKind(h) || isFullHouse(h) || isThreeOfAKind(h))
            return (false);
        sortByRank(h);
        a1 = h[0].getValue() == h[1].getValue() && h[2].getValue() == h[3].getValue();
        a2 = h[0].getValue() == h[1].getValue() && h[3].getValue() == h[4].getValue();
        a3 = h[1].getValue() == h[2].getValue() && h[3].getValue() == h[4].getValue();
        return (a1 || a2 || a3);
    }

    /**
     * Determine if the hand is One Pair
     *
     * @param h the 5 cards array
     *
     * @return true if it's one pair, otherwise false
     */
    public static boolean isOnePair(Card[] h) {
        boolean a1, a2, a3, a4;
        if (h.length != 5)
            return (false);
        if (isFourOfAKind(h) || isFullHouse(h) || isThreeOfAKind(h) || isTwoPairs(h))
            return (false);
        sortByRank(h);
        a1 = h[0].getValue() == h[1].getValue();
        a2 = h[1].getValue() == h[2].getValue();
        a3 = h[2].getValue() == h[3].getValue();
        a4 = h[3].getValue() == h[4].getValue();
        return (a1 || a2 || a3 || a4);
    }

    /**
     * Determine if the hand is Flush
     *
     * @param h the 5 cards array
     *
     * @return true if it's flush, otherwise false
     */
    public static boolean isFlush(Card[] h) {
        if (h.length != 5)
            return (false);
        sortBySuit(h);
        return (h[0].getSuit() == h[4].getSuit());
    }

    /**
     * Determine if the hand is Straight
     *
     * @param h the 5 cards array
     *
     * @return true if it's straight, otherwise false
     */
    public static boolean isStraight(Card[] h) {
        int i, testRank;
        if (h.length != 5)
            return (false);
        sortByRank(h);
        //Check if there's an ace
        if (h[4].getValue() == 14) {
            //Checking straight with ace
            boolean a = h[0].getValue() == 2 && h[1].getValue() == 3 && h[2].getValue() == 4 && h[3].getValue() == 5;
            boolean b = h[0].getValue() == 10 && h[1].getValue() == 11 && h[2].getValue() == 12 && h[3].getValue() == 13;
            return (a || b);
        } else {
            //General case
            testRank = h[0].getValue() + 1;
            for (i = 1; i < 5; i++) {
                if (h[i].getValue() != testRank)
                    return false;
                testRank++;
            }
            return true;
        }
    }

    /**
     * Order the cards by value or rank (increasing).
     *
     * @param h the 5 cards array
     */
    public static void sortByRank(Card[] h) {
        Arrays.sort(h, Comparator.comparingInt(Card::getValue));
    }

    /**
     * Order the cards by suit (increasing).
     *
     * @param h the 5 cards array
     */
    public static void sortBySuit(Card[] h) {
        Arrays.sort(h, Comparator.comparingInt(Card::getSuit));
    }
}
