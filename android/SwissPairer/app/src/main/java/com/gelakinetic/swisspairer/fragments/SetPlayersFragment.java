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

/**
 * Created by Adam on 1/27/2017.
 */

public class SetPlayersFragment extends SwissFragment {

    private PlayerListAdapter mPlayersAdapter;


    View.OnClickListener continueListener = new View.OnClickListener() {
        /**
         * TODO document
         * @param view
         */
        @Override
        public void onClick(View view) {

            // Create a new Fragment to be placed in the activity layout
            RoundFragment firstRoundFragment = new RoundFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            // firstFragment.setArguments(getIntent().getExtras());

            Bundle extras = new Bundle();

            if (mTournament.getRound(0).getPlayersSize() % 2 == 1) {
                Player bye = new Player("Bye", null, true);
                if (mTournament.getRound(0).containsPlayer(bye)) {
                    mTournament.getRound(0).removePlayer(bye);
                } else {
                    mTournament.getRound(0).addPlayer(bye);
                }
            }

            saveTournamentData(mTournamentFilename);
            extras.putInt(KEY_ROUND, 1);
            extras.putString(KEY_JSON_FILENAME, mTournamentFilename);

            firstRoundFragment.setArguments(extras);

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, firstRoundFragment)
                    .commit();
        }
    };

    View.OnClickListener addListener = new View.OnClickListener() {
        /**
         * TODO document
         * @param view
         */
        @Override
        public void onClick(View view) {
            showAddPlayerDialog(-1);
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

        ((MainActivity) getActivity()).setTitle("Add Players");

        mTournamentFilename = getArguments().getString(KEY_JSON_FILENAME);
        loadTournamentData(mTournamentFilename);

        if (mTournament.getRounds().isEmpty()) {
            mTournament.addRound();
        }

        View view = inflater.inflate(R.layout.fragment_set_players, null);

        setupButtons(view, R.string.add_player, addListener, R.string.start, continueListener);
        setRightButtonVisibility(View.VISIBLE);
        setLeftButtonVisibility(View.VISIBLE);

        mPlayersAdapter = new PlayerListAdapter(getContext(), mTournament.getRound(0).getPlayers(), false);
        ListView listViewPlayers = (ListView) view.findViewById(R.id.player_list);
        listViewPlayers.setAdapter(mPlayersAdapter);
        listViewPlayers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showAddPlayerDialog(i);
            }
        });

        //TODO just for testing
        if (mTournament.getRound(0).getPlayersSize() == 0) {
            if (mTournament.getTeams() == null || mTournament.getTeams().isEmpty()) {
                mTournament.getRound(0).addPlayer(new Player("Adam", null, false));
                mTournament.getRound(0).addPlayer(new Player("Bob", null, false));
                mTournament.getRound(0).addPlayer(new Player("Charlie", null, false));
                mTournament.getRound(0).addPlayer(new Player("Dan", null, false));
                mTournament.getRound(0).addPlayer(new Player("Edward", null, false));
                mTournament.getRound(0).addPlayer(new Player("Frank", null, false));
                mTournament.getRound(0).addPlayer(new Player("George", null, false));
                mTournament.getRound(0).addPlayer(new Player("Henry", null, false));
                mTournament.getRound(0).addPlayer(new Player("Ira", null, false));
                mTournament.getRound(0).addPlayer(new Player("Jeremy", null, false));
            } else {
                mTournament.getRound(0).addPlayer(new Player("Adam", mTournament.getTeams().get(0), false));
                mTournament.getRound(0).addPlayer(new Player("Bob", mTournament.getTeams().get(0), false));
                mTournament.getRound(0).addPlayer(new Player("Charlie", mTournament.getTeams().get(0), false));
                mTournament.getRound(0).addPlayer(new Player("Dan", mTournament.getTeams().get(0), false));
                mTournament.getRound(0).addPlayer(new Player("Edward", mTournament.getTeams().get(0), false));
                mTournament.getRound(0).addPlayer(new Player("Frank", mTournament.getTeams().get(1), false));
                mTournament.getRound(0).addPlayer(new Player("George", mTournament.getTeams().get(1), false));
                mTournament.getRound(0).addPlayer(new Player("Henry", mTournament.getTeams().get(1), false));
                mTournament.getRound(0).addPlayer(new Player("Ira", mTournament.getTeams().get(1), false));
                mTournament.getRound(0).addPlayer(new Player("Jeremy", mTournament.getTeams().get(1), false));
            }
        }
        return view;
    }

    /**
     * TODO document
     *
     * @param playerIdx
     */
    private void showAddPlayerDialog(final int playerIdx) {

        View customView = getLayoutInflater(null).inflate(R.layout.dialog_add_player, null);

        final EditText playerNameEditText = (EditText) customView.findViewById(R.id.player_name_edit_text);

        final Spinner teamSpinner = (Spinner) customView.findViewById(R.id.team_spinner);
        if (mTournament.getTeams() == null || mTournament.getTeams().size() == 0) {
            teamSpinner.setVisibility(View.GONE);
        } else {
            ArrayAdapter teamAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mTournament.getTeams());
            teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            teamSpinner.setAdapter(teamAdapter);
        }

        if (playerIdx >= 0) {
            playerNameEditText.setText(mTournament.getRound(0).getPlayer(playerIdx).getName());
            if (null != mTournament.getRound(0).getPlayer(playerIdx).getTeam()) {
                for (int i = 0; i < mTournament.getTeams().size(); i++) {
                    if (mTournament.getTeams().get(i).equals(mTournament.getRound(0).getPlayer(playerIdx).getTeam())) {
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
                                for (int playersIdx = 0; playersIdx < mTournament.getRound(0).getPlayersSize(); playersIdx++) {
                                    if (playersIdx != playerIdx &&
                                            mTournament.getRound(0).getPlayer(playersIdx).getName().equals(newPlayerName) &&
                                            mTournament.getRound(0).getPlayer(playersIdx).getTeam().equals(newTeamName)) {
                                        return; // duplicate
                                    }
                                }
                                mTournament.getRound(0).getPlayer(playerIdx).setName(newPlayerName);
                                mTournament.getRound(0).getPlayer(playerIdx).setTeam(newTeamName);
                            } else if (!mTournament.getRound(0).containsPlayer(newPlayer)) {
                                mTournament.getRound(0).addPlayer(newPlayer);
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
                    mTournament.getRound(0).removePlayer(playerIdx);
                    mPlayersAdapter.notifyDataSetChanged();
                }
            });
        }

        builder.show();
    }

}
