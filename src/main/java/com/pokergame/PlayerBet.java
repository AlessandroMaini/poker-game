package com.pokergame;

/**
 * Associates each player to his turn bet as well as other properties such as: 'Has the player folded?' and 'Is the
 * player big blind?'
 *
 * @author Alessandro Maini
 * @version 2023.06.28
 */
public class PlayerBet {
    public Player player;
    public long bet;
    public boolean folded;
    public boolean bigBlind;

    /**
     * Initialize a new player turn bet.
     *
     * @param player is the player object
     * @param bigBlind determine if the player has the role of big blind
     * default folded is false
     * default bet is -1 to distinguish those who have not yet bet from those who have bet 0 (check)
     */
    public PlayerBet(Player player, boolean bigBlind) {
        this.player = player;
        this.bet = -1L;
        this.folded = false;
        this.bigBlind = bigBlind;
    }

    public long getBet() {
        return bet;
    }

    /**
     * Says that the player have bet
     */
    public void initializeBet() {
        this.bet = this.bet == -1 ? 0 : this.bet;
    }

    public void setBet(long bet) {
        this.bet = bet;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void addBet(long value) {
        this.bet += value;
    }

    public boolean isFolded() {
        return folded;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }

    public boolean isBigBlind() {
        return bigBlind;
    }
}
