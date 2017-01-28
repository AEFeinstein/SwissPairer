package com.gelakinetic.swisspairer.fragments;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Adam on 1/27/2017.
 */

public abstract class SwissFragment extends Fragment {

    static final String KEY_TEAMS = "Teams";
    static final String KEY_PLAYERS = "Players";
    static final String KEY_ROUND = "Round";


    public abstract void onContinueFabClick(View view);
    public abstract void onAddFabClick(View view);
}
