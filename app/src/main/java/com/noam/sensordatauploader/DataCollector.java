package com.noam.sensordatauploader;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


// return ceil(log2(a))
class Util {
    public static int nextPowerOf2(int a) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(a - 1);
    }

    public static String getDeviceParams() {
        return "";
    }
}

public class DataCollector {

    private RingBuffer ring;
    public static int MAX_SAMPLE_RATE = 50; // Hz
    public static int WINDOW_DURATION = 10; // moving window of T seconds
    public static int RING_CAPACITY = MAX_SAMPLE_RATE * WINDOW_DURATION;

    private static String TAG = "nwm-DataCollector";
    Context context;

    public DataCollector(Context c) {
        context = c;
        ring = new RingBuffer(Util.nextPowerOf2(RING_CAPACITY));
    }

    public void addPoint(DataPoint v) {
        ring.put(v);
    }

    public void dumpToFile(String fileName, boolean with_device_param) {
        boolean android = "The Android Project".equals(System.getProperty("java.specification.vendor"));
        OutputStream out;
        if (android) {
            try {
                out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            } catch (IOException e) {
                Log.e(TAG, "can't open file");
                return;
            }
        } else {
            // This will be used on non android platform - such as linux unit tests
            try {
                FileOutputStream fos = new FileOutputStream(fileName);
                out = new DataOutputStream(new BufferedOutputStream(fos));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        String data = getDataString();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);
            if (with_device_param) {
                outputStreamWriter.write(Util.getDeviceParams());
                outputStreamWriter.write('\n');
            }
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
    }

    /**
     * get all the data we have in one string.
     * CSV format with json one-liner header
     */
    public String getDataString() {
        StringBuilder builder = new StringBuilder();
        int n = ring.available();
        Object values[] = new Object[n];
        ring.take(values, n);
        for (int i = 0; i < n; i++) {
            String tmp = (values[i]).toString();
            builder.append(tmp).append('\n');
        }
        return builder.toString();
    }

    public int available() {
        return ring.available();
    }

    public void reset() {
        ring.reset();
    }
}
