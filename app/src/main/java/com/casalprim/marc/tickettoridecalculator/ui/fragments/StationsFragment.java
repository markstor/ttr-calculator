package com.casalprim.marc.tickettoridecalculator.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.casalprim.marc.tickettoridecalculator.R;
import com.casalprim.marc.tickettoridecalculator.game.Player;
import com.casalprim.marc.tickettoridecalculator.ui.GameEditionByFragmentListener;

import java.util.HashMap;
import java.util.Locale;

import static com.casalprim.marc.tickettoridecalculator.game.Game.PLAYER_COLOR_MAP;
import static com.casalprim.marc.tickettoridecalculator.game.Game.PLAYER_TEXT_COLOR_MAP;

public class StationsFragment extends Fragment {

    private static final String ARG_PLAYERS = "players";

    private HashMap<Player.PlayerColor, Player> playersInGame;

    private GameEditionByFragmentListener mListener;

    public StationsFragment() {
        // Required empty public constructor
    }

    public static StationsFragment newInstance(HashMap<Player.PlayerColor, Player> players) {
        StationsFragment fragment = new StationsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAYERS, players);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playersInGame = (HashMap<Player.PlayerColor, Player>) getArguments().getSerializable(ARG_PLAYERS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (playersInGame == null || playersInGame.isEmpty()) {
            return inflater.inflate(R.layout.layout_no_players, container, false);
        }

        TableLayout ll = new TableLayout(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(layoutParams);
        ll.setGravity(Gravity.CENTER);
        //ll.setPadding(20,20,20,20);
        for (final Player player : playersInGame.values()) {
            TableRow row = new TableRow(getContext());
            layoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 0, 1);
            row.setLayoutParams(layoutParams);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackgroundColor(PLAYER_COLOR_MAP.get(player.getColor()));
            row.setPadding(20, 20, 20, 20);

            TableRow.LayoutParams lparams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            TextView name = new TextView(getContext());
            name.setLayoutParams(lparams);
            name.setTextColor(PLAYER_TEXT_COLOR_MAP.get(player.getColor()));
            name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            name.setGravity(Gravity.CENTER);
            name.setText(player.getName());
            name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            name.setPadding(0, 0, 20, 0);

            lparams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1);
            SeekBar bar = new SeekBar(getContext());
            bar.setLayoutParams(lparams);
            bar.setMax(3);

            lparams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
            final TextView value = new TextView(getContext());
            value.setTextColor(PLAYER_TEXT_COLOR_MAP.get(player.getColor()));
            value.setText(String.format(Locale.US, "%d", player.getUnusedStations()));
            value.setLayoutParams(lparams);
            value.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            value.setGravity(Gravity.CENTER);
            value.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            value.setPadding(40, 0, 40, 0);


            bar.setProgress(player.getUnusedStations());
            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    player.setUnusedStations(progress);
                    value.setText(String.format(Locale.US, "%d", player.getUnusedStations()));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

            });
            row.addView(name);
            row.addView(bar);
            row.addView(value);
            ll.addView(row);
        }
        //container.addView(ll);
        return ll;
    }

    @Override
    public void onStart() {
        super.onStart();
/*        FloatingActionButton floatingButton = getView().findViewById(R.id.next_map_button);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMapFragmentClick();
                }
            }
        });*/

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GameEditionByFragmentListener) {
            mListener = (GameEditionByFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
