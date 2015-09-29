package com.github.siyamed.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;

import com.github.siyamed.shapeimageview.shader.ShaderHelper;
import com.github.siyamed.shapeimageview.shader.SvgShader;

public class ShapeImageView extends ShaderImageView {
    private SvgShader shader;

    public ShapeImageView(Context context) {
        super(context);
    }

    public ShapeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShapeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        shader = new SvgShader();
        return shader;
    }

    public void setStrokeMiter(int strokeMiter) {
        if(shader != null) {
            shader.setStrokeMiter(strokeMiter);
            invalidate();
        }
    }

    public void setStrokeCap(int strokeCap) {
        if(shader != null) {
            shader.setStrokeCap(strokeCap);
            invalidate();
        }
    }

    public void setStrokeJoin(int strokeJoin) {
        if(shader != null) {
            shader.setStrokeJoin(strokeJoin);
            invalidate();
        }
    }

    public void setBorderType(int borderType) {
        if(shader != null) {
            shader.setBorderType(borderType);
            invalidate();
        }
    }

    public void setShapeResId(int resId) {
        if(shader != null) {
            shader.setShapeResId(getContext(), resId);
            invalidate();
        }
    }

}
