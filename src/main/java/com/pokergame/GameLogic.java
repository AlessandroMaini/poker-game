package com.pokergame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Utility class to manage the poker game logic.
 *
 * @author Alessandro Maini
 * @version 2023.07.02
 */
public class GameLogic {
    /** Number of players at the table */
    public static final int NUM_PLAYERS = 4;
    /** Value of the blind, the minimal bet */
    public static final long BLIND = 20L;
    private Deck deck;
    private Pot pot;
    private List<Pot> sidePots;
    private List<Player> players;
    private List<PlayerHand> hands;
    private List<PlayerBet> bets;
    private List<Boolean> bluffs;
    private CommunityCards communityCards;
    /** Dealer index in the players list */
    private int dealer;
    /** Determine the current maximum raise */
    private long maxRaise;
    /** Determine if is the big blind turn */
    private boolean bigBlindAction;
    /** Determine if one player has a void balance */
    private boolean brokePlayer;
    private final RandomGenerator randomGenerator = RandomGenerator.getDefault();

    /**
     * A utility inner class to decide the bot players actions.
     *
     * @author Alessandro Maini
     * @version 2023.07.06
     */
    public class BotPlayerLogic {
        public static final int BASE_SCORE = 420000;
        private final int index;
        private final Player botPlayer;
        private final PlayerHand botHand;
        private final PlayerBet botBet;

        /**
         * Initialize a new bot player.
         *
         * @param index the index of the bot player in the players list
         */
        public BotPlayerLogic(int index) {
            this.index = index;
            this.botPlayer = players.get(index);
            this.botHand = hands.get(index);
            this.botBet = bets.get(index);
        }

        /**
         * Determine the bot action before the flop.
         *
         * @return a string describing the action of the bot
         */
        public String preFlopBet() {
            if (bluffs.get(index - 1))
                return raisePreFlop() ? raise(botBet, Math.max(maxRaise, BLIND)) : call(botBet, maxBet());
            Card card1 = botHand.getCards().get(0);
            Card card2 = botHand.getCards().get(1);
            if (card1.getValue() < card2.getValue()) {
                card1 = botHand.getCards().get(1);
                card2 = botHand.getCards().get(0);
            }
            if (index == dealer)
                return preFlopBetButton(card1, card2);
            else if (index == (dealer + 3) % NUM_PLAYERS)
                return preFlopBetLatePos(card1, card2);
            else
                return preFlopBetEarlyPos(card1, card2, index == (dealer + 2) % NUM_PLAYERS && maxBet() == BLIND);
        }

        /**
         * Determine the bot action before the flop when the bot occupy the dealer's position.
         *
         * @param c1 the first card in the bots hand
         * @param c2 the second card in the bots hand
         *
         * @return a string describing the action of the bot
         */
        public String preFlopBetButton(Card c1, Card c2) {
            if (c1.getValue() == 14 || c1.getValue() == c2.getValue() || (c1.getSuit() == c2.getSuit() && (c1.getValue() == 13 || c1.getValue() > 9 && c2.getValue() > 7 || (c1.getValue() - c2.getValue() < 3 && c1.getValue() > 6) || c1.getValue() - c2.getValue() == 1)) || (c1.getSuit() != c2.getSuit() && c1.getValue() > 10 && c2.getValue() > 8)) {
                if (botPlayer.getBalance() < maxBet() - botBet.getBet())
                    return allIn(botBet);
                else
                    return raisePreFlop() ? raise(botBet, Math.max(maxRaise, BLIND)) : call(botBet, maxBet());
            } else
                return botFold(countFold()) ? fold(botBet) : call(botBet, maxBet());
        }

        /**
         * Determine the bot action before the flop when the bot occupy a late position in the game.
         *
         * @param c1 the first card in the bots hand
         * @param c2 the second card in the bots hand
         *
         * @return a string describing the action of the bot
         */
        public String preFlopBetLatePos(Card c1, Card c2) {
            if (c1.getValue() == c2.getValue() || (c1.getSuit() == c2.getSuit() && (c1.getValue() == 14 || c1.getValue() == 13 && c2.getValue() > 8 || c1.getValue() > 10 && c2.getValue() > 9)) || (c1.getSuit() != c2.getSuit() && (c1.getValue() == 14 && c2.getValue() > 8 || c1.getValue() == 13 && c2.getValue() > 9 || c1.getValue() == 12 && c2.getValue() == 11))) {
                if (botPlayer.getBalance() < maxBet() - botBet.getBet())
                    return allIn(botBet);
                else
                    return raisePreFlop() ? raise(botBet, Math.max(maxRaise, BLIND)) : call(botBet, maxBet());
            } else
                return botFold(countFold()) ? fold(botBet) : call(botBet, maxBet());
        }

        /**
         * Determine the bot action before the flop when the bot occupy an early position in the game.
         *
         * @param c1 the first card in the bots hand
         * @param c2 the second card in the bots hand
         * @param check determine if the bot can check
         * *
         *
         * @return a string describing the action of the bot
         */
        public String preFlopBetEarlyPos(Card c1, Card c2, boolean check) {
            if (c1.getValue() == c2.getValue() && c1.getValue() > 6 || (c1.getSuit() == c2.getSuit() && (c1.getValue() == 14 && c2.getValue() > 9 || c1.getValue() == 13 && c2.getValue() == 12)) || (c1.getSuit() != c2.getSuit() && c1.getValue() == 14 && c2.getValue() > 10)) {
                if (botPlayer.getBalance() < maxBet() - botBet.getBet())
                    return allIn(botBet);
                else
                    return raisePreFlop() ? raise(botBet, Math.max(maxRaise, BLIND)) : call(botBet, maxBet());
            } else if (check && randomGenerator.nextInt() % 5 != 0) {
                return check(botBet);
            } else
                return botFold(countFold()) ? fold(botBet) : call(botBet, maxBet());
        }

        /**
         * Determine the bot action after the flop, the turn and the river.
         *
         * @return a string describing the action of the bot
         */
        public String flopTurnRiverBet() {
            int scorePlayerHand;
            Hand hand = new Hand(getAllPlayerCards(botHand));
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
            if ((maxBet() - botBet.getBet()) * 100 / botPlayer.getBalance() < tolerance(scorePlayerHand))
                return raiseOrCall(scorePlayerHand);
            else
                return foldOrCall(maxBet() == 0, countFold());
        }

        /**
         * Determine the bet tolerance of the bot based on the score of his best hand.
         *
         * @param score the net score of the bot hand
         *
         * @return a tolerance percentage
         */
        public int tolerance(int score) {
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
         * @return true if the bot should raise, otherwise false
         */
        public boolean raisePreFlop() {
            if (brokePlayer)
                return false;
            return randomGenerator.nextInt() % (int) Math.pow(2, ((int) (maxRaise / GameLogic.BLIND) + 1)) == 0;
        }

        /**
         * Decide if the bot should fold.
         *
         * @param numFold the number of players that folded
         *
         * @return true if the bot should fold, otherwise false
         */
        public boolean botFold(int numFold) {
            return randomGenerator.nextInt() % (int) (Math.pow(2, numFold + 1) * (13 - (12.0 / (1.0 + Math.pow(Math.E, -(double) (6 * (maxBet() - botBet.getBet())) / (double) botPlayer.getBalance()))))) == 0;
        }

        /**
         * Determine if the bot should fold or call or eventually check.
         *
         * @param check determine if the bot can check
         * @param numFold the number of players that folded
         *
         * @return a string describing the action of the bot
         */
        public String foldOrCall(boolean check, int numFold) {
            if (check && randomGenerator.nextInt() % 5 != 0) {
                return check(botBet);
            } else
                return botFold(numFold) ? fold(botBet) : call(botBet, maxBet());
        }

        /**
         * Determine if the bot should raise or call, in the first case determine also the amount to raise.
         *
         * @param score the net score of the bot hand
         *
         * @return a string describing the action of the bot
         */
        public String raiseOrCall(int score) {
            if (brokePlayer)
                return call(botBet, maxBet());
            if (botPlayer.getBalance() < maxBet() - botBet.getBet())
                return allIn(botBet);
            for (int i = score / BASE_SCORE; i > 0; i--) {
                if (randomGenerator.nextInt() % (int) Math.pow(2, (int) ((i * (double) Math.max(maxRaise, BLIND) / (double) botPlayer.getBalance()) * 4 + 1)) == 0)
                    return raise(botBet, Math.max(maxRaise, BLIND) * i);
            }
            return call(botBet, maxBet());
        }

        /**
         * Count the number of player that folded.
         *
         * @return the number of player that folded
         */
        public int countFold() {
            return ((int) bets.stream().filter(PlayerBet::isFolded).count());
        }
    }

    /**
     * Initialize a new poker game.
     *
     * @param player the user's player
     */
    public GameLogic(Player player) {
        dealer = -1;
        setPlayers(player);
    }

    /**
     * Resets the game to manage the next match.
     *
     * @return false if the user's balance is void, otherwise true
     */
    public boolean initialize() {
        deck = new Deck();
        deck.shuffle();
        pot = new Pot(NUM_PLAYERS);
        sidePots = new ArrayList<>();
        communityCards = new CommunityCards();
        brokePlayer = false;
        bigBlindAction = false;
        maxRaise = 0L;
        if (replacePlayers()) {
            setBluffs();
            setDealer();
            setBets();
            setBlinds();
            setHands();
            return true;
        } else
            return false;
    }

    /**
     * Replaces the players with a void balance.
     *
     * @return false if the user has a void balance, otherwise true
     */
    public boolean replacePlayers() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (players.get(i).getBalance() <= 0) {
                if (i == 0)
                    return false;
                else
                    players.set(i, generateBot(players.get(0)));
            }
        }
        return true;
    }

    /**
     * Generate the flop.
     */
    public void flop() {
        communityCards.setFlopShown(true);
        deck.drawCard();
        List<Card> cards = List.of(deck.drawCard(), deck.drawCard(), deck.drawCard());
        communityCards.setFlop(cards);
    }

    /**
     * Generate the turn.
     */
    public void turn() {
        communityCards.setTurnShown(true);
        deck.drawCard();
        communityCards.setTurn(deck.drawCard());
    }

    /**
     * Generate the river.
     */
    public void river() {
        communityCards.setRiverShown(true);
        deck.drawCard();
        communityCards.setRiver(deck.drawCard());
    }

    /**
     * Determine the winners of the pot and updates their balances.
     *
     * @param points the score of the player's hands
     * @param pot the pot considered
     */
    public List<Player> getPotWinners(List<Integer> points, Pot pot) {
        List<Integer> potPoints = new ArrayList<>();
        List<Player> winners = new ArrayList<>();
        for (int i = 0; i < NUM_PLAYERS; i++)
            if (pot.getPlayerAmount(i) > 0)
                potPoints.add(points.get(i));
            else
                potPoints.add(0);
        for (int i = 0; i < NUM_PLAYERS; i++)
            if (potPoints.get(i).equals(Collections.max(potPoints)))
                winners.add(players.get(i));
        winners.forEach(winner -> handleVictory(winner, pot, winners.size()));
        return winners;
    }

    /**
     * Updates the winner balance.
     *
     * @param p the winning player
     * @param pot the pot won by the player
     * @param numWinners the number of winners for the pot
     */
    public void handleVictory(Player p, Pot pot, int numWinners) {
        p.setBalance(p.getBalance() + (pot.getAmount() / numWinners));
    }

    /**
     * Determine the scores of the players hands.
     *
     * @return a list with the scores
     */
    public List<Integer> getHandsScores() {
        List<Integer> points = new ArrayList<>();
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (bets.get(i).isFolded()) {
                points.add(0);
                continue;
            }
            Hand hand = new Hand(getAllPlayerCards(hands.get(i)));
            points.add(hand.getBestHand());
        }
        return points;
    }

    /**
     * Obtains all the cards associated with a player.
     *
     * @param playerHand the player's hand
     *
     * @return the cards in the player's hand and the community cards
     */
    public List<Card> getAllPlayerCards(PlayerHand playerHand) {
        List<Card> cardList = new ArrayList<>();
        cardList.addAll(playerHand.getCards());
        cardList.addAll(communityCards.getCommunityCards());
        return cardList;
    }

    /**
     * Generate the players list, with the user's player and bots.
     *
     * @param player the user's player
     */
    public void setPlayers(Player player) {
        players = new ArrayList<>();
        players.add(player);
        for (int i = 1; i < NUM_PLAYERS; i++)
            players.add(generateBot(player));
    }

    /**
     * Generate a bot player.
     *
     * @param player the user's player
     *
     * @return the bot player
     */
    public Player generateBot(Player player) {
        Player p = new Player();
        p.setBalance(Math.max(randomGenerator.nextLong((long) (player.getBalance() - (player.getBalance() * 0.5)), (long) (player.getBalance() + (player.getBalance() * 0.5))), 1L));
        p.setUsername(String.format("Bot%d", randomGenerator.nextInt(1, 1000)));
        return p;
    }

    /**
     * Sets a list of boolean to determine if each bot player should bluff or not.
     */
    public void setBluffs() {
        bluffs = new ArrayList<>();
        for (int i = 0; i < NUM_PLAYERS - 1; i++) {
            if (randomGenerator.nextInt() % 15 == 0)
                bluffs.add(true);
            else
                bluffs.add(false);
        }
    }

    /**
     * Decide the dealer position.
     */
    public void setDealer() {
        dealer = (dealer + 1) % NUM_PLAYERS;
    }

    /**
     * Initialize the playerBet objects list.
     */
    public void setBets() {
        bets = new ArrayList<>();
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (i == (dealer + 2) % NUM_PLAYERS)
                bets.add(new PlayerBet(players.get(i), true));
            else
                bets.add(new PlayerBet(players.get(i), false));
        }
    }

    /**
     * Sets the blind bets.
     */
    public void setBlinds() {
        bets.get((dealer + 1) % NUM_PLAYERS).initializeBet();
        bets.get((dealer + 2) % NUM_PLAYERS).initializeBet();
        long bet = Math.min(BLIND / 2, players.get((dealer + 1) % NUM_PLAYERS).getBalance());
        if (bet == players.get((dealer + 1) % NUM_PLAYERS).getBalance())
            brokePlayer = true;
        playerBets(bets.get((dealer + 1) % NUM_PLAYERS), bet);
        bet = brokePlayer ? Math.min(bets.get((dealer + 1) % NUM_PLAYERS).getBet(), players.get((dealer + 2) % NUM_PLAYERS).getBalance()) : Math.min(BLIND, players.get((dealer + 2) % NUM_PLAYERS).getBalance());
        if (bet == players.get((dealer + 2) % NUM_PLAYERS).getBalance())
            brokePlayer = true;
        playerBets(bets.get((dealer + 2) % NUM_PLAYERS), bet);
    }

    /**
     * Generate the players hands.
     */
    public void setHands() {
        hands = initializeHands();
        for (int i = 0; i < NUM_PLAYERS; i++) {
            List<Card> hand = List.of(deck.drawCard(), deck.drawCard());
            hands.set((dealer + 1 + i) % NUM_PLAYERS, new PlayerHand(hand));
        }
    }

    /**
     * Initialize a null list of NUM_PLAYERS PlayerHand.
     *
     * @return the PlayerHand list
     */
    public List<PlayerHand> initializeHands() {
        PlayerHand[] playerHands = new PlayerHand[NUM_PLAYERS];
        Arrays.fill(playerHands, null);
        return Arrays.asList(playerHands);
    }

    /**
     * Resets the playerBet objects list for a new turn.
     */
    public void resetBets() {
        bets.forEach(playerBet -> playerBet.setBet(-1L));
        maxRaise = 0L;
    }

    /**
     * Initialize the turn bet of a player.
     *
     * @param index the index of the player in the players list
     */
    public void initBet(int index) {
        if (index == (dealer + 2) % NUM_PLAYERS && !bigBlindAction)
            bigBlindAction = true;
        bets.get(index).initializeBet();
    }

    /**
     * Determine the action of the bot in the current turn.
     *
     * @param index the index of the bot in the players list
     *
     * @return a string describing the action
     */
    public String botAction(int index) {
        BotPlayerLogic botPlayerLogic = new BotPlayerLogic(index);
        if (communityCards.isNotFlopShown())
            return botPlayerLogic.preFlopBet();
        else
            return botPlayerLogic.flopTurnRiverBet();
    }

    /**
     * Execute the check action.
     *
     * @param playerBet the player who checks
     *
     * @return a string describing the action
     */
    public String check(PlayerBet playerBet) {
        playerBets(playerBet, 0L);
        return "CHECK";
    }

    /**
     * Execute the call action.
     *
     * @param playerBet the player who calls
     * @param bet the bet to call
     *
     * @return a string describing the action
     */
    public String call(PlayerBet playerBet, long bet) {
        if (bet == 0 || (playerBet.isBigBlind() && communityCards.isNotFlopShown() && bet == BLIND))
            return raise(playerBet, BLIND);
        if ((bet -= playerBet.getBet()) >= playerBet.getPlayer().getBalance())
            return allIn(playerBet);
        else {
            playerBets(playerBet, bet);
            return "CALL";
        }
    }

    /**
     * Execute the raise action.
     *
     * @param playerBet the player who raises
     * @param amount the amount to raise
     *
     * @return a string describing the action
     */
    public String raise(PlayerBet playerBet, long amount) {
        maxRaise = Math.max(amount, maxRaise);
        if ((amount += (maxBet() - playerBet.getBet())) >= playerBet.getPlayer().getBalance())
            return allIn(playerBet);
        else {
            playerBets(playerBet, amount);
            return "RAISE";
        }
    }

    /**
     * Execute the fold action.
     *
     * @param playerBet the player who folds
     *
     * @return a string describing the action
     */
    public String fold(PlayerBet playerBet) {
        playerBet.setFolded(true);
        return "FOLD";
    }

    /**
     * Execute the all in action.
     *
     * @param playerBet the player who all in
     *
     * @return a string describing the action
     */
    public String allIn(PlayerBet playerBet) {
        handleSidePots(playerBet.getPlayer().getBalance() + playerBet.getBet());
        playerBets(playerBet, playerBet.getPlayer().getBalance());
        return "ALL IN";
    }

    /**
     * Handles the creation of side pots.
     *
     * @param amount the new maximum bet amount for the main pot
     */
    public void handleSidePots(long amount) {
        brokePlayer = true;
        boolean addSidePot = false;
        Pot sPot = new Pot(NUM_PLAYERS);
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (bets.get(i).getBet() > amount) {
                addSidePot = true;
                sPot.addAmount(bets.get(i).getBet() - amount);
                pot.subAmount(bets.get(i).getBet() - amount);
                sPot.addPlayerAmount(i, bets.get(i).getBet() - amount);
                pot.subPlayerAmount(i, bets.get(i).getBet() - amount);
                bets.get(i).setBet(amount);
            }
        }
        if (addSidePot)
            sidePots.add(sPot);
    }

    /**
     * Handle the player's bet
     *
     * @param playerBet the player who bets
     * @param bet the amount of the bet
     */
    public void playerBets(PlayerBet playerBet, long bet) {
        playerBet.getPlayer().setBalance(playerBet.getPlayer().getBalance() - bet);
        playerBet.addBet(bet);
        pot.addAmount(bet);
        pot.addPlayerAmount(bets.indexOf(playerBet), bet);
    }

    /**
     * Determine if the bet cycle is ended for the current turn.
     *
     * @return true if the cycle have to stop, otherwise false
     */
    public boolean stopBetting() {
        int count = 0;
        long bet = 0L;
        boolean equals = true, first = true;
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (!bets.get(i).isFolded()) {
                count++;
                if (first) {
                    bet = bets.get(i).getBet();
                    if (bet < 0)
                        equals = false;
                    first = false;
                } else if (bet != bets.get(i).getBet())
                    equals = false;
            }
        }
        if (count == 1)
            return true;
        //In the first round of bets the Big Blind can check, raise or fold.
        if (communityCards.isNotFlopShown() && !bigBlindAction && !brokePlayer)
            return false;
        return equals;
    }

    /**
     * Determine the maximum bet in the current turn.
     *
     * @return the maximum bet
     */
    public long maxBet() {
        long max = 0;
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (max < bets.get(i).getBet())
                max = bets.get(i).getBet();
        }
        return max;
    }

    public Pot getPot() {
        return pot;
    }

    public List<Pot> getSidePots() {
        return sidePots;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public CommunityCards getCommunityCards() {
        return communityCards;
    }

    public int getDealer() {
        return dealer;
    }

    public long getMaxRaise() {
        return maxRaise;
    }

    public boolean isBrokePlayer() {
        return brokePlayer;
    }

    public Player getPlayerAt(int index) {
        return players.get(index);
    }

    public PlayerHand getHandAt(int index) {
        return hands.get(index);
    }

    public PlayerBet getBetAt(int index) {
        return bets.get(index);
    }
}
