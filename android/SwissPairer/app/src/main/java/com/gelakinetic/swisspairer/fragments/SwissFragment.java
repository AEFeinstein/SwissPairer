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

/**
 * Created by Adam on 1/27/2017.
 */

public abstract class SwissFragment extends Fragment {

    Tournament mTournament = new Tournament();

    static final String KEY_ROUND = "Round";

    private Button mLeftButton;
    private Button mRightButton;
    private View mButtonLayout;

    public void loadTournamentData() {
        try {
            FileReader reader = new FileReader(new File(getContext().getFilesDir(), "Tourney.json"));
            mTournament = (new Gson()).fromJson(reader, Tournament.class);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTournamentData() {
        try {
            FileWriter writer = new FileWriter(new File(getContext().getFilesDir(), "Tourney.json"));
            writer.write((new Gson()).toJson(mTournament));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public void setLeftButtonVisibility(int visibility) {
        mLeftButton.setVisibility(visibility);

        if (mLeftButton.getVisibility() == View.GONE && mRightButton.getVisibility() == View.GONE) {
            mButtonLayout.setVisibility(View.GONE);
        } else {
            mButtonLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setRightButtonVisibility(int visibility) {
        mRightButton.setVisibility(visibility);

        if (mLeftButton.getVisibility() == View.GONE && mRightButton.getVisibility() == View.GONE) {
            mButtonLayout.setVisibility(View.GONE);
        } else {
            mButtonLayout.setVisibility(View.VISIBLE);
        }
    }
}
