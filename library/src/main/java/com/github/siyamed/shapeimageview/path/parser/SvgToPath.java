package com.github.siyamed.shapeimageview.path.parser;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Log;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

//https://github.com/geosolutions-it/mapsforge/tree/master/svg-android
/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

public class SvgToPath {
    static final String TAG = SvgToPath.class.getSimpleName();
    private static final float DPI = 72.0f;

    public static PathInfo getSVGFromInputStream(InputStream inputStream) {
        return SvgToPath.parse(inputStream, true, DPI);
    }

    private static PathInfo parse(InputStream in, boolean ignoreDefs, float dpi)  {
        try {
            XmlPullParser xr = new KXmlParser();
            SvgToPath svgHandler = new SvgToPath(xr);
            svgHandler.setDpi(dpi);

            if (ignoreDefs) {
                xr.setInput(new InputStreamReader(in));
                svgHandler.processSvg();
            } else {
                CopyInputStream cin = new CopyInputStream(in);

                XmlPullParser ids = new KXmlParser();
                ids.setInput(new InputStreamReader(cin.getCopy()));
                IdHandler idHandler = new IdHandler(ids);
                idHandler.processIds();
                svgHandler.idXml = idHandler.idXml;

                xr.setInput(new InputStreamReader(cin.getCopy()));
                svgHandler.processSvg();
            }

            return svgHandler.pathInfo;
        } catch (Exception e) {
            Log.w(TAG, "Parse error: " + e);
            throw new RuntimeException(e);
        }
    }

    private final static Matrix IDENTITY_MATRIX = new Matrix();

    private HashMap<String, String> idXml = new HashMap<String, String>();
    private final XmlPullParser atts;
    private final RectF rect = new RectF();
    private float dpi = DPI;
    private boolean hidden = false;
    private int hiddenLevel = 0;
    private boolean inDefsElement = false;

    private final Deque<Path> pathStack = new LinkedList<Path>();
    private final Deque<Matrix> matrixStack = new LinkedList<Matrix>();

    private float width;
    private float height;
    private Path path;
    private PathInfo pathInfo = null;

    private SvgToPath(XmlPullParser atts) {
        this.atts = atts;
    }

    void setDpi(float dpi) {
        this.dpi = dpi;
    }

    void processSvg() throws XmlPullParserException, IOException {
        int eventType = atts.getEventType();
        do {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                case XmlPullParser.END_DOCUMENT:
                case XmlPullParser.TEXT:
                    // no op
                    break;
                case XmlPullParser.START_TAG:
                    startElement();
                    break;
                case XmlPullParser.END_TAG:
                    endElement();
                    break;
            }
            eventType = atts.next();
        } while (eventType != XmlPullParser.END_DOCUMENT);
    }

    private void pushTransform(XmlPullParser atts) {
        final String transform = ParseUtil.getStringAttr("transform", atts);
        final Matrix matrix = transform == null ? IDENTITY_MATRIX : TransformParser.parseTransform(transform);
        matrixStack.push(matrix);
    }

    private void pushTransform(Matrix pMatrix) {
        final Matrix matrix = (pMatrix == null) ? IDENTITY_MATRIX : pMatrix;
        matrixStack.push(matrix);
    }

    private Matrix popTransform() {
        return matrixStack.pop();
    }

    private void pushPath() {
        Path path = new Path();
        this.path = path;
        pathStack.add(path);
    }

    private Path popPath() {
        Path poppedPath = pathStack.pop();
        this.path = pathStack.peek();
        return poppedPath;
    }

    void startElement() {
        String localName = atts.getName();

        if (inDefsElement) {
            return;
        }

        if (localName.equals("svg")) {
            width = Math.round(getFloatAttr("width", atts, 0f));
            height = Math.round(getFloatAttr("height", atts, 0f));

            NumberParse viewbox = NumberParse.getNumberParseAttr("viewBox", atts);

            pushPath();
            Matrix matrix = IDENTITY_MATRIX;

            if (viewbox != null && viewbox.numbers != null && viewbox.numbers.size() == 4) {
                if(width < 0.1f || height < -0.1f){
                    width = (viewbox.numbers.get(2) - viewbox.numbers.get(0));
                    width = (viewbox.numbers.get(3) - viewbox.numbers.get(3));
                } else {
                    float sx = width / (viewbox.numbers.get(2) - viewbox.numbers.get(0)) ;
                    float sy = height / (viewbox.numbers.get(3) - viewbox.numbers.get(1));
                    matrix.setScale(sx, sy);
                }
            }

            pushTransform(matrix);
        } else if (localName.equals("defs")) {
            inDefsElement = true;
        } else if (localName.equals("use")) {
            String href = ParseUtil.getStringAttr("xlink:href", atts);
            String attTransform = ParseUtil.getStringAttr("transform", atts);
            String attX = ParseUtil.getStringAttr("x", atts);
            String attY = ParseUtil.getStringAttr("y", atts);

            StringBuilder sb = new StringBuilder();
            sb.append("<g");
            sb.append(" xmlns='http://www.w3.org/2000/svg' ");
            sb.append(" xmlns:xlink='http://www.w3.org/1999/xlink' ");
            sb.append(" xmlns:sodipodi='http://sodipodi.sourceforge.net/DTD/sodipodi-0.dtd' ");
            sb.append(" xmlns:inkscape='http://www.inkscape.org/namespaces/inkscape' version='1.1'");
            if (attTransform != null || attX != null || attY != null) {
                sb.append(" transform='");
                if (attTransform != null) {
                    sb.append(ParseUtil.escape(attTransform));
                }
                if (attX != null || attY != null) {
                    sb.append("translate(");
                    sb.append(attX != null ? ParseUtil.escape(attX) : "0");
                    sb.append(",");
                    sb.append(attY != null ? ParseUtil.escape(attY) : "0");
                    sb.append(")");
                }
                sb.append("'");
            }

            for (int i = 0; i < atts.getAttributeCount(); i++) {
                String attrQName = atts.getAttributeName(i);
                if (!"x".equals(attrQName) && !"y".equals(attrQName) &&
                        !"width".equals(attrQName) && !"height".equals(attrQName) &&
                        !"xlink:href".equals(attrQName) && !"transform".equals(attrQName)) {

                    sb.append(" ");
                    sb.append(attrQName);
                    sb.append("='");
                    sb.append(ParseUtil.escape(atts.getAttributeValue(i)));
                    sb.append("'");
                }
            }

            sb.append(">");

            sb.append(idXml.get(href.substring(1)));

            sb.append("</g>");
        } else if (localName.equals("g")) {
            if (hidden) {
                hiddenLevel++;
            }
            // Go in to hidden mode if display is "none"
            if ("none".equals(ParseUtil.getStringAttr("display", atts))) {
                if (!hidden) {
                    hidden = true;
                    hiddenLevel = 1;
                }
            }
            pushTransform(atts); // sau
            pushPath();
        } else if (!hidden && localName.equals("rect")) {
            Float x = getFloatAttr("x", atts, 0f);
            Float y = getFloatAttr("y", atts, 0f);
            Float width = getFloatAttr("width", atts);
            Float height = getFloatAttr("height", atts);
            Float rx = getFloatAttr("rx", atts, 0f);
            Float ry = getFloatAttr("ry", atts, 0f);
            Path p = new Path();
            if (rx <= 0f && ry <= 0f) {
                p.addRect(x, y, x + width, y + height, Path.Direction.CW);
            } else {
                rect.set(x, y, x + width, y + height);
                p.addRoundRect(rect, rx, ry, Path.Direction.CW);
            }

            pushTransform(atts);
            Matrix matrix = popTransform();
            p.transform(matrix);
            path.addPath(p);
        } else if (!hidden && localName.equals("line")) {
            Float x1 = getFloatAttr("x1", atts);
            Float x2 = getFloatAttr("x2", atts);
            Float y1 = getFloatAttr("y1", atts);
            Float y2 = getFloatAttr("y2", atts);
            Path p = new Path();
            p.moveTo(x1, y1);
            p.lineTo(x2, y2);
            pushTransform(atts);
            Matrix matrix = popTransform();
            p.transform(matrix);
            path.addPath(p);
        } else if (!hidden && localName.equals("circle")) {
            Float centerX = getFloatAttr("cx", atts);
            Float centerY = getFloatAttr("cy", atts);
            Float radius = getFloatAttr("r", atts);
            if (centerX != null && centerY != null && radius != null) {
                Path p = new Path();
                p.addCircle(centerX, centerY, radius, Path.Direction.CW);
                pushTransform(atts);
                Matrix matrix = popTransform();
                p.transform(matrix);
                path.addPath(p);
            }
        } else if (!hidden && localName.equals("ellipse")) {
            Float centerX = getFloatAttr("cx", atts);
            Float centerY = getFloatAttr("cy", atts);
            Float radiusX = getFloatAttr("rx", atts);
            Float radiusY = getFloatAttr("ry", atts);
            if (centerX != null && centerY != null && radiusX != null && radiusY != null) {
                rect.set(centerX - radiusX, centerY - radiusY, centerX + radiusX, centerY + radiusY);
                Path p = new Path();
                p.addOval(rect, Path.Direction.CW);
                pushTransform(atts);
                Matrix matrix = popTransform();
                p.transform(matrix);
                path.addPath(p);
            }
        } else if (!hidden && (localName.equals("polygon") || localName.equals("polyline"))) {
            NumberParse numbers = NumberParse.getNumberParseAttr("points", atts);
            if (numbers != null) {
                Path p = new Path();
                ArrayList<Float> points = numbers.numbers;
                if (points.size() > 1) {
                    p.moveTo(points.get(0), points.get(1));
                    for (int i = 2; i < points.size(); i += 2) {
                        float x = points.get(i);
                        float y = points.get(i + 1);
                        p.lineTo(x, y);
                    }
                    // Don't close a polyline
                    if (localName.equals("polygon")) {
                        p.close();
                    }

                    pushTransform(atts);
                    Matrix matrix = popTransform();
                    p.transform(matrix);
                    path.addPath(p);
                }
            }
        } else if (!hidden && localName.equals("path")) {
            Path p = PathParser.doPath(ParseUtil.getStringAttr("d", atts));
            pushTransform(atts);
            Matrix matrix = popTransform();
            p.transform(matrix);
            path.addPath(p);
        } else if (!hidden && localName.equals("metadata")) {
            // skip
        } else if (!hidden) {
            Log.d(TAG, String.format("Unrecognized tag: %s (%s)", localName, showAttributes(atts)));
        }
    }

    private String showAttributes(XmlPullParser a) {
        String result = "";
        for(int i=0; i < a.getAttributeCount(); i++) {
            result += " " + a.getAttributeName(i) + "='" + a.getAttributeValue(i) + "'";
        }
        return result;
    }

    void endElement() {

        String localName = atts.getName();
        if (inDefsElement) {
            if (localName.equals("defs")) {
                inDefsElement = false;
            }
            return;
        }

        if (localName.equals("svg")) {
            Path p = popPath();
            Matrix matrix = popTransform();
            p.transform(matrix);
            pathInfo = new PathInfo(p, width, height);
        } else if (localName.equals("g")) {
            // Break out of hidden mode
            if (hidden) {
                hiddenLevel--;
                if (hiddenLevel == 0) {
                    hidden = false;
                }
            }

            Path p = popPath();
            Matrix matrix = popTransform();
            p.transform(matrix);
            path.addPath(p);
        }
    }

    final Float getFloatAttr(String name, XmlPullParser attributes) {
        return getFloatAttr(name, attributes, null);
    }

    final Float getFloatAttr(String name, XmlPullParser attributes, Float defaultValue) {
        Float result = ParseUtil.convertUnits(name, attributes, dpi, width, height);
        return result == null ? defaultValue : result;
    }
}