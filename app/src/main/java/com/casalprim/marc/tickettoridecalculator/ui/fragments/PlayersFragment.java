package com.casalprim.marc.tickettoridecalculator.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.casalprim.marc.tickettoridecalculator.R;
import com.casalprim.marc.tickettoridecalculator.game.Player;
import com.casalprim.marc.tickettoridecalculator.ui.GameEditionByFragmentListener;
import com.casalprim.marc.tickettoridecalculator.ui.PlayerButtonDrawable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS;
import static com.casalprim.marc.tickettoridecalculator.game.Game.PLAYER_COLOR_MAP;
import static com.casalprim.marc.tickettoridecalculator.game.Game.PLAYER_TEXT_COLOR_MAP;

public class PlayersFragment extends Fragment {

    public static final Map<Integer, Player.PlayerColor> BUTTON_ID_TO_PLAYER_COLOR_MAP;
    private static final String ARG_PLAYERS = "players";

    static {
        Map<Integer, Player.PlayerColor> aMap = new HashMap<>();
        aMap.put(R.id.redPlayerSelector, Player.PlayerColor.RED);
        aMap.put(R.id.greenPlayerSelector, Player.PlayerColor.GREEN);
        aMap.put(R.id.yellowPlayerSelector, Player.PlayerColor.YELLOW);
        aMap.put(R.id.blackPlayerSelector, Player.PlayerColor.BLACK);
        aMap.put(R.id.bluePlayerSelector, Player.PlayerColor.BLUE);
        BUTTON_ID_TO_PLAYER_COLOR_MAP = Collections.unmodifiableMap(aMap);
    }

    private HashMap<Player.PlayerColor, Player> playersInGame;

    private GameEditionByFragmentListener mListener;

    public PlayersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param players Map of players in game.
     * @return A new instance of fragment PlayersFragment.
     */
    public static PlayersFragment newInstance(HashMap<Player.PlayerColor, Player> players) {
        PlayersFragment fragment = new PlayersFragment();
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
        return inflater.inflate(R.layout.fragment_players, container, false);
    }


    public void onButtonPressed(ToggleButton button) {
        if (mListener != null) {
            Player.PlayerColor color = BUTTON_ID_TO_PLAYER_COLOR_MAP.get(button.getId());
            if (button.isChecked()) {
                mListener.onPlayerAdded(color);
            } else {
                mListener.onPlayerRemoved(color);
                button.setTextOn("");
            }
        }
    }

    public void onButtonLongPressed(final ToggleButton button) {
        if (mListener != null) {
            final Player.PlayerColor color = BUTTON_ID_TO_PLAYER_COLOR_MAP.get(button.getId());
            Player player = this.playersInGame.get(color);
            if (player != null && button.isChecked()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                final EditText edittext = new EditText(getContext());
                edittext.setPadding(20, 20, 20, 20);
                edittext.setHint(player.getName());
                edittext.setInputType(TYPE_TEXT_FLAG_CAP_WORDS);

                alert.setTitle(R.string.change_name);

                alert.setView(edittext);

                alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String newName = edittext.getText().toString();
                        if (!newName.equals("")) {
                            mListener.onPlayerNameChanged(color, newName);
                            button.setTextOn(newName);
                            button.setChecked(button.isChecked()); //update button
                        }

                    }
                });

                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();
                edittext.requestFocus();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        setPlayerSelectors();
        updateButtons(playersInGame);
        FloatingActionButton floatingButton = getView().findViewById(R.id.next_map_button);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onMapFragmentClick();
                }
            }
        });

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
        for (int buttonId : BUTTON_ID_TO_PLAYER_COLOR_MAP.keySet()) {
            View v = getView().findViewById(buttonId);
            if (v instanceof ToggleButton) {
                ToggleButton button = (ToggleButton) v;
                int color = PLAYER_COLOR_MAP.get(BUTTON_ID_TO_PLAYER_COLOR_MAP.get(buttonId));//button.getShadowColor();
                int textColor = PLAYER_TEXT_COLOR_MAP.get(BUTTON_ID_TO_PLAYER_COLOR_MAP.get(buttonId));
                StateListDrawable bd = new StateListDrawable();
                bd.addState(new int[]{android.R.attr.state_checked}, new PlayerButtonDrawable(color, true));
                bd.addState(StateSet.WILD_CARD, new PlayerButtonDrawable(color, false));
                button.setBackgroundDrawable(bd);
                button.setTextColor(textColor);

                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        onButtonPressed((ToggleButton) compoundButton);
                    }
                });

                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        onButtonLongPressed((ToggleButton) view);
                        return true;
                    }
                });

            }
        }


    }

    public void updateButtons(HashMap<Player.PlayerColor, Player> playersInGame) {
        //Select players that are in the game
        for (int buttonId : BUTTON_ID_TO_PLAYER_COLOR_MAP.keySet()) {
            ToggleButton button = getView().findViewById(buttonId);
            Player player = playersInGame.get(BUTTON_ID_TO_PLAYER_COLOR_MAP.get(buttonId));
            if (player != null) {
                if (player.isNameEdited())
                    button.setTextOn(player.getName());
                button.setChecked(true);
            } else {
                button.setChecked(false);
            }
        }
    }


}
