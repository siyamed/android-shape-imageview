package com.github.siyamed.shapeimageview.path.parser;

import org.xmlpull.v1.XmlPullParser;

class Properties {
    StyleSet styles = null;
    XmlPullParser atts;

    Properties(XmlPullParser atts) {
        this.atts = atts;
        String styleAttr = ParseUtil.getStringAttr("style", atts);
        if (styleAttr != null) {
            styles = new StyleSet(styleAttr);
        }
    }

    public String getAttr(String name) {
        String v = null;
        if (styles != null) {
            v = styles.getStyle(name);
        }
        if (v == null) {
            v = ParseUtil.getStringAttr(name, atts);
        }
        return v;
    }

    public String getString(String name) {
        return getAttr(name);
    }

    @SuppressWarnings("unused")
    public Float getFloat(String name, float defaultValue) {
        Float v = getFloat(name);
        if (v == null) {
            return defaultValue;
        } else {
            return v;
        }
    }

    public Float getFloat(String name) {
        String v = getAttr(name);
        if (v == null) {
            return null;
        } else {
            try {
                return Float.parseFloat(v);
            } catch (NumberFormatException nfe) {
                return null;
            }
        }
    }
}

