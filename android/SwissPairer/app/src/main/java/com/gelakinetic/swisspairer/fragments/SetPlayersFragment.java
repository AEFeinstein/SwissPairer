package com.gelakinetic.swisspairer.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.gelakinetic.swisspairer.MainActivity;
import com.gelakinetic.swisspairer.R;
import com.gelakinetic.swisspairer.adapters.PlayerListAdapter;
import com.gelakinetic.swisspairer.algorithm.Player;

import java.util.ArrayList;

/**
 * Created by Adam on 1/27/2017.
 */

public class SetPlayersFragment extends SwissFragment {

    ArrayList<Player> mPlayers = new ArrayList<>();
    private PlayerListAdapter mPlayersAdapter;
    private String mTeams[];

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ((MainActivity) getActivity()).showContinueFab();
        ((MainActivity) getActivity()).showAddFab();

        View view = inflater.inflate(R.layout.fragment_set_players, null);
        mPlayersAdapter = new PlayerListAdapter(getContext(), mPlayers, false);
        ListView listViewPlayers = (ListView) view.findViewById(R.id.player_list);
        listViewPlayers.setAdapter(mPlayersAdapter);
        listViewPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showAddPlayerDialog(i);
            }
        });

        mTeams = (String[]) getArguments().getSerializable(KEY_TEAMS);

        //TODO just for testing
        if (mPlayers.isEmpty()) {
            mPlayers.add(new Player("Adam", mTeams[0], false));
            mPlayers.add(new Player("Bob", mTeams[0], false));
            mPlayers.add(new Player("Charlie", mTeams[0], false));
            mPlayers.add(new Player("Dan", mTeams[0], false));
            mPlayers.add(new Player("Edward", mTeams[0], false));
            mPlayers.add(new Player("Frank", mTeams[1], false));
            mPlayers.add(new Player("George", mTeams[1], false));
            mPlayers.add(new Player("Henry", mTeams[1], false));
            mPlayers.add(new Player("Ira", mTeams[1], false));
            mPlayers.add(new Player("Jeremy", mTeams[1], false));
        }
        return view;
    }

    private void showAddPlayerDialog(final int playerIdx) {

        View customView = getLayoutInflater(null).inflate(R.layout.dialog_add_player, null);

        final EditText playerNameEditText = (EditText) customView.findViewById(R.id.player_name_edit_text);

        final Spinner teamSpinner = (Spinner) customView.findViewById(R.id.team_spinner);
        if (mTeams == null || mTeams.length == 0) {
            teamSpinner.setVisibility(View.GONE);
        } else {
            ArrayAdapter teamAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mTeams);
            teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            teamSpinner.setAdapter(teamAdapter);
        }

        if (playerIdx >= 0) {
            playerNameEditText.setText(mPlayers.get(playerIdx).getName());
            if (null != mPlayers.get(playerIdx).getTeam()) {
                for (int i = 0; i < mTeams.length; i++) {
                    if (mTeams[i].equals(mPlayers.get(playerIdx).getTeam())) {
                        teamSpinner.setSelection(i, false);
                        break;
                    }
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Add a Player")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String newPlayerName = playerNameEditText.getText().toString();
                        String newTeamName;
                        try {
                            newTeamName = teamSpinner.getSelectedItem().toString();
                        } catch (NullPointerException e) {
                            newTeamName = null;
                        }
                        Player newPlayer = new Player(newPlayerName, newTeamName, false);

                        if (!newPlayerName.isEmpty()) {
                            if (playerIdx >= 0) {
                                for (int playersIdx = 0; playersIdx < mPlayers.size(); playersIdx++) {
                                    if (playersIdx != playerIdx &&
                                            mPlayers.get(playersIdx).getName().equals(newPlayerName) &&
                                            mPlayers.get(playersIdx).getTeam().equals(newTeamName)) {
                                        return; // duplicate
                                    }
                                }
                                mPlayers.get(playerIdx).setName(newPlayerName);
                                mPlayers.get(playerIdx).setTeam(newTeamName);
                            } else if (!mPlayers.contains(newPlayer)) {
                                mPlayers.add(newPlayer);
                            }
                        }
                        mPlayersAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setView(customView);

        if (playerIdx >= 0) {
            builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mPlayers.remove(playerIdx);
                    mPlayersAdapter.notifyDataSetChanged();
                }
            });
        }

        builder.show();
    }

    @Override
    public void onContinueFabClick(View view) {

        // Create a new Fragment to be placed in the activity layout
        RoundFragment firstRoundFragment = new RoundFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        // firstFragment.setArguments(getIntent().getExtras());

        Bundle extras = new Bundle();

        extras.putSerializable(KEY_TEAMS, mTeams);

        if (mPlayers.size() % 2 == 1) {
            Player bye = new Player("Bye", null, true);
            if (mPlayers.contains(bye)) {
                mPlayers.remove(bye);
            } else {
                mPlayers.add(bye);
            }
        }
        Player playersArray[] = new Player[mPlayers.size()];
        mPlayers.toArray(playersArray);
        extras.putSerializable(KEY_PLAYERS, playersArray);

        extras.putInt(KEY_ROUND, 1);

        firstRoundFragment.setArguments(extras);

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, firstRoundFragment)
                .commit();
    }

    @Override
    public void onAddFabClick(View view) {
        showAddPlayerDialog(-1);
    }
}
