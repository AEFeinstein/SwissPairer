package com.gelakinetic.swisspairer.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class SwissPairings {

    /*************************
     * Functions for pairing *
     *************************/

    public static ArrayList<Pairing> pairRoundOne(Player[] players, String[] teams) {
        ArrayList<Pairing> pairings = new ArrayList<>();

        shuffle(players);
        if (teams != null) {
            shuffle(teams);

            int teamIdx = 0;
            /* For all players */
            for (int idx = 0; idx < players.length; idx++) {
                /* If the player's team isn't what it should be */
                if (!players[idx].getTeam().equals(teams[teamIdx])) {
                    /* Look through the remaining players for a correct team */
                    boolean swapped = false;
                    for (int searchIdx = idx + 1; searchIdx < players.length; searchIdx++) {
                        /* Once one is found, swap the two players */
                        if (players[searchIdx].getTeam().equals(teams[teamIdx])) {
                            Player tmp = players[idx];
                            players[idx] = players[searchIdx];
                            players[searchIdx] = tmp;
                            swapped = true;
                            break;
                        }
                    }
                    /* If no swap was found, try the next team in the same index */
                    if (!swapped) {
                        idx--;
                    }
                }
                /* Make sure the next player is from a different team */
                teamIdx = (teamIdx + 1) % teams.length;
            }

            Player copy[] = new Player[players.length];
            System.arraycopy(players, 0, copy, 0, copy.length);

            int toPairInd = 0;
            while (pairings.size() < players.length / 2) {
                for (int i = 0; i < players.length; i++) {
                    int potentialPairInd = (toPairInd + i + players.length / 2) % players.length;
                    if (null != copy[toPairInd] &&
                            null != copy[potentialPairInd] &&
                            copy[toPairInd].canPairAgainst(copy[potentialPairInd])) {
                        pairings.add(new Pairing(players[toPairInd], players[potentialPairInd]));
                        copy[toPairInd] = null;
                        copy[potentialPairInd] = null;
                        break;
                    }
                }
                toPairInd++;
            }


        } else {
            /* No teams, and players are already shuffled, so pair players across the table */
            for (int i = 0; i < players.length / 2; i++) {
                pairings.add(new Pairing(players[i], players[(i + players.length / 2) % players.length]));
            }
        }

        return pairings;
    }

    /**
     * Given a list of players, use Swiss Pairing to pair them off, starting
     * with the player with the most points and working down recursively.
     * This does it's best to pair high ranking players with other high ranking
     * players. It will not pair players on the same team against each other,
     * nor will it pair players that have already played.
     *
     * @param players A list of players to pair
     * @return A list of pairings
     */
    public static ArrayList<Pairing> pairTree(Player[] players) {

		/* Randomize the player order for pairing */
        shuffle(players);

		/* Make a root node for the search tree */
        ArrayList<Pairing> pairings = new ArrayList<>();
        PairingTreeNode root = new PairingTreeNode(null, null);
        root.setNumPlayers(players.length);

		/* Recursively search for the best pairing */
        recursivelyFindPairings(root, players, pairings);

		/* Return the pairing */
        return pairings;
    }

    /**
     * For a PairingTreeNode, find all possible child pairs for the player
     * with the most points who is unpaired. Then, if there are still unpaired
     * players, continue recursively finding pairs for that PairingTreeNode.
     * <p>
     * Eventually there will be no more unpaired players, i.e. it's at the
     * bottom of the search tree. When it first hit bottom, that path down the
     * tree is the round's pairings. Because it always pairs starting with the
     * player with the most points, and picks the pair with the smallest points
     * delta, the pairings should be optimal by Wizard's rules.
     *
     * @param parent   The parent node for this node. It knows what players have
     *                 already been paired down this branch.
     * @param players  All of the players in the tournament
     * @param pairings The pairings for the round are placed in this ArrayList
     *                 for returning once they are found
     * @return true if the search is over, false if it must continue
     */
    private static boolean recursivelyFindPairings(PairingTreeNode parent,
                                                   Player[] players, ArrayList<Pairing> pairings) {

		/* Find the unpaired player with the most points */
        int maxPoints = -1;
        Player maxPointPlayer = null;
        for (Player player : players) {
            if (player.getPoints() > maxPoints && !parent.isPaired(player) &&
                    !player.isBye()) {
                maxPointPlayer = player;
                maxPoints = player.getPoints();
            }
        }

		/* Something failed horribly */
        if (maxPointPlayer == null) {
            return false;
        }

		/* Find all potential matches for that player */
        ArrayList<Pairing> tmpPairings = new ArrayList<>();
        for (Player player : players) {
            if (player.canPairAgainst(maxPointPlayer) &&
                    !parent.isPaired(player)) {
                tmpPairings.add(new Pairing(maxPointPlayer, player));
            }
        }

		/* Something failed horribly */
        switch (tmpPairings.size()) {
            case 0:
                /* No pairings, so stop searching */
                return false;
            case 1:
                /* One pairing, no need to sort */
                break;
            default:
                /* Sort the pairings by how closely the player's points match */
                Collections.sort(tmpPairings);
        }

		/* Add all pairings to the search tree */
        for (Pairing pairing : tmpPairings) {

			/* Add the pair to the search tree */
            PairingTreeNode child = new PairingTreeNode(parent, pairing);
            parent.addPairingChild(child);

			/* Check if the search can continue */
            if (!child.canHaveChildren()) {
                /* There are no more players to pair, so we're done
                 * Start exiting from the recursion, adding to the list of
				 * pairs at each level
				 */
                pairings.add(0, child.getPairing());
                return true;
            } else {
                /* If there are more players to pair, recurse and find the pairs */
                if (recursivelyFindPairings(child, players, pairings)) {
					/* Add this pair to the pairings and keep exiting from
					 * the recursion
					 */
                    pairings.add(0, child.getPairing());
                    return true;
                }
            }
        }

		/* This branch of the search can't pair all players, so return false */
        return false;
    }

    /**
     * Rearranges an array of objects in uniformly random order
     * (under the assumption that {@code Math.random()} generates independent
     * and uniformly distributed numbers between 0 and 1).
     *
     * @param a the array to be shuffled
     */
    private static void shuffle(Object[] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            /* choose index uniformly in [i, n-1] */
            int r = i + (int) (Math.random() * (n - i));
            Object swap = a[r];
            a[r] = a[i];
            a[i] = swap;
        }
    }

    /*************************
     * Functions for testing *
     *************************/

    /**
     * This simulates a round where each pairing is assigned either a winner/loser, or a draw
     *
     * @param pairings The pairings to simulate a round for
     */
    public static void randomlyAssignWinners(ArrayList<Pairing> pairings) {
        Random rand = new Random();
        for (Pairing pairing : pairings) {

            if (pairing.getPlayerOne().isBye()) {
                pairing.getPlayerOne().addLoss(pairing.getPlayerTwo());
                pairing.getPlayerTwo().addWin(pairing.getPlayerOne());
            } else if (pairing.getPlayerTwo().isBye()) {
                pairing.getPlayerOne().addWin(pairing.getPlayerTwo());
                pairing.getPlayerTwo().addLoss(pairing.getPlayerOne());
            } else {

                switch (rand.nextInt(3)) {
                    case 0:
                        pairing.getPlayerOne().addWin(pairing.getPlayerTwo());
                        pairing.getPlayerTwo().addLoss(pairing.getPlayerOne());
                        break;
                    case 1:
                        pairing.getPlayerOne().addLoss(pairing.getPlayerTwo());
                        pairing.getPlayerTwo().addWin(pairing.getPlayerOne());
                        break;
                    case 2:
                        pairing.getPlayerOne().addDraw(pairing.getPlayerTwo());
                        pairing.getPlayerTwo().addDraw(pairing.getPlayerOne());
                        break;
                }
            }
        }
    }
}