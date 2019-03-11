package com.desktop.ultraman.game;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.desktop.ultraman.R;
import com.desktop.ultraman.function.GameMusic;
import com.desktop.ultraman.game.doorbreak.GameActivity;
import com.desktop.ultraman.game.flying.FlyingActivity;
import com.desktop.ultraman.game.papersumo.PaperSumoActivity;


public class transActivity extends Activity {

    Bitmap gameFall = null, gameWin = null;
    Bitmap[] showPic; //保存各介绍
    int touchCount;
    int touchTime;
    boolean touchFlag;
    float alphaValue;
    int nextActivity;
    Paint paint, alphaPaint;
    int state; //此时游戏的状态
    int begin;
    boolean showAni;
    Dialog dialog;
    boolean paused;
    Intent intentServices=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //stopService(new Intent(this,GameMusic.class));
        Intent intent = getIntent();
        touchCount = intent.getIntExtra("touchCount", 2);
        nextActivity = intent.getIntExtra("nextActivity", 1);
        // 1:游戏1进入  2:游戏2进入 3：游戏3进入
        state = intent.getIntExtra("gameState", 0);
        // 0:正在游戏   1:游戏胜利  2:游戏一失败 3:游戏二失败 4:游戏三失败
        if(state!=0) touchCount=1;

        intentServices = new Intent(this, GameMusic.class);
        intentServices.putExtra("musicSource", 1);
        if (state == 1) intentServices.putExtra("musicID", 5);
        else if (state == 0) intentServices.putExtra("musicID", 4);
        else intentServices.putExtra("musicID", 6);

        Log.i("debug.gameMusic","music start");
        Log.i("debug.gameMusic","game state: "+state);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                showPic = new Bitmap[9];
                showPic[0] = BitmapFactory.decodeResource(getResources(), R.drawable.text_new);
                showPic[1] = BitmapFactory.decodeResource(getResources(), R.drawable.text_back01);
                showPic[2] = BitmapFactory.decodeResource(getResources(), R.drawable.text_back02);
                showPic[3] = BitmapFactory.decodeResource(getResources(), R.drawable.text_game01);
                showPic[4] = BitmapFactory.decodeResource(getResources(), R.drawable.text_game02);
                showPic[5] = BitmapFactory.decodeResource(getResources(), R.drawable.text_game03);
                showPic[6] = BitmapFactory.decodeResource(getResources(), R.drawable.text_game04);
                showPic[7] = BitmapFactory.decodeResource(getResources(), R.drawable.text_game05);
                showPic[8] = BitmapFactory.decodeResource(getResources(), R.drawable.text_game06);

                if (state == 1)
                    gameWin = BitmapFactory.decodeResource(getResources(), R.drawable.gamewin);
                else if (state == 2)
                    gameFall = BitmapFactory.decodeResource(getResources(), R.drawable.gamefall_01);
                else if (state == 3)
                    gameFall = BitmapFactory.decodeResource(getResources(), R.drawable.gamefall_02);
                else if (state == 4)
                    gameFall = BitmapFactory.decodeResource(getResources(), R.drawable.gamefall_03);
            }
        });


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(new transView(this));

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == 4)
            exitByBackKey();
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("NewApi")
    public void exitByBackKey() {
        // TODO Auto-generated method stub
        paused = true;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window window = dialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.pause);

        Button exit, resume;
        exit = dialog.findViewById(R.id.bexit);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                System.exit(0);
            }
        });
        resume = dialog.findViewById(R.id.bresume);
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paused = false;
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public class transView extends View {
        Context context;

        public transView(Context context) {
            super(context);
            this.context = context;
            init();
            showAnimation();

        }

        private void init() {
            startService(intentServices);
            showAni = true;
            touchFlag = false;
            alphaValue = 0;
            touchTime = 0;
            paused = false;

            paint = new Paint();
            alphaPaint = new Paint();
            alphaPaint.setAlpha((int) alphaValue);
            if (nextActivity == 1) begin = 0;
            else if (nextActivity == 2) begin = 5;
            else if (nextActivity == 3) begin = 7;

            if (state == 1)
                setBackgroundResource(R.drawable.gamewin_back);
            else if (state == 2)
                setBackgroundResource(R.drawable.gamefall_back01);
            else if (state == 3)
                setBackgroundResource(R.drawable.gamefall_back02);
            else if (state == 4)
                setBackgroundResource(R.drawable.gamefall_back03);
            else setBackgroundResource(R.drawable.background);


        }

        @SuppressLint({"DrawAllocation", "CanvasSize"})
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (state == 1) {
                alphaPaint.setAlpha((int) alphaValue);
                canvas.drawBitmap(gameWin,
                        null,
                        new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), alphaPaint);
            } else if (state != 0) {
                alphaPaint.setAlpha((int) alphaValue);
                canvas.drawBitmap(gameFall,
                        null,
                        new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), alphaPaint);
            } else {

                if (showAni) {
                    canvas.drawBitmap(showPic[begin + touchTime],
                            new Rect(0, 0, showPic[begin + touchTime].getWidth(), showPic[begin + touchTime].getHeight()),
                            new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), alphaPaint);
                } else {
                    canvas.drawBitmap(showPic[begin + touchTime],
                            new Rect(0, 0, showPic[begin + touchTime].getWidth(), showPic[begin + touchTime].getHeight()),
                            new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), paint);
                }
            }

        }


        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchFlag = true;
                    break;
                case MotionEvent.ACTION_UP:
                    touchFlag = false;

                    if (!paused) {
                        Log.i("debug.gameTestTouch", "touch : " + touchTime);
                        if (touchTime == (touchCount - 1)) {
                            Intent nextintent = new Intent();
                            nextintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            nextintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            stopService(new Intent(context, GameMusic.class));
                            Log.i("debug.gameMusic", "music stop");
                            if (state != 0) {
                                finish();
                                System.exit(0);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                Log.i("debug.gameTest", "Game Over");
                            } else if (nextActivity == 1) {
                                nextintent.setComponent(new ComponentName(context, GameActivity.class));
                                context.startActivity(nextintent);
                            }
                            else if (nextActivity == 2){
                                nextintent.setComponent(new ComponentName(context, FlyingActivity.class));
                                context.startActivity(nextintent);
                            }
                            else if (nextActivity == 3){
                                nextintent.setComponent(new ComponentName(context, PaperSumoActivity.class));
                                context.startActivity(nextintent);
                            }
                            intentServices = null;
                            finish();
                        } else {
                            if (showAni) {
                                showAni = false;
                                invalidate();
                            } else {
                                showAni = true;
                                touchTime += 1;
                                showAnimation();
                            }
                        }
                    }
                    break;
            }
            return true;
        }

        private void showAnimation() {
            ValueAnimator alphaAni = ValueAnimator.ofFloat(0, 255);
            alphaAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    alphaValue = (float) valueAnimator.getAnimatedValue();
                    alphaPaint.setAlpha((int) alphaValue);
                    invalidate();
                }
            });
            AnimatorSet set = new AnimatorSet();
            set.setDuration(3000);
            set.play(alphaAni);
            set.start();
        }
    }

}
