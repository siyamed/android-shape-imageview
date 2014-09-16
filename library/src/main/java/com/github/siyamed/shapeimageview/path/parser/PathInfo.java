package com.github.siyamed.shapeimageview.path.parser;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

public class PathInfo {
    private float width;
    private float height;
    private Path path;
    private RectF bounds = new RectF();

    PathInfo(Path path, float width, float height) {
        this.path = path;
        this.width = width;
        this.height = height;
        path.computeBounds(bounds, true);
        if(width <= 0 && height <= 0) {
            this.width = (float) Math.ceil(bounds.width());
            this.height = (float) Math.ceil(bounds.height());
            path.offset(-1 * (float) Math.floor(bounds.left),
                    -1 * (float) Math.round(bounds.top));
        }
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Path getPath() {
        return path;
    }

    public void transform(Matrix matrix, Path dst) {
        path.transform(matrix, dst);
    }
}
