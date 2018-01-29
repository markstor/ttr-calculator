package com.casalprim.marc.tickettoridecalculator.ui.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.casalprim.marc.tickettoridecalculator.game.RouteCard;

/**
 * Created by marc on 29/01/18.
 */

public class TicketView extends View {

    private final RouteCard ticket;

    public TicketView(Context context, RouteCard ticket) {
        super(context);
        this.ticket = ticket;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
