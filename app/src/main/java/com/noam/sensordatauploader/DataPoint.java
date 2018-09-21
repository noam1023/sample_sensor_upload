package com.noam.sensordatauploader;

/**
 * a measurement of whatever we care to collect
 */
public class DataPoint {
    public double accel_x, accel_y, accel_z;

    public DataPoint(long t_nano, double x, double y, double z) {
        when = t_nano;
        accel_x = x;
        accel_y = y;
        accel_z = z;
    }

    public long when; // time in [nanoSec] when the measuring was done

    public String toString() {
        return String.format("%d, %f,%f,%f", when, accel_x, accel_y, accel_z);
    }
}