package com.github.siyamed.shapeimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

public class RoundedShader extends ShaderHelper {

    private RectF borderRect = new RectF();
    private RectF imageRect = new RectF();

    private int radius = 0;
    private int bitmapRadius;

    public RoundedShader() {
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);
        borderPaint.setStrokeWidth(borderWidth*2);
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaderImageView, defStyle, 0);
            radius = typedArray.getDimensionPixelSize(R.styleable.ShaderImageView_radius, radius);
            typedArray.recycle();
        }
    }

    @Override
    public void draw(Canvas canvas, Paint imagePaint, Paint borderPaint) {
        canvas.drawRoundRect(borderRect, radius, radius, borderPaint);
        canvas.save();
        canvas.concat(matrix);
        canvas.drawRoundRect(imageRect, bitmapRadius, bitmapRadius, imagePaint);
        canvas.restore();
    }

    @Override
    public void onSizeChanged(int width, int height) {
        super.onSizeChanged(width, height);
        borderRect.set(borderWidth, borderWidth, viewWidth - borderWidth, viewHeight - borderWidth);
    }

    @Override
    public void calculate(int bitmapWidth, int bitmapHeight,
                          float width, float height,
                          float scale,
                          float translateX, float translateY) {
        imageRect.set(-translateX, -translateY, bitmapWidth + translateX, bitmapHeight + translateY);
        bitmapRadius = Math.round(radius / scale);
    }

    @Override
    public void reset() {
        imageRect.set(0,0,0,0);
        bitmapRadius = 0;
    }
}