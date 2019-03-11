package com.desktop.ultraman.function;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.desktop.ultraman.R;
import com.desktop.ultraman.windowActivity;

import java.util.Random;

public class MusicView extends View {//implements SurfaceHolder.Callback {

    private Bitmap button_yes, button_no;
    private Rect rect_yes, rect_no1, rect_no2, rect_show;
    private Bitmap showBitmap, eggBitmap;

    private boolean ifMusic = false;

    private Context context;
    Paint paint;
    boolean ifEgg;

    public MusicView(Context context) {
        super(context);

        button_no = BitmapFactory.decodeResource(getResources(), R.drawable.music_no);
        button_yes = BitmapFactory.decodeResource(getResources(), R.drawable.music_yes);
        showBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pig_person);
        eggBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.egg);
        paint = new Paint();
        paint.setStrokeWidth(10.0f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        ifEgg = (new Random().nextInt(50) == 1);
        setBackgroundResource(R.drawable.music_background);
        this.context = context;
        //getHolder().addCallback(this);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int mHeight = MeasureSpec.getSize(heightMeasureSpec);
        int buttonWidth = mWidth / 4;
        int showWidth = mWidth / 2;
        rect_no1 = new Rect(100, mHeight - buttonWidth - 200,
                100 + buttonWidth, mHeight - 200);
        rect_yes = new Rect(mWidth - 100 - buttonWidth, mHeight - buttonWidth - 200,
                mWidth - 100, mHeight - 200);
        rect_no2 = new Rect(mWidth / 2 - buttonWidth / 2, mHeight - buttonWidth - 200,
                mWidth / 2 + buttonWidth / 2, mHeight - 200);
        rect_show = new Rect(mWidth / 4, mHeight / 6, mWidth / 4 + showWidth, mHeight / 6 + showWidth);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (ifEgg)
            canvas.drawBitmap(eggBitmap, null, rect_show, paint);
        else canvas.drawBitmap(showBitmap, null, rect_show, paint);

        if (!ifMusic) {
            canvas.drawBitmap(button_yes,
                    new Rect(0, 0, button_yes.getWidth(), button_yes.getHeight()),
                    rect_yes, paint);
            canvas.drawBitmap(button_no,
                    new Rect(0, 0, button_no.getWidth(), button_no.getHeight()),
                    rect_no1, paint);
        } else {
            canvas.drawBitmap(button_no,
                    new Rect(0, 0, button_no.getWidth(), button_no.getHeight()),
                    rect_no2, paint);
        }

    }

    public void setIfMusic(boolean ifMusic) {
        this.ifMusic = ifMusic;
    }

    public boolean getIfMusic() {
        return ifMusic;
    }

    public Rect getRect_yes() {
        return rect_yes;
    }

    public Rect getRect_no1() {
        return rect_no1;
    }

    public Rect getRect_no2() {
        return rect_no2;
    }
/*
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //t.interrupt();
    }

    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    invalidate();
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();
                if (!getIfMusic() && getRect_yes().contains(x, y)) {
                    setIfMusic(true);
                    context.stopService(new Intent(context, GameMusic.class));
                    Intent intent = new Intent(context, GameMusic.class);
                    intent.putExtra("musicSource", 0);
                    intent.putExtra("musicEgg", ifEgg);
                    postInvalidate();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    context.startService(intent);
                } else if (!getIfMusic() && getRect_no1().contains(x, y)) {
                    context.stopService(new Intent(context, GameMusic.class));
                    context.startActivity(new Intent(context, windowActivity.class));
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else if (getIfMusic() && getRect_no2().contains(x, y)) {
                    context.stopService(new Intent(context, GameMusic.class));
                    context.startActivity(new Intent(context, windowActivity.class));
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}
