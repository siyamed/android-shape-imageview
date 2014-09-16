package com.github.siyamed.shapeimageview.path.parser;

import android.graphics.Matrix;
import android.util.Log;

class TransformParser {
    private static final String TAG = SvgToPath.class.getSimpleName();

    // Process a list of transforms
    // foo(n,n,n...) bar(n,n,n..._ ...)
    // delims are whitespace or ,'s
    static Matrix parseTransform(String s) {
        //Log.d(TAG, s);
        Matrix matrix = new Matrix();
        while (true) {
            parseTransformItem(s, matrix);
            int rparen = s.indexOf(")");
            if (rparen > 0 && s.length() > rparen + 1) {
                s = s.substring(rparen + 1).replaceFirst("[\\s,]*", "");
            } else {
                break;
            }
        }
        return matrix;
    }

    private static void parseTransformItem(String s, Matrix matrix) {
        if (s.startsWith("matrix(")) {
            NumberParse np = NumberParse.parseNumbers(s.substring("matrix(".length()));
            if (np.numbers.size() == 6) {
                Matrix mat = new Matrix();
                mat.setValues(new float[] {
                        // Row 1
                        np.numbers.get(0),
                        np.numbers.get(2),
                        np.numbers.get(4),
                        // Row 2
                        np.numbers.get(1),
                        np.numbers.get(3),
                        np.numbers.get(5),
                        // Row 3
                        0,
                        0,
                        1,
                });
                matrix.preConcat(mat);
            }
        } else if (s.startsWith("translate(")) {
            NumberParse np = NumberParse.parseNumbers(s.substring("translate(".length()));
            if (np.numbers.size() > 0) {
                float tx = np.numbers.get(0);
                float ty = 0;
                if (np.numbers.size() > 1) {
                    ty = np.numbers.get(1);
                }
                matrix.preTranslate(tx, ty);
            }
        } else if (s.startsWith("scale(")) {
            NumberParse np = NumberParse.parseNumbers(s.substring("scale(".length()));
            if (np.numbers.size() > 0) {
                float sx = np.numbers.get(0);
                float sy = sx;
                if (np.numbers.size() > 1) {
                    sy = np.numbers.get(1);
                }
                matrix.preScale(sx, sy);
            }
        } else if (s.startsWith("skewX(")) {
            NumberParse np = NumberParse.parseNumbers(s.substring("skewX(".length()));
            if (np.numbers.size() > 0) {
                float angle = np.numbers.get(0);
                matrix.preSkew((float) Math.tan(angle), 0);
            }
        } else if (s.startsWith("skewY(")) {
            NumberParse np = NumberParse.parseNumbers(s.substring("skewY(".length()));
            if (np.numbers.size() > 0) {
                float angle = np.numbers.get(0);
                matrix.preSkew(0, (float) Math.tan(angle));
            }
        } else if (s.startsWith("rotate(")) {
            NumberParse np = NumberParse.parseNumbers(s.substring("rotate(".length()));
            if (np.numbers.size() > 0) {
                float angle = np.numbers.get(0);
                float cx = 0;
                float cy = 0;
                if (np.numbers.size() > 2) {
                    cx = np.numbers.get(1);
                    cy = np.numbers.get(2);
                }
                matrix.preTranslate(cx, cy);
                matrix.preRotate(angle);
                matrix.preTranslate(-cx, -cy);
            }
        } else {
            Log.w(TAG, "Invalid transform (" + s + ")");
        }
    }
}
