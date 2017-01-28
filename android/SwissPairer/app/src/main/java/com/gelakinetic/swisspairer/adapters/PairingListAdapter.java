package com.gelakinetic.swisspairer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gelakinetic.swisspairer.R;
import com.gelakinetic.swisspairer.algorithm.Pairing;

import java.util.List;

/**
 * Created by Adam on 1/28/2017.
 */

public class PairingListAdapter extends ArrayAdapter<Pairing> {

    public PairingListAdapter(Context context, List<Pairing> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /* Get the data item for this position */
        Pairing pairing = getItem(position);

        /* Check if an existing view is being reused, otherwise inflate the view */
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entry_pairing_list, parent, false);
        }

        /* Lookup view for data population */
        ((TextView) convertView.findViewById(R.id.player_left)).setText(pairing.getPlayerOne().getPairingString());
        ((TextView) convertView.findViewById(R.id.player_right)).setText(pairing.getPlayerTwo().getPairingString());

        /* Return the completed view to render on screen */
        return convertView;
    }
}
