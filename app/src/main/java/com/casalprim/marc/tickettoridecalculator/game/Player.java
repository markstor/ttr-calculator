package com.casalprim.marc.tickettoridecalculator.game;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marc on 16/01/18.
 */


public class Player implements Serializable {
    public static final int TOTAL_NUMBER_OF_TRAINS = 45;
    static final Map<Integer, Integer> SCORE_TABLE = scoreMapConstructor();
    private boolean hasLongestPath;
    private String name;
    private PlayerColor color;
    private TrainMap trains;
    private ArrayList<RouteCard> routes;
    private int unusedStations;
    private ArrayList<Edge> longestPath;

    public Player(PlayerColor color) {
        this.color = color;
        this.trains = new TrainMap();
        this.routes = new ArrayList<>();
        this.longestPath = new ArrayList<>();
        this.unusedStations = 3;
    }

    private static Map<Integer, Integer> scoreMapConstructor() {
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        result.put(1, 1);
        result.put(2, 2);
        result.put(3, 4);
        result.put(4, 7);
        result.put(6, 15);
        result.put(8, 21);
        return Collections.unmodifiableMap(result);
    }

    public String getName() {
        if (name == null) {
            return this.getColor().name();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayerColor getColor() {
        return color;
    }

    public void setColor(PlayerColor color) {
        this.color = color;
    }

    public void assignRoute(RouteCard card) {
        if (!this.routes.contains(card)) {
            this.routes.add(card);
            card.setOwner(this);
        }
    }

    public void unassignRoute(RouteCard card) {
        if (this.routes.contains(card)) {
            this.routes.remove(card);
            card.setOwned(false);
        }
    }

    public ArrayList<RouteCard> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<RouteCard> routes) {
        this.routes = routes;
    }

    public ArrayList<Edge> computeLongestPath() {
        longestPath = trains.computeLongestPath();
        return longestPath;
    }

    public ArrayList<Edge> getLongestPath() {
        return longestPath;
    }

    public void setLongestPath(boolean hasLongestPath) {
        this.hasLongestPath = hasLongestPath;
    }

    public boolean isInLongestPath(Edge edge) {
        return longestPath.contains(edge);
    }

    public int getRemainingTrains() {
        return this.TOTAL_NUMBER_OF_TRAINS - trains.getNumberOfTrains();
    }

    public TrainMap getTrainMap() {
        return this.trains;
    }

    public int getScore() {
        int score = 0;
        HashMap<Integer, Integer> trainsDistribution = trains.getTrainsDistribution();
        for (int trainLength : trainsDistribution.keySet()) {
            score += SCORE_TABLE.get(trainLength) * trainsDistribution.get(trainLength);
        }
        score += unusedStations * 4;
        for (RouteCard card : routes) {
            if (card.isCompleted())
                score += card.getPoints();
            else
                score -= card.getPoints();
        }
        if (this.hasLongestPath())
            score += 10;
        return score;
    }

    public boolean hasLongestPath() {
        return this.hasLongestPath;
    }

    public void addTrain(Edge edge) {
        if (edge != null) {
            //Log.d("addTrain",city1.getName()+" to "+city2.getName()+" length:"+edge.getWeight());
            this.trains.addTrain(edge);
            edge.addOccupant(this);
            this.computeLongestPath();
        }
    }

    public void removeTrain(Edge edge) {
        if (edge != null) {
            //Log.d("removeTrain",city1.getName()+" to "+city2.getName()+" length:"+edge.getWeight());
            this.trains.removeTrain(edge);
            edge.removeOccupant(this);
            this.computeLongestPath();
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public enum PlayerColor {BLUE, YELLOW, RED, GREEN, BLACK}

}