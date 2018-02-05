package com.casalprim.marc.tickettoridecalculator.game;

import android.support.v4.util.Pair;
import android.util.Log;

import com.casalprim.marc.tickettoridecalculator.utils.PairS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by marc on 16/01/18.
 */

public class TrainMap implements Serializable {

    //private SparseIntArray trainsDistribution;
    private HashSet<Edge> deployedTrains;
    private HashMap<PairS<City>, ArrayList<Edge>> longestPaths;
    private HashMap<City, HashSet<Pair<City, Edge>>> citiesAdjList;
    public TrainMap() {
        //initialize set of trains
        this.deployedTrains = new HashSet<>();
    }

    public static int length(ArrayList<Edge> path) {
        int length = 0;
        for (Edge edge : path) {
            length += edge.getLength();
        }
        return length;
    }

    public HashSet<Edge> getDeployedTrains() {
        return deployedTrains;
    }

    public HashMap<Integer, Integer> getTrainsDistribution() {
        HashMap<Integer, Integer> trainsDistribution = new HashMap<Integer, Integer>();
        for (Edge edge : deployedTrains) {
            int trainLength = edge.getLength();
            Integer previousCount = trainsDistribution.get(trainLength);
            if (previousCount == null)  //no previous value
                trainsDistribution.put(trainLength, 1);
            else
                trainsDistribution.put(trainLength, previousCount + 1);
        }
        return trainsDistribution;
    }

    public int getNumberOfTrains() {
        int sum = 0;
        for (Edge edge : deployedTrains) {
            sum += edge.getLength();
        }
        return sum;
    }

    public void addTrain(Edge edge) throws IllegalArgumentException {
        int numberOfTrains = getNumberOfTrains();
        if ((numberOfTrains + edge.getLength()) > Player.TOTAL_NUMBER_OF_TRAINS) {
            Log.i("Add train", "The Player doesn't have enough trains");
            throw new IllegalArgumentException("Not enough trains. Player has only " + (Player.TOTAL_NUMBER_OF_TRAINS - getNumberOfTrains()) + " remaining trains.");
        } else {
            deployedTrains.add(edge);
            Log.i("Add train", "Train added");
        }
    }

    public void removeTrain(Edge edge) {
        boolean result = deployedTrains.remove(edge);
        if (result) {
            Log.i("Remove train", "Train removed");
        } else {
            Log.i("Remove train", "There is no such train to remove.");
        }
    }

    public void computeCitiesAdjacencies() {
        citiesAdjList = new HashMap<>();
        for (Edge edge : deployedTrains) {
            Pair<City, Edge> pair = new Pair<>(edge.getCity2(), edge);
            HashSet<Pair<City, Edge>> adjCities = citiesAdjList.get(edge.getCity1());
            if (adjCities == null)
                adjCities = new HashSet<>();
            adjCities.add(pair);
            citiesAdjList.put(edge.getCity1(), adjCities);

            pair = new Pair<>(edge.getCity1(), edge);
            adjCities = citiesAdjList.get(edge.getCity2());
            if (adjCities == null)
                adjCities = new HashSet<>();
            adjCities.add(pair);
            citiesAdjList.put(edge.getCity2(), adjCities);
        }
    }

    public ArrayList<Edge> computeLongestPath() {
        //Get the longest possible path in this train map (impossible to do right now)
        ArrayList<Edge> longestPath = new ArrayList<>();
        ArrayList<Edge> farthest_path;
        longestPaths = new HashMap<>();
        citiesAdjList = new HashMap<>();
        computeCitiesAdjacencies();
        for (City city : citiesAdjList.keySet()) {
            farthest_path = getFarthestPathFrom(city);
            if (length(farthest_path) > length(longestPath)) {
                longestPath = farthest_path;
            }
        }
        return longestPath;
    }

    public ArrayList<Edge> getFarthestPathFrom(City from) {
        Log.d("getFarthestPathFrom", "From: " + from);
        ArrayList<Edge> longestPath = new ArrayList<>();
        ArrayList<Edge> farthest_path;
        for (City to : citiesAdjList.keySet()) {
            PairS<City> entry = new PairS<City>(from, to);
            if (!from.equals(to) && !longestPaths.containsKey(entry)) {
                farthest_path = getLongestPathFromTo(from, to);
                longestPaths.put(entry, farthest_path);
                //put all the subpaths here
                if (length(farthest_path) > length(longestPath)) {
                    longestPath = farthest_path;
                }

            }

        }
        return longestPath;
    }

    public ArrayList<Edge> getLongestPathFromTo(City from, City to) {
        Log.d("getLongestPathFromTo", "From: " + from + " To: " + to);
        HashSet<Edge> usedEdges = new HashSet<>();
        ArrayList<Edge> voidPath = new ArrayList<>();

        //find all trails that start at fromCity to target toCity
        ArrayList<ArrayList<Edge>> paths = findPaths(usedEdges, voidPath, null, from, to);
        //ArrayList<ArrayList<Edge>> paths = convertCityPathsToEdgePaths(cityPaths);
        ArrayList<Edge> longestPath = new ArrayList<>();
        for (ArrayList<Edge> path : paths) {
            if (length(path) > length(longestPath)) {
                longestPath = path;
            }
        }
        return longestPath;

    }

    private ArrayList<ArrayList<Edge>> convertCityPathsToEdgePaths(ArrayList<ArrayList<City>> cityPaths) {
        ArrayList<ArrayList<Edge>> paths = new ArrayList<>();
        for (ArrayList<City> path : cityPaths) {
            ArrayList<Edge> edgePath = new ArrayList<>();
            for (int i = 0; i < path.size() - 1; i++) {
                Edge edge = path.get(i).getEdgeTo(path.get(i + 1));
                if (edge != null)
                    edgePath.add(edge);
            }
            paths.add(edgePath);
        }
        return paths;
    }

    private ArrayList<ArrayList<Edge>> findPaths(HashSet<Edge> usedEdgesToGetThere, ArrayList<Edge> pathFollowed, Edge edgeUsedToGetThere, City start, City target) {
        Log.d("FindPaths", "Start: " + start + " Target: " + target);
        //PairS<City> pathKey = new PairS<>(start,target);
        ArrayList<ArrayList<Edge>> paths = new ArrayList<>();
        ArrayList<Edge> currentPath = pathFollowed;
        HashSet<Edge> usedEdges = new HashSet<>(usedEdgesToGetThere); //copy
//        ArrayList<Edge> path = longestPaths.get(pathKey);
//        if(path!=null){ // if the path is in longestPaths return immediately and check edges as used
//            paths.add(path);
//            for(Edge edge : path){
//                usedEdges.add(edge);
//            }
//            return  paths;
//        }
        if (edgeUsedToGetThere != null) {
            usedEdges.add(edgeUsedToGetThere);
            currentPath = new ArrayList<>(pathFollowed); //we create a fork of the path. If not, we reference always to the same object.
            currentPath.add(edgeUsedToGetThere);
        }
        ArrayList<Pair<City, Edge>> childEdgesList = getValidAdjacencies(start, usedEdges);
        // if (childEdgesList.isEmpty()) {// || start.equals(target)){
            if (start.equals(target)) {
                paths.add(currentPath);
            }
        //    return paths;
        // }
        for (Pair<City, Edge> pair : childEdgesList) {
            City city = pair.first;
            Edge edge = pair.second;
            usedEdges.add(edge);
            ArrayList<ArrayList<Edge>> newPaths = findPaths(usedEdges, currentPath, edge, city, target);
            paths.addAll(newPaths);
        }
        return paths;
    }

    private ArrayList<Pair<City, Edge>> getValidAdjacencies(City source, HashSet<Edge> usedEdges) {
        ArrayList<Pair<City, Edge>> validAdjacencies = new ArrayList<>();
        HashSet<Pair<City, Edge>> adjacencies = citiesAdjList.get(source);
        for (Pair<City, Edge> pair : adjacencies) {
            if (!usedEdges.contains(pair.second)) { //if track is not used and is deployed
                Log.d("GetAdjacencies", "Source: " + source + " Edge: " + pair.second);
                validAdjacencies.add(pair);
            }
        }
        return validAdjacencies;
    }

    public boolean checkRouteCard(RouteCard card) {
        String fromStr = card.getFrom();
        String toStr = card.getTo();
        City from = null;
        City to = null;
        if (citiesAdjList == null) {
            return false;
        }
        for (City city : citiesAdjList.keySet()) {
            if (city.getName().equalsIgnoreCase(fromStr))
                from = city;
            if (city.getName().equalsIgnoreCase(toStr))
                to = city;
        }
        if (from == null || to == null) {
            return false;
        } else {
            List<Edge> path = this.getLongestPathFromTo(from, to);
            return path.size() > 1;
        }
    }
}