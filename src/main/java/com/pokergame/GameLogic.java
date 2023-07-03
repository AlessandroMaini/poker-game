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
    final public static int NUM_PLAYERS = 4;
    /** Value of the blind, the minimal bet */
    final public static long BLIND = 20L;
    Deck deck;
    Pot pot;
    List<Pot> sidePots;
    List<Player> players;
    List<PlayerHand> hands;
    List<PlayerBet> bets;
    CommunityCards communityCards;
    /** Dealer index in the players list */
    int dealer;
    /** Determine the current maximal raise */
    long maxRaise;
    /** Determine if is the big blind turn */
    boolean bigBlindAction;
    /** Determine if there are side pots */
    boolean sidePot;
    /** Determine if one player has a void balance */
    boolean brokePlayer;
    RandomGenerator randomGenerator = RandomGenerator.getDefault();

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
        sidePot = false;
        brokePlayer = false;
        bigBlindAction = false;
        maxRaise = 0L;
        if (replacePlayers()) {
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
        p.setBalance(randomGenerator.nextLong((long) (player.getBalance() - (player.getBalance() * 0.5)), (long) (player.getBalance() + (player.getBalance() * 0.5))));
        p.setUsername(String.format("Bot%d", randomGenerator.nextInt(1, 1000)));
        return p;
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
        playerBets(bets.get((dealer + 1) % NUM_PLAYERS), BLIND / 2);
        playerBets(bets.get((dealer + 2) % NUM_PLAYERS), BLIND);
    }

    /**
     * Generate the players hands.
     */
    public void setHands() {
        hands = initializeHands();
        for (int i = 0; i < NUM_PLAYERS; i++) {
            List<Card> hand = List.of(deck.drawCard(), deck.drawCard());
            hands.set((dealer + 1 + i) % NUM_PLAYERS, new PlayerHand(players.get((dealer + 1 + i) % NUM_PLAYERS), hand));
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
    public String botBet(int index) {
        if (communityCards.isNotFlopShown())
            return executeBotAction(BotPlayerLogic.preFlopBet(hands.get(index), bets, index, dealer, maxBet()), index);
        if (bets.get(index).getBet() < maxBet()) {
            if (randomGenerator.nextInt() % 2 == 0 || brokePlayer || players.get(index).getBalance() <= (maxBet() - bets.get(index).getBet()))
                return call(bets.get(index), maxBet());
            else if (randomGenerator.nextInt() % 3 != 0)
                return raise(bets.get(index), Math.max(maxRaise, BLIND));
            else
                return fold(bets.get(index));
        } else
            return check(bets.get(index));
    }

    public String executeBotAction(String action, int index) {
        return switch (action) {
            case "CALL" -> call(bets.get(index), maxBet());
            case "RAISE" -> raise(bets.get(index), Math.max(maxRaise, BLIND));
            case "FOLD" -> fold(bets.get(index));
            case "ALL IN" -> allIn(bets.get(index));
            default -> check(bets.get(index));
        };
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
        if (bet == 0)
            bet = BLIND;
        if (playerBet.isBigBlind() && communityCards.isNotFlopShown() && bet == BLIND)
            bet = 2 * BLIND;
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
        Pot sPot = new Pot(NUM_PLAYERS);
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (bets.get(i).getBet() > amount) {
                sidePot = true;
                sPot.addAmount(bets.get(i).getBet() - amount);
                pot.subAmount(bets.get(i).getBet() - amount);
                sPot.addPlayerAmount(i, bets.get(i).getBet() - amount);
                pot.subPlayerAmount(i, bets.get(i).getBet() - amount);
                bets.get(i).setBet(amount);
            }
        }
        if (sidePot)
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
        if (communityCards.isNotFlopShown() && !bigBlindAction)
            return false;
        return equals;
    }

    /**
     * Determine the maximal bet in the current turn.
     *
     * @return the maximal bet
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

    public boolean isSidePot() {
        return sidePot;
    }

    public boolean isNotBrokePlayer() {
        return !brokePlayer;
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
