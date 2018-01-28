package com.casalprim.marc.tickettoridecalculator.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.casalprim.marc.tickettoridecalculator.game.City;
import com.casalprim.marc.tickettoridecalculator.game.Edge;
import com.casalprim.marc.tickettoridecalculator.game.GameMap;
import com.casalprim.marc.tickettoridecalculator.game.Player;

import java.util.HashMap;

import static com.casalprim.marc.tickettoridecalculator.game.Game.PLAYER_COLOR_MAP;

/**
 * Created by marc on 22/01/18.
 */

public class MapView extends View {
    private PointF center, d, p1, p2;
    private Player.PlayerColor selectedColor;
    private OnEdgeSelectedListener mListener;
    private City selectedCity;
    private Rect r;
    private Paint mCityNamePaint, mEdgesPaint, mCityCirclePaint, mSelectionStrokePaint;
    private int spRadiusSize;
    private GameMap gameMap;
    private float radiusCities;
    private float maxX, maxY;
    private float drawableWidth, drawableHeight, xpad, ypad;
    private HashMap<PointF, City> cityLocations;
    private float normalizationFactor;
    private boolean rotateCityCoordinates;
    private ScaleGestureDetector mScaleDetector;
    private float mLastCityTouchX, mLastCityTouchY;
    private float mPosX, mPosY;
    private float mTrackWidthFactor, mTrackInLongPathWidthFactor;
    private int mCityCircleColor, mCityHighlightedColor, mSelectionStrokeColor;
    private int mCityNameColor;
    private int mCityHighlightedNameColor;

    public MapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        r = new Rect();
        selectedCity = null;
        selectedColor = null;
        mListener = null;
        cityLocations = new HashMap<>();
        //mScaleDetector = new ScaleGestureDetector();
        mCityCircleColor = Color.LTGRAY;
        spRadiusSize = 10;
        radiusCities = spRadiusSize * getResources().getDisplayMetrics().scaledDensity;
        mTrackWidthFactor = 0.3f;
        mTrackInLongPathWidthFactor = 2f * mTrackWidthFactor;

        mSelectionStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCityNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCityNameColor = Color.BLACK;
        mCityHighlightedNameColor = mCityNameColor;
        mCityNamePaint.setColor(mCityNameColor);
        mCityNamePaint.setTextAlign(Paint.Align.LEFT);
        mCityCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCityCirclePaint.setColor(mCityCircleColor);
        mEdgesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEdgesPaint.setStyle(Paint.Style.FILL);

        mCityHighlightedColor = Color.LTGRAY;
        mSelectionStrokeColor = Color.LTGRAY;
        mSelectionStrokePaint.setStrokeWidth(mTrackWidthFactor * radiusCities);
        mCityNamePaint.setTextSize(radiusCities);
        normalizationFactor = 0.8f;
        rotateCityCoordinates = true;
        drawableHeight = 1200;
        drawableWidth = 900;
        maxX = 800;
        maxY = 500;


        //this.gameMap=new GameMap();


    }

    public void setSelectedColor(Player.PlayerColor selectedColor) {
        this.selectedColor = selectedColor;
        mCityHighlightedColor = PLAYER_COLOR_MAP.get(selectedColor);
        mSelectionStrokeColor = PLAYER_COLOR_MAP.get(selectedColor);
        mCityHighlightedNameColor = Color.BLACK;
        mSelectionStrokePaint.setColor(mSelectionStrokeColor);
        //mCityNamePaint.setColor(mCityNameColor);
        invalidate();
    }

    public void setGameMap(GameMap gameMap) {
        for (City city : gameMap.getCities()) {
            if (city.getCoordX() > maxX) maxX = city.getCoordX();
            if (city.getCoordY() > maxY) maxY = city.getCoordY();
        }
        this.gameMap = gameMap;
        invalidate();
        requestLayout();
        //updateNormFactor();
        //Log.d("SetGameMap","maxX: "+maxX+" maxY: "+maxY+" Rotate: "+ rotateCityCoordinates +", NormFactor: "+ normalizationFactor);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Account for padding
        xpad = (float) (getPaddingLeft() + getPaddingRight());
        ypad = (float) (getPaddingTop() + getPaddingBottom());
        //this.radiusCities=0.03f*w;
        drawableWidth = (float) w - xpad - 2.2f * radiusCities;
        drawableHeight = (float) h - ypad - 2.2f * radiusCities;
        rotateCityCoordinates = (drawableWidth < drawableHeight);
        Log.d("SizeChanged", "Width: " + drawableWidth + " Height: " + drawableHeight + " Radius: " + radiusCities);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (gameMap != null) {

            //Log.i("Draw Map","Drawing edges");
            for (Edge edge : gameMap.getEdges()) {
                mEdgesPaint.setStrokeWidth(mTrackWidthFactor * radiusCities);
                p1 = transformCoordinates(edge.getCity1().getCoordX(), edge.getCity1().getCoordY());
                p2 = transformCoordinates(edge.getCity2().getCoordX(), edge.getCity2().getCoordY());
                mEdgesPaint.setColor(edge.getColor());
                if (edge.hasOccupant(selectedColor)) {
                    if (edge.isInLongestPath(selectedColor)) {
                        mEdgesPaint.setStrokeWidth(mTrackInLongPathWidthFactor * radiusCities);
                    }
                }

                if (edge.hasTwoRails()) {
                    double angle = Math.atan2((p1.y - p2.y), (p1.x - p2.x));
                    if ((p1.x - p2.x) < 0) angle += 2 * Math.PI;
                    angle += Math.PI / 2;
                    float factor = 0.5f * mEdgesPaint.getStrokeWidth() / radiusCities;
                    float dx = radiusCities * ((float) Math.cos(angle)) * factor;
                    float dy = radiusCities * ((float) Math.sin(angle)) * factor;
                    canvas.drawLine(p1.x + dx, p1.y + dy, p2.x + dx, p2.y + dy, mEdgesPaint);
                    mEdgesPaint.setColor(edge.getColor2());
                    canvas.drawLine(p1.x - dx, p1.y - dy, p2.x - dx, p2.y - dy, mEdgesPaint);
                } else {
                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mEdgesPaint);
                }
            }
            //Log.i("Draw Map","Drawing selection");
            if (mLastCityTouchX > 0 && mLastCityTouchY > 0 && mPosX > 0 && mPosY > 0) {
                canvas.drawLine(mLastCityTouchX, mLastCityTouchY, mPosX, mPosY, mSelectionStrokePaint);
            }

            //Log.i("Draw Map","Drawing cities");
            for (City city : gameMap.getCities()) {

                PointF center = transformCoordinates(city.getCoordX(), city.getCoordY());
                //Log.d("Draw Map","Drawing city "+city.getName()+" at ("+center.x+","+center.y+") coordinates. Radius: "+radiusCities);
                if (city.equals(selectedCity) || (selectedColor != null && city.hasPlayerRail(selectedColor))) {
                    mCityCirclePaint.setColor(mCityHighlightedColor);
                    mCityNamePaint.setColor(mCityHighlightedNameColor);
                } else {
                    mCityCirclePaint.setColor(mCityCircleColor);
                }
                canvas.drawCircle(center.x, center.y, this.radiusCities, mCityCirclePaint);
                cityLocations.put(center, city);

                String cityInitial = city.getName().substring(0, 1).toUpperCase();
                mCityNamePaint.getTextBounds(cityInitial, 0, 1, r);
                canvas.drawText(cityInitial, center.x - r.width() / 2f, center.y + r.height() / 2f, mCityNamePaint);
            }


        } else {
            Log.i("Draw Map", "Game Map is null");
        }
    }

    public void setOnEdgeSelectedListener(OnEdgeSelectedListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let the ScaleGestureDetector inspect all events.
        //mScaleDetector.onTouchEvent(event);

        final int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                final float y = event.getY();

                // Remember where we started (for dragging)
                mPosX = x;
                mPosY = y;
                //Log.d("TouchEvent","Action Down"+" ("+ mLastCityTouchX +","+ mLastCityTouchY +")");
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float x = event.getX();
                final float y = event.getY();

                // Calculate the distance moved
                final float dx = x - mPosX;
                final float dy = y - mPosY;

                mPosX += dx;
                mPosY += dy;

                City city = obtainCity(new PointF(x, y));

                //Log.d("TouchEvent","Action Move"+" ("+ mLastCityTouchX +","+ mLastCityTouchY +") to ("+ mPosX +","+ mPosY+")");

                if (city != null) {
                    if (selectedCity == null) { //no previous
                        selectedCity = city;
                        mLastCityTouchX = x;
                        mLastCityTouchY = y;
                    } else if (!selectedCity.equals(city)) {//add edge
                        assignEdge(selectedCity, city, selectedColor);
                        selectedCity = city;
                        mLastCityTouchX = -1;
                        mLastCityTouchY = -1;
                    }
                }

                invalidate();
                break;
            }

            default: {
                selectedCity = null;
                mLastCityTouchX = -1;
                mLastCityTouchY = -1;
                break;
            }
        }
        return true;
    }

    private void assignEdge(City selectedCity, City city, Player.PlayerColor selectedColor) {
        if (mListener != null) {
            Edge edge = selectedCity.getEdgeTo(city);
            if (edge != null) {
                Log.d("Edge Assign", "Assigning edge from" + edge.getCity1().getName() + " to " + edge.getCity2().getName());
                mListener.onEdgeSelected(edge, selectedColor);
            }
        }
    }

    private PointF transformCoordinates(float x, float y) {
        return transformCoordinates(new PointF(x, y));

    }

    private PointF transformCoordinates(PointF pin) {
        //pin in image coordinates. pout in canvas coordinates
        float cxin = pin.x;
        float cyin = pin.y;

        float normalizationFactorX = drawableWidth / maxX;
        float normalizationFactorY = drawableHeight / maxY;

        if (rotateCityCoordinates) {
            cxin = maxY - pin.y;
            cyin = pin.x;
            normalizationFactorX = drawableWidth / maxY;
            normalizationFactorY = drawableHeight / maxX;
        }
        float xoffset = getPaddingLeft() + 1.1f * radiusCities;
        float yoffset = getPaddingTop() + 1.1f * radiusCities;

        float cxout = cxin * normalizationFactorX + xoffset;
        float cyout = cyin * normalizationFactorY + yoffset;
        //Log.d("CoordTransform","("+cxin+","+cyin+") to ("+cxout+","+cyout+")");
        return new PointF(cxout, cyout);
    }

    private City obtainCity(PointF coordinates) {
        for (PointF cityCenter : cityLocations.keySet()) {

            float dx = coordinates.x - cityCenter.x;
            float dy = coordinates.y - cityCenter.y;
            if ((dx * dx + dy * dy) < 7 * radiusCities) { //coordinates near city
                City city = cityLocations.get(cityCenter);
                //Log.d("Obtain City", "City "+city.getName()+" found at " + coordinates);
                return city;
            }
        }
        //Log.d("Obtain City","City not found at "+coordinates);
        return null;
    }

    public interface OnEdgeSelectedListener {
        void onEdgeSelected(Edge selectedEdge, Player.PlayerColor pColor);
    }

}
