package com.casalprim.marc.tickettoridecalculator.game;

import android.graphics.Color;
import android.util.Log;
import android.util.Xml;

import com.casalprim.marc.tickettoridecalculator.R;
import com.casalprim.marc.tickettoridecalculator.ui.MainActivity;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marc on 16/01/18.
 */
@SuppressWarnings("serial")
public class Game {

    public static final Map<Player.PlayerColor, Integer> PLAYER_COLOR_MAP;
    public static final Map<Player.PlayerColor, Integer> PLAYER_TEXT_COLOR_MAP;

    static {
        Map<Player.PlayerColor, Integer> aMap = new HashMap<>();
        aMap.put(Player.PlayerColor.RED, Color.RED);
        aMap.put(Player.PlayerColor.GREEN, Color.GREEN);
        aMap.put(Player.PlayerColor.YELLOW, Color.YELLOW);
        aMap.put(Player.PlayerColor.BLACK, Color.BLACK);
        aMap.put(Player.PlayerColor.BLUE, Color.BLUE);
        PLAYER_COLOR_MAP = Collections.unmodifiableMap(aMap);
    }

    static {
        Map<Player.PlayerColor, Integer> aMap = new HashMap<>();
        aMap.put(Player.PlayerColor.RED, Color.BLACK);
        aMap.put(Player.PlayerColor.GREEN, Color.BLACK);
        aMap.put(Player.PlayerColor.YELLOW, Color.BLACK);
        aMap.put(Player.PlayerColor.BLACK, Color.WHITE);
        aMap.put(Player.PlayerColor.BLUE, Color.BLACK);
        PLAYER_TEXT_COLOR_MAP = Collections.unmodifiableMap(aMap);
    }


    private HashMap<Player.PlayerColor, Player> players;
    private ArrayList<RouteCard> routeCards;
    private GameMap gameMap;

    public Game() {
        this.players = new HashMap<>();
        this.routeCards = generateRouteCards(R.raw.routecards_eur);
        this.gameMap = new GameMap();
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public HashMap<Player.PlayerColor, Player> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<Player.PlayerColor, Player> players) {
        this.players = players;
    }

    public Player addNewPlayer(Player.PlayerColor color) {
        return this.players.put(color, new Player(color));
    }

    public Player removePlayer(Player.PlayerColor color) {
        return this.players.remove(color);
    }

    public ArrayList<RouteCard> getRouteCards() {
        return routeCards;
    }

    public void setRouteCards(ArrayList<RouteCard> routeCards) {
        this.routeCards = routeCards;
    }

    public void assignRouteCard(Player.PlayerColor color, RouteCard cardToGet) {
        Player player = this.players.get(color);
        if (routeCards.contains(cardToGet)) {
            RouteCard card = this.routeCards.get(this.routeCards.indexOf(cardToGet));
            player.assignRoute(card);
        }
    }

    public void unassignRouteCard(Player.PlayerColor color, RouteCard cardToGet) {
        if (routeCards.contains(cardToGet)) {
            RouteCard card = this.routeCards.get(this.routeCards.indexOf(cardToGet));
            (card.getOwner()).unassignRoute(card);

        }
    }

    public void addTrain(Player.PlayerColor color, Edge edge) {
        Player player = this.players.get(color);
        player.addTrain(edge);
    }

    public void removeTrain(Player.PlayerColor color, Edge edge) {
        Player player = this.players.get(color);
        player.removeTrain(edge);
    }

    public ArrayList<Player> getLongestPathPlayers() {
        int maxLength = 0;
        ArrayList<Player> longestPathPlayers = new ArrayList<>();
        for (Player player : players.values()) {
            ArrayList<Edge> path = player.getLongestPath();
            int len = TrainMap.length(path);
            if (len > maxLength) {
                maxLength = len;
                longestPathPlayers = new ArrayList<>();
                longestPathPlayers.add(player);
            } else if (len == maxLength) {
                longestPathPlayers.add(player);
            }
        }
        return longestPathPlayers;
    }


    public ArrayList<RouteCard> generateRouteCards(int XMLfileID) {
        ArrayList<RouteCard> list = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        InputStream stream = null;
        try {
            // auto-detect the encoding from the stream
            stream = MainActivity.RESOURCES.openRawResource(XMLfileID);
            parser.setInput(stream, null);
            int eventType = parser.getEventType();
            boolean done = false;
            RouteCard item = null;
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("card")) {
                            String from = parser.getAttributeValue(null, "from");
                            String to = parser.getAttributeValue(null, "to");
                            String points = parser.getAttributeValue(null, "points");
                            Log.i("New card", "From " + from + ", To: " + to + ", Points: " + points);
                            item = new RouteCard(from, to, Integer.parseInt(points));
                            list.add(item);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        Log.i("End tag", name);
                        if (name.equalsIgnoreCase("routecards")) {
                            done = true;
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

}
