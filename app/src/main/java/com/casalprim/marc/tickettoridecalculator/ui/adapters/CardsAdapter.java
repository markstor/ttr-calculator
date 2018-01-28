package com.casalprim.marc.tickettoridecalculator.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.casalprim.marc.tickettoridecalculator.game.RouteCard;

import java.util.ArrayList;

/**
 * Created by marc on 18/01/18.
 */

public class CardsAdapter extends BaseAdapter {
    private ArrayList<RouteCard> cards;
    private Context context;

    public CardsAdapter(Context context, ArrayList<RouteCard> cards) {
        this.context = context;
        this.cards = cards;
    }

    public void setCards(ArrayList<RouteCard> cards) {
        this.cards = cards;
        this.notifyDataSetChanged();
    }

    public void addCard(RouteCard card) {
        this.cards.add(card);
        this.notifyDataSetChanged();
    }

    public void removeCard(RouteCard card) {
        this.cards.remove(card);
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int i) {
        return cards.get(i);
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
            textview.setTextColor(Color.BLACK);
        } else {
            textview = (TextView) convertView;
        }
        RouteCard card = cards.get(i);

        textview.setText(card.getFrom() + " - " + card.getTo() + "  " + card.getPoints());
        return textview;
    }
}
