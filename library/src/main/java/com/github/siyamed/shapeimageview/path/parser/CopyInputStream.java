package com.github.siyamed.shapeimageview.path.parser;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

class CopyInputStream {
    private static final String TAG = SvgToPath.TAG;;

    private InputStream _is;
    private ByteArrayOutputStream _copy;

    public CopyInputStream(InputStream is)
    {
        _is = is;

        try {
            int count = copy();
        }
        catch(IOException ex) {
            Log.w(TAG, "IOException in CopyInputStream " + ex.toString());
        }
    }

    private int copy() throws IOException {
        _copy = new ByteArrayOutputStream();
        int read = 0;
        int chunk = 0;
        byte[] data = new byte[256];

        while(-1 != (chunk = _is.read(data)))
        {
            read += data.length;
            // System.out.println("chunk = " + chunk);
            // System.out.println("read = " + read);

            _copy.write(data, 0, chunk);
        }
        _copy.flush();

        return read;
    }

    public ByteArrayInputStream getCopy()
    {
        return new ByteArrayInputStream(_copy.toByteArray());
    }
}

