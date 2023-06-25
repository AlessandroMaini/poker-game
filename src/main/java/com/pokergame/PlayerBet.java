package com.pokergame;

/**
 * Associates each player to his turn bet as well as other properties such as: 'Has the player folded?' and 'Is the
 * player big blind?'
 */
public class PlayerBet {
    public Player player;
    public long bet;
    public boolean folded;
    public boolean bigBlind;

    public PlayerBet(Player player, boolean bigBlind) {
        this.player = player;
        this.bet = -1L;
        this.folded = false;
        this.bigBlind = bigBlind;
    }

    public long getBet() {
        return bet;
    }

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
