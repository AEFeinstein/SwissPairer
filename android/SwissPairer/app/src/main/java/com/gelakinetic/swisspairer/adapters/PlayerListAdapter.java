package com.gelakinetic.swisspairer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gelakinetic.swisspairer.R;
import com.gelakinetic.swisspairer.algorithm.Player;

import java.util.List;

/**
 * Created by Adam on 1/28/2017.
 */

public class PlayerListAdapter extends ArrayAdapter<Player> {

    private final boolean mShowStandings;

    /**
     * TODO document
     *
     * @param context
     * @param objects
     * @param showStandings
     */
    public PlayerListAdapter(Context context, List<Player> objects, boolean showStandings) {
        super(context, 0, objects);
        mShowStandings = showStandings;
    }

    /**
     * TODO document
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        /* Get the data item for this position */
        Player player = getItem(position);

        /* Check if an existing view is being reused, otherwise inflate the view */
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entry_player_list, parent, false);
        }

        if (player == null) {
            return convertView;
        }

        if (player.isBye()) {
            convertView.findViewById(R.id.entry_player_list_layout).setVisibility(View.GONE);
            return convertView;
        } else {
            convertView.findViewById(R.id.entry_player_list_layout).setVisibility(View.VISIBLE);
        }

        /* Lookup view for data population */
        if (player.getTeam() != null) {
            convertView.findViewById(R.id.player_team).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.player_team)).setText(String.format("[%s]", player.getTeam()));
        } else {
            convertView.findViewById(R.id.player_team).setVisibility(View.GONE);
        }
        ((TextView) convertView.findViewById(R.id.player_name)).setText(player.getName());

        if (mShowStandings) {
            convertView.findViewById(R.id.player_record).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.player_record)).setText(player.getRecordString());
        } else {
            convertView.findViewById(R.id.player_record).setVisibility(View.GONE);
        }

        /* Return the completed view to render on screen */
        return convertView;
    }
}
