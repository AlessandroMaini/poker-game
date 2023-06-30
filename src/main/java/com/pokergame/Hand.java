package com.pokergame;

import java.util.Arrays;
import java.util.List;

public class Hand {
    final public int NUM_HAND_CARDS = 5;
    public List<Card> cards;
    public int numCards;

    public Hand(List<Card> cards) {
        this.cards = cards;
        this.numCards = cards.size();
    }

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

    public int evaluateHand(boolean[] flags) {
        Card[] hand = new Card[NUM_HAND_CARDS];
        for (int i = 0, count = 0; i < numCards && count < NUM_HAND_CARDS; ++i) {
            if (flags[i]) {
                hand[count] = cards.get(i);
                ++count;
            }
        }
        return EvaluateHand.valueHand(hand);
    }
}
