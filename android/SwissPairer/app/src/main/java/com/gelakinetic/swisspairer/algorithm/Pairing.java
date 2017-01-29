package com.gelakinetic.swisspairer.algorithm;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gelakinetic.swisspairer.R;

import java.util.Locale;

public class Pairing implements Comparable<Pairing> {

    private final Player[] mPlayers = new Player[2];
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
    Pairing(Player player0, Player player1) {
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
     *
     * @return
     */
    private int getDelta() {
        if (mPlayers[0].isBye() || mPlayers[1].isBye()) {
            return Integer.MAX_VALUE;
        }
        return Math.abs(mPlayers[0].getPoints() - mPlayers[1].getPoints());
    }

    /**
     * TODO document
     */
    @Override
    public int compareTo(@NonNull Pairing o) {
        return getDelta() - o.getDelta();
    }

    /**
     * TODO document
     *
     * @return
     */
    public String getPairingString(Context context) {
        return String.format(Locale.getDefault(), context.getString(R.string.vs), getPlayerOne().getName(), getPlayerTwo().getName());
    }

    /**
     * TODO document
     *
     * @return
     */
    public boolean isReported() {
        return mReported;
    }

    /**
     * TODO document
     *
     * @param playerOneWins
     * @param playerTwoWins
     * @param draws
     */
    public void reportMatch(int playerOneWins, int playerTwoWins, int draws) {

        mPlayerOneWins = playerOneWins;
        mPlayerTwoWins = playerTwoWins;
        mDraws = draws;

        mReported = playerOneWins > 0 || playerTwoWins > 0 || draws > 0;

    }

    /**
     * TODO document
     */
    void commitMatchesToPlayers() {
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

    /**
     * TODO document
     */
    void uncommitMatchesToPlayers() {
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

    /**
     * TODO document
     *
     * @return
     */
    public boolean playerOneWon() {
        return mReported && mPlayerOneWins > mPlayerTwoWins;
    }

    /**
     * TODO document
     *
     * @return
     */
    public boolean playerTwoWon() {
        return mReported && mPlayerOneWins < mPlayerTwoWins;
    }

    /**
     * TODO document
     *
     * @return
     */
    public int getPlayerOneWins() {
        return mPlayerOneWins;
    }

    /**
     * TODO document
     *
     * @return
     */
    public int getPlayerTwoWins() {
        return mPlayerTwoWins;
    }

    /**
     * TODO document
     *
     * @return
     */
    public int getDraws() {
        return mDraws;
    }
}
