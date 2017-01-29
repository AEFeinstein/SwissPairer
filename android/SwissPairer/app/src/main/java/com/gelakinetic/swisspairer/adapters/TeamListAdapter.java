package com.gelakinetic.swisspairer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gelakinetic.swisspairer.R;

import java.util.List;

/**
 * Created by Adam on 1/28/2017.
 */

public class TeamListAdapter extends ArrayAdapter<String> {

    /**
     * TODO document
     *
     * @param context
     * @param objects
     */
    public TeamListAdapter(Context context, List<String> objects) {
        super(context, 0, objects);
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
        String team = getItem(position);

        /* Check if an existing view is being reused, otherwise inflate the view */
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entry_team_list, parent, false);
        }

        /* Lookup view for data population */
        ((TextView) convertView.findViewById(R.id.team_name)).setText(team);

        /* Return the completed view to render on screen */
        return convertView;
    }
}
