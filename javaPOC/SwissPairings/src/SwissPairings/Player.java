package SwissPairings;

import java.util.ArrayList;

public class Player implements Comparable<Player> {

	private String mName;
	private String mTeam;

	private int mWins = 0;
	private int mLosses = 0;
	private int mDraws = 0;

	private ArrayList<Player> mPlayedAgainst;
	private boolean mIsBye;

	/**
	 * TODO document
	 *
	 * @param name
	 * @param team
	 */
	public Player(String name, String team, boolean isBye) {
		this.mName = name;
		this.mTeam = team;
		this.mPlayedAgainst = new ArrayList<Player>();
		this.mIsBye = isBye;
	}

	/**
	 * TODO document
	 *
	 * @param other
	 */
	public void addWin(Player other) {
		mWins++;
		if (!mPlayedAgainst.contains(other)) {
			mPlayedAgainst.add(other);
		}
	}

	/**
	 * TODO document
	 *
	 * @param other
	 */
	public void addDraw(Player other) {
		mDraws++;
		if (!mPlayedAgainst.contains(other)) {
			mPlayedAgainst.add(other);
		}
	}

	/**
	 * TODO document
	 *
	 * @param other
	 */
	public void addLoss(Player other) {
		mLosses++;
		if (!mPlayedAgainst.contains(other)) {
			mPlayedAgainst.add(other);
		}
	}

	/**
	 * TODO document
	 *
	 * @return
	 */
	public int getPoints() {
		return (mWins * 3) + (mDraws * 1);
	}

	/**
	 * TODO document
	 *
	 * @param other
	 * @return
	 */
	public boolean canPairAgainst(Player other) {
		return (this.mTeam == null || other.mTeam == null || !this.mTeam.equals(other.mTeam)) &&
				!mPlayedAgainst.contains(other) &&
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
	public int compareTo(Player other) {
		return other.getPoints() - getPoints();
	}

	/**
	 * TODO document
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return String.format("%s [%s] %d-%d-%d (%2d)", mName, mTeam, mWins, mLosses, mDraws, getPoints());
	}
}
