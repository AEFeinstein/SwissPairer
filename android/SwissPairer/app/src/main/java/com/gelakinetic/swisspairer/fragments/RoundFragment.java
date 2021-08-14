package com.gelakinetic.swisspairer.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;

import com.gelakinetic.swisspairer.R;
import com.gelakinetic.swisspairer.adapters.PairingListAdapter;
import com.gelakinetic.swisspairer.adapters.PlayerListAdapter;
import com.gelakinetic.swisspairer.algorithm.SwissPairings;

import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Adam on 1/27/2017.
 */

public class RoundFragment extends SwissFragment {

    private PlayerListAdapter mStandingsAdapter;
    private PairingListAdapter mPairingsAdapter;
    private ListView mPairingsListView;
    private NestedScrollView mScrollView;
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
            getParentFragmentManager()
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mTournamentFilename = requireArguments().getString(KEY_JSON_FILENAME);
        assert getArguments() != null;
        mRound = getArguments().getInt(KEY_ROUND);

        View v = inflater.inflate(R.layout.fragment_round, container, false);

        setupButtons(v, 0, null, R.string.next_round, continueListener);
        setRightButtonVisibility(View.GONE);
        setLeftButtonVisibility(View.GONE);

        /* Get a reference to the scroll view */
        mScrollView = v.findViewById(R.id.scroll_view);

        /* Set up the standings list */
        mStandingsListView = v.findViewById(R.id.standings_list_view);

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
            ((TextView) Objects.requireNonNull(v).findViewById(R.id.round_title)).setText(R.string.final_results);
            v.findViewById(R.id.pairings_list_view).setVisibility(View.GONE);
            v.findViewById(R.id.pairings_title).setVisibility(View.GONE);
            Collections.sort(mTournament.getRound(mRound).getPlayers());
            mStandingsAdapter.notifyDataSetChanged();
        } else {
            /* This is a regular round */
            ((TextView) Objects.requireNonNull(v).findViewById(R.id.round_title)).setText(String.format(Locale.getDefault(), getString(R.string.round), mRound));
            /* Set up the pairings list */
            mPairingsListView = v.findViewById(R.id.pairings_list_view);
            mPairingsAdapter = new PairingListAdapter(getContext(), mTournament.getRound(mRound).getPairings());
            mPairingsListView.setAdapter(mPairingsAdapter);
            mPairingsListView.setOnItemClickListener((adapterView, view, i, l) -> showMatchResultDialog(i));
            ListUtils.setDynamicHeight(mPairingsListView);

            /* If there aren't pairings already, make pairings */
            if (mTournament.getRound(mRound).getPairings().isEmpty()) {
                // Maybe add a progressBar while the pairings are being paired?
                /* Find the pairings in the background */
                new PairingAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this);
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

        View customView = ((LayoutInflater) (Objects.requireNonNull(requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)))).inflate(R.layout.dialog_report_match, null, false);

        ((TextView) customView.findViewById(R.id.player_one_name)).setText(mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerOne().getName());
        ((TextView) customView.findViewById(R.id.player_two_name)).setText(mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerTwo().getName());

        final Spinner playerOneSpinner = customView.findViewById(R.id.player_one_wins_spinner);
        final Spinner playerTwoSpinner = customView.findViewById(R.id.player_two_wins_spinner);
        final Spinner drawsSpinner = customView.findViewById(R.id.draws_spinner);

        playerOneSpinner.setSelection(mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerOneWins());
        playerTwoSpinner.setSelection(mTournament.getRound(mRound).getPairing(pairingIdx).getPlayerTwoWins());
        drawsSpinner.setSelection(mTournament.getRound(mRound).getPairing(pairingIdx).getDraws());

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        builder.setTitle(mTournament.getRound(mRound).getPairing(pairingIdx).getPairingString(requireContext()))
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
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
                        if (mTournament.getRounds().size() > mRound + 1) {
                            mTournament.getRounds().subList(mRound + 1, mTournament.getRounds().size()).clear();
                        }
                    }

                    /* Save the tournament data */
                    saveTournamentData(mTournamentFilename); // Match Reported

                    /* Update the UI */
                    mPairingsAdapter.notifyDataSetChanged();
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {

                })
                .setView(customView);

        builder.show();
    }

    static class ListUtils {
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

    static class PairingAsyncTask extends AsyncTask<RoundFragment, Void, RoundFragment> {

        @Override
        protected RoundFragment doInBackground(RoundFragment... roundFragments) {
            RoundFragment roundFragment = roundFragments[0];
            if (roundFragment.mRound == 1) {
                /* This both sorts the players and adds the pairings */
                roundFragment.mTournament.getRound(roundFragment.mRound)
                        .addPairings(SwissPairings.pairRoundOne(
                                roundFragment.mTournament.getRound(roundFragment.mRound).getPlayers(),
                                roundFragment.mTournament.getTeams()));
            } else {
                /* Find the pairings */
                roundFragment.mTournament.getRound(roundFragment.mRound)
                        .addPairings(SwissPairings.pairTree(
                                roundFragment.mTournament.getRound(roundFragment.mRound).getPlayers()));
                /* Sort the players for the standings */
                Collections.sort(roundFragment.mTournament.getRound(roundFragment.mRound).getPlayers());
            }

            /* Save the pairings */
            roundFragment.saveTournamentData(roundFragment.mTournamentFilename); // Pairings Created

            return roundFragment;
        }

        @SuppressWarnings("CommentedOutCode")
        @Override
        protected void onPostExecute(RoundFragment roundFragment) {
            /* Update the UI */
            roundFragment.mStandingsAdapter.notifyDataSetChanged();
            roundFragment.mPairingsAdapter.notifyDataSetChanged();
            ListUtils.setDynamicHeight(roundFragment.mPairingsListView);
            roundFragment.mScrollView.fullScroll(ScrollView.FOCUS_UP);

//          //TODO just for testing
//          SwissPairings.randomlyAssignWinners(mTournament.getRound(mRound).getPairings());
//          mPairingsAdapter.notifyDataSetChanged();
//          setRightButtonVisibility(View.VISIBLE);
        }

    }
}
