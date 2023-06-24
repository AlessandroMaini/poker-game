package com.pokergame;

import java.util.random.RandomGenerator;

public class Game {
    final public int NUM_PLAYERS = 4;
    final public int BLIND = 20;
    public Deck deck;
    public Pot pot;
    public Player[] players = new Player[NUM_PLAYERS];
    public PlayerHand[] hands = new PlayerHand[NUM_PLAYERS];
    public PlayerBet[] bets = new PlayerBet[NUM_PLAYERS];
    public int dealer = -1;
    public boolean PLAYER_MOVE = false;
    public long maxRaise = 0L;

    public Game() {
        deck = new Deck();
        deck.shuffle();
    }

    public void startGame(Player player) {
        setPlayers(player);
        setBets();
        setDealer();
        setBlinds();
        setHands();
        betCycle();
    }

    public void setDealer() {
        dealer = (dealer + 1) % NUM_PLAYERS;
    }

    public void setPlayers(Player player) {
        players[0] = player;
        for (int i = 1; i < NUM_PLAYERS; i++)
            players[i] = generateBot(player);
    }

    public Player generateBot(Player player) {
        Player p = new Player();
        p.setBalance(RandomGenerator.getDefault().nextLong((long) (player.getBalance() - (player.getBalance() * 0.5)), (long) (player.getBalance() + (player.getBalance() * 0.5))));
        p.setUsername(String.format("Bot%d", RandomGenerator.getDefault().nextInt(1, 1000)));
        return p;
    }

    public void setBlinds() {
        playerBets(bets[(dealer + 1) % NUM_PLAYERS], BLIND / 2);
        playerBets(bets[(dealer + 2) % NUM_PLAYERS], BLIND);
    }

    public void setHands() {
        for (int i = 0; i < NUM_PLAYERS; i++) {
            Card[] hand = {deck.drawCard(), deck.drawCard()};
            hands[(dealer + 1 + i) % NUM_PLAYERS] = new PlayerHand(players[(dealer + 1 + i) % NUM_PLAYERS], hand);
        }
    }

    public void setBets() {
        for (int i = 0; i < NUM_PLAYERS; i++)
            bets[i] = new PlayerBet(players[i]);
    }

    public void betCycle() {
        while (!stopBetting()) {
            for (int i = 0; i < NUM_PLAYERS; i++) {
                int j = (dealer + 3 + i) % NUM_PLAYERS;
                if (bets[j].isFolded())
                    continue;
                if (j == 0) {
                    PLAYER_MOVE = true;
                } else if (!PLAYER_MOVE) {
                    if (bets[j].getBet() < maxBet())
                        call(bets[j], maxBet());
                    else check();
                }
            }
        }
    }

    public void check() {

    }

    public void call(PlayerBet player, long bet) {
        playerBets(player, bet - player.getBet());
    }

    public boolean raise(PlayerBet playerBet, long amount) {
        if (amount < maxRaise)
            return false;
        playerBets(playerBet, amount);
        return true;
    }

    public void fold(PlayerBet playerBet) {
        playerBet.setFolded(true);
    }

    public void playerBets(PlayerBet playerBet, long bet) {
        playerBet.getPlayer().setBalance(playerBet.getPlayer().getBalance() - bet);
        playerBet.addBet(bet);
        pot.addAmount(bet);
    }

    private boolean stopBetting() {
        int count = 0;
        long bet = bets[0].getBet();
        boolean equals = true;
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (!bets[i].isFolded())
                count++;
            if (bet != bets[i].getBet())
                equals = false;
        }
        return equals || count == 1;
    }

    private long maxBet() {
        long max = 0;
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (max < bets[i].getBet())
                max = bets[i].getBet();
        }
        return max;
    }
}
