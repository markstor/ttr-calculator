package com.casalprim.marc.tickettoridecalculator.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by marc on 17/01/18.
 */

public class PlayerButtonDrawable extends Drawable {

    private int color;
    private boolean selected;

    public PlayerButtonDrawable(int color, boolean selected) {
        this.color = color;
        this.selected = selected;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        //int lvl = getLevel();
        Rect b = getBounds();
        Paint paint = new Paint();
        if (selected)
            this.setAlpha(255);
        else
            this.setAlpha(50);
        paint.setColor(color);
        canvas.drawRect(b, paint);
        if (selected) {
            paint.setColor(Color.LTGRAY);
            paint.setStrokeWidth(10.0f);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(b, paint);
        }
    }


    @Override
    public void setAlpha(int i) {
        int alpha = i;
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        this.color = Color.argb(alpha, red, green, blue);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
