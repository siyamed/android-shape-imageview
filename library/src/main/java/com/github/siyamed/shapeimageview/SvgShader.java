package com.github.siyamed.shapeimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
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
    public void calculate(int bitmapWidth, int bitmapHeight, float width, float height, float scale, float translateX, float translateY) {
        path.reset();

        pathDimensions[0] = (float) SvgUtil.HEART_PATH_WIDTH;
        pathDimensions[1] = (float) SvgUtil.HEART_PATH_HEIGHT;

        pathMatrix.reset();

        scale = Math.min(width / pathDimensions[0], height / pathDimensions[1]);
        translateX = Math.round((width - pathDimensions[0] * scale) * 0.5f);
        translateY = Math.round((height- pathDimensions[1] * scale) * 0.5f);
        pathMatrix.setScale(scale, scale);
        pathMatrix.postTranslate(translateX, translateY);
        shapePath.transform(pathMatrix, path);
        path.offset(borderWidth, borderWidth);

//                if(borderWidth > 0) {
//                    path.offset(borderWidth, borderWidth);
//                    pathMatrix.reset();
//                    scale = Math.min(viewWidth / pathDimensions[0], viewHeight / pathDimensions[1]);
//                    translateX = Math.round((viewWidth - pathDimensions[0] * scale) * 0.5f);
//                    translateY = Math.round((viewHeight- pathDimensions[1] * scale) * 0.5f);
//                    pathMatrix.setScale(scale, scale);
//                    pathMatrix.postTranslate(translateX, translateY);
//                    shapePath.transform(pathMatrix, borderPath);
//                    borderPath.op(path, Path.Op.DIFFERENCE);
//                }

        pathMatrix.reset();
        matrix.invert(pathMatrix);
        path.transform(pathMatrix);
    }

    @Override
    public void reset() {
        path.reset();
    }
}