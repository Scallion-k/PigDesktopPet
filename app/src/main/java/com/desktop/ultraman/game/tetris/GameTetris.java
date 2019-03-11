package com.desktop.ultraman.game.tetris;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;

import com.desktop.ultraman.R;

public class GameTetris extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TetrisView tetrisView=new TetrisView(this);
        //tetrisView.setZOrderOnTop(true);
        tetrisView.setZOrderMediaOverlay(true);
        setContentView(tetrisView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("debug.backPressed","backPressed");
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
