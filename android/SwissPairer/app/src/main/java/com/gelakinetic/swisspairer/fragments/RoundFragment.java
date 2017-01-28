package com.gelakinetic.swisspairer.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gelakinetic.swisspairer.MainActivity;
import com.gelakinetic.swisspairer.R;
import com.gelakinetic.swisspairer.adapters.PairingListAdapter;
import com.gelakinetic.swisspairer.adapters.PlayerListAdapter;
import com.gelakinetic.swisspairer.algorithm.Pairing;
import com.gelakinetic.swisspairer.algorithm.Player;
import com.gelakinetic.swisspairer.algorithm.SwissPairings;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Adam on 1/27/2017.
 */

public class RoundFragment extends SwissFragment {

    private int mRound;
    private Player mPlayers[];
    private ArrayList<Pairing> mPairings = new ArrayList<>();
    private PlayerListAdapter mStandingsAdapter;
    private PairingListAdapter mPairingsAdapter;
    private ListView mPairingsListView;
    private ScrollView mScrollView;
    private boolean mPairingsCalculated = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((MainActivity)getActivity()).showContinueFab();
        ((MainActivity)getActivity()).hideAddFab();

        View v = inflater.inflate(R.layout.fragment_round, null);

        /* Get a reference to the scroll view */
        mScrollView = (ScrollView) v.findViewById(R.id.scroll_view);

        /* Make a local copy of the player objects */
        Player lastRound[] = (Player[]) getArguments().getSerializable(KEY_PLAYERS);
        mPlayers = new Player[lastRound.length];
        for (int i = 0; i < lastRound.length; i++) {
            mPlayers[i] = new Player(lastRound[i]);
        }

        /* Figure out what round this is, and display it */
        mRound = getArguments().getInt(KEY_ROUND);
        ((TextView) v.findViewById(R.id.round_title)).setText("Round " + mRound);

        /* Set up the standings list */
        ListView standingsListView = (ListView) v.findViewById(R.id.standings_list_view);
        mStandingsAdapter = new PlayerListAdapter(getContext(), mPlayers, true);
        standingsListView.setAdapter(mStandingsAdapter);
        ListUtils.setDynamicHeight(standingsListView);

        /* Set up the pairings list */
        mPairingsListView = (ListView) v.findViewById(R.id.pairings_list_view);
        mPairingsAdapter = new PairingListAdapter(getContext(), mPairings);
        mPairingsListView.setAdapter(mPairingsAdapter);
        mPairingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO fill in
                Toast.makeText(getContext(), "Submit round result", Toast.LENGTH_SHORT).show();
            }
        });
        ListUtils.setDynamicHeight(mPairingsListView);

        /* If there aren't pairings already, make pairings */
        if (!mPairingsCalculated) {
            mPairingsCalculated = true;
            // TODO progress spinner ?
            /* Find the pairings in the background */
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    /* Find the pairings */
                    mPairings.addAll(SwissPairings.pairTree(mPlayers));
                    /* Sort the players for the standings */
                    Arrays.sort(mPlayers);
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
        }

        return v;
    }

    @Override
    public void onContinueFabClick(View view) {

        // TODO just for testing
        // TODO problematic, when going backwards to a round, the results are retroactively shown
        // TODO solution, pass W/L/T as a separate object, don't modify Player objects
        SwissPairings.randomlyAssignWinners(mPairings);

        // Create a new Fragment to be placed in the activity layout
        RoundFragment firstFragment = new RoundFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        // firstFragment.setArguments(getIntent().getExtras());

        Bundle extras = new Bundle();
        extras.putSerializable(KEY_PLAYERS, mPlayers);
        extras.putInt(KEY_ROUND, mRound + 1);
        firstFragment.setArguments(extras);

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, firstFragment)
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
}
