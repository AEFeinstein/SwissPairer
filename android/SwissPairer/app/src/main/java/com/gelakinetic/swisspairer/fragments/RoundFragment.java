package com.gelakinetic.swisspairer.fragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.gelakinetic.swisspairer.MainActivity;
import com.gelakinetic.swisspairer.R;
import com.gelakinetic.swisspairer.adapters.PairingListAdapter;
import com.gelakinetic.swisspairer.adapters.PlayerListAdapter;
import com.gelakinetic.swisspairer.algorithm.SwissPairings;

import java.util.Collections;

/**
 * Created by Adam on 1/27/2017.
 */

public class RoundFragment extends SwissFragment {

    private PlayerListAdapter mStandingsAdapter;
    private PairingListAdapter mPairingsAdapter;
    private ListView mPairingsListView;
    private ScrollView mScrollView;

    private int mRound;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((MainActivity) getActivity()).hideContinueFab();
        ((MainActivity) getActivity()).hideAddFab();

        loadTournamentData();
        mRound = getArguments().getInt(KEY_ROUND);

        if (mTournament.getRounds().size() == mRound) {
            mTournament.addRound();
        }

        View v = inflater.inflate(R.layout.fragment_round, null);

        /* Get a reference to the scroll view */
        mScrollView = (ScrollView) v.findViewById(R.id.scroll_view);

        /* Set up the standings list */
        ListView standingsListView = (ListView) v.findViewById(R.id.standings_list_view);
        mStandingsAdapter = new PlayerListAdapter(getContext(), mTournament.getRound(mRound).getPlayers(), true);
        standingsListView.setAdapter(mStandingsAdapter);
        ListUtils.setDynamicHeight(standingsListView);

        if (mRound > mTournament.getMaxRounds()) {
            ((MainActivity) getActivity()).hideContinueFab();
            ((MainActivity) getActivity()).setTitle("Final Results");
            v.findViewById(R.id.pairings_list_view).setVisibility(View.GONE);
            v.findViewById(R.id.pairings_title).setVisibility(View.GONE);
            Collections.sort(mTournament.getRound(mRound).getPlayers());
            mStandingsAdapter.notifyDataSetChanged();
        } else {
            ((MainActivity) getActivity()).setTitle("Round " + mRound);
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
                // TODO progress spinner ?
                /* Find the pairings in the background */
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        if (mRound == 1) {
                            /* This both sorts the players and adds the pairings */
                            mTournament.getRound(mRound).addPairings(SwissPairings.pairRoundOne(mTournament.getRound(mRound).getPlayers(), mTournament.getTeams()));
                        } else {
                            /* Find the pairings */
                            mTournament.getRound(mRound).addPairings(SwissPairings.pairTree(mTournament.getRound(mRound).getPlayers()));
                            /* Sort the players for the standings */
                            Collections.sort(mTournament.getRound(mRound).getPlayers());
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        /* Update the UI */
                        mStandingsAdapter.notifyDataSetChanged();
                        mPairingsAdapter.notifyDataSetChanged();
                        ListUtils.setDynamicHeight(mPairingsListView);
                        mScrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                }.execute();
            } else {
                mStandingsAdapter.notifyDataSetChanged();
                mPairingsAdapter.notifyDataSetChanged();
                ListUtils.setDynamicHeight(mPairingsListView);
                mScrollView.fullScroll(ScrollView.FOCUS_UP);

                if(mTournament.getRound(mRound).allMatchesReported()) {
                    ((MainActivity) getActivity()).showContinueFab();
                }
            }
        }
        return v;
    }

    @Override
    public void onContinueFabClick(View view) {

        /* Now that all matches are reported, commit them to the player objects before starting
         * the next fragment
         */
        mTournament.getRound(mRound).commitAllPairings();

        // TODO just for testing
        // TODO problematic, when going backwards to a round, the results are retroactively shown
        // TODO solution, pass W/L/T as a separate object, don't modify Player objects
        //SwissPairings.randomlyAssignWinners(mTournament.getRound(mRound).getPairings());

        // Create a new Fragment to be placed in the activity layout
        RoundFragment nextRoundFragment = new RoundFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        // firstFragment.setArguments(getIntent().getExtras());

        Bundle extras = new Bundle();
        extras.putInt(KEY_ROUND, mRound + 1);

        saveTournamentData();

        nextRoundFragment.setArguments(extras);

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, nextRoundFragment)
                .commit();
    }

    @Override
    public void onAddFabClick(View view) {

    }

    public static class ListUtils {
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

    private void showMatchResultDialog(final int pairingIdx) {

        View customView = getLayoutInflater(null).inflate(R.layout.dialog_report_match, null);

        ((TextView) customView.findViewById(R.id.player_one_name)).setText(mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerOne().getName());
        ((TextView) customView.findViewById(R.id.player_two_name)).setText(mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerTwo().getName());

        final Spinner playerOneSpinner = ((Spinner) customView.findViewById(R.id.player_one_wins_spinner));
        final Spinner playerTwoSpinner = ((Spinner) customView.findViewById(R.id.player_two_wins_spinner));
        final Spinner drawsSpinner = ((Spinner) customView.findViewById(R.id.draws_spinner));

        playerOneSpinner.setSelection(mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerOneWins());
        playerTwoSpinner.setSelection(mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerTwoWins());
        drawsSpinner.setSelection(mTournament.getRound(mRound).getPairing(pairingIdx).getDraws());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(mTournament.getRound(mRound).getPairing(pairingIdx).getPairingString())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int playerOneWins = Integer.parseInt((String) playerOneSpinner.getSelectedItem());
                        int playerTwoWins = Integer.parseInt((String) playerTwoSpinner.getSelectedItem());
                        int draws = Integer.parseInt((String) drawsSpinner.getSelectedItem());

                        mTournament.getRound(mRound).getPairing(pairingIdx).reportMatch(playerOneWins, playerTwoWins, draws);

                        if (mTournament.getRound(mRound).allMatchesReported()) {
                            ((MainActivity) getActivity()).showContinueFab();
                        } else {
                            ((MainActivity) getActivity()).hideContinueFab();
                        }

                        saveTournamentData();

                        mPairingsAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setView(customView);

        builder.show();
    }
}
