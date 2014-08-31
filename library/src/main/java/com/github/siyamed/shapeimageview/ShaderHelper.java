package com.github.siyamed.shapeimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import ext.com.android.R;

public abstract class ShaderHelper {
    private static int ALPHA_MAX = 255;

    protected int viewWidth;
    protected int viewHeight;

    protected int borderColor = Color.BLACK;
    protected int borderWidth = 0;
    protected float borderAlpha = 1f;
    protected boolean square = false;

    protected Paint borderPaint;
    protected Paint imagePaint;
    protected BitmapShader shader;
    protected Drawable drawable;
    protected Matrix matrix = new Matrix();



    public ShaderHelper() {
        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);

        imagePaint = new Paint();
        imagePaint.setAntiAlias(true);
    }

    public abstract void draw(Canvas canvas, Paint imagePaint, Paint borderPaint);
    public abstract Bitmap calculateDrawableSizes();

    protected final int dpToPx(DisplayMetrics displayMetrics, int dp) {
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public boolean isSquare() {
        return square;
    }

    public void init(Context context, AttributeSet attrs, int defStyle) {
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaderImageView, defStyle, 0);
            borderColor = typedArray.getColor(R.styleable.ShaderImageView_borderColor, borderColor);
            borderWidth = typedArray.getDimensionPixelSize(R.styleable.ShaderImageView_borderWidth, borderWidth);
            borderAlpha = typedArray.getFloat(R.styleable.ShaderImageView_borderAlpha, borderAlpha);
            square = typedArray.getBoolean(R.styleable.ShaderImageView_square, square);
            typedArray.recycle();
        }

        borderPaint.setColor(borderColor);
        borderPaint.setAlpha(Float.valueOf(borderAlpha * ALPHA_MAX).intValue());
        borderPaint.setStrokeWidth(borderWidth);
    }

    public boolean onDraw(Canvas canvas) {
        if (shader == null) {
            createShader();
        }
        if (shader != null && viewWidth > 0 && viewHeight > 0) {
            draw(canvas, imagePaint, borderPaint);
            return true;
        }

        return false;
    }

    public void onSizeChanged(int width, int height) {
        viewWidth = width;
        viewHeight = height;
        if(isSquare()) {
            viewWidth = viewHeight = Math.min(width, height);
        }
        if(shader != null) {
            calculateDrawableSizes();
        }
    }

    public final void onImageDrawableReset(Drawable drawable) {
        this.drawable = drawable;
        shader = null;
        imagePaint.setShader(null);
    }

    protected void createShader() {
        Bitmap bitmap = calculateDrawableSizes();
        if(bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            imagePaint.setShader(shader);
        }
    }

    protected Bitmap getBitmap() {
        Bitmap bitmap = null;
        if(drawable != null) {
            if(drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            }
        }

        return bitmap;
    }
}