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

    /**
     * TODO document
     *
     * @param context
     * @param objects
     */
    public PairingListAdapter(Context context, List<Pairing> objects) {
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
        Pairing pairing = getItem(position);

        /* Check if an existing view is being reused, otherwise inflate the view */
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entry_pairing_list, parent, false);
        }

        if (pairing == null) {
            return convertView;
        }

        /* Lookup view for data population */
        TextView playerOneLabel = ((TextView) convertView.findViewById(R.id.player_left));
        TextView playerTwoLabel = ((TextView) convertView.findViewById(R.id.player_right));
        playerOneLabel.setText(pairing.getPlayerOne().getPairingString());
        playerTwoLabel.setText(pairing.getPlayerTwo().getPairingString());

        if (pairing.isReported()) {
            if (pairing.playerOneWon()) {
                playerOneLabel.setTextColor(getContext().getResources().getColor(R.color.green));
                playerTwoLabel.setTextColor(getContext().getResources().getColor(R.color.red));
            } else if (pairing.playerTwoWon()) {
                playerOneLabel.setTextColor(getContext().getResources().getColor(R.color.red));
                playerTwoLabel.setTextColor(getContext().getResources().getColor(R.color.green));
            } else {
                playerOneLabel.setTextColor(getContext().getResources().getColor(R.color.blue));
                playerTwoLabel.setTextColor(getContext().getResources().getColor(R.color.blue));
            }
        } else {
            playerOneLabel.setTextColor(getContext().getResources().getColor(R.color.black));
            playerTwoLabel.setTextColor(getContext().getResources().getColor(R.color.black));
        }

        /* Return the completed view to render on screen */
        return convertView;
    }
}
