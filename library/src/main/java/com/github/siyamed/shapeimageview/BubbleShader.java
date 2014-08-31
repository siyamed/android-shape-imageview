package com.github.siyamed.shapeimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import ext.com.android.R;

public class BubbleShader extends ShaderHelper {
    private static final int DEFAULT_HEIGHT_DP = 10;

    private enum ArrowPosition {
        LEFT,
        RIGHT
    }

    Path path = new Path();

    private int triangleHeightPx;
    private ArrowPosition arrowPosition = ArrowPosition.LEFT;

    public BubbleShader() {
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);
        borderWidth = 0;
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaderImageView, defStyle, 0);
            triangleHeightPx = typedArray.getDimensionPixelSize(R.styleable.ShaderImageView_triangleHeight, 0);
            int arrowPositionInt = typedArray.getInt(R.styleable.ShaderImageView_arrowPosition, ArrowPosition.LEFT.ordinal());
            arrowPosition = ArrowPosition.values()[arrowPositionInt];
            typedArray.recycle();
        }

        if(triangleHeightPx == 0) {
            triangleHeightPx = dpToPx(context.getResources().getDisplayMetrics(), DEFAULT_HEIGHT_DP);
        }
    }

    @Override
    public void draw(Canvas canvas, Paint imagePaint, Paint borderPaint) {
        canvas.save();
        canvas.concat(matrix);
        canvas.drawPath(path, imagePaint);
        canvas.restore();
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

                path.reset();

                float x = -translateX;
                float y = -translateY;
                float scaledTriangleHeight = triangleHeightPx / scale;
                float resultWidth = bitmapWidth + 2 * translateX;
                float resultHeight = bitmapHeight + 2 * translateY;
                float centerY  = resultHeight / 2f + y;
                float triangle = scaledTriangleHeight; //scaledTriangleHeight * 2f / (float) Math.sqrt(3) / 2;

                path.setFillType(Path.FillType.EVEN_ODD);
                float rectLeft;
                float rectRight;
                switch (arrowPosition) {
                    case LEFT:
                        rectLeft = scaledTriangleHeight + x;
                        rectRight = resultWidth + rectLeft;
                        path.addRect(rectLeft, y, rectRight, resultHeight + y, Path.Direction.CW);

                        path.moveTo(x, centerY);
                        path.lineTo(rectLeft, centerY - triangle);
                        path.lineTo(rectLeft, centerY + triangle);
                        path.lineTo(x, centerY);
                        break;
                    case RIGHT:
                        rectLeft = x;
                        float imgRight = resultWidth + rectLeft;
                        rectRight = imgRight - scaledTriangleHeight;
                        path.addRect(rectLeft, y, rectRight, resultHeight + y, Path.Direction.CW);
                        path.moveTo(imgRight, centerY);
                        path.lineTo(rectRight, centerY - triangle);
                        path.lineTo(rectRight, centerY + triangle);
                        path.lineTo(imgRight, centerY);
                        break;
                }

                return bitmap;
            }
        }
        return null;
    }
}