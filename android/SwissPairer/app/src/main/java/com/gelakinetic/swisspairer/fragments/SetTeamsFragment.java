package com.gelakinetic.swisspairer.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private CheckBox mTeamCheckbox;
    private ListView mListViewTeams;
    private TextView mTeamsLabel;
    private Spinner mRoundSpinner;
    private EditText mTournamentName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((MainActivity) getActivity()).showContinueFab();
        ((MainActivity) getActivity()).showAddFab();

        View view = inflater.inflate(R.layout.fragment_set_teams, null);

        mTournamentName = (EditText) view.findViewById(R.id.tournament_name);

        mTeamCheckbox = (CheckBox) view.findViewById(R.id.team_checkbox);
        mTeamCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ((MainActivity) getActivity()).showAddFab();
                    mListViewTeams.setVisibility(View.VISIBLE);
                    mTeamsLabel.setVisibility(View.VISIBLE);
                } else {
                    ((MainActivity) getActivity()).hideAddFab();
                    mListViewTeams.setVisibility(View.GONE);
                    mTeamsLabel.setVisibility(View.GONE);
                }
            }
        });
        mTeamsLabel = (TextView) view.findViewById(R.id.teams_label);
        mTeamsAdapter = new TeamListAdapter(getContext(), mTeams);
        mListViewTeams = (ListView) view.findViewById(R.id.team_list);
        mListViewTeams.setAdapter(mTeamsAdapter);
        mListViewTeams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showAddTeamDialog(i);
            }
        });

        mRoundSpinner = (Spinner) view.findViewById(R.id.num_round_spinner);

        // TODO just for testing
        mTournamentName.setText("Test Tournament");
        mTeamCheckbox.setChecked(true);
        if (mTeams.isEmpty()) {
            mTeams.add("Red");
            mTeams.add("Blk");
        }
        mRoundSpinner.setSelection(4);

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
        SetPlayersFragment setPlayersFragment = new SetPlayersFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        // firstFragment.setArguments(getIntent().getExtras());

        Bundle extras = new Bundle();

        String tName = mTournamentName.getText().toString();
        if (tName.isEmpty()) {
            Toast.makeText(getContext(), "Tournament needs a name", Toast.LENGTH_SHORT).show();
            return;
        } else {
            extras.putString(KEY_NAME, mTournamentName.getText().toString());
        }

        if (mTeamCheckbox.isChecked()) {
            String teamsArray[] = new String[mTeams.size()];
            mTeams.toArray(teamsArray);
            extras.putStringArray(KEY_TEAMS, teamsArray);
        }

        extras.putInt(KEY_MAX_ROUNDS, Integer.parseInt((String) mRoundSpinner.getSelectedItem()));

        setPlayersFragment.setArguments(extras);

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, setPlayersFragment)
                .commit();
    }

    @Override
    public void onAddFabClick(View view) {
        showAddTeamDialog(-1);
    }
}
