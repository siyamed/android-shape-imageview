package com.github.siyamed.shapeimageview.path.parser;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

public class PathInfo {
    private final float width;
    private final float height;
    private final Path path;

    PathInfo(Path path, float width, float height) {
        this.path = path;

        float tmpWidth = width;
        float tmpHeight = height;
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        if(width <= 0 && height <= 0) {
            tmpWidth = (float) Math.ceil(bounds.width());
            tmpHeight = (float) Math.ceil(bounds.height());
            path.offset(-1 * (float) Math.floor(bounds.left),
                    -1 * (float) Math.round(bounds.top));
        }

        this.width = tmpWidth;
        this.height = tmpHeight;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void transform(Matrix matrix, Path dst) {
        path.transform(matrix, dst);
    }
}
