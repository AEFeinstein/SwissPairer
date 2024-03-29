package com.gelakinetic.swisspairer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.gelakinetic.swisspairer.R;
import com.gelakinetic.swisspairer.adapters.TeamListAdapter;

import java.util.Objects;

/**
 * Created by Adam on 1/27/2017.
 */

public class SetTeamsFragment extends SwissFragment {

    private ListView mListViewTeams;
    private Spinner mRoundSpinner;
    private EditText mTournamentName;
    private CheckBox mTeamCheckbox;
    private final View.OnClickListener continueListener = new View.OnClickListener() {
        /**
         * TODO document
         * @param view
         */
        @Override
        public void onClick(View view) {
            // Create a new Fragment to be placed in the activity layout
            SetPlayersFragment setPlayersFragment = new SetPlayersFragment();

            // Make sure the tournament has a name
            String tName = mTournamentName.getText().toString();
            if (tName.isEmpty()) {
                Toast.makeText(getContext(), R.string.tournament_no_name, Toast.LENGTH_SHORT).show();
                return;
            } else {
                mTournament.setName(mTournamentName.getText().toString());
            }

            if (!mTeamCheckbox.isChecked()) {
                mTournament.getTeams().clear();
            } else if (mTournament.getTeams().size() < 2) {
                Toast.makeText(getContext(), R.string.no_teams, Toast.LENGTH_SHORT).show();
                return;
            }

            mTournament.setMaxRounds(Integer.parseInt((String) mRoundSpinner.getSelectedItem()));
            mTournamentFilename = tName;
            saveTournamentData(mTournamentFilename); // Created a Tournament

            Bundle extras = new Bundle();
            extras.putString(KEY_JSON_FILENAME, mTournamentFilename);
            setPlayersFragment.setArguments(extras);

            // Add the fragment to the 'fragment_container' FrameLayout
            getParentFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, setPlayersFragment)
                    .commit();
        }
    };
    /* Helper UI Objects */
    private TeamListAdapter mTeamsAdapter;
    private final View.OnClickListener addListener = new View.OnClickListener() {
        /**
         * TODO document
         * @param view
         */
        @Override
        public void onClick(View view) {
            showAddTeamDialog(-1);
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
    @SuppressWarnings("CommentedOutCode")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_set_teams, container, false);

        setupButtons(view, R.string.add_team, addListener, R.string.add_players, continueListener);
        setRightButtonVisibility(View.VISIBLE);
        setLeftButtonVisibility(View.GONE);

        mTournamentName = view.findViewById(R.id.tournament_name);

        mTeamCheckbox = view.findViewById(R.id.team_checkbox);
        mTeamCheckbox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                setLeftButtonVisibility(View.VISIBLE);
                mListViewTeams.setVisibility(View.VISIBLE);
            } else {
                setLeftButtonVisibility(View.GONE);
                mListViewTeams.setVisibility(View.INVISIBLE);
            }
        });
        mTeamsAdapter = new TeamListAdapter(getContext(), mTournament.getTeams());
        mListViewTeams = view.findViewById(R.id.team_list);
        mListViewTeams.setAdapter(mTeamsAdapter);
        mListViewTeams.setOnItemClickListener((adapterView, view1, i, l) -> showAddTeamDialog(i));

        mRoundSpinner = view.findViewById(R.id.num_round_spinner);

//        // TODO just for testing
//        mTournamentName.setText("Test Tournament");
//        mTeamCheckbox.setChecked(true);
//        if (mTournament.getTeams().isEmpty()) {
//            mTournament.getTeams().add("Red");
//            mTournament.getTeams().add("Blk");
//        }
//        mRoundSpinner.setSelection(4);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTournament(mTournamentFilename);
    }

    /**
     * TODO document
     *
     * @param teamIdx
     */
    private void showAddTeamDialog(final int teamIdx) {

        View customView = ((LayoutInflater) (Objects.requireNonNull(requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)))).inflate(R.layout.dialog_add_team, null, false);

        final EditText teamNameEditText = customView.findViewById(R.id.team_name_edit_text);

        if (teamIdx >= 0) {
            teamNameEditText.setText(mTournament.getTeams().get(teamIdx));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        builder.setTitle(R.string.add_a_team)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {

                    String newTeamName = teamNameEditText.getText().toString();

                    if (!newTeamName.isEmpty()) {
                        if (teamIdx >= 0) {
                            for (int teamsIdx = 0; teamsIdx < mTournament.getTeams().size(); teamsIdx++) {
                                if (teamsIdx != teamIdx && mTournament.getTeams().get(teamsIdx).equals(newTeamName)) {
                                    return; // duplicate
                                }
                            }
                            mTournament.getTeams().set(teamIdx, newTeamName);
                        } else if (!mTournament.getTeams().contains(teamNameEditText.getText().toString())) {
                            mTournament.getTeams().add(teamNameEditText.getText().toString());
                        }
                    }
                    mTeamsAdapter.notifyDataSetChanged();

                    /* Clear out player data & save the tournament */
                    mTournament.getRounds().clear();
                    saveTournamentData(mTournamentFilename); // Team Added
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                })
                .setView(customView);

        if (teamIdx >= 0) {
            builder.setNeutralButton(R.string.remove, (dialogInterface, i) -> {
                mTournament.getTeams().remove(teamIdx);
                mTeamsAdapter.notifyDataSetChanged();

                /* Clear out player data & save the tournament */
                mTournament.getRounds().clear();
                saveTournamentData(mTournamentFilename); // Team Removed
            });
        }

        builder.show();
    }

    /**
     * TODO document
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_set_teams, menu);
    }

    /**
     * TODO document
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        int itemId = item.getItemId();
        if (itemId == R.id.load_tournament) {
            showLoadTournamentDialog();
            return true;
        } else if (itemId == R.id.delete_tournament) {
            showDeleteTournamentDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * TODO document
     */
    private void showLoadTournamentDialog() {

        final String[] filenames = getTournaments();

        if (filenames.length == 0) {
            Toast.makeText(getContext(), R.string.no_tournaments, Toast.LENGTH_SHORT).show();
            return;
        }

        (new AlertDialog.Builder(requireContext())).setTitle(R.string.load_tournament_title)
                .setItems(filenames, (dialogInterface, i) -> setTournament(filenames[i]))
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {

                })
                .show();
    }

    private void setTournament(String filename) {
        mTournamentFilename = filename;
        if (mTournamentFilename != null) {
            loadTournamentData(mTournamentFilename);
        }

        mTournamentName.setText(mTournament.getName());
        mRoundSpinner.setSelection(mTournament.getMaxRounds() - 1);
        mTeamCheckbox.setChecked(!mTournament.getTeams().isEmpty());
        mTeamsAdapter = new TeamListAdapter(getContext(), mTournament.getTeams());
        mListViewTeams.setAdapter(mTeamsAdapter);
        mTeamsAdapter.notifyDataSetChanged();
    }

    /**
     * TODO document
     */
    private void showDeleteTournamentDialog() {

        final String[] filenames = getTournaments();

        if (filenames.length == 0) {
            Toast.makeText(getContext(), R.string.no_tournaments, Toast.LENGTH_SHORT).show();
            return;
        }

        (new AlertDialog.Builder(requireContext())).setTitle(R.string.delete_tournament_title)
                .setItems(filenames, (dialogInterface, i) -> deleteTournamentData(filenames[i]))
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {

                })
                .show();
    }
}
