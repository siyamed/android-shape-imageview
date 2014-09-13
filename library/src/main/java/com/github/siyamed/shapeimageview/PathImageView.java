package com.github.siyamed.shapeimageview;

import android.content.Context;
import android.util.AttributeSet;

public class PathImageView extends ShaderImageView {

    public PathImageView(Context context) {
        super(context);
    }

    public PathImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PathImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader();
    }
}
