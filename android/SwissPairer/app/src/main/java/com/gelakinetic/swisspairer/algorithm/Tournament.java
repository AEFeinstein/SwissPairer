package com.gelakinetic.swisspairer.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 1/28/2017.
 */

public class Tournament {
    private String mName;
    private long mDate;
    private int mMaxRounds;
    private ArrayList<String> mTeams = new ArrayList<>();
    private ArrayList<Round> mRounds = new ArrayList<>();

    /**
     * TODO document
     *
     * @return
     */
    public List<String> getTeams() {
        return mTeams;
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
     * @param maxRounds
     */
    public void setMaxRounds(int maxRounds) {
        this.mMaxRounds = maxRounds;
    }

    /**
     * TODO document
     *
     * @param date
     */
    public void setDate(long date) {
        this.mDate = date;
    }

    /**
     * TODO document
     *
     * @param round
     * @return
     */
    public Round getRound(int round) {
        return mRounds.get(round);
    }

    /**
     * TODO document
     */
    public void addRound() {
        Round newRound = new Round();
        if (!mRounds.isEmpty()) {
            Round lastRound = mRounds.get(mRounds.size() - 1);
            for (Player player : lastRound.getPlayers()) {
                newRound.addPlayer(new Player(player));
            }
        }
        mRounds.add(newRound);
    }

    /**
     * TODO document
     *
     * @return
     */
    public ArrayList<Round> getRounds() {
        return mRounds;
    }

    /**
     * TODO document
     *
     * @return
     */
    public int getMaxRounds() {
        return mMaxRounds;
    }

    /**
     * TODO document
     *
     * @return
     */
    public String getName() {
        return mName;
    }
}
