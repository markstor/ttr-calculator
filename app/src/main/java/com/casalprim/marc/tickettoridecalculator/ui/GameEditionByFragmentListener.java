package com.casalprim.marc.tickettoridecalculator.ui;

import com.casalprim.marc.tickettoridecalculator.game.Edge;
import com.casalprim.marc.tickettoridecalculator.game.Player;
import com.casalprim.marc.tickettoridecalculator.game.RouteCard;

/**
 * Created by marc on 18/01/18.
 */

public interface GameEditionByFragmentListener {

    void onPlayerAdded(Player.PlayerColor color);

    void onPlayerRemoved(Player.PlayerColor color);

    void onRouteCardAssigned(Player.PlayerColor color, RouteCard card);

    void onRouteCardUnassigned(Player.PlayerColor color, RouteCard card);

    //    void onUnusedStationAdded(Player.PlayerColor color);
//    void onUnusedStationRemoved(Player.PlayerColor color);
    void onTrainAdded(Player.PlayerColor color, Edge edge);

    void onTrainRemoved(Player.PlayerColor color, Edge edge);

    void onMapFragmentClick();

}
