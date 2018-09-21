package com.noam.sensordatauploader;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SensorCollector implements SensorEventListener {
    private final String TAG = "nwm-sense";
    private SensorManager sensorManager;

    private Context ctx;
    private DataCollector dataCollector;


    public SensorCollector(@Nullable Context ctx) {
        if (ctx != null) {
            sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        }
        this.ctx = ctx;
    }

    public void startListen() {
        // register this class as a listener for
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_GAME); // need to get samples fast enough to detect fall from standing < 50mSec.
        dataCollector = new DataCollector(ctx);
    }

    public void stopListen() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                    dataCollector.addPoint(new DataPoint(event.timestamp, event.values[0], event.values[1], event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.v(TAG, "onAccuracyChanged " + sensor.toString() + " , acc = " + accuracy);
    }


    public void uploadData() {
        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        String fname = String.format("accelData-%s.csv", currentDateandTime );
        dataCollector.dumpToFile(fname, true);
        dataCollector.reset();
        (new FileUploader()).uploadFile(ctx.getFileStreamPath(fname));
    }
}
