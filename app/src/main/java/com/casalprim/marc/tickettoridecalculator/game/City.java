package com.casalprim.marc.tickettoridecalculator.game;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by marc on 22/01/18.
 */
public class City implements Serializable {
    private String name;
    private int coordX;
    private int coordY;
    private ArrayList<Edge> edges;

    public City() {
        this.name = "";
        coordX = 0;
        coordY = 0;
        edges = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCoordX() {
        return coordX;
    }

    public void setCoordX(int coordX) {
        this.coordX = coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public void setCoordY(int coordY) {
        this.coordY = coordY;
    }

    public void addEdge(Edge edge) {
        if (this.edges.contains(edge)) {
            Log.d("AddEdge", "Repeated edge " + edge.toString());
        }
        this.edges.add(edge);
    }

    public Edge getEdgeTo(City city2) {
        for (Edge edge : this.edges) {
            if (edge.getCity1().equals(city2) || edge.getCity2().equals(city2)) {
                Log.i("GetEdge", "Edge found from " + this.getName() + " to " + city2.getName());
                return edge;
            }
        }
        return null;
    }

    public boolean hasPlayerRail(Player.PlayerColor playerColor) {
        if (playerColor == null)
            return false;
        for (Edge edge : edges) {
            if (edge.hasOccupant(playerColor)) {
                return true;
            }
        }
        return false;


    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        City city = (City) obj;
        return this.name.equalsIgnoreCase(city.getName());
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
