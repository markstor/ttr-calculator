package com.casalprim.marc.tickettoridecalculator.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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
        LinearLayout ll;
        if (convertView == null) {

            ll = new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setGravity(Gravity.CENTER);
        } else {
            ll = (LinearLayout) convertView;
            ll.removeAllViewsInLayout();
        }
        RouteCard card = cards.get(i);
        if (card.isCompleted()) {
            ll.setBackgroundColor(Color.argb(200, 20, 230, 20));
        } else {
            ll.setBackgroundColor(Color.argb(200, 230, 20, 20));
        }


        TextView fromView = new TextView(context);
        fromView.setText(card.getFrom());
        fromView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        TextView pointsView = new TextView(context);
        pointsView.setText(((Integer) card.getPoints()).toString());
        pointsView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        pointsView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        TextView toView = new TextView(context);
        toView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        toView.setText(card.getTo());

        ll.addView(fromView);
        ll.addView(pointsView);
        ll.addView(toView);
        return ll;
    }
}
