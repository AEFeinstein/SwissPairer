package com.gelakinetic.swisspairer.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 1/28/2017.
 */

public class Round {
    private ArrayList<Player> mPlayers = new ArrayList<>();
    private ArrayList<Pairing> mPairings = new ArrayList<>();

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

    public boolean allMatchesReported() {
        for (Pairing pairing : mPairings) {
            if (!pairing.isReported()) {
                return false;
            }
        }
        return true;
    }

    public void commitAllPairings() {
        for(Pairing pairing : mPairings) {
            pairing.commitMatchesToPlayers();
        }
    }
}
