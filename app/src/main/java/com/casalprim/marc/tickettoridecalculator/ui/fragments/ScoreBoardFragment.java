package com.casalprim.marc.tickettoridecalculator.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.casalprim.marc.tickettoridecalculator.R;
import com.casalprim.marc.tickettoridecalculator.game.Player;
import com.casalprim.marc.tickettoridecalculator.game.TrainMap;
import com.casalprim.marc.tickettoridecalculator.ui.GameEditionByFragmentListener;

import java.util.HashMap;

public class ScoreBoardFragment extends Fragment {

    private static final String ARG_PLAYERS = "players";

    private HashMap<Player.PlayerColor, Player> playersInGame;
    private TableLayout scoreTable;

    private GameEditionByFragmentListener mListener;

    public ScoreBoardFragment() {
        // Required empty public constructor
    }


    public static ScoreBoardFragment newInstance(HashMap<Player.PlayerColor, Player> players) {
        ScoreBoardFragment fragment = new ScoreBoardFragment();
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
        // Inflate the layout for this fragment
        if (playersInGame == null || playersInGame.isEmpty()) {
            return inflater.inflate(R.layout.layout_no_players, container, false);
        }
        View view = inflater.inflate(R.layout.fragment_score_board, container, false);
        buildTables((LinearLayout) view);
        return view;
    }

    private void buildTables(LinearLayout view) {
        for (Player.PlayerColor pColor : playersInGame.keySet()) {
            TableLayout table = new TableLayout(view.getContext());
            TableLayout.LayoutParams lParams = new TableLayout.LayoutParams();
            lParams.width = 0;
            lParams.height = LayoutParams.MATCH_PARENT;
            lParams.weight = 1f;
            table.setLayoutParams(lParams);
            Player player = playersInGame.get(pColor);
            TableRow tableHeader = new TableRow(view.getContext());
            TextView headerText = new TextView(view.getContext());
            headerText.setText(player.getName());
            headerText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tableHeader.addView(headerText);
            table.addView(tableHeader);

            ScrollView scrollViewLayout = new ScrollView(view.getContext());
            scoreTable = new TableLayout(view.getContext());

            TableRow totalRow = new TableRow(view.getContext());
            TextView totalNameText = new TextView(view.getContext());
            TextView totalValueText = new TextView(view.getContext());
            totalNameText.setText("Total");
            totalValueText.setText(String.format("%d", player.getScore()));
            totalRow.addView(totalNameText);
            totalRow.addView(totalValueText);
            scoreTable.addView(totalRow);

            TableRow longestPathRow = new TableRow(view.getContext());
            TextView longestPathNameText = new TextView(view.getContext());
            TextView longestPathValueText = new TextView(view.getContext());
            longestPathNameText.setText("Longest Path");
            longestPathValueText.setText(String.format("%d", TrainMap.length(player.getLongestPath())));
            longestPathRow.addView(longestPathNameText);
            longestPathRow.addView(longestPathValueText);
            scoreTable.addView(longestPathRow);


            scrollViewLayout.addView(scoreTable);
            table.addView(scrollViewLayout);

            view.addView(table);


        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GameEditionByFragmentListener) {
            mListener = (GameEditionByFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GameEditionByFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
