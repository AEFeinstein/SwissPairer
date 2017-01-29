package com.gelakinetic.swisspairer.algorithm;

import java.util.ArrayList;

class PairingTreeNode {

	private Pairing mPairing;
	private final ArrayList<Player> mPairedPlayers;
	private int mNumPlayers;

	/**
	 * TODO document
	 *
	 * @param parent
	 * @param pairing
	 */
	public PairingTreeNode(PairingTreeNode parent, Pairing pairing) {

		mPairedPlayers = new ArrayList<>();

		if (pairing != null) {
			mPairing = pairing;
			mPairedPlayers.add(pairing.getPlayerOne());
			mPairedPlayers.add(pairing.getPlayerTwo());
		}

		/* Link the parent and copy in the parent's paired players */
		if (parent != null) {
			mNumPlayers = parent.mNumPlayers;
			mPairedPlayers.addAll(parent.mPairedPlayers);
		}
	}

	/**
	 * TODO document
	 *
	 * @param player
	 * @return
	 */
	public boolean isNotPaired(Player player) {
		return !mPairedPlayers.contains(player);
	}

	/**
	 * TODO document
	 *
	 * @return
	 */
	public boolean canHaveChildren() {
		return mPairedPlayers.size() != mNumPlayers;
	}

	/**
	 * TODO document
	 *
	 * @param length
	 */
	public void setNumPlayers(int length) {
		mNumPlayers = length;
	}

	/**
	 * TODO document
	 *
	 * @return
	 */
	public Pairing getPairing() {
		return mPairing;
	}
}
