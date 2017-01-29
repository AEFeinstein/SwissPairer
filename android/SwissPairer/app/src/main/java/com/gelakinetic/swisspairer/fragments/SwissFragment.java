package com.gelakinetic.swisspairer.fragments;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;

import com.gelakinetic.swisspairer.R;
import com.gelakinetic.swisspairer.algorithm.Tournament;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Adam on 1/27/2017.
 */

public abstract class SwissFragment extends Fragment {

    Tournament mTournament = new Tournament();
    String mTournamentFilename;

    static final String KEY_ROUND = "Round";
    static final String KEY_JSON_FILENAME = "Filename";

    private Button mLeftButton;
    private Button mRightButton;
    private View mButtonLayout;

    static final String JSON_SUFFIX = ".json";

    /**
     * TODO document
     *
     * @return
     */
    public String[] getTournaments() {
        ArrayList<String> filenames = new ArrayList<>();
        for (File file : getContext().getFilesDir().listFiles()) {
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
    public void loadTournamentData(String filename) {
        try {
            FileReader reader = new FileReader(new File(getContext().getFilesDir(), filename + JSON_SUFFIX));
            mTournament = (new Gson()).fromJson(reader, Tournament.class);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * TODO document
     *
     * @param filename
     */
    public void saveTournamentData(String filename) {
        try {
            FileWriter writer = new FileWriter(new File(getContext().getFilesDir(), filename + JSON_SUFFIX));
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
    public void deleteTournamentData(String filename) {
        File tournamentFile = new File(getContext().getFilesDir(), filename + JSON_SUFFIX);
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
    public void setupButtons(View view, int leftLabel, View.OnClickListener leftListener,
                             int rightLabel, View.OnClickListener rightListener) {
        mLeftButton = (Button) view.findViewById(R.id.left_button);
        if (leftLabel != 0) {
            mLeftButton.setText(leftLabel);
            mLeftButton.setOnClickListener(leftListener);
        } else {
            mLeftButton.setVisibility(View.GONE);
        }

        mRightButton = (Button) view.findViewById(R.id.right_button);
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
    public void setLeftButtonVisibility(int visibility) {
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
    public void setRightButtonVisibility(int visibility) {
        mRightButton.setVisibility(visibility);

        if (mLeftButton.getVisibility() == View.GONE && mRightButton.getVisibility() == View.GONE) {
            mButtonLayout.setVisibility(View.GONE);
        } else {
            mButtonLayout.setVisibility(View.VISIBLE);
        }
    }
}
