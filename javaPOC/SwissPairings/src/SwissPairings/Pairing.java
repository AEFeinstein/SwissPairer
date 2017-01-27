package SwissPairings;

public class Pairing implements Comparable<Pairing> {

	private Player mPlayers[] = new Player[2];

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
		if(mPlayers[0].isBye() || mPlayers[1].isBye()) {
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
}
