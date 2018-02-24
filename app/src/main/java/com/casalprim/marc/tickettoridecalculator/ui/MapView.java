package com.casalprim.marc.tickettoridecalculator.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.casalprim.marc.tickettoridecalculator.game.City;
import com.casalprim.marc.tickettoridecalculator.game.Edge;
import com.casalprim.marc.tickettoridecalculator.game.GameMap;
import com.casalprim.marc.tickettoridecalculator.game.Player;

import java.util.HashMap;
import java.util.Locale;

import static com.casalprim.marc.tickettoridecalculator.game.Game.PLAYER_COLOR_MAP;
import static com.casalprim.marc.tickettoridecalculator.game.Game.PLAYER_TEXT_COLOR_MAP;

/**
 * Created by marc on 22/01/18.
 */

public class MapView extends View {
    private PointF p1, p2;
    private Player.PlayerColor selectedColor;
    private OnEdgeSelectedListener mListener;
    private City selectedCity;
    private Rect r, backgroundRect;
    private Paint mCityNamePaint, mEdgesPaint, mCityCirclePaint, mSelectionStrokePaint;
    private int spRadiusSize;
    private GameMap gameMap;
    private float radiusCities;
    private float mapWidth, mapHeight;
    private float drawableWidth, drawableHeight;
    private HashMap<PointF, City> cityLocations;
    private boolean rotateCityCoordinates;
    private float mLastCityTouchX, mLastCityTouchY;
    private float mPosX, mPosY;
    private float mTrackWidthFactor, mTrackInLongPathWidthFactor;
    private int mCityCircleColor, mCityHighlightedColor, mSelectionStrokeColor;
    private int mCityNameColor;
    private int mCityHighlightedNameColor;
    private Bitmap bgimage, orgBgImage;
    private float xoffset, yoffset;

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
        spRadiusSize = 15;


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

        rotateCityCoordinates = true;
        drawableHeight = 1200;
        drawableWidth = 900;
        mapWidth = 800;
        mapHeight = 500;

        changeRadiusCities(spRadiusSize * getResources().getDisplayMetrics().scaledDensity);

        //this.gameMap=new GameMap();


    }

    private void changeRadiusCities(float v) {
        radiusCities = v;
        mSelectionStrokePaint.setStrokeWidth(mTrackWidthFactor * radiusCities);
        mCityNamePaint.setTextSize(radiusCities);
    }

    public void setSelectedColor(Player.PlayerColor selectedColor) {
        this.selectedColor = selectedColor;
        mCityHighlightedColor = PLAYER_COLOR_MAP.get(selectedColor);
        mSelectionStrokeColor = PLAYER_COLOR_MAP.get(selectedColor);
        mCityHighlightedNameColor = PLAYER_TEXT_COLOR_MAP.get(selectedColor);
        mSelectionStrokePaint.setColor(mSelectionStrokeColor);
        //mCityNamePaint.setColor(mCityNameColor);
        invalidate();
    }

    public void setGameMap(GameMap gameMap) {

        this.gameMap = gameMap;
        invalidate();
        requestLayout();
        //updateNormFactor();
        //Log.d("SetGameMap","mapWidth: "+mapWidth+" mapHeight: "+mapHeight+" Rotate: "+ rotateCityCoordinates +", NormFactor: "+ normalizationFactor);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Account for padding
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());
        float extraPadding = 0;//1.5f * radiusCities;

        drawableWidth = (float) w - xpad - extraPadding;
        drawableHeight = (float) h - ypad - extraPadding;

        xoffset = getPaddingLeft() + extraPadding / 2;
        yoffset = getPaddingTop() + extraPadding / 2;

        rotateCityCoordinates = (drawableWidth < drawableHeight);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        orgBgImage = BitmapFactory.decodeResource(getResources(), this.gameMap.getBackgroundImageId(), options);
        mapWidth = orgBgImage.getWidth();
        mapHeight = orgBgImage.getHeight();
        if (rotateCityCoordinates) {
            PointF lt = transformCoordinates(new PointF(0, orgBgImage.getHeight()));
            PointF rb = transformCoordinates(new PointF(orgBgImage.getWidth(), 0));
            backgroundRect = new Rect((int) lt.x, (int) lt.y, (int) rb.x, (int) rb.y);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bgimage = Bitmap.createBitmap(orgBgImage, 0, 0, orgBgImage.getWidth(), orgBgImage.getHeight(), matrix, true);
        } else {
            PointF lt = transformCoordinates(new PointF(0, 0));
            PointF rb = transformCoordinates(new PointF(orgBgImage.getWidth(), orgBgImage.getHeight()));
            backgroundRect = new Rect((int) lt.x, (int) lt.y, (int) rb.x, (int) rb.y);
            bgimage = orgBgImage;
        }

        float d = calcMinimumDistanceBetweenCities();
        if (d > 0) {
            changeRadiusCities(0.9f * d / 2);
        }


        Log.d("SizeChanged", "Width: " + drawableWidth + " Height: " + drawableHeight + " Radius: " + radiusCities);

    }

    private float calcMinimumDistanceBetweenCities() {
        if (gameMap == null || gameMap.getCities().isEmpty()) {
            return -1;
        }

        float minDistance = Float.MAX_VALUE;
        for (int i = 0; i < gameMap.getCities().size(); i++) {
            for (int j = i + 1; j < gameMap.getCities().size(); j++) {
                PointF center1 = transformCoordinates(gameMap.getCities().get(i).getCoordX(), gameMap.getCities().get(i).getCoordY());
                PointF center2 = transformCoordinates(gameMap.getCities().get(j).getCoordX(), gameMap.getCities().get(j).getCoordY());
                float dx = center1.x - center2.x;
                float dy = center1.y - center2.y;
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                if (d < minDistance)
                    minDistance = d;
            }
        }
        return minDistance;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bgimage, null, backgroundRect, null);
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
                boolean seeLengths = false;
                if (seeLengths) {
                    Paint paint = new Paint();
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(20);
                    canvas.drawText(String.format(Locale.US, "%d", edge.getLength()), (p1.x + p2.x) / 2, (p1.y + p2.y) / 2, paint);
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
                    mCityNamePaint.setColor(mCityNameColor);
                    mCityCirclePaint.setColor(mCityCircleColor);
                }
                if (city.equals(selectedCity))
                    canvas.drawCircle(center.x, center.y, 1.5f * this.radiusCities, mCityCirclePaint);
                else
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
                City city = obtainCity(new PointF(x, y));

                //Log.d("TouchEvent","Action Move"+" ("+ mLastCityTouchX +","+ mLastCityTouchY +") to ("+ mPosX +","+ mPosY+")");

                if (city != null) {
                    if (selectedCity == null) { //no previous
                        selectedCity = city;
                        PointF cityCoordinates = transformCoordinates(city.getCoordX(), city.getCoordY());
                        mLastCityTouchX = cityCoordinates.x;
                        mLastCityTouchY = cityCoordinates.y;
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

            case MotionEvent.ACTION_MOVE: {
                final float x = event.getX();
                final float y = event.getY();

                mPosX = x;
                mPosY = y;

                City city = obtainCity(new PointF(x, y));

                //Log.d("TouchEvent","Action Move"+" ("+ mLastCityTouchX +","+ mLastCityTouchY +") to ("+ mPosX +","+ mPosY+")");

                if (city != null) {
                    if (selectedCity == null) { //no previous
                        selectedCity = city;
                        PointF cityCoordinates = transformCoordinates(city.getCoordX(), city.getCoordY());
                        mLastCityTouchX = cityCoordinates.x;
                        mLastCityTouchY = cityCoordinates.y;
                    } else if (!selectedCity.equals(city)) {//add edge
                        assignEdge(selectedCity, city, selectedColor);
                        selectedCity = city;
                        PointF cityCoordinates = transformCoordinates(city.getCoordX(), city.getCoordY());
                        mLastCityTouchX = cityCoordinates.x;
                        mLastCityTouchY = cityCoordinates.y;
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

        float normalizationFactorX = drawableWidth / mapWidth;
        float normalizationFactorY = drawableHeight / mapHeight;

        if (rotateCityCoordinates) {
            cxin = mapHeight - pin.y;
            cyin = pin.x;
            normalizationFactorX = drawableWidth / mapHeight;
            normalizationFactorY = drawableHeight / mapWidth;
        }


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
