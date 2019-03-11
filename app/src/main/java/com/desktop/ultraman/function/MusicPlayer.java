package com.desktop.ultraman.function;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.desktop.ultraman.R;
import com.desktop.ultraman.windowActivity;

import java.io.IOException;

public class MusicPlayer extends Activity {

    private MusicView view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, GameMusic.class);
        intent.putExtra("musicSource", 1);
        intent.putExtra("musicID", 0);
        startService(intent);
        view = new MusicView(this);
        setContentView(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
/*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (!view.getIfMusic() && view.getRect_yes().contains(x, y)) {
                    view.setIfMusic(true);
                    stopService(new Intent(this, GameMusic.class));
                    Intent intent = new Intent(this, GameMusic.class);
                    intent.putExtra("musicSource", 0);
                    startService(intent);
                    view.postInvalidate();
                } else if (!view.getIfMusic() && view.getRect_no1().contains(x, y)) {
                    stopService(new Intent(this, GameMusic.class));
                    startActivity(new Intent(this, windowActivity.class));
                    finish();
                    //android.os.Process.killProcess(android.os.Process.myPid());
                } else if (view.getIfMusic() && view.getRect_no2().contains(x, y)) {
                    stopService(new Intent(this, GameMusic.class));
                    startActivity(new Intent(this, windowActivity.class));
                    finish();
                    //android.os.Process.killProcess(android.os.Process.myPid());
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
    */
}
