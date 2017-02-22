package com.gelakinetic.swisspairer.fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gelakinetic.swisspairer.R;
import com.gelakinetic.swisspairer.adapters.PairingListAdapter;
import com.gelakinetic.swisspairer.adapters.PlayerListAdapter;
import com.gelakinetic.swisspairer.algorithm.SwissPairings;

import java.util.Collections;
import java.util.Locale;

/**
 * Created by Adam on 1/27/2017.
 */

public class RoundFragment extends SwissFragment {

    private PlayerListAdapter mStandingsAdapter;
    private PairingListAdapter mPairingsAdapter;
    private ListView mPairingsListView;
    private ScrollView mScrollView;
    private ListView mStandingsListView;

    private int mRound;

    private final View.OnClickListener continueListener = new View.OnClickListener() {
        /**
         * TODO document
         *
         * @param view
         */
        @Override
        public void onClick(View view) {

            /* Now that all matches are reported, commit them to the player objects before starting
             * the next fragment
             */
            mTournament.getRound(mRound).commitAllPairings();
            saveTournamentData(mTournamentFilename); // Commit Pairings, Moving to next round

            // Create a new Fragment to be placed in the activity layout
            RoundFragment nextRoundFragment = new RoundFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            // firstFragment.setArguments(getIntent().getExtras());

            Bundle extras = new Bundle();
            extras.putInt(KEY_ROUND, mRound + 1);
            extras.putString(KEY_JSON_FILENAME, mTournamentFilename);

            nextRoundFragment.setArguments(extras);

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, nextRoundFragment)
                    .commit();
        }
    };

    /**
     * TODO document
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mTournamentFilename = getArguments().getString(KEY_JSON_FILENAME);
        mRound = getArguments().getInt(KEY_ROUND);

        View v = inflater.inflate(R.layout.fragment_round, container, false);

        setupButtons(v, 0, null, R.string.next_round, continueListener);
        setRightButtonVisibility(View.GONE);
        setLeftButtonVisibility(View.GONE);

        /* Get a reference to the scroll view */
        mScrollView = (ScrollView) v.findViewById(R.id.scroll_view);

        /* Set up the standings list */
        mStandingsListView = (ListView) v.findViewById(R.id.standings_list_view);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        View v = getView();

        loadTournamentData(mTournamentFilename);
        if (mTournament.getRounds().size() == mRound) {
            mTournament.addRound();
        }

        mStandingsAdapter = new PlayerListAdapter(getContext(), mTournament.getRound(mRound).getPlayers(), true);
        mStandingsListView.setAdapter(mStandingsAdapter);
        ListUtils.setDynamicHeight(mStandingsListView);

        if (mRound > mTournament.getMaxRounds()) {
            /* This is the tournament results */
            setRightButtonVisibility(View.GONE);
            ((TextView) v.findViewById(R.id.round_title)).setText(R.string.final_results);
            v.findViewById(R.id.pairings_list_view).setVisibility(View.GONE);
            v.findViewById(R.id.pairings_title).setVisibility(View.GONE);
            Collections.sort(mTournament.getRound(mRound).getPlayers());
            mStandingsAdapter.notifyDataSetChanged();
        } else {
            /* This is a regular round */
            ((TextView) v.findViewById(R.id.round_title)).setText(String.format(Locale.getDefault(), getString(R.string.round), mRound));
            /* Set up the pairings list */
            mPairingsListView = (ListView) v.findViewById(R.id.pairings_list_view);
            mPairingsAdapter = new PairingListAdapter(getContext(), mTournament.getRound(mRound).getPairings());
            mPairingsListView.setAdapter(mPairingsAdapter);
            mPairingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    showMatchResultDialog(i);
                }
            });
            ListUtils.setDynamicHeight(mPairingsListView);

            /* If there aren't pairings already, make pairings */
            if (mTournament.getRound(mRound).getPairings().isEmpty()) {
                // Maybe add a progressBar while the pairings are being paired?
                /* Find the pairings in the background */
                AsyncTask pairingTask = new AsyncTask<Object, Void, Void>() {

                    @Override
                    protected Void doInBackground(Object... voids) {
                        if (mRound == 1) {
                            /* This both sorts the players and adds the pairings */
                            mTournament.getRound(mRound).addPairings(SwissPairings.pairRoundOne(mTournament.getRound(mRound).getPlayers(), mTournament.getTeams()));
                        } else {
                            /* Find the pairings */
                            mTournament.getRound(mRound).addPairings(SwissPairings.pairTree(mTournament.getRound(mRound).getPlayers()));
                            /* Sort the players for the standings */
                            Collections.sort(mTournament.getRound(mRound).getPlayers());
                        }

                        /* Save the pairings */
                        saveTournamentData(mTournamentFilename); // Pairings Created

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        /* Update the UI */
                        mStandingsAdapter.notifyDataSetChanged();
                        mPairingsAdapter.notifyDataSetChanged();
                        ListUtils.setDynamicHeight(mPairingsListView);
                        mScrollView.fullScroll(ScrollView.FOCUS_UP);

//                        //TODO just for testing
//                        SwissPairings.randomlyAssignWinners(mTournament.getRound(mRound).getPairings());
//                        mPairingsAdapter.notifyDataSetChanged();
//                        setRightButtonVisibility(View.VISIBLE);
                    }
                };
                AsyncTaskCompat.executeParallel(pairingTask);
            } else {
                if (mTournament.getRound(mRound).allMatchesReported()) {
                    mTournament.getRound(mRound).uncommitAllPairings(mTournament.getRound(mRound).getPlayers());
                    saveTournamentData(mTournamentFilename); // Uncommit Pairings
                    setRightButtonVisibility(View.VISIBLE);
                }
                mStandingsAdapter.notifyDataSetChanged();
                mPairingsAdapter.notifyDataSetChanged();
                ListUtils.setDynamicHeight(mPairingsListView);
                mScrollView.fullScroll(ScrollView.FOCUS_UP);

            }
        }
    }

    /**
     * TODO document
     *
     * @param pairingIdx
     */
    private void showMatchResultDialog(final int pairingIdx) {

        View customView = getLayoutInflater(null).inflate(R.layout.dialog_report_match, null, false);

        ((TextView) customView.findViewById(R.id.player_one_name)).setText(mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerOne().getName());
        ((TextView) customView.findViewById(R.id.player_two_name)).setText(mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerTwo().getName());

        final Spinner playerOneSpinner = ((Spinner) customView.findViewById(R.id.player_one_wins_spinner));
        final Spinner playerTwoSpinner = ((Spinner) customView.findViewById(R.id.player_two_wins_spinner));
        final Spinner drawsSpinner = ((Spinner) customView.findViewById(R.id.draws_spinner));

        playerOneSpinner.setSelection(mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerOneWins());
        playerTwoSpinner.setSelection(mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerTwoWins());
        drawsSpinner.setSelection(mTournament.getRound(mRound).getPairing(pairingIdx).getDraws());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(mTournament.getRound(mRound).getPairing(pairingIdx).getPairingString(getContext()))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /* Get data from the dialog */
                        int playerOneWins = Integer.parseInt((String) playerOneSpinner.getSelectedItem());
                        int playerTwoWins = Integer.parseInt((String) playerTwoSpinner.getSelectedItem());
                        int draws = Integer.parseInt((String) drawsSpinner.getSelectedItem());

                        /* Check to see if there were any changes */
                        boolean changed = false;
                        if (playerOneWins != mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerOneWins() ||
                                playerTwoWins != mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerTwoWins() ||
                                draws != mTournament.getRound(mRound).getPairing(pairingIdx).getDraws()) {
                            changed = true;
                        }

                        /* Report the match */
                        mTournament.getRound(mRound).getPairing(pairingIdx).reportMatch(playerOneWins, playerTwoWins, draws);

                        /* If all matches are reported, show the button to move on */
                        if (mTournament.getRound(mRound).allMatchesReported()) {
                            setRightButtonVisibility(View.VISIBLE);
                        } else {
                            setRightButtonVisibility(View.GONE);
                        }

                        /* If something changed, delete all rounds after this one */
                        if (changed) {
                            for (int round = mTournament.getRounds().size() - 1; round > mRound; round--) {
                                mTournament.getRounds().remove(round);
                            }
                        }

                        /* Save the tournament data */
                        saveTournamentData(mTournamentFilename); // Match Reported

                        /* Update the UI */
                        mPairingsAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setView(customView);

        builder.show();
    }

    public static class ListUtils {
        /**
         * TODO document
         *
         * @param mListView
         */
        static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }
}
