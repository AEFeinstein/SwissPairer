package com.gelakinetic.swisspairer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.gelakinetic.swisspairer.fragments.SetTeamsFragment;
import com.gelakinetic.swisspairer.fragments.SwissFragment;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton continueFab;
    private FloatingActionButton addFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        continueFab = (FloatingActionButton) findViewById(R.id.fab_continue);
        continueFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SwissFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container)).onContinueFabClick(view);
            }
        });

        addFab = (FloatingActionButton) findViewById(R.id.fab_add);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SwissFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container)).onAddFabClick(view);
            }
        });

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            SetTeamsFragment firstFragment = new SetTeamsFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, firstFragment)
                    .commit();
        }
    }

    public void showContinueFab() {
        continueFab.setVisibility(View.VISIBLE);
    }

    public void hideContinueFab() {
        continueFab.setVisibility(View.GONE);
    }

    public void showAddFab() {
        addFab.setVisibility(View.VISIBLE);
    }

    public void hideAddFab() {
        addFab.setVisibility(View.GONE);
    }
}
