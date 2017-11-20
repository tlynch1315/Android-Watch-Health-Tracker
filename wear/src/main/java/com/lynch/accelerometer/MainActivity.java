package com.lynch.accelerometer;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import java.util.Collections;
import java.util.Vector;

public class MainActivity extends Activity implements SensorEventListener{

    private TextView mTextView;
    TextView acceleration;
    Sensor accelerometer;
    Sensor stepcounter;
    SensorManager sensorManager;
    Vector<Float> heartRateVals = new Vector<Float>();
    Vector<Float> stepCountVals = new Vector<Float>();
    boolean updated;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        updated = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.acceleration);
            }
        });

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        stepcounter   = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        /*if (accelerometer == null){
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
            for (Sensor sensor1 : sensors){
                Log.i(TAG, sensor1.getName() + ": ");
            }
        }*/
        sensorManager.registerListener(this, stepcounter, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        acceleration = (TextView)findViewById(R.id.acceleration);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //Log.d("SENSOR CHANGED", "onSensorChanged is being executed");
        //updateDisplay();
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            //if (sensorEvent.values[0] != 0) {
            stepCountVals.addElement(sensorEvent.values[0]);
            Log.i("Step Count", Float.toString(sensorEvent.values[0]));
            //}
        }
        if (sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            //if (sensorEvent.values[0] != 0) {
            heartRateVals.addElement(sensorEvent.values[0]);
            Log.i("HEART RATE", Float.toString(sensorEvent.values[0]));
            Log.i("HR sample size: ", Integer.toString(heartRateVals.size()));
            //}
        }
        if (heartRateVals.size() > 100 && updated == false){
            updated = true;
            Log.i("Update Called", Integer.toString(heartRateVals.size()));
            testUpdate();
        }
        //updateDisplay();
        //Log.d("FIRST VALUE", Float.toString(sensorEvent.values[0]));
        //Log.d("Size of sample: ", Integer.toString(heartRateVals.size()));
        /*for(float num : heartRateVals) {
            Log.d("Values: ", Float.toString(num) );
        }*/
        /*acceleration.setText("X: " + sensorEvent.values[0] +
                            "\nY: "+ sensorEvent.values[1] +
                            "\nZ: "+ sensorEvent.values[2]);*/
        //Log.d("AFTER SET TEXT", "Exit function");
        //int level = batteryStatus.getIn

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    /*
    @Override
    public void onStop(){
        super.onStop();
        sensorManager.unregisterListener(this);
    }*/
    public void testUpdate(){
        Collections.sort(heartRateVals);
        float twentyfive, fifty, seventyfive, ninetyfive;
        Log.i("TEST", "BEFORE 25");
        int twentyfivepercent = (int)heartRateVals.size()/4;
        Log.i("heart rate vals size", Integer.toString(heartRateVals.size()));
        Log.i("25", Integer.toString(twentyfivepercent));
        twentyfive = heartRateVals.get(twentyfivepercent);
        Log.i("TEST", "past 25");
        fifty = heartRateVals.get((int)(heartRateVals.size()/2));
        Log.i("TEST", "past 50");
        seventyfive = heartRateVals.get((int)((heartRateVals.size()/4) * 3));
        Log.i("TEST", "past 75");
        ninetyfive = heartRateVals.get((int)((heartRateVals.size()/20)*19));
        Log.i("TEST", "past 95");
        float sum = 0;
        float variance;
        for(int i = 0; i < heartRateVals.size(); i++){
            //TODO check if 0
            sum += (heartRateVals.get(i));
        }
        float average = sum/heartRateVals.size();
        acceleration = (TextView)findViewById(R.id.acceleration);
        acceleration.setText(Float.toString(average));
        float temp = 0;
        for(float a : heartRateVals){
            temp += (a-average)*(a-average);
        }
        variance = temp/(heartRateVals.size()-1);
        double a = variance;
        double std = Math.sqrt(a);
        Log.i("Standard Deviation", Double.toString(std));
        /*Log.i("25%: ", Integer.toString(twentyfive));
        Log.i("50%: ", Integer.toString(fifty));
        Log.i("75%: ", Integer.toString(seventyfive));
        Log.i("95%: ", Integer.toString(ninetyfive));*/
        return;

    }
}
