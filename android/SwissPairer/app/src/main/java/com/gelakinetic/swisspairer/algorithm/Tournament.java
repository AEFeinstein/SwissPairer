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

    public List<String> getTeams() {
        return mTeams;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setMaxRounds(int maxRounds) {
        this.mMaxRounds = maxRounds;
    }

    public void setDate(long date) {
        this.mDate = date;
    }

    public Round getRound(int round) {
        return mRounds.get(round);
    }

    public void addRound() {
        Round newRound = new Round();
        if(!mRounds.isEmpty()) {
            Round lastRound = mRounds.get(mRounds.size() - 1);
            for(Player player : lastRound.getPlayers()) {
                newRound.addPlayer(new Player(player));
            }
        }
        mRounds.add(newRound);
    }

    public ArrayList<Round> getRounds() {
        return mRounds;
    }

    public int getMaxRounds() {
        return mMaxRounds;
    }
}
