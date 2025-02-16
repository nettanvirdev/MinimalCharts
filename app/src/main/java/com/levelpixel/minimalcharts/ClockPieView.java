/*
 * Copyright © 2025 Tanvir Ahamed
 *  All rights reserved.
 *
 *  This software is developed and maintained by Tanvir Ahamed, CEO and Founder of LevelPixela.
 *  Unauthorized copying, modification, distribution, or use of this software in any medium is strictly prohibited.
 *
 *  For inquiries, permissions, or contributions, contact LevelPixel at [https://www.levelpixel.net].
 */

package com.levelpixel.minimalcharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class ClockPieView extends View {

    // Constants for default colors
    private static final int TEXT_COLOR = Color.parseColor("#9B9A9B");
    private static final int GRAY_COLOR = Color.parseColor("#D4D3D4");
    private static final int RED_COLOR = Color.argb(50, 255, 0, 51);

    // Paint objects for drawing
    private final Paint textPaint = new Paint();
    private final Paint redPaint = new Paint();
    private final Paint linePaint = new Paint();
    private final Paint whitePaint = new Paint();
    // Measurement points
    private final Point pieCenterPoint = new Point();
    private final Point tempPoint = new Point();
    private final Point tempPointRight = new Point();
    // Bounds and drawing areas
    private final RectF cirRect = new RectF();
    private final Rect textRect = new Rect();
    // List of pie slices
    private final ArrayList<ClockPie> pieArrayList = new ArrayList<>();
    // Animator for pie slice motion
    private final Runnable animator = new Runnable() {
        @Override
        public void run() {
            boolean needNewFrame = false;
            for (ClockPie pie : pieArrayList) {
                pie.update();
                if (!pie.isAtRest()) {
                    needNewFrame = true;
                }
            }
            if (needNewFrame) {
                postDelayed(this, 10);
            }
            invalidate();
        }
    };
    // Dimensions and metrics
    private int mViewWidth;
    private int mViewHeight;
    private int textSize;
    private int pieRadius;
    private int lineLength;
    private int lineThickness;
    // Text measurements
    private float leftTextWidth;
    private float rightTextWidth;
    private float topTextHeight;

    // Constructors
    public ClockPieView(Context context) {
        this(context, null);
    }

    public ClockPieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializePaints(context);
    }

    // Initializes paint objects and metrics
    private void initializePaints(Context context) {
        textSize = Utils.sp2px(context, 15);
        lineThickness = Utils.dip2px(context, 1);
        lineLength = Utils.dip2px(context, 10);

        configurePaint(textPaint, TEXT_COLOR, Paint.Align.CENTER);
        redPaint.set(textPaint);
        redPaint.setColor(RED_COLOR);

        linePaint.set(textPaint);
        linePaint.setColor(GRAY_COLOR);
        linePaint.setStrokeWidth(lineThickness);

        whitePaint.set(linePaint);
        whitePaint.setColor(Color.WHITE);

        leftTextWidth = textPaint.measureText("18");
        rightTextWidth = textPaint.measureText("6");
        topTextHeight = textRect.height();
    }

    private void configurePaint(Paint paint, int color, Paint.Align align) {
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setTextSize(textSize);
        paint.setTextAlign(align);
        paint.getTextBounds("18", 0, 1, textRect);
    }

    // Sets pie slice data and starts the animator
    public void setDate(ArrayList<ClockPie> helperList) {
        updatePieList(helperList);
        removeCallbacks(animator);
        post(animator);
    }

    private void updatePieList(ArrayList<ClockPie> helperList) {
        if (helperList != null && !helperList.isEmpty()) {
            syncPieList(helperList);
        } else {
            pieArrayList.clear();
        }
    }

    private void syncPieList(ArrayList<ClockPie> helperList) {
        int existingSize = pieArrayList.size();
        for (int i = 0; i < helperList.size(); i++) {
            if (i >= existingSize) {
                pieArrayList.add(new ClockPie(0, 0, helperList.get(i)));
            } else {
                pieArrayList.set(i, pieArrayList.get(i).setTarget(helperList.get(i)));
            }
        }
        removeExtraPies(helperList.size());
    }

    private void removeExtraPies(int newSize) {
        int excessCount = pieArrayList.size() - newSize;
        for (int i = 0; i < excessCount; i++) {
            pieArrayList.remove(pieArrayList.size() - 1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawPies(canvas);
    }

    private void drawBackground(Canvas canvas) {
        for (int i = 0; i < 12; i++) {
            calculateLinePoints(i);
            canvas.drawLine(tempPoint.x, tempPoint.y, tempPointRight.x, tempPointRight.y, linePaint);
        }
        drawCircles(canvas);
        drawText(canvas);
    }

    private void calculateLinePoints(int i) {
        double angle = Math.PI / 12 * i;
        int offsetX = (int) (Math.sin(angle) * (pieRadius + lineLength));
        int offsetY = (int) (Math.cos(angle) * (pieRadius + lineLength));

        tempPoint.set(pieCenterPoint.x - offsetX, pieCenterPoint.y - offsetY);
        tempPointRight.set(pieCenterPoint.x + offsetX, pieCenterPoint.y + offsetY);
    }

    private void drawCircles(Canvas canvas) {
        canvas.drawCircle(pieCenterPoint.x, pieCenterPoint.y, pieRadius + lineLength / 2, whitePaint);
        canvas.drawCircle(pieCenterPoint.x, pieCenterPoint.y, pieRadius + lineThickness, linePaint);
        canvas.drawCircle(pieCenterPoint.x, pieCenterPoint.y, pieRadius, whitePaint);
    }

    private void drawText(Canvas canvas) {
        canvas.drawText("0", pieCenterPoint.x, topTextHeight, textPaint);
        canvas.drawText("12", pieCenterPoint.x, mViewHeight, textPaint);
        canvas.drawText("18", leftTextWidth / 2, pieCenterPoint.y + textRect.height() / 2, textPaint);
        canvas.drawText("6", mViewWidth - rightTextWidth / 2,
                pieCenterPoint.y + textRect.height() / 2, textPaint);
    }

    private void drawPies(Canvas canvas) {
        for (ClockPie helper : pieArrayList) {
            canvas.drawArc(cirRect, helper.getStart(), helper.getSweep(), true, redPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewWidth = measureDimension(widthMeasureSpec, 3);
        mViewHeight = measureDimension(heightMeasureSpec, mViewWidth);

        calculateDrawingArea();
        setMeasuredDimension(mViewWidth, mViewHeight);
    }

    private int measureDimension(int measureSpec, int preferredSize) {
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.EXACTLY:
                return specSize;
            case MeasureSpec.AT_MOST:
                return Math.min(preferredSize, specSize);
            default:
                return preferredSize;
        }
    }

    private void calculateDrawingArea() {
        pieRadius = mViewWidth / 2 - lineLength * 2 - (int) (textPaint.measureText("18") / 2);
        pieCenterPoint.set(
                mViewWidth / 2 - (int) rightTextWidth / 2 + (int) leftTextWidth / 2,
                mViewHeight / 2 + textSize / 2 - (int) (textPaint.measureText("18") / 2)
        );
        cirRect.set(pieCenterPoint.x - pieRadius, pieCenterPoint.y - pieRadius,
                pieCenterPoint.x + pieRadius, pieCenterPoint.y + pieRadius);
    }
}
