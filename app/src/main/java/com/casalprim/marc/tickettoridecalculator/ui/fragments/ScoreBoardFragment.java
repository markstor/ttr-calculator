package com.casalprim.marc.tickettoridecalculator.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.casalprim.marc.tickettoridecalculator.R;
import com.casalprim.marc.tickettoridecalculator.game.Game;
import com.casalprim.marc.tickettoridecalculator.game.Player;
import com.casalprim.marc.tickettoridecalculator.game.RouteCard;
import com.casalprim.marc.tickettoridecalculator.game.TrainMap;
import com.casalprim.marc.tickettoridecalculator.ui.GameEditionByFragmentListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import static com.casalprim.marc.tickettoridecalculator.game.Player.SCORE_TABLE;

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
        buildTables((LinearLayout) view.findViewById(R.id.tableLinearLayout));
        return view;
    }

    private void buildTables(LinearLayout view) {
        for (Player.PlayerColor pColor : playersInGame.keySet()) {
            int bgColor = Game.PLAYER_COLOR_MAP.get(pColor);
            int bgLtColor = Color.argb(200, Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor));
            int txtColor = Game.PLAYER_TEXT_COLOR_MAP.get(pColor);

            TableLayout table = new TableLayout(view.getContext());
            view.addView(table);
            LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) table.getLayoutParams();
            lParams.width = LayoutParams.WRAP_CONTENT;
            lParams.height = LayoutParams.MATCH_PARENT;
            lParams.weight = 1f;
            //lParams.setMargins(20,20,20,20);
            table.setLayoutParams(lParams);
            table.setPadding(20, 20, 20, 20);
            table.setBackgroundColor(bgLtColor);
            Player player = playersInGame.get(pColor);
            TableRow tableHeader = new TableRow(view.getContext());
            tableHeader.setBackgroundColor(bgColor);
            TextView headerText = new TextView(view.getContext());
            headerText.setTextColor(txtColor);
            headerText.setText(player.getName());
            headerText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            headerText.setTypeface(null, Typeface.BOLD);
            tableHeader.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            tableHeader.setGravity(Gravity.CENTER);
            tableHeader.addView(headerText);
            TableRow.LayoutParams trParams = (TableRow.LayoutParams) headerText.getLayoutParams();
            trParams.span = 2;
            headerText.setLayoutParams(trParams);
            table.addView(tableHeader);
            table.setColumnStretchable(0, true);


            //tlParams.width=LayoutParams.MATCH_PARENT;

            boolean show0points = false; //show information that doesn't add points to the player?

            if (show0points || !player.getTrainMap().getDeployedTrains().isEmpty())
                addInfoTrains(table, player.getTrainMap(), bgColor, bgLtColor, txtColor);

            if (show0points || player.hasLongestPath()) {
                int longestPathScore = 0;
                if (player.hasLongestPath()) {
                    longestPathScore = 10;
                }
                addNewRow(table, getString(R.string.longest_path), longestPathScore, bgColor, txtColor);
            }
            if (show0points || player.getUnusedStations() > 0)
                addNewRow(table, getString(R.string.stations), player.getUnusedStations() * 4, bgColor, txtColor);

            if (show0points || !player.getRoutes().isEmpty())
                addInfoCards(table, player.getRoutes(), bgColor, bgLtColor, txtColor);


            TableLayout.LayoutParams tlParams = new TableLayout.LayoutParams();
            tlParams.weight = 1f;

            TableRow totalRow = new TableRow(view.getContext());
            totalRow.setLayoutParams(tlParams);
            //totalRow.setBackgroundColor(bgColor);
            TextView totalNameText = new TextView(view.getContext());
            totalNameText.setTextColor(txtColor);
            totalNameText.setBackgroundColor(bgColor);
            TextView totalValueText = new TextView(view.getContext());
            totalValueText.setTextColor(txtColor);
            totalValueText.setBackgroundColor(bgColor);
            totalNameText.setTypeface(null, Typeface.BOLD);
            totalValueText.setTypeface(null, Typeface.BOLD);
            totalNameText.setText(R.string.total);
            totalValueText.setText(String.format(Locale.US, "%d", player.getScore()));
            totalNameText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            totalValueText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

            totalRow.addView(totalNameText);
            totalRow.addView(totalValueText);
            totalRow.setGravity(Gravity.BOTTOM);
            //scoreTable.addView(totalRow);

            //table.addView(scoreTable);
            table.addView(totalRow);


        }
    }

    private void addInfoTrains(TableLayout scoreTable, TrainMap trainMap, int bgColor, int bgLtColor, int txtColor) {
        int score = 0;
        HashMap<Integer, Integer> trainsDistribution = trainMap.getTrainsDistribution();
        for (int trainLength : trainsDistribution.keySet()) {
            score += SCORE_TABLE.get(trainLength) * trainsDistribution.get(trainLength);
        }
        addNewRow(scoreTable, getString(R.string.trains), score, bgColor, txtColor);

        ArrayList<Integer> sortedKeys = new ArrayList<>(trainsDistribution.keySet());
        Collections.sort(sortedKeys);
        for (int trainLength : sortedKeys) {
            int number = trainsDistribution.get(trainLength);
            score = SCORE_TABLE.get(trainLength) * number;
            addNewRowIndented(scoreTable, String.format(Locale.US, "%dTx%d", trainLength, number), score, bgLtColor, txtColor);
        }
    }

    private void addInfoCards(TableLayout scoreTable, ArrayList<RouteCard> routes, int bgColor, int bgLtColor, int txtColor) {
        int score = 0;
        for (RouteCard card : routes) {
            if (card.isCompleted())
                score += card.getPoints();
            else
                score -= card.getPoints();
        }
        addNewRow(scoreTable, getString(R.string.tickets), score, bgColor, txtColor);

        for (RouteCard card : routes) {
            if (card.isCompleted())
                score = card.getPoints();
            else
                score = -card.getPoints();
            addNewRowIndented(scoreTable, card.getFrom() + "-" + card.getTo(), score, bgLtColor, txtColor);
        }
    }

    private void addNewRowIndented(TableLayout scoreTableView, String name, int value, int bgColor, int txtColor) {
        TableRow tableRow = new TableRow(scoreTableView.getContext());
        //tableRow.setBackgroundColor(bgColor);
        TextView nameText = new TextView(scoreTableView.getContext());
        TextView valueText = new TextView(scoreTableView.getContext());
        nameText.setTextColor(txtColor);
        valueText.setTextColor(txtColor);
        nameText.setText("\t" + name);
        valueText.setText(String.format(Locale.US, "%d", value));
        valueText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        tableRow.addView(nameText);
        tableRow.addView(valueText);
        scoreTableView.addView(tableRow);
    }

    private void addNewRow(TableLayout scoreTableView, String name, int value, int bgColor, int txtColor) {
        TableRow tableRow = new TableRow(scoreTableView.getContext());
        tableRow.setBackgroundColor(bgColor);
        TextView nameText = new TextView(scoreTableView.getContext());
        TextView valueText = new TextView(scoreTableView.getContext());
        nameText.setTextColor(txtColor);
        valueText.setTextColor(txtColor);
        nameText.setTypeface(null, Typeface.BOLD);
        valueText.setTypeface(null, Typeface.BOLD);
        nameText.setText(name);
        valueText.setText(String.format(Locale.US, "%d", value));
        valueText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        nameText.setPadding(0, 0, 20, 0);
        tableRow.addView(nameText);
        tableRow.addView(valueText);
        scoreTableView.addView(tableRow);
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
