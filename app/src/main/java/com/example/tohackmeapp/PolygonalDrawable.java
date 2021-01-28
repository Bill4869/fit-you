package com.example.tohackmeapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import java.util.List;

public class PolygonalDrawable extends Drawable {
    static int VALUE_RANGE = 10;
    private int numberOfSides = 3;
    private Path polygon = new Path();
    private Path polygon2 = new Path();
    private Path temporal = new Path();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<Integer> valueList;


    public PolygonalDrawable(int sides, List<Integer> values) {
        valueList = values;

        int color1 = Color.parseColor("#dbe2ef");
        int color2 = Color.parseColor("#112d4e");
        paint.setColor(color1);
        paint2.setColor(color2);

//        polygon.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        this.numberOfSides = sides;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(polygon, paint);
        canvas.drawPath(polygon2, paint2);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
        paint2.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
        paint2.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return paint.getAlpha();
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        computeHex(bounds);
        invalidateSelf();
    }

    public void computeHex(Rect bounds) {

        final int width = bounds.width();
        final int height = bounds.height();
        final int size = Math.min(width, height);
        final int centerX = bounds.left + (width / 2);
        final int centerY = bounds.top + (height / 2);

        polygon.reset();
        polygon.addPath(createHexagon(size, centerX, centerY));
        polygon2.reset();
//        polygon2.addPath(createHexagon2((int) (size * .8f), centerX, centerY));
        polygon2.addPath(createHexagon2(size, centerX, centerY));
    }

    private Path createHexagon(int size, int centerX, int centerY) {
        final float section = (float) (2.0 * Math.PI / numberOfSides);
        int radius = size / 2;
        Path polygonPath = temporal;
        polygonPath.reset();
        polygonPath.moveTo((centerX + radius * (float)Math.cos(0)), (centerY + radius * (float)Math.sin(0)));
//        polygonPath.moveTo(centerX, (centerY - radius));

        for (int i = 1; i < numberOfSides; i++) {
            polygonPath.lineTo((centerX + radius * (float)Math.cos(section * i)),
                    (centerY + radius * (float)Math.sin(section * i)));
        }

        polygonPath.close();
        return polygonPath;
    }
    private Path createHexagon2(int size, int centerX, int centerY) {
        final float section = (float) (2.0 * Math.PI / numberOfSides);
        int radius = size / 2;
        Path polygonPath = temporal;
        polygonPath.reset();
        polygonPath.moveTo((centerX + (float)(radius * valueList.get(0) / VALUE_RANGE) * (float)Math.cos(0)), (centerY + (float)(radius * valueList.get(0) / VALUE_RANGE) * (float)Math.sin(0)));
//        polygonPath.moveTo(centerX, (centerY - radius));

        for (int i = 1; i < numberOfSides; i++) {
            polygonPath.lineTo((centerX + (float)(radius * valueList.get(i) / VALUE_RANGE) * (float)Math.cos(section * i)),
                    (centerY + (float)(radius * valueList.get(i) / VALUE_RANGE) * (float)Math.sin(section * i)));
        }

        polygonPath.close();
        return polygonPath;
    }
}
