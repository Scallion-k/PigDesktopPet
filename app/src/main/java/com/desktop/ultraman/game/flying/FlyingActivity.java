package com.desktop.ultraman.game.flying;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.desktop.ultraman.R;
import com.desktop.ultraman.function.GameMusic;

/**
 * Created by Amandeep Kaur on 1/27/2017.
 */
public class FlyingActivity extends Activity implements SensorEventListener {
    SensorManager sm;
    FlyingView fly;
    Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


        Intent intent=new Intent(this,GameMusic.class);
        intent.putExtra("musicID",2);
        intent.putExtra("musicSource",1);
        startService(intent);


        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            Sensor s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_GAME);
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        fly = new FlyingView(this);
        setContentView(fly);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (event.values[0] == 0) {
        } else {
            if (event.values[0] > 0)
                FlyingView.imagex = FlyingView.imagex - (int) event.values[0]  * 3;
            Log.e("eventvalue=",(int)event.values[0]+", imagex="+ FlyingView.imagex);
            if (FlyingView.imagex < 0)
                FlyingView.imagex = 0;
            else
                FlyingView.imagex = FlyingView.imagex - (int) event.values[0] * 3;
            if (FlyingView.imagex > FlyingView.canvaswidth)
                FlyingView.imagex = FlyingView.canvaswidth;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode== 4)
            exitByBackKey();
        return super.onKeyDown(keyCode, event);
    }
    @SuppressLint("NewApi")
    public void exitByBackKey(){
        // TODO Auto-generated method stub
        FlyingView.gamepaused=true;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window window = dialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        dialog.setCancelable(false);
        sm.unregisterListener(this);
        dialog.setContentView(R.layout.pause);

        Button exit,resume;
        exit=dialog.findViewById(R.id.bexit);

        exit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.exit(0);
            }
        });

        resume=dialog.findViewById(R.id.bresume);
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlyingView.gamepaused=false;
                dialog.dismiss();
            }
        });
        dialog.show();
    }



    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            Sensor s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_GAME);
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            Sensor s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_GAME);
        }

    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        finish();
    }
}
