package com.pokergame;

import java.util.Arrays;
import java.util.Comparator;

public class EvaluateHand {
    /* --------------------------------------------------------------
   Contains method to evaluate the strength of Poker hands

   I made them as STATIC (class) methods, because they 
   are like methods such as "sin(x)", "cos(x)", that
   evaluate the sine, cosine of a value x

   Input of each method:

     Card[] h;  (5 Cards)

   Output of each method:

     An integer value represent the strength
     The higher the integer, the stronger the hand
   -------------------------------------------------------------- */
    public static final int STRAIGHT_FLUSH = 8000000;
    public static final int FOUR_OF_A_KIND = 7000000;
    public static final int FULL_HOUSE = 6000000;
    public static final int FLUSH = 5000000;
    public static final int STRAIGHT = 4000000;
    public static final int SET = 3000000;
    public static final int TWO_PAIRS = 2000000;
    public static final int ONE_PAIR = 1000000;

    /***********************************************************
     Methods used to determine a certain Poker hand
     ***********************************************************/

/* --------------------------------------------------------
  valueHand(): return value of a hand
  -------------------------------------------------------- */
    public static int valueHand(Card[] h) {
        if (isFlush(h) && isStraight(h))
            return valueStraightFlush(h);
        else if (is4s(h))
            return valueFourOfAKind(h);
        else if (isFullHouse(h))
            return valueFullHouse(h);
        else if (isFlush(h))
            return valueFlush(h);
        else if (isStraight(h))
            return valueStraight(h);
        else if (is3s(h))
            return valueSet(h);
        else if (is22s(h))
            return valueTwoPairs(h);
        else if (is2s(h))
            return valueOnePair(h);
        else
            return valueHighCard(h);
    }

    /* -----------------------------------------------------
       valueFlush(): return value of a Flush hand
 
             value = FLUSH + valueHighCard()
       ----------------------------------------------------- */
    public static int valueStraightFlush(Card[] h) {
        return STRAIGHT_FLUSH + valueHighCard(h);
    }

    /* -----------------------------------------------------
       valueFlush(): return value of a Flush hand
 
             value = FLUSH + valueHighCard()
       ----------------------------------------------------- */
    public static int valueFlush(Card[] h) {
        return FLUSH + valueHighCard(h);
    }

    /* -----------------------------------------------------
       valueStraight(): return value of a Straight hand
 
             value = STRAIGHT + valueHighCard()
       ----------------------------------------------------- */
    public static int valueStraight(Card[] h) {
        return STRAIGHT + valueHighCard(h);
    }

    /* ---------------------------------------------------------
       valueFourOfAKind(): return value of a 4 of a kind hand
 
             value = FOUR_OF_A_KIND + 4sCardRank
 
       Trick: card h[2] is always a card that is part of 
              the 4-of-a-kind hand
       --------------------------------------------------------- */
    public static int valueFourOfAKind(Card[] h) {
        int val;

        sortByRank(h);

        if (h[0].getValue() == h[3].getValue())
            val = 14 * 14 * 14 * h[2].getValue() + 14 * 14 * h[4].getValue();
        else
            val = 14 * 14 * 14 * h[2].getValue() + 14 * 14 * h[0].getValue();

        return FOUR_OF_A_KIND + val;
    }

    /* -----------------------------------------------------------
       valueFullHouse(): return value of a Full House hand
 
             value = FULL_HOUSE + SetCardRank
 
       Trick: card h[2] is always a card that is part of
              the 3-of-a-kind in the full house hand
       ----------------------------------------------------------- */
    public static int valueFullHouse(Card[] h) {
        int val;

        sortByRank(h);

        if (h[0].getValue() == h[2].getValue() && h[3].getValue() == h[4].getValue())
            val = 14 * 14 * 14 * h[2].getValue() + 14 * 14 * h[4].getValue();
        else
            val = 14 * 14 * 14 * h[2].getValue() + 14 * 14 * h[0].getValue();

        return FULL_HOUSE + val;
    }

    /* ---------------------------------------------------------------
       valueSet(): return value of a Set hand
 
             value = SET + SetCardRank
 
       Trick: card h[2] is always a card that is part of the set hand
       --------------------------------------------------------------- */
    public static int valueSet(Card[] h) {
        int val;

        sortByRank(h);

        if (h[0].getValue() == h[2].getValue())
            val = 14 * 14 * 14 * h[2].getValue() + 14 * h[3].getValue() + 14 * 14 * h[4].getValue();
        else if (h[1].getValue() == h[3].getValue())
            val = 14 * 14 * 14 * h[2].getValue() + 14 * h[0].getValue() + 14 * 14 * h[4].getValue();
        else
            val = 14 * 14 * 14 * h[2].getValue() + 14 * h[0].getValue() + 14 * 14 * h[1].getValue();

        return SET + val;
    }

    /* -----------------------------------------------------
       valueTwoPairs(): return value of a Two-Pairs hand
 
             value = TWO_PAIRS
                    + 14*14*HighPairCard
                    + 14*LowPairCard
                    + UnmatchedCard
       ----------------------------------------------------- */
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

    /* -----------------------------------------------------
       valueOnePair(): return value of a One-Pair hand
 
             value = ONE_PAIR 
                    + 14^3*PairCard
                    + 14^2*HighestCard
                    + 14*MiddleCard
                    + LowestCard
       ----------------------------------------------------- */
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

    /* -----------------------------------------------------
       valueHighCard(): return value of a high card hand
 
             value =  14^4*highestCard 
                    + 14^3*2ndHighestCard
                    + 14^2*3rdHighestCard
                    + 14^1*4thHighestCard
                    + LowestCard
       ----------------------------------------------------- */
    public static int valueHighCard(Card[] h) {
        int val;

        sortByRank(h);

        val = h[0].getValue() + 14 * h[1].getValue() + 14 * 14 * h[2].getValue() + 14 * 14 * 14 * h[3].getValue() + 14 * 14 * 14 * 14 * h[4].getValue();

        return val;
    }

    /***********************************************************
     Methods used to determine a certain Poker hand
     ***********************************************************/


/* ---------------------------------------------
  is4s(): true if h has 4 of a kind
          false otherwise
  --------------------------------------------- */
    public static boolean is4s(Card[] h) {
        boolean a1, a2;

        if (h.length != 5)
            return (false);

        sortByRank(h);

        a1 = h[0].getValue() == h[3].getValue();

        a2 = h[1].getValue() == h[4].getValue();

        return (a1 || a2);
    }

    /* ----------------------------------------------------
       isFullHouse(): true if h has Full House
                      false otherwise
       ---------------------------------------------------- */
    public static boolean isFullHouse(Card[] h) {
        boolean a1, a2;

        if (h.length != 5)
            return (false);

        sortByRank(h);

        a1 = h[0].getValue() == h[2].getValue() && h[3].getValue() == h[4].getValue();

        a2 = h[0].getValue() == h[1].getValue() && h[2].getValue() == h[4].getValue();

        return (a1 || a2);
    }

    /* ----------------------------------------------------
       is3s(): true if h has 3 of a kind
               false otherwise
 
       **** Note: use is3s() ONLY if you know the hand
                  does not have 4 of a kind 
       ---------------------------------------------------- */
    public static boolean is3s(Card[] h) {
        boolean a1, a2, a3;

        if (h.length != 5)
            return (false);

        if (is4s(h) || isFullHouse(h))
            return (false);        // The hand is not 3 of a kind (but better)

  /* ----------------------------------------------------------
     Now we know the hand is not 4 of a kind or a full house !
     ---------------------------------------------------------- */
        sortByRank(h);

        a1 = h[0].getValue() == h[2].getValue();

        a2 = h[1].getValue() == h[3].getValue();

        a3 = h[2].getValue() == h[4].getValue();

        return (a1 || a2 || a3);
    }

    /* -----------------------------------------------------
       is22s(): true if h has 2 pairs
                false otherwise
 
       **** Note: use is22s() ONLY if you know the hand
                  does not have 3 of a kind or better
       ----------------------------------------------------- */
    public static boolean is22s(Card[] h) {
        boolean a1, a2, a3;

        if (h.length != 5)
            return (false);

        if (is4s(h) || isFullHouse(h) || is3s(h))
            return (false);        // The hand is not 2 pairs (but better)

        sortByRank(h);

        a1 = h[0].getValue() == h[1].getValue() && h[2].getValue() == h[3].getValue();

        a2 = h[0].getValue() == h[1].getValue() && h[3].getValue() == h[4].getValue();

        a3 = h[1].getValue() == h[2].getValue() && h[3].getValue() == h[4].getValue();

        return (a1 || a2 || a3);
    }

    /* -----------------------------------------------------
       is2s(): true if h has one pair
               false otherwise
 
       **** Note: use is22s() ONLY if you know the hand
                  does not have 2 pairs or better
       ----------------------------------------------------- */
    public static boolean is2s(Card[] h) {
        boolean a1, a2, a3, a4;

        if (h.length != 5)
            return (false);

        if (is4s(h) || isFullHouse(h) || is3s(h) || is22s(h))
            return (false);        // The hand is not one pair (but better)

        sortByRank(h);

        a1 = h[0].getValue() == h[1].getValue();
        a2 = h[1].getValue() == h[2].getValue();
        a3 = h[2].getValue() == h[3].getValue();
        a4 = h[3].getValue() == h[4].getValue();

        return (a1 || a2 || a3 || a4);
    }

    /* ---------------------------------------------
       isFlush(): true if h has a flush
                  false otherwise
       --------------------------------------------- */
    public static boolean isFlush(Card[] h) {
        if (h.length != 5)
            return (false);

        sortBySuit(h);

        return (h[0].getSuit() == h[4].getSuit());   // All cards have same suit
    }

    /* ---------------------------------------------
       isStraight(): true if h is a Straight
                     false otherwise
       --------------------------------------------- */
    public static boolean isStraight(Card[] h) {
        int i, testRank;

        if (h.length != 5)
            return (false);

        sortByRank(h);

  /* ===========================
     Check if hand has an Ace
     =========================== */
        if (h[4].getValue() == 14) {
     /* =================================
        Check straight using an Ace
        ================================= */
            boolean a = h[0].getValue() == 2 && h[1].getValue() == 3 && h[2].getValue() == 4 && h[3].getValue() == 5;
            boolean b = h[0].getValue() == 10 && h[1].getValue() == 11 && h[2].getValue() == 12 && h[3].getValue() == 13;

            return (a || b);
        } else {
     /* ===========================================
        General case: check for increasing values
        =========================================== */
            testRank = h[0].getValue() + 1;

            for (i = 1; i < 5; i++) {
                if (h[i].getValue() != testRank)
                    return false;        // Straight failed...

                testRank++;
            }

            return true;        // Straight found !
        }
    }

/* ===========================================================
  Helper methods
  =========================================================== */

    /* ---------------------------------------------
       Sort hand by rank:
 
           smallest ranked card first .... 
 
       (Finding a straight is easier that way)
       --------------------------------------------- */
    public static void sortByRank(Card[] h) {
        Arrays.sort(h, Comparator.comparingInt(Card::getValue));
    }

    /* ---------------------------------------------
       Sort hand by suit:
 
           smallest suit card first .... 
 
       (Finding a flush is easier that way)
       --------------------------------------------- */
    public static void sortBySuit(Card[] h) {
        Arrays.sort(h, Comparator.comparingInt(Card::getSuit));
    }
}
