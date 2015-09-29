package com.github.siyamed.shapeimageview.shader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import com.github.siyamed.shapeimageview.R;
import com.github.siyamed.shapeimageview.path.SvgUtil;
import com.github.siyamed.shapeimageview.path.parser.PathInfo;

@SuppressWarnings("WeakerAccess")
public class SvgShader extends ShaderHelper {
    public static final int BORDER_TYPE_DEFAULT = 0;
    public static final int BORDER_TYPE_FILL = 1;

    public static final int STROKE_CAP_DEFAULT = -1;
    public static final int STROKE_CAP_BUTT = 0;
    public static final int STROKE_CAP_ROUND = 1;
    public static final int STROKE_CAP_SQUARE = 2;

    public static final int STROKE_JOIN_DEFAULT = -1;
    public static final int STROKE_JOIN_BEVEL = 0;
    public static final int STROKE_JOIN_MITER = 1;
    public static final int STROKE_JOIN_ROUND= 2;

    private final Path path = new Path();
    private final Path borderPath = new Path();
    private final Matrix pathMatrix = new Matrix();
    private final float[] pathDimensions = new float[2];
    private PathInfo shapePath;
    private int resId = -1;
    private int borderType = BORDER_TYPE_DEFAULT;
    private int strokeCap = STROKE_CAP_DEFAULT;
    private int strokeJoin = STROKE_JOIN_DEFAULT;
    private int strokeMiter = 0;

    public SvgShader() {

    }

    public SvgShader(int resId) {
        this.resId = resId;
    }

    public SvgShader(int resId, int borderType) {
        this.resId = resId;
        this.borderType = borderType;
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaderImageView, defStyle, 0);
            resId = typedArray.getResourceId(R.styleable.ShaderImageView_siShape, resId);
            borderType = typedArray.getInt(R.styleable.ShaderImageView_siBorderType, borderType);
            strokeCap = typedArray.getInt(R.styleable.ShaderImageView_siStrokeCap, strokeCap);
            strokeJoin = typedArray.getInt(R.styleable.ShaderImageView_siStrokeJoin, strokeJoin);
            strokeMiter = typedArray.getDimensionPixelSize(R.styleable.ShaderImageView_siStrokeMiter, strokeMiter);
            typedArray.recycle();
        }

        setShapeResId(context, resId);
        setBorderType(borderType);
        setStrokeCap(strokeCap);
        setStrokeJoin(strokeJoin);
        setStrokeMiter(strokeMiter);
    }

    public void setShapeResId(Context context, int resId) {
        if(resId != -1) {
            shapePath = SvgUtil.readSvg(context, resId);
        } else {
            throw new RuntimeException("No resource is defined as shape");
        }
    }

    public void setStrokeMiter(int strokeMiter) {
        this.strokeMiter = strokeMiter;
        if(strokeMiter > 0) {
            borderPaint.setStrokeMiter(strokeMiter);
        }
    }

    public void setStrokeCap(int strokeCap) {
        this.strokeCap = strokeCap;
        switch(strokeCap) {
            case STROKE_CAP_BUTT:
                borderPaint.setStrokeCap(Paint.Cap.BUTT);
                break;
            case STROKE_CAP_ROUND:
                borderPaint.setStrokeCap(Paint.Cap.ROUND);
                break;
            case STROKE_CAP_SQUARE:
                borderPaint.setStrokeCap(Paint.Cap.SQUARE);
                break;
            case STROKE_CAP_DEFAULT:
            default:
                break;
        }
    }

    public void setStrokeJoin(int strokeJoin) {
        this.strokeJoin = strokeJoin;
        switch(strokeJoin) {
            case STROKE_JOIN_BEVEL:
                borderPaint.setStrokeJoin(Paint.Join.BEVEL);
                break;
            case STROKE_JOIN_MITER:
                borderPaint.setStrokeJoin(Paint.Join.MITER);
                break;
            case STROKE_JOIN_ROUND:
                borderPaint.setStrokeJoin(Paint.Join.ROUND);
                break;
            case STROKE_CAP_DEFAULT:
            default:
                break;
        }
    }

    public void setBorderType(int borderType) {
        this.borderType = borderType;
        switch (borderType) {
            case BORDER_TYPE_FILL:
                borderPaint.setStyle(Paint.Style.FILL);
                break;
            case BORDER_TYPE_DEFAULT:
            default:
                borderPaint.setStyle(Paint.Style.STROKE);
                break;
        }
    }

    @Override
    public void draw(Canvas canvas, Paint imagePaint, Paint borderPaint) {
        canvas.save();
        canvas.drawPath(borderPath, borderPaint);
        canvas.concat(matrix);
        canvas.drawPath(path, imagePaint);
        canvas.restore();
    }

    @Override
    public void calculate(int bitmapWidth, int bitmapHeight, float width, float height, float scale, float translateX, float translateY) {
        path.reset();
        borderPath.reset();

        pathDimensions[0] = shapePath.getWidth();
        pathDimensions[1] = shapePath.getHeight();

        pathMatrix.reset();

        scale = Math.min(width / pathDimensions[0], height / pathDimensions[1]);
        translateX = Math.round((width - pathDimensions[0] * scale) * 0.5f);
        translateY = Math.round((height- pathDimensions[1] * scale) * 0.5f);
        pathMatrix.setScale(scale, scale);
        pathMatrix.postTranslate(translateX, translateY);
        shapePath.transform(pathMatrix, path);
        path.offset(borderWidth, borderWidth);

        if(borderWidth > 0) {
            pathMatrix.reset();
            float newWidth;
            float newHeight;
            float d;
            if(borderType == BORDER_TYPE_DEFAULT) {
                newWidth = viewWidth-borderWidth;
                newHeight = viewHeight-borderWidth;
                d = borderWidth / 2f;
            } else {
                newWidth = viewWidth;
                newHeight = viewHeight;
                d = 0;
            }
            scale = Math.min(newWidth / pathDimensions[0], newHeight / pathDimensions[1]);
            translateX = Math.round((newWidth - pathDimensions[0] * scale) * 0.5f + d);
            translateY = Math.round((newHeight - pathDimensions[1] * scale) * 0.5f + d);
            pathMatrix.setScale(scale, scale);
            pathMatrix.postTranslate(translateX, translateY);
            shapePath.transform(pathMatrix, borderPath);
//            borderPath.op(path, Path.Op.DIFFERENCE);
        }

        pathMatrix.reset();
        matrix.invert(pathMatrix);
        path.transform(pathMatrix);
    }

    @Override
    public void reset() {
        path.reset();
        borderPath.reset();
    }


}