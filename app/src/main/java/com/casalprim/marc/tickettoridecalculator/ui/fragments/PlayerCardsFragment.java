package com.casalprim.marc.tickettoridecalculator.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.casalprim.marc.tickettoridecalculator.R;
import com.casalprim.marc.tickettoridecalculator.game.Game;
import com.casalprim.marc.tickettoridecalculator.game.Player;
import com.casalprim.marc.tickettoridecalculator.game.RouteCard;
import com.casalprim.marc.tickettoridecalculator.ui.GameEditionByFragmentListener;
import com.casalprim.marc.tickettoridecalculator.ui.adapters.CardsAdapter;
import com.casalprim.marc.tickettoridecalculator.ui.adapters.CitiesAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by marc on 17/01/18.
 */

public class PlayerCardsFragment extends Fragment {
    private static final String ARG_PLAYER = "player";
    private static final String ARG_CARDS = "cards";
    private Player player;
    private ArrayList<RouteCard> routeCards;
    private CitiesAdapter citiesAdapter;
    private TextView assigningLabel;
    private Button cancelFromCity;
    private GridView citiesGridview, cardsGridview;
    private AdapterView.OnItemClickListener cityClickListener, destinationClickListener;
    private GameEditionByFragmentListener mListener;
    private String fromCity;
    private CardsAdapter cardsAdapter;

    public PlayerCardsFragment() {
    }

    public static PlayerCardsFragment newInstance(Player player, ArrayList<RouteCard> cards) {
        PlayerCardsFragment fragment = new PlayerCardsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAYER, player);
        args.putSerializable(ARG_CARDS, cards);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            player = (Player) getArguments().getSerializable(ARG_PLAYER);
            routeCards = (ArrayList<RouteCard>) getArguments().getSerializable(ARG_CARDS);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_playercards, container, false);

        assigningLabel = view.findViewById(R.id.playerLabel);
        assigningLabel.setText(R.string.assign_ticket_from);
        citiesGridview = view.findViewById(R.id.unassignedCardsGridView);
        cardsGridview = view.findViewById(R.id.assignedCardsGridView);
        citiesAdapter = new CitiesAdapter(this.getContext(), getUnassignedCities(routeCards));
        citiesAdapter.setBackgroundTextColor(Game.PLAYER_COLOR_MAP.get(player.getColor()));
        citiesAdapter.setTextColor(Game.PLAYER_TEXT_COLOR_MAP.get(player.getColor()));
        cardsAdapter = new CardsAdapter(this.getContext(), player.getRoutes());

        citiesGridview.setAdapter(citiesAdapter);
        cardsGridview.setAdapter(cardsAdapter);


        cityClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                fromCity = (String) citiesAdapter.getItem(position);
                waitingFromCity(false);
            }
        };
        destinationClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String destinationCity = (String) citiesAdapter.getItem(position);
                assignRouteCard(fromCity, destinationCity);
                waitingFromCity(true);

            }
        };
        citiesGridview.setOnItemClickListener(cityClickListener);
        cardsGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RouteCard clickedCard = (RouteCard) cardsAdapter.getItem(i);
                unassignRouteCard(clickedCard);
            }
        });

        cardsGridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                RouteCard clickedCard = (RouteCard) cardsAdapter.getItem(position);
                clickedCard.setCompleted(!clickedCard.getCompleted());
                cardsAdapter.notifyDataSetInvalidated();
                return true;
            }
        });


        cancelFromCity = view.findViewById(R.id.cancelButton);
        cancelFromCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waitingFromCity(true);
            }
        });

        TextView bottomLabel = view.findViewById(R.id.assigned_cards_text);
        bottomLabel.setText(getString(R.string.assigned_cards, player.getName()));

        return view;
    }

    private void assignRouteCard(String from, String to) {
        for (RouteCard card : this.routeCards) {
            if (card.equals(new RouteCard(from, to, card.getPoints()))) {
                if (mListener != null) {
                    mListener.onRouteCardAssigned(player.getColor(), card);
                    waitingFromCity(true);
                }
                return;
            }
        }
        //throw new NoSuchFieldException("RouteCard not found. From "+from+" to "+to+".");
    }

    private void unassignRouteCard(RouteCard card) {
        if (mListener != null) {
            mListener.onRouteCardUnassigned(player.getColor(), card);
            waitingFromCity(true);
        }
    }


    public ArrayList<String> getUnassignedCities(ArrayList<RouteCard> cards) {
        ArrayList<String> cities = new ArrayList<>();
        for (RouteCard card : cards) {
            String city = card.getFrom();
            if (!(card.isOwned() || cities.contains(city))) {
                cities.add(city);
            }
        }
        Collections.sort(cities);
        return cities;
    }

    public ArrayList<String> getDestinations(ArrayList<RouteCard> cards, String departingCity) {
        ArrayList<String> destinations = new ArrayList<>();
        for (RouteCard card : cards) {
            if (card.getFrom().equals(departingCity) && !card.isOwned()) {
                destinations.add(card.getTo());
            }
        }
        Collections.sort(destinations);
        return destinations;
    }

    public void notifyChangeInDataset() {
        if (cardsAdapter != null && citiesAdapter != null) {
            cardsAdapter.notifyDataSetChanged();
            citiesAdapter.setCities(getUnassignedCities(routeCards));
        }
    }

    public void waitingFromCity(boolean waiting) {
        if (waiting) {
            citiesAdapter.setCities(getUnassignedCities(routeCards));
            cancelFromCity.setVisibility(View.INVISIBLE);
            assigningLabel.setText(R.string.assign_ticket_from);
            citiesGridview.setOnItemClickListener(cityClickListener);
        } else {
            assigningLabel.setText(getString(R.string.assign_ticket_from_to, fromCity));
            citiesAdapter.setCities(getDestinations(routeCards, fromCity));
            citiesGridview.setOnItemClickListener(destinationClickListener);
            cancelFromCity.setVisibility(View.VISIBLE);
        }
    }


}
