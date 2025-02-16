/*
 * Copyright © 2025 Tanvir Ahamed
 * All rights reserved.
 *
 * This software is developed and maintained by Tanvir Ahamed, CEO and Founder of LevelPixela.
 * Unauthorized copying, modification, distribution, or use of this software in any medium is strictly prohibited.
 *
 * For inquiries, permissions, or contributions, contact LevelPixela at [https://www.levelpixel.net].
 */

package com.levelpixel.minimalcharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class ChartBarView extends View {

    // Constants for bar and text styling
    private static final int TEXT_COLOR = Color.parseColor("#9B9A9B");
    private static final int BACKGROUND_COLOR = Color.parseColor("#F6F6F6");
    private static final int FOREGROUND_COLOR = Color.parseColor("#FC496D");

    // Layout-related dimensions
    private int MINI_BAR_WIDTH;
    private int BAR_SIDE_MARGIN;
    private int TEXT_TOP_MARGIN;

    // Paint objects for drawing
    private Paint textPaint;
    private Paint bgPaint;
    private Paint fgPaint;

    // Data-related attributes
    private ArrayList<Float> percentList;
    private ArrayList<Float> targetPercentList;
    // Animation-related runnable
    private final Runnable animator = new Runnable() {
        @Override
        public void run() {
            boolean needNewFrame = false;
            for (int i = 0; i < targetPercentList.size(); i++) {
                float current = percentList.get(i);
                float target = targetPercentList.get(i);

                if (Math.abs(target - current) < 0.02f) {
                    percentList.set(i, target);
                } else {
                    percentList.set(i, current + (target > current ? 0.02f : -0.02f));
                    needNewFrame = true;
                }
            }

            if (needNewFrame) {
                postDelayed(this, 20);
            }
            invalidate();
        }
    };
    private ArrayList<String> bottomTextList = new ArrayList<>();
    private int barWidth;
    private int bottomTextDescent;
    private int bottomTextHeight;
    private final boolean autoSetWidth = true;
    // Rect for drawing bars
    private Rect rect;
    // Top margin for bars
    private int topMargin;

    // Constructor for programmatic instantiation
    public ChartBarView(Context context) {
        this(context, null);
    }

    // Constructor for XML-based instantiation
    public ChartBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    // Initialize paint objects and dimensions
    private void initialize(Context context) {
        bgPaint = createPaint(BACKGROUND_COLOR);
        fgPaint = createPaint(FOREGROUND_COLOR);
        textPaint = createTextPaint(context);

        rect = new Rect();
        topMargin = Utils.dip2px(context, 5);
        barWidth = Utils.dip2px(context, 22);
        MINI_BAR_WIDTH = Utils.dip2px(context, 22);
        BAR_SIDE_MARGIN = Utils.dip2px(context, 22);
        TEXT_TOP_MARGIN = Utils.dip2px(context, 5);

        percentList = new ArrayList<>();
    }

    // Helper method to create a Paint object
    private Paint createPaint(int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        return paint;
    }

    // Helper method to create a text Paint object
    private Paint createTextPaint(Context context) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(TEXT_COLOR);
        paint.setTextSize(Utils.sp2px(context, 15));
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }

    // Set the bottom text labels
    public void setBottomTextList(ArrayList<String> bottomStringList) {
        bottomTextList = bottomStringList;
        updateBarDimensions();
        postInvalidate();
    }

    // Update bar dimensions based on the text
    private void updateBarDimensions() {
        Rect textBounds = new Rect();
        bottomTextDescent = 0;
        barWidth = MINI_BAR_WIDTH;

        for (String text : bottomTextList) {
            textPaint.getTextBounds(text, 0, text.length(), textBounds);

            bottomTextHeight = Math.max(bottomTextHeight, textBounds.height());
            if (autoSetWidth) {
                barWidth = Math.max(barWidth, textBounds.width());
            }
            bottomTextDescent = Math.max(bottomTextDescent, Math.abs(textBounds.bottom));
        }

        setMinimumWidth(2);
    }

    // Set the data for the chart
    public void setDataList(ArrayList<Integer> dataList, int max) {
        if (max == 0) max = 1;

        targetPercentList = new ArrayList<>();
        for (int value : dataList) {
            targetPercentList.add(1 - (float) value / max);
        }

        adjustPercentListSize();
        removeCallbacks(animator);
        post(animator);
    }

    // Adjust the size of percentList to match targetPercentList
    private void adjustPercentListSize() {
        if (percentList.size() < targetPercentList.size()) {
            for (int i = percentList.size(); i < targetPercentList.size(); i++) {
                percentList.add(1f);
            }
        } else if (percentList.size() > targetPercentList.size()) {
            percentList.subList(targetPercentList.size(), percentList.size()).clear();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBars(canvas);
        drawBottomText(canvas);
    }

    // Draw the bars
    private void drawBars(Canvas canvas) {
        if (percentList == null || percentList.isEmpty()) return;

        int i = 1;
        for (Float percent : percentList) {
            drawBarBackground(canvas, i);
            drawBarForeground(canvas, i, percent);
            i++;
        }
    }

    // Draw the background of a single bar
    private void drawBarBackground(Canvas canvas, int i) {
        rect.set(
                BAR_SIDE_MARGIN * i + barWidth * (i - 1),
                topMargin,
                (BAR_SIDE_MARGIN + barWidth) * i,
                getHeight() - bottomTextHeight - TEXT_TOP_MARGIN
        );
        canvas.drawRect(rect, bgPaint);
    }

    // Draw the foreground of a single bar
    private void drawBarForeground(Canvas canvas, int i, Float percent) {
        rect.set(
                BAR_SIDE_MARGIN * i + barWidth * (i - 1),
                topMargin + (int) ((getHeight() - topMargin - bottomTextHeight - TEXT_TOP_MARGIN) * percent),
                (BAR_SIDE_MARGIN + barWidth) * i,
                getHeight() - bottomTextHeight - TEXT_TOP_MARGIN
        );
        canvas.drawRect(rect, fgPaint);
    }

    // Draw the bottom text labels
    private void drawBottomText(Canvas canvas) {
        if (bottomTextList == null || bottomTextList.isEmpty()) return;

        int i = 1;
        for (String text : bottomTextList) {
            canvas.drawText(
                    text,
                    BAR_SIDE_MARGIN * i + barWidth * (i - 1) + barWidth / 2,
                    getHeight() - bottomTextDescent,
                    textPaint
            );
            i++;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int measureSpec) {
        int preferred = bottomTextList.size() * (barWidth + BAR_SIDE_MARGIN);
        return getMeasurement(measureSpec, preferred);
    }

    private int measureHeight(int measureSpec) {
        int preferred = 222;
        return getMeasurement(measureSpec, preferred);
    }

    private int getMeasurement(int measureSpec, int preferred) {
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.EXACTLY:
                return specSize;
            case MeasureSpec.AT_MOST:
                return Math.min(preferred, specSize);
            default:
                return preferred;
        }
    }
}
