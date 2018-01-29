package com.casalprim.marc.tickettoridecalculator.game;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by marc on 22/01/18.
 */
public class GameMap implements Serializable {
    private ArrayList<City> cities;
    private ArrayList<Edge> edges;

    public GameMap(InputStream file) {
        this.cities = parseMap(file);
        this.edges = retrieveEdges();
    }

    public ArrayList<City> parseMap(InputStream stream) {
        ArrayList<City> list = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        try {
            // auto-detect the encoding from the stream
            //stream = MainActivity.RESOURCES.openRawResource(R.raw.map_eur);
            parser.setInput(stream, null);
            int eventType = parser.getEventType();
            boolean done = false;
            City item = null;
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("city")) {
                            //Log.i("New city", "Create new item");
                            item = new City();
                            for (int i = 0; i < parser.getAttributeCount(); i++) {
                                String attrName = parser.getAttributeName(i);
                                String value = parser.getAttributeValue(i);
                                //Log.i("Attribute", attrName + ": " + value);
                                switch (attrName) {
                                    case "name":
                                        item.setName(value);
                                        break;
                                    case "x":
                                        item.setCoordX(Integer.parseInt(value));
                                        break;
                                    case "y":
                                        item.setCoordY(Integer.parseInt(value));
                                        break;
                                }
                            }
                            list.add(item);
                        } else if (name.equalsIgnoreCase("edge")) {
                            String city1 = parser.getAttributeValue(null, "from");
                            String city2 = parser.getAttributeValue(null, "to");
                            String weight = parser.getAttributeValue(null, "length");
                            String color = parser.getAttributeValue(null, "color");
                            String color2 = parser.getAttributeValue(null, "color2");
                            //Log.i("New edge", "From " + city1 + ", To: " + city2 + ", Weight: " + weight + ", Color: " + color);
                            this.addEdge(list, city1, city2, weight, color, color2);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        //Log.i("End tag", name);
                        if (name.equalsIgnoreCase("map")) {
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

    private void addEdge(List<City> list, String city1name, String city2name, String weightString, String colorString, String color2) {
        City city1 = null;
        City city2 = null;
        for (City city : list) {
            if ((city.getName()).equals(city1name)) {
                city1 = city;
            } else if ((city.getName()).equals(city2name)) {
                city2 = city;
            }
            if (city2 != null && city1 != null)
                break;
        }
        int weight = Integer.parseInt(weightString);
        if (colorString.equalsIgnoreCase("orange"))
            colorString = "#FF7D3D";
        if (color2 != null && color2.equalsIgnoreCase("orange"))
            color2 = "#FF7D3D";
        int color = Color.parseColor(colorString);
        Edge edge = new Edge(city1, city2, weight, color);

        if (color2 != null) {
            edge.setWidth(2);
            edge.setColor2(Color.parseColor(color2));
        }
    }

    @Nullable
    public City getCity(String cityName) {
        for (City city : cities) {
            if (city.getName().equals(cityName)) {
                Log.i("GetCity", "City found " + cityName);
                return city;
            }
        }
        Log.i("GetCity", "City not found " + cityName);
        return null;
    }

    public ArrayList<City> getCities() {
        return this.cities;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public ArrayList<Edge> retrieveEdges() {
        ArrayList<Edge> edges = new ArrayList<>();
        for (City city : cities) {
            for (Edge edge : city.getEdges()) {
                if (!edges.contains(edge)) {
                    edges.add(edge);
                }
            }
        }
        return edges;
    }
}
