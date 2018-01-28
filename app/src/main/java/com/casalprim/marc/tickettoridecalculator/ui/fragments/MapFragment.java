package com.casalprim.marc.tickettoridecalculator.ui.fragments;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.casalprim.marc.tickettoridecalculator.R;
import com.casalprim.marc.tickettoridecalculator.game.Edge;
import com.casalprim.marc.tickettoridecalculator.game.GameMap;
import com.casalprim.marc.tickettoridecalculator.game.Player;
import com.casalprim.marc.tickettoridecalculator.ui.GameEditionByFragmentListener;
import com.casalprim.marc.tickettoridecalculator.ui.MapView;
import com.casalprim.marc.tickettoridecalculator.ui.PlayerButtonDrawable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.casalprim.marc.tickettoridecalculator.game.Game.PLAYER_COLOR_MAP;

public class MapFragment extends Fragment {

    public static final Map<Integer, Player.PlayerColor> COLOR_MAP;
    private static final String ARG_PLAYERS = "players";
    private static final String ARG_MAP = "map";

    static {
        Map<Integer, Player.PlayerColor> aMap = new HashMap<>();
        aMap.put(R.id.redPlayerPencil, Player.PlayerColor.RED);
        aMap.put(R.id.greenPlayerPencil, Player.PlayerColor.GREEN);
        aMap.put(R.id.yellowPlayerPencil, Player.PlayerColor.YELLOW);
        aMap.put(R.id.blackPlayerPencil, Player.PlayerColor.BLACK);
        aMap.put(R.id.bluePlayerPencil, Player.PlayerColor.BLUE);
        COLOR_MAP = Collections.unmodifiableMap(aMap);
    }

    private HashMap<Player.PlayerColor, Player> playersInGame;
    private GameMap gameMap;
    private MapView mapView;
    private Player.PlayerColor selectedPlayer;

    private GameEditionByFragmentListener mListener;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(HashMap<Player.PlayerColor, Player> players, GameMap gameMap) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAYERS, players);
        args.putSerializable(ARG_MAP, gameMap);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playersInGame = (HashMap<Player.PlayerColor, Player>) getArguments().getSerializable(ARG_PLAYERS);
            gameMap = (GameMap) getArguments().getSerializable(ARG_MAP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }


    public void onButtonPressed(CompoundButton button) {
        if (mListener != null) {
            Player.PlayerColor color = COLOR_MAP.get(button.getId());
            //resetbuttons
            for (Integer buttonId : COLOR_MAP.keySet()) {
                ((ToggleButton) getView().findViewById(buttonId)).setChecked(false);
            }

            selectedPlayer = color;
            mapView.setSelectedColor(color);
            button.setChecked(true);


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView = getView().findViewById(R.id.map_view);
        mapView.setGameMap(gameMap);
        mapView.setOnEdgeSelectedListener(new MapView.OnEdgeSelectedListener() {
            @Override
            public void onEdgeSelected(Edge selectedEdge, Player.PlayerColor pColor) {
                edgeSelected(selectedEdge, pColor);
            }
        });
        setPlayerSelectors();
        updateButtons(playersInGame);
    }

    private void edgeSelected(Edge edge, Player.PlayerColor playerColor) {
        if (mListener != null) {
            if (edge.hasOccupant(playerColor)) {
                mListener.onTrainRemoved(playerColor, edge);
            } else {
                mListener.onTrainAdded(playerColor, edge);
            }
            //updateViews();
        }
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

    private void setPlayerSelectors() {
        //sets the Drawables on the toggle buttons inside the layout, and sets them with a listener
        for (int buttonId : COLOR_MAP.keySet()) {
            View v = getView().findViewById(buttonId);
            if (v instanceof ToggleButton) {
                final ToggleButton button = (ToggleButton) v;
                int color = PLAYER_COLOR_MAP.get(COLOR_MAP.get(buttonId));//button.getShadowColor();
                StateListDrawable bd = new StateListDrawable();
                bd.addState(new int[]{android.R.attr.state_checked}, new PlayerButtonDrawable(color, true));
                bd.addState(StateSet.WILD_CARD, new PlayerButtonDrawable(color, false));
                button.setBackgroundDrawable(bd);

//                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                        onButtonPressed(compoundButton);
//                    }
//                });
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onButtonPressed((CompoundButton) view);
                    }
                });

            }
        }


    }

    public void updateButtons(HashMap<Player.PlayerColor, Player> playersInGame) {
        ToggleButton button = null;
        boolean firstClicked = false;
        //Select players that are in the game
        for (int buttonId : COLOR_MAP.keySet()) {
            button = (ToggleButton) getView().findViewById(buttonId);
            button.setChecked(false);
            if (playersInGame.containsKey(COLOR_MAP.get(buttonId))) {
                button.setVisibility(View.VISIBLE);
                if (!firstClicked) {
                    button.callOnClick();
                    firstClicked = true;
                }
            } else {
                button.setVisibility(View.GONE);
            }
        }
    }

    public void updateViews() {
        mapView.invalidate();
    }


}
