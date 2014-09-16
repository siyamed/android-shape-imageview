package com.github.siyamed.shapeimageview.mask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class PorterCircularImageView extends PorterImageView {
    private final RectF rect = new RectF();

    public PorterCircularImageView(Context context) {
        super(context);
        setup();
    }

    public PorterCircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public PorterCircularImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    private void setup() {
        setSquare(true);
    }

    @Override
    protected void paintMaskCanvas(Canvas maskCanvas, Paint maskPaint, int width, int height) {
        float radius = Math.min(width, height) / 2f;
        rect.set(0, 0, width, height);
        maskCanvas.drawCircle(rect.centerX(), rect.centerY(), radius, maskPaint);
    }
}