package com.noam.sensordatauploader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    SensorCollector collector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        collector = new SensorCollector(this);
        collector.startListen();
    }

    public void upload(@SuppressWarnings("unused") View v) {
        collector.stopListen();
        collector.uploadData();
    }
}
