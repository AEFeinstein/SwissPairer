package com.gelakinetic.swisspairer.algorithm;

import java.util.ArrayList;

public class PairingTreeNode {

	private Pairing mPairing;
	private ArrayList<Player> mPairedPlayers;
	private ArrayList<PairingTreeNode> mChildren;
	private int mNumPlayers;

	/**
	 * TODO document
	 *
	 * @param parent
	 * @param pairing
	 */
	public PairingTreeNode(PairingTreeNode parent, Pairing pairing) {

		mPairedPlayers = new ArrayList<Player>();
		mChildren = new ArrayList<PairingTreeNode>();

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
	 * @param child
	 */
	public void addPairingChild(PairingTreeNode child) {
		mChildren.add(child);
	}

	/**
	 * TODO document
	 *
	 * @param player
	 * @return
	 */
	public boolean isPaired(Player player) {
		return mPairedPlayers.contains(player);
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
