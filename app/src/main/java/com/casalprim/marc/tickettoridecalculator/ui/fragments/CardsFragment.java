package com.casalprim.marc.tickettoridecalculator.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.casalprim.marc.tickettoridecalculator.R;
import com.casalprim.marc.tickettoridecalculator.game.Player;
import com.casalprim.marc.tickettoridecalculator.game.RouteCard;
import com.casalprim.marc.tickettoridecalculator.ui.adapters.PlayerCardsCollectionPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by marc on 17/01/18.
 */

public class CardsFragment extends Fragment {

    private static final String ARG_PLAYERS = "players";
    private static final String ARG_CARDS = "cards";
    PlayerCardsCollectionPagerAdapter pagerAdapter;
    ViewPager viewPager;
    private HashMap<Player.PlayerColor, Player> playersInGame;
    private ArrayList<RouteCard> routeCards;

    public CardsFragment() {

    }

    public static CardsFragment newInstance(HashMap<Player.PlayerColor, Player> players, ArrayList<RouteCard> cards) {
        CardsFragment fragment = new CardsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAYERS, players);
        args.putSerializable(ARG_CARDS, cards);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            playersInGame = (HashMap<Player.PlayerColor, Player>) getArguments().getSerializable(ARG_PLAYERS);
            routeCards = (ArrayList<RouteCard>) getArguments().getSerializable(ARG_CARDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cards, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.cards_viewpager);
        pagerAdapter = new PlayerCardsCollectionPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (playersInGame.isEmpty()) {
            Fragment fragment = new NoPlayersFragment();
            pagerAdapter.addFragment(fragment, "No players selected");
        }
        for (Player player : playersInGame.values()) {
            Fragment fragment = PlayerCardsFragment.newInstance(player, routeCards);
            pagerAdapter.addFragment(fragment, player.getName());
        }
        //pagerAdapter.notifyDataSetChanged();
    }

    public void notifyAllFragments() {
        pagerAdapter.notifyFragments();
    }

}
