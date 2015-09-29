package com.github.siyamed.shapeimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.github.siyamed.shapeimageview.shader.ShaderHelper;

@SuppressWarnings("WeakerAccess")
public abstract class ShaderImageView extends ImageView {

    private final static boolean DEBUG = false;
    private ShaderHelper pathHelper;

    public ShaderImageView(Context context) {
        super(context);
        setup(context, null, 0);
    }

    public ShaderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs, 0);
    }

    public ShaderImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup(context, attrs, defStyle);
    }

    private void setup(Context context, AttributeSet attrs, int defStyle) {
        getPathHelper().init(context, attrs, defStyle);
    }

    protected ShaderHelper getPathHelper() {
        if(pathHelper == null) {
            pathHelper = createImageViewHelper();
        }
        return pathHelper;
    }

    protected abstract ShaderHelper createImageViewHelper();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(getPathHelper().isSquare()) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    //Required by path helper
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        getPathHelper().onImageDrawableReset(getDrawable());
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        getPathHelper().onImageDrawableReset(getDrawable());
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        getPathHelper().onImageDrawableReset(getDrawable());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getPathHelper().onSizeChanged(w, h);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if(DEBUG) {
            canvas.drawRGB(10, 200, 200);
        }

        if(!getPathHelper().onDraw(canvas)) {
            super.onDraw(canvas);
        }
    }

    public void setBorderColor(final int borderColor) {
        getPathHelper().setBorderColor(borderColor);
        invalidate();
    }

    public int getBorderWidth() {
        return getPathHelper().getBorderWidth();
    }

    public void setBorderWidth(final int borderWidth) {
        getPathHelper().setBorderWidth(borderWidth);
        invalidate();
    }

    public float getBorderAlpha() {
        return getPathHelper().getBorderAlpha();
    }

    public void setBorderAlpha(final float borderAlpha) {
        getPathHelper().setBorderAlpha(borderAlpha);
        invalidate();
    }

    public void setSquare(final boolean square) {
        getPathHelper().setSquare(square);
        invalidate();
    }
}