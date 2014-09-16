package com.github.siyamed.shapeimageview.path.parser;

import java.io.InputStream;

@SuppressWarnings("FinalStaticMethod")
public class IoUtil {
    public static final void closeQuitely(InputStream is) {
        if(is != null) {
            try {
                is.close();
            } catch (Throwable ignored) {
                //ignored
            }
        }
    }
}
