package com.casalprim.marc.tickettoridecalculator.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.casalprim.marc.tickettoridecalculator.R;

/**
 * Created by marc on 18/01/18.
 */

public class NoPlayersFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_no_players, container, false);
    }
}
