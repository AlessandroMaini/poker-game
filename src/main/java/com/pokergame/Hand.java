package com.pokergame;

import java.util.Arrays;
import java.util.List;

/**
 * A list of 5 to 7 cards.
 *
 * @author Alessandro Maini
 * @version 2023.07.02
 */
public class Hand {
    final public int NUM_HAND_CARDS = 5;
    public List<Card> cards;
    public int numCards;

    /**
     * Initialize a new hand.
     *
     * @param cards the list of cards
     */
    public Hand(List<Card> cards) {
        this.cards = cards;
        this.numCards = cards.size();
    }

    /**
     * Determine the best possible combination of 5 cards from those in the list.
     *
     * @return the score of the best combination
     */
    public int getBestHand() {
        int bestHand = 0;
        boolean[] flags = new boolean[numCards];
        Arrays.fill(flags, 0, NUM_HAND_CARDS, true);
        int start = 0;
        int end = NUM_HAND_CARDS - 1;
        bestHand = Math.max(bestHand, evaluateHand(flags));
        while (end < (numCards - 1)) {
            if (start == end) {
                flags[end] = false;
                flags[end + 1] = true;
                start += 1;
                end += 1;
                while (end + 1 < numCards && flags[end + 1]) {
                    ++end;
                }
            } else {
                if (start == 0) {
                    flags[end] = false;
                    flags[end + 1] = true;
                    end -= 1;
                } else {
                    flags[end + 1] = true;
                    Arrays.fill(flags, start, end + 1, false);
                    Arrays.fill(flags, 0, end - start, true);
                    end = end - start - 1;
                    start = 0;
                }
            }
            bestHand = Math.max(bestHand, evaluateHand(flags));
        }
        return bestHand;
    }

    /**
     * Evaluate a 5 cards combination.
     *
     * @param flags a boolean array to determine which cards to choose from those in the list
     *
     * @return the score of the combination
     */
    public int evaluateHand(boolean[] flags) {
        Card[] hand = new Card[NUM_HAND_CARDS];
        int valueExtra = 0;
        for (int i = 0, count = 0; i < numCards; ++i) {
            if (flags[i]) {
                hand[count] = cards.get(i);
                ++count;
            } else
                valueExtra += cards.get(i).getValue();
        }
        return EvaluateHand.valueHand(hand) + valueExtra;
    }
}
