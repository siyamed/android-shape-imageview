package com.github.siyamed.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;

import com.github.siyamed.shapeimageview.shader.BubbleShader;
import com.github.siyamed.shapeimageview.shader.ShaderHelper;

public class BubbleImageView extends ShaderImageView {
    private BubbleShader shader;

    public BubbleImageView(Context context) {
        super(context);
    }

    public BubbleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        shader = new BubbleShader();
        return shader;
    }

    public int getTriangleHeightPx() {
        if(shader != null) {
            return shader.getTriangleHeightPx();
        }
        return 0;
    }

    public void setTriangleHeightPx(final int triangleHeightPx) {
        if(shader != null) {
            shader.setTriangleHeightPx(triangleHeightPx);
            invalidate();
        }
    }

    public BubbleShader.ArrowPosition getArrowPosition() {
        if(shader != null) {
            return shader.getArrowPosition();
        }

        return BubbleShader.ArrowPosition.LEFT;
    }

    public void setArrowPosition(final BubbleShader.ArrowPosition arrowPosition) {
        if(shader != null) {
            shader.setArrowPosition(arrowPosition);
            invalidate();
        }
    }
}
