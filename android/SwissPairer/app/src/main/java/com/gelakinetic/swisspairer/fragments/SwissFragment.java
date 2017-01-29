package com.gelakinetic.swisspairer.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

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

    public abstract void onContinueFabClick(View view);

    public abstract void onAddFabClick(View view);

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
}
