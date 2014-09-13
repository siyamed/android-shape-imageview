package com.github.siyamed.shapeimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Matrix;
import android.util.AttributeSet;

public class SvgShader extends ShaderHelper {
    private Path shapePath;

    private Path path = new Path();
    private Matrix pathMatrix = new Matrix();
    private float[] pathDimensions = new float[2];

    public SvgShader() {
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);
        borderWidth = 0;
        int resId = -1;
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaderImageView, defStyle, 0);
            resId = typedArray.getResourceId(R.styleable.ShaderImageView_shape, resId);
            typedArray.recycle();
        }

        shapePath = SvgUtil.readSvg(context, resId);
//        if(resId != -1) {
//
//        }
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
        path.reset();
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
                    translateY = Math.round((height/scale - bitmapHeight) / 2f);
                }

                matrix.setScale(scale, scale);
                matrix.preTranslate(translateX, translateY);
                matrix.postTranslate(borderWidth, borderWidth);

                pathDimensions[0] = (float) SvgUtil.HEART_PATH_WIDTH;
                pathDimensions[1] = (float) SvgUtil.HEART_PATH_HEIGHT;

                pathMatrix.reset();
                scale = Math.min(width / pathDimensions[0], height / pathDimensions[1]);
                translateX = Math.round((width - pathDimensions[0] * scale) * 0.5f);
                translateY = Math.round((height- pathDimensions[1] * scale) * 0.5f);
                pathMatrix.setScale(scale, scale);
                pathMatrix.postTranslate(translateX, translateY);
                shapePath.transform(pathMatrix, path);

                pathMatrix.reset();
                matrix.invert(pathMatrix);
                path.transform(pathMatrix);

                return bitmap;
            }
        }

        return null;
    }
}