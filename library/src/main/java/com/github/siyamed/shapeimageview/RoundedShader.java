package com.github.siyamed.shapeimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
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
    public Bitmap calculateDrawableSizes() {
        Bitmap bitmap = getBitmap();
        if(bitmap != null) {
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            if(bitmapWidth > 0 && bitmapHeight > 0) {
                float width = Math.round(viewWidth - 2f * borderWidth);
                float height = Math.round(viewHeight - 2f * borderWidth);

                float scale = 1f;
                float translateX = 0;
                float translateY = 0;

                if (bitmapWidth * height > width * bitmapHeight) {
                    scale = height / bitmapHeight;
                    translateX = Math.round((width/scale - bitmapWidth) / 2f);
                } else {
                    scale = width / (float) bitmapWidth;
                    translateY = Math.round((height/scale - bitmapHeight) / 2f);;
                }

                matrix.setScale(scale, scale);
                matrix.preTranslate(translateX, translateY);
                matrix.postTranslate(borderWidth, borderWidth);

                imageRect.set(-translateX, -translateY, bitmapWidth + translateX, bitmapHeight + translateY);
                bitmapRadius = Math.round(radius / scale);
                return bitmap;
            }
        }
        return null;
    }
}