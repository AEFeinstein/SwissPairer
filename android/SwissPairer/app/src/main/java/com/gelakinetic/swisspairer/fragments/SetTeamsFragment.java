package com.gelakinetic.swisspairer.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.gelakinetic.swisspairer.MainActivity;
import com.gelakinetic.swisspairer.R;
import com.gelakinetic.swisspairer.adapters.TeamListAdapter;

import java.util.ArrayList;

/**
 * Created by Adam on 1/27/2017.
 */

public class SetTeamsFragment extends SwissFragment {

    ArrayList<String> mTeams = new ArrayList<>();
    private TeamListAdapter mTeamsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((MainActivity)getActivity()).showContinueFab();
        ((MainActivity)getActivity()).showAddFab();

        View view = inflater.inflate(R.layout.fragment_set_teams, null);

        // TODO hide teams until one is added?
        mTeamsAdapter = new TeamListAdapter(getContext(), mTeams);
        ListView listViewTeams = (ListView) view.findViewById(R.id.team_list);
        listViewTeams.setAdapter(mTeamsAdapter);
        listViewTeams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showAddTeamDialog(i);
            }
        });

        // TODO just for testing
        if(mTeams.isEmpty()) {
            mTeams.add("Red");
            mTeams.add("Blk");
        }

        return view;
    }

    // TODO edit team rather than make a new one
    private void showAddTeamDialog(final int teamIdx) {

        View customView = getLayoutInflater(null).inflate(R.layout.dialog_add_team, null);

        final EditText teamNameEditText = (EditText) customView.findViewById(R.id.team_name_edit_text);

        if (teamIdx >= 0) {
            teamNameEditText.setText(mTeams.get(teamIdx));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Add a Team")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String newTeamName = teamNameEditText.getText().toString();

                        if (!newTeamName.isEmpty()) {
                            if (teamIdx >= 0) {
                                for (int teamsIdx = 0; teamsIdx < mTeams.size(); teamsIdx++) {
                                    if (teamsIdx != teamIdx && mTeams.get(teamsIdx).equals(newTeamName)) {
                                        return; // duplicate
                                    }
                                }
                                mTeams.set(teamIdx, newTeamName);
                            } else if (!mTeams.contains(teamNameEditText.getText().toString())) {
                                mTeams.add(teamNameEditText.getText().toString());
                            }
                        }
                        mTeamsAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setView(customView);

        if (teamIdx >= 0) {
            builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mTeams.remove(teamIdx);
                    mTeamsAdapter.notifyDataSetChanged();
                }
            });
        }

        builder.show();
    }

    @Override
    public void onContinueFabClick(View view) {
        // Create a new Fragment to be placed in the activity layout
        SetPlayersFragment firstFragment = new SetPlayersFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        // firstFragment.setArguments(getIntent().getExtras());

        Bundle extras = new Bundle();
        String teamsArray[] = new String[mTeams.size()];
        mTeams.toArray(teamsArray);
        extras.putSerializable(KEY_TEAMS, teamsArray);
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
        showAddTeamDialog(-1);
    }
}
