package com.github.siyamed.shapeimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class CircleShader extends ShaderHelper {
    float center;
    float bitmapCenterX;
    float bitmapCenterY;
    float borderRadius;
    int bitmapRadius;

    public CircleShader() {
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);
        square = true;
    }

    @Override
    public void draw(Canvas canvas, Paint imagePaint, Paint borderPaint) {
        canvas.drawCircle(center, center, borderRadius, borderPaint);
        canvas.save();
        canvas.concat(matrix);
        canvas.drawCircle(bitmapCenterX, bitmapCenterY, bitmapRadius, imagePaint);
        canvas.restore();
    }

    @Override
    public void onSizeChanged(int width, int height) {
        super.onSizeChanged(width, height);
        center = Math.round(viewWidth / 2f);
        borderRadius = Math.round((viewWidth - borderWidth) / 2f);
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

                bitmapCenterX = Math.round(bitmapWidth / 2f);
                bitmapCenterY = Math.round(bitmapHeight / 2f);
                bitmapRadius = Math.round(width / scale / 2f + 0.5f);
                return bitmap;
            }
        }
        return null;
    }
}