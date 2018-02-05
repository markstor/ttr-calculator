package com.casalprim.marc.tickettoridecalculator.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by marc on 18/01/18.
 */

public class CitiesAdapter extends BaseAdapter {
    private ArrayList<String> cities;
    private Context context;
    private int backgroundTextColor, textColor;

    public CitiesAdapter(Context context, ArrayList<String> cities) {
        this.context = context;
        this.cities = cities;
        this.backgroundTextColor = Color.GREEN;
        this.textColor = Color.BLACK;
    }

    public void setBackgroundTextColor(int color) {
        this.backgroundTextColor = color;
    }

    public void setTextColor(int color) {
        this.textColor = color;
    }

    public void setCities(ArrayList<String> cities) {
        this.cities = cities;
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public Object getItem(int i) {
        return cities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        TextView textview = new TextView(context);
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            //textview.setLayoutParams(new GridView.LayoutParams(85, 85));
            //textview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //textview.setPadding(8, 8, 8, 8);
            textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textview.setGravity(Gravity.CENTER);
            textview.setBackgroundColor(backgroundTextColor);
            textview.setTextColor(textColor);
        } else {
            textview = (TextView) convertView;
        }
        textview.setText(cities.get(i));
        textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return textview;
    }
}
