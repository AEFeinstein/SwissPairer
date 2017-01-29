package com.gelakinetic.swisspairer.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 1/28/2017.
 */

public class Round {
    private final ArrayList<Player> mPlayers = new ArrayList<>();
    private final ArrayList<Pairing> mPairings = new ArrayList<>();
    private boolean mPairingsCommitted = false;

    /**
     * TODO document
     *
     * @param player
     */
    public void addPlayer(Player player) {
        mPlayers.add(player);
    }

    /**
     * TODO document
     *
     * @param playerIdx
     * @return
     */
    public Player getPlayer(int playerIdx) {
        return mPlayers.get(playerIdx);
    }

    /**
     * TODO document
     *
     * @param playerIdx
     */
    public void removePlayer(int playerIdx) {
        mPlayers.remove(playerIdx);
    }

    /**
     * TODO document
     *
     * @param player
     */
    public void removePlayer(Player player) {
        mPlayers.remove(player);
    }

    /**
     * TODO document
     *
     * @param player
     * @return
     */
    public boolean containsPlayer(Player player) {
        return mPlayers.contains(player);
    }

    /**
     * TODO document
     *
     * @return
     */
    public int getPlayersSize() {
        return mPlayers.size();
    }


    /**
     * TODO document
     *
     * @param pairingIdx
     * @return
     */
    public Pairing getPairing(int pairingIdx) {
        return mPairings.get(pairingIdx);
    }

    /**
     * TODO document
     *
     * @param pairings
     */
    public void addPairings(ArrayList<Pairing> pairings) {
        mPairings.addAll(pairings);
    }

    /**
     * TODO document
     *
     * @return
     */
    public ArrayList<Player> getPlayers() {
        return mPlayers;
    }

    /**
     * TODO document
     *
     * @return
     */
    public List<Pairing> getPairings() {
        return mPairings;
    }

    /**
     * TODO document
     *
     * @return
     */
    public boolean allMatchesReported() {
        for (Pairing pairing : mPairings) {
            if (!pairing.isReported()) {
                return false;
            }
        }
        return true;
    }

    /**
     * TODO document
     */
    public void commitAllPairings() {
        if (!mPairingsCommitted) {
            for (Pairing pairing : mPairings) {
                pairing.commitMatchesToPlayers();
            }
            mPairingsCommitted = true;
        }
    }

    /**
     * TODO document
     *
     * @param players
     */
    public void uncommitAllPairings(ArrayList<Player> players) {
        if (mPairingsCommitted) {
            for (Pairing pairing : mPairings) {
                pairing.uncommitMatchesToPlayers();

                /* Replace old player objects with new ones */
                players.remove(pairing.getPlayerOne());
                players.add(pairing.getPlayerOne());
                players.remove(pairing.getPlayerTwo());
                players.add(pairing.getPlayerTwo());
            }
            mPairingsCommitted = false;
        }
    }
}
