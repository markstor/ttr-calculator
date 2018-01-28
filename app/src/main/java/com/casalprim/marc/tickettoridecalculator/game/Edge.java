package com.casalprim.marc.tickettoridecalculator.game;

import android.graphics.Color;

import java.io.Serializable;
import java.util.ArrayList;

import static com.casalprim.marc.tickettoridecalculator.game.Game.PLAYER_COLOR_MAP;

/**
 * Created by marc on 22/01/18.
 */

public class Edge implements Serializable {
    private City city1, city2;
    private int weight;
    private int color;
    private int color2;
    private int width; //maximum number of occupants
    private ArrayList<Player> occupants;
    private boolean inLongPath;

    public Edge(City destination, City origin, int weight, int color) {
        this.city1 = destination;
        this.city2 = origin;
        this.weight = weight;
        this.color = color;
        this.color2 = color;
        this.width = 1;
        this.inLongPath = true;
        this.occupants = new ArrayList<>();
        origin.addEdge(this);
        destination.addEdge(this);
    }

    public void setWidth(int w) {
        this.width = w;
    }

    public City getCity1() {
        return city1;
    }

    public void setCity1(City city1) {
        this.city1 = city1;
    }

    public City getCity2() {
        return city2;
    }

    public void setCity2(City city2) {
        this.city2 = city2;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getColor() {
        int alpha = 50;
        int drawColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
        if (!this.occupants.isEmpty()) {
            Player player = occupants.get(0);
            int pColor = PLAYER_COLOR_MAP.get(player.getColor());
            drawColor = Color.argb(255, Color.red(pColor), Color.green(pColor), Color.blue(pColor));
        }
        return drawColor;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor2() {
        int alpha = 50;
        int drawColor = Color.argb(alpha, Color.red(color2), Color.green(color2), Color.blue(color2));
        if (this.hasTwoOccupants()) {
            Player player = occupants.get(1);
            int pColor = PLAYER_COLOR_MAP.get(player.getColor());
            drawColor = Color.argb(255, Color.red(pColor), Color.green(pColor), Color.blue(pColor));

        }
        return drawColor;
    }

    public void setColor2(int color2) {
        this.color2 = color2;
    }

    public ArrayList<Player> getOccupants() {
        return occupants;
    }

    public boolean hasOccupant(Player.PlayerColor pColor) {
        for (Player occupant : occupants) {
            if (occupant.getColor().equals(pColor))
                return true;
        }
        return false;
    }

    public boolean hasTwoOccupants() {
        return this.occupants.size() >= 2;
    }

    public boolean hasTwoRails() {
        return this.width >= 2;
    }

    public void addOccupant(Player player) {
        if (this.occupants.size() < this.width)
            this.occupants.add(player);
    }

    public void removeOccupant(Player player) {
        this.occupants.remove(player);
    }

    public boolean isInLongestPath(Player.PlayerColor pColor) {
        for (Player player : occupants) {
            if (pColor == player.getColor()) {
                return player.isInLongestPath(this);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return city1.getName() + "-" + city2.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        Edge e = (Edge) obj;
        return e.city1.equals(this.city1) && e.city2.equals(this.city2);
    }
}
