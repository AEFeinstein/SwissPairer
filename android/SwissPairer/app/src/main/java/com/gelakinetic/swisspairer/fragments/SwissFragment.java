package com.gelakinetic.swisspairer.fragments;

import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.gelakinetic.swisspairer.R;
import com.gelakinetic.swisspairer.algorithm.Pairing;
import com.gelakinetic.swisspairer.algorithm.Round;
import com.gelakinetic.swisspairer.algorithm.Tournament;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Adam on 1/27/2017.
 */

public abstract class SwissFragment extends Fragment {

    static final String KEY_ROUND = "Round";
    static final String KEY_JSON_FILENAME = "Filename";
    private static final String JSON_SUFFIX = ".json";
    Tournament mTournament = new Tournament();
    String mTournamentFilename;
    private Button mLeftButton;
    private Button mRightButton;
    private View mButtonLayout;

    /**
     * TODO document
     *
     * @return
     */
    String[] getTournaments() {
        ArrayList<String> filenames = new ArrayList<>();
        for (File file : Objects.requireNonNull(requireContext().getFilesDir().listFiles())) {
            String filename = file.getName();
            if (filename.endsWith(JSON_SUFFIX)) {
                filenames.add(filename.subSequence(0, filename.length() - JSON_SUFFIX.length()).toString());
            }
        }
        String[] filenamesArr = new String[filenames.size()];
        filenames.toArray(filenamesArr);
        return filenamesArr;
    }

    /**
     * TODO document
     *
     * @param filename
     */
    void loadTournamentData(String filename) {
        try {
            FileReader reader = new FileReader(new File(requireContext().getFilesDir(), filename + JSON_SUFFIX));
            mTournament = (new Gson()).fromJson(reader, Tournament.class);
            reader.close();

            /* For each round, replace the separate player objects in the players ArrayList with
             * references to the player objects in the pairings
             */
            for (Round round : mTournament.getRounds()) {
                if (!round.getPairings().isEmpty()) {
                    round.getPlayers().clear();
                    for (Pairing pairing : round.getPairings()) {
                        round.addPlayer(pairing.getPlayerOne());
                        round.addPlayer(pairing.getPlayerTwo());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO document
     *
     * @param filename
     */
    void saveTournamentData(String filename) {
        try {
            FileWriter writer = new FileWriter(new File(requireContext().getFilesDir(), filename + JSON_SUFFIX));
            writer.write((new Gson()).toJson(mTournament));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO document
     *
     * @param filename
     */
    void deleteTournamentData(String filename) {
        File tournamentFile = new File(requireContext().getFilesDir(), filename + JSON_SUFFIX);
        if (!tournamentFile.delete()) {
            try {
                throw new IOException("Delete Failed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO document
     *
     * @param view
     * @param leftLabel
     * @param leftListener
     * @param rightLabel
     * @param rightListener
     */
    void setupButtons(View view, int leftLabel, View.OnClickListener leftListener,
                      int rightLabel, View.OnClickListener rightListener) {
        mLeftButton = view.findViewById(R.id.left_button);
        if (leftLabel != 0) {
            mLeftButton.setText(leftLabel);
            mLeftButton.setOnClickListener(leftListener);
        } else {
            mLeftButton.setVisibility(View.GONE);
        }

        mRightButton = view.findViewById(R.id.right_button);
        if (rightLabel != 0) {
            mRightButton.setText(rightLabel);
            mRightButton.setOnClickListener(rightListener);
        } else {
            mRightButton.setVisibility(View.GONE);
        }

        mButtonLayout = view.findViewById(R.id.bottom_button_bar);
    }

    /**
     * TODO document
     *
     * @param visibility
     */
    void setLeftButtonVisibility(int visibility) {
        mLeftButton.setVisibility(visibility);

        if (mLeftButton.getVisibility() == View.GONE && mRightButton.getVisibility() == View.GONE) {
            mButtonLayout.setVisibility(View.GONE);
        } else {
            mButtonLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * TODO document
     *
     * @param visibility
     */
    void setRightButtonVisibility(int visibility) {
        mRightButton.setVisibility(visibility);

        if (mLeftButton.getVisibility() == View.GONE && mRightButton.getVisibility() == View.GONE) {
            mButtonLayout.setVisibility(View.GONE);
        } else {
            mButtonLayout.setVisibility(View.VISIBLE);
        }
    }
}
