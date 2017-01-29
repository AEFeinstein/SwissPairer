package com.gelakinetic.swisspairer.algorithm;

public class Pairing implements Comparable<Pairing> {

    private Player mPlayers[] = new Player[2];
    private boolean mReported = false;
    private int mPlayerOneWins;
    private int mPlayerTwoWins;
    private int mDraws;

    /**
     * TODO document
     *
     * @param player0
     * @param player1
     */
    public Pairing(Player player0, Player player1) {
        mPlayers[0] = player0;
        mPlayers[1] = player1;
    }

    /**
     * TODO document
     */
    public Player getPlayerOne() {
        return mPlayers[0];
    }

    /**
     * TODO document
     */
    public Player getPlayerTwo() {
        return mPlayers[1];
    }

    /**
     * TODO document
     */
    @Override
    public String toString() {
        return mPlayers[0] + " vs. " + mPlayers[1];
    }

    /**
     * TODO document
     *
     * @return
     */
    public int getDelta() {
        if (mPlayers[0].isBye() || mPlayers[1].isBye()) {
            return Integer.MAX_VALUE;
        }
        return Math.abs(mPlayers[0].getPoints() - mPlayers[1].getPoints());
    }

    /**
     * TODO document
     */
    @Override
    public int compareTo(Pairing o) {
        return getDelta() - o.getDelta();
    }

    public String getPairingString() {
        return getPlayerOne().getName() + " vs. " + getPlayerTwo().getName();
    }

    public boolean isReported() {
        return mReported;
    }

    public void reportMatch(int playerOneWins, int playerTwoWins, int draws) {

        mPlayerOneWins = playerOneWins;
        mPlayerTwoWins = playerTwoWins;
        mDraws = draws;

        if (playerOneWins > 0 || playerTwoWins > 0 || draws > 0) {
            mReported = true;
        } else {
            mReported = false;
        }

    }

    public void commitMatchesToPlayers() {
        if (mPlayerOneWins > mPlayerTwoWins) {
            getPlayerOne().addWin(getPlayerTwo());
            getPlayerTwo().addLoss(getPlayerOne());
        } else if (mPlayerOneWins < mPlayerTwoWins) {
            getPlayerOne().addLoss(getPlayerTwo());
            getPlayerTwo().addWin(getPlayerOne());
        } else {
            getPlayerOne().addDraw(getPlayerTwo());
            getPlayerTwo().addDraw(getPlayerOne());
        }
    }

    // TODO call this when going backwards
    public void uncommitMatchesToPlayers() {
        if (mPlayerOneWins > mPlayerTwoWins) {
            getPlayerOne().removeWin(getPlayerTwo());
            getPlayerTwo().removeLoss(getPlayerOne());
        } else if (mPlayerOneWins < mPlayerTwoWins) {
            getPlayerOne().removeLoss(getPlayerTwo());
            getPlayerTwo().removeWin(getPlayerOne());
        } else {
            getPlayerOne().removeDraw(getPlayerTwo());
            getPlayerTwo().removeDraw(getPlayerOne());
        }
    }

    public boolean playerOneWon() {
        return mReported && mPlayerOneWins > mPlayerTwoWins;
    }

    public boolean playerTwoWon() {
        return mReported && mPlayerOneWins < mPlayerTwoWins;
    }

    public int getPlayerOneWins() {
        return mPlayerOneWins;
    }

    public int getPlayerTwoWins() {
        return mPlayerTwoWins;
    }

    public int getDraws() {
        return mDraws;
    }
}
