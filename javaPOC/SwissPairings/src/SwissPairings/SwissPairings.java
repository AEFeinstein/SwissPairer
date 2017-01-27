package SwissPairings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class SwissPairings {

	/**
	 * TODO document
	 *
	 * @param s
	 */
	static void debugStringln(String s) {
		System.out.println(s);
	}

	/**
	 * TODO document
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		for (int j = 0; j < 1; j++) {

			debugStringln("Test " + j);

			String teams[] = {
					"Black",
					" Red "
			};

			Player players[] = {
					new Player("Adam   ", teams[0], false),
					new Player("Bob    ", teams[0], false),
					new Player("Charlie", teams[0], false),
					new Player("Dan    ", teams[0], false),
					new Player("Edward ", teams[0], false),
					new Player("Frank  ", teams[1], false),
					new Player("George ", teams[1], false),
					new Player("Henry  ", teams[1], false),
					new Player("Ira    ", teams[1], false),
					new Player("Jeremy ", teams[1], false),
//					new Player("Bye    ", null, true)
				};

//			Player players[] = {
//					new Player("Adam   ", null),
//					new Player("Bob    ", null),
//					new Player("Charlie", null),
//					new Player("Dan    ", null),
//					new Player("Edward ", null),
//					new Player("Frank  ", null),
//					new Player("George ", null),
//					new Player("Henry  ", null),
//				};

			ArrayList<Pairing> pairingArrayList;

			for (int i = 0; i < 5; i++) {
				debugStringln("Round " + (i + 1));
				debugStringln("");
				Arrays.sort(players);
				printStandings(players);
				debugStringln("");

				pairingArrayList = pairTree(players);
				printPairing(pairingArrayList);
				randomlyAssignWinners(pairingArrayList);

				debugStringln("\n=========================================================\n");
			}
			debugStringln("Final");
			Arrays.sort(players);
			printStandings(players);

			int teamSums[] = new int[teams.length];

			for(int i = 0; i < teams.length; i++) {
				teamSums[i] = 0;
			}

			for(Player player : players) {
				for(int i = 0; i < teams.length; i++) {
					if(player.getTeam().equals(teams[i])) {
						teamSums[i] += player.getPoints();
					}
				}
			}

			debugStringln("");
			debugStringln("Team Totals:");
			for(int i = 0; i < teams.length; i++) {
				debugStringln(teams[i] + ": " + teamSums[i]);
			}
		}
	}

	/*************************
	 * Functions for pairing *
	 *************************/

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
	private static ArrayList<Pairing> pairTree(Player[] players) {

		/* Randomize the player order for pairing */
		shuffle(players);

		/* Make a root node for the search tree */
		ArrayList<Pairing> pairings = new ArrayList<Pairing>();
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
	 *
	 * Eventually there will be no more unpaired players, i.e. it's at the
	 * bottom of the search tree. When it first hit bottom, that path down the
	 * tree is the round's pairings. Because it always pairs starting with the
	 * player with the most points, and picks the pair with the smallest points
	 * delta, the pairings should be optimal by Wizard's rules.
	 *
	 * @param parent The parent node for this node. It knows what players have
	 *               already been paired down this branch.
	 * @param players All of the players in the tournament
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

		/* Find all potential matches for that player */
		ArrayList<Pairing> tmpPairings = new ArrayList<Pairing>();
		for (Player player : players) {
			if (player.canPairAgainst(maxPointPlayer) &&
					!parent.isPaired(player)) {
				tmpPairings.add(new Pairing(maxPointPlayer, player));
			}
		}

		/* Sort the pairings by how closely the player's points match */
		Collections.sort(tmpPairings);

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
			}
			else {
				/* If there are more players to pair, recurse and find the pairs */
				if (true == recursivelyFindPairings(child, players, pairings)) {
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
    public static void shuffle(Object[] a) {
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
	 * TODO document
	 *
	 * @param pairings
	 */
	private static void randomlyAssignWinners(ArrayList<Pairing> pairings) {
		Random rand = new Random();
		for (Pairing pairing : pairings) {

			if (pairing.getPlayerOne().isBye()) {
				pairing.getPlayerOne().addLoss(pairing.getPlayerTwo());
				pairing.getPlayerTwo().addWin(pairing.getPlayerOne());
			}
			else if (pairing.getPlayerTwo().isBye()) {
				pairing.getPlayerOne().addWin(pairing.getPlayerTwo());
				pairing.getPlayerTwo().addLoss(pairing.getPlayerOne());
			}
			else {

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

	/**
	 * TODO document
	 *
	 * @param players
	 */
	private static void printStandings(Player[] players) {
		debugStringln("Standings");
		debugStringln("---------");
		for (Player player : players) {
			debugStringln(player.toString());
		}
	}

	/**
	 * TODO document
	 *
	 * @param pairings
	 */
	private static void printPairing(ArrayList<Pairing> pairings) {
		debugStringln("Pairings");
		debugStringln("--------");
		for (Pairing pairing : pairings) {
			debugStringln(pairing.toString());
		}
	}
}
