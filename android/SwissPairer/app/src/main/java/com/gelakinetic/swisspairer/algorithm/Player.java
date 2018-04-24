package com.gelakinetic.swisspairer.algorithm;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class Player implements Comparable<Player>, Serializable {

    private final long mUuid;

    private String mName;
    private String mTeam;

    private int mWins = 0;
    private int mLosses = 0;
    private int mDraws = 0;

    private final ArrayList<Long> mPlayedAgainst;
    private final boolean mIsBye;

    /**
     * TODO document
     *
     * @param name
     * @param team
     */
    public Player(String name, String team, boolean isBye) {
        this.mUuid = UUID.randomUUID().getMostSignificantBits();

        this.mName = name;
        this.mTeam = team;
        this.mPlayedAgainst = new ArrayList<>();
        this.mIsBye = isBye;
    }

    /**
     * TODO document
     *
     * @param player
     */
    public Player(Player player) {
        this.mUuid = player.mUuid;

        mName = player.mName;
        mTeam = player.mTeam;

        mWins = player.mWins;
        mLosses = player.mLosses;
        mDraws = player.mDraws;

        this.mPlayedAgainst = new ArrayList<>();
        mPlayedAgainst.addAll(player.mPlayedAgainst);

        mIsBye = player.mIsBye;
    }

    /**
     * TODO document
     *
     * @param other
     */
    public void addWin(Player other) {
        mWins++;
        if (!mPlayedAgainst.contains(other.mUuid)) {
            mPlayedAgainst.add(other.mUuid);
        }
    }

    /**
     * TODO document
     *
     * @param other
     */
    public void addDraw(Player other) {
        mDraws++;
        if (!mPlayedAgainst.contains(other.mUuid)) {
            mPlayedAgainst.add(other.mUuid);
        }
    }

    /**
     * TODO document
     *
     * @param other
     */
    public void addLoss(Player other) {
        mLosses++;
        if (!mPlayedAgainst.contains(other.mUuid)) {
            mPlayedAgainst.add(other.mUuid);
        }
    }

    /**
     * TODO document
     *
     * @param other
     */
    public void removeWin(Player other) {
        mWins--;
        mPlayedAgainst.remove(other.mUuid);
    }

    /**
     * TODO document
     *
     * @param other
     */
    public void removeDraw(Player other) {
        mDraws--;
        mPlayedAgainst.remove(other.mUuid);
    }

    /**
     * TODO document
     *
     * @param other
     */
    public void removeLoss(Player other) {
        mLosses--;
        mPlayedAgainst.remove(other.mUuid);
    }

    /**
     * TODO document
     *
     * @return
     */
    public int getPoints() {
        return (mWins * 3) + (mDraws);
    }

    /**
     * TODO document
     *
     * @param other
     * @return
     */
    public boolean canPairAgainst(Player other) {
        return (this.mTeam == null || other.mTeam == null || !this.mTeam.equals(other.mTeam)) &&
                !mPlayedAgainst.contains(other.mUuid) &&
                !this.equals(other);
    }

    /**
     * TODO document
     *
     * @return
     */
    public boolean isBye() {
        return mIsBye;
    }

    /**
     * TODO document
     *
     * @return
     */
    public String getTeam() {
        return mTeam;
    }

    /**
     * TODO document
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(@NonNull Player other) {
        return other.getPoints() - getPoints();
    }

    /**
     * TODO document
     *
     * @return
     */
    public String getName() {
        return mName;
    }

    /**
     * TODO document
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {

        /* Make sure it's a nonnull player */
        if ((obj == null) || !(obj instanceof Player)) {
            return false;
        }

        String thatTeam = ((Player) obj).getTeam();
        return ((thatTeam == null && getTeam() == null) || thatTeam != null && thatTeam.equals(getTeam())) &&
                ((Player) obj).getName().equals(getName());
    }

    /**
     * TODO document
     *
     * @param name
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * TODO document
     *
     * @param team
     */
    public void setTeam(String team) {
        this.mTeam = team;
    }

    /**
     * TODO document
     *
     * @return
     */
    public String getRecordString() {
        return String.format(Locale.US, "%d-%d-%d (%d)", mWins, mLosses, mDraws, getPoints());
    }

    /**
     * TODO document
     *
     * @return
     */
    public String getPairingString() {
        return getName();
    }

}
