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
    Float mean1 = null;
    Float std1 = null;
    Float mean2 = null;
    Float std2 = null;
    Float mean1STEP = null;
    Float std1STEP = null;
    Float mean2STEP = null;
    Float std2STEP = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        heartRateVals.removeAllElements();
        stepCountVals.removeAllElements();
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
        //stepcounter   = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        boolean hasBodySensor = sensorManager.getDefaultSensor(34, true) != null;
        if (hasBodySensor){
            Log.i("Has Body Sensor", "Confirmed");
        }
        else{
            Log.i("Does not have", "body sensor");
        }
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
        if (heartRateVals.size() > 100 ){
            if (checkNull()){
                heartRateVals.removeAllElements();
                stepCountVals.removeAllElements();
            }
            else {
                //updated = true;
                Log.i("Update Called", Integer.toString(heartRateVals.size()));
                testUpdate();
            }
        }
    }

    public boolean checkNull(){
        int count = 0;
        for(int i = 0; i < heartRateVals.size(); i++){
            if (heartRateVals.get(i) == 0){
                count += 1;
            }
        }
        if (count >= 76){
            return true;
        }
        return false;
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
        Log.i("25", Integer.toString(twentyfivepercent));
        twentyfive = heartRateVals.get(twentyfivepercent);
        fifty = heartRateVals.get((int)(heartRateVals.size()/2));
        seventyfive = heartRateVals.get((int)((heartRateVals.size()/4) * 3));
        ninetyfive = heartRateVals.get((int)((heartRateVals.size()/20)*19));


        float sum = 0;
        float variance;
        for(int i = 0; i < heartRateVals.size(); i++){
            if (heartRateVals.get(i) != 0) {
                sum += (heartRateVals.get(i));
            }
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
        if (mean1 == null){
            Log.i("mean1 ", "null");
            mean1 = average;
            std1 = (float) std;
        }
        else{
            Log.i("mean2 ", "updated");
            mean2 = average;
            std2 = (float) std;
            checkSameUser();
        }
        Log.i("Standard Deviation", Double.toString(std));
        /*Log.i("25%: ", Integer.toString(twentyfive));
        Log.i("50%: ", Integer.toString(fifty));
        Log.i("75%: ", Integer.toString(seventyfive));
        Log.i("95%: ", Integer.toString(ninetyfive));*/

        // STEP COUNTER
        /*
        float twentyfiveSTEP, fiftySTEP, seventyfiveSTEP, ninetyfiveSTEP;
        Log.i("TEST", "BEFORE 25");
        int twentyfivepercentSTEP = (int)stepCountVals.size()/4;
        Log.i("25", Integer.toString(twentyfivepercent));
        twentyfive = stepCountVals.get(twentyfivepercent);
        fifty = stepCountVals.get((int)(stepCountVals.size()/2));
        seventyfive = stepCountVals.get((int)((stepCountVals.size()/4) * 3));
        ninetyfive = stepCountVals.get((int)((stepCountVals.size()/20)*19));


        float sumSTEP = 0;
        float varianceSTEP;
        for(int i = 0; i < stepCountVals.size(); i++){
            if (stepCountVals.get(i) != 0) {
                sumSTEP += (stepCountVals.get(i));
            }
        }

        float averageSTEP = sumSTEP/stepCountVals.size();
        acceleration = (TextView)findViewById(R.id.acceleration);
        acceleration.setText(Float.toString(averageSTEP));
        float tempSTEP = 0;
        for(float aSTEP : stepCountVals){
            tempSTEP += (a-averageSTEP)*(a-averageSTEP);
        }
        varianceSTEP = tempSTEP/(stepCountVals.size()-1);
        double aSTEP = varianceSTEP;
        double stdSTEP = Math.sqrt(aSTEP);
        if (mean1STEP == null){
            Log.i("mean1 ", "null");
            mean1STEP = averageSTEP;
            std1STEP = (float) stdSTEP;
        }
        else{
            Log.i("mean2 ", "updated");
            mean2STEP = averageSTEP;
            std2STEP = (float) stdSTEP;
        }*/
        Log.i("Standard Deviation", Double.toString(std));
        /*Log.i("25%: ", Integer.toString(twentyfive));
        Log.i("50%: ", Integer.toString(fifty));
        Log.i("75%: ", Integer.toString(seventyfive));
        Log.i("95%: ", Integer.toString(ninetyfive));*/
        heartRateVals.removeAllElements();
        stepCountVals.removeAllElements();
        return;

    }
    public void checkSameUser(){
        Log.i("In checkSameUser", "function");
        Float lowerBound = mean1 - std1;
        Float upperBound = mean1 + std1;
        int count = 0;
        int positive = 0;
        for (float i : heartRateVals){
            if (i > 0){
                positive ++;
            }
            if (i >= lowerBound && i <= upperBound){
                count ++;
            }
        }
        if (count >= positive/2){
            Log.i("diff user ", "detected");
            acceleration.setText("Different User Detected");
            sensorManager.unregisterListener(this);
        }
        else{
            Log.i("same user ", "detected");
            acceleration.setText("Same User");
            mean1 = mean2;
            std1 = std2;
        }

    }
}
