package com.pokergame;

public class PlayerBet {
    public Player player;
    public long bet;
    public boolean folded;

    public PlayerBet(Player player) {
        this.player = player;
        this.bet = -1L;
        this.folded = false;
    }

    public long getBet() {
        return bet;
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
}
