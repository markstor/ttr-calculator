package com.casalprim.marc.tickettoridecalculator.game;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by marc on 16/01/18.
 */

public class RouteCard implements Serializable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RouteCard> CREATOR = new Parcelable.Creator<RouteCard>() {
        @Override
        public RouteCard createFromParcel(Parcel in) {
            return new RouteCard(in);
        }

        @Override
        public RouteCard[] newArray(int size) {
            return new RouteCard[size];
        }
    };
    private String from;
    private String to;
    private int points;
    private boolean completed;
    private boolean owned;
    private Player owner;

    public RouteCard(String from, String to, int points) {
        this.from = from;
        this.to = to;
        this.points = points;
        this.completed = false;
        this.owned = false;
    }

    protected RouteCard(Parcel in) {
        from = in.readString();
        to = in.readString();
        points = in.readInt();
        completed = in.readByte() != 0x00;
        owned = in.readByte() != 0x00;
        owner = (Player) in.readValue(Player.class.getClassLoader());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteCard card = (RouteCard) o;

        if (!from.equals(card.from)) return false;
        return to.equals(card.to);
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getPoints() {
        return points;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    public void setCompleted(boolean state) {
        this.completed = state;
    }

    public boolean isOwned() {
        return owned;
    }

    public void setOwned(boolean state) {
        this.owned = state;
    }

    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
        this.owned = true;
    }
}