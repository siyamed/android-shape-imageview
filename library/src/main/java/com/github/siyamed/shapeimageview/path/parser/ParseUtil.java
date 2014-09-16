package com.github.siyamed.shapeimageview.path.parser;

import org.xmlpull.v1.XmlPullParser;

@SuppressWarnings("FinalStaticMethod")
class ParseUtil {

    static final String escape (String s) {
        return s
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&apos")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("&", "&amp;");
    }

    static final String getStringAttr(String name, XmlPullParser attributes) {
        int n = attributes.getAttributeCount();
        for (int i = 0; i < n; i++) {
            if (attributes.getAttributeName(i).equals(name)) {
                return attributes.getAttributeValue(i);
            }
        }
        return null;
    }

    /*
     * Some SVG unit conversions.  This is approximate
     */
    static final Float convertUnits(String name, XmlPullParser atts, float dpi, float width, float height) {
        String value = getStringAttr(name, atts);
        if (value == null) {
            return null;
        } else if (value.endsWith("px")) {
            return Float.parseFloat(value.substring(0, value.length() - 2));
        } else if (value.endsWith("pt")) {
            return Float.valueOf(value.substring(0, value.length() - 2)) * dpi / 72;
        } else if (value.endsWith("pc")) {
            return Float.valueOf(value.substring(0, value.length() - 2)) * dpi / 6;
        } else if (value.endsWith("cm")) {
            return Float.valueOf(value.substring(0, value.length() - 2)) * dpi / 2.54f;
        } else if (value.endsWith("mm")) {
            return Float.valueOf(value.substring(0, value.length() - 2)) * dpi / 254;
        } else if (value.endsWith("in")) {
            return Float.valueOf(value.substring(0, value.length() - 2)) * dpi;
        } else if (value.endsWith("%")) {
            Float result = Float.valueOf(value.substring(0, value.length() - 1));
            float mult;
            if (name.contains("x") || name.equals("width") ) {
                mult = width / 100f;
            } else if (name.contains("y") || name.equals("height")) {
                mult = height / 100f;
            } else {
                mult = (height + width) / 2f;
            }
            return result * mult;
        } else {
            return Float.valueOf(value);
        }
    }
}
