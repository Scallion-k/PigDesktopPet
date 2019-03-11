package com.desktop.ultraman.game.flying;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.View;

import com.desktop.ultraman.R;
import com.desktop.ultraman.function.GameMusic;
import com.desktop.ultraman.game.transActivity;

import java.util.Random;

/**
 * Created by Amandeep Kaur on 2/1/2017.
 */
public class FlyingView extends View {

    static boolean gamepaused;
    private Bitmap gmend = null;

    public static int score;
    int temp;
    int newplatformheight = 0;
    Bitmap imagePlayer;
    int dollflag = 0;
    boolean isjumping;
    boolean isfalling;
    static boolean gameover;


    int jumpspeed, fallspeed;
    int jumpspeedvalue;
    public static int imagex;
    public static int canvaswidth;
    int imagey;


    //----砖

    int totalplatforms = 8;
    int platformdistance;
    int platformWidth;

    int[][] platform = new int[8][7];
    Bitmap platformBitmap;

    //----云
    int[][] cloud = new int[5][4];
    boolean firstDraw = true;
    int countCloud = 5;
    Bitmap cloudBitmap;
    int cloudWidth, cloudHeight;

    Context context;

    private SoundPool soundPool;
    int flySound;

    public FlyingView(Context context) {
        super(context);
        this.context=context;
        gmend = BitmapFactory.decodeResource(getResources(), R.drawable.gameover);
        setBackgroundResource(R.drawable.gamebackground_02);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            SoundPool.Builder builder = new SoundPool.Builder();
            AudioAttributes.Builder attributes = new AudioAttributes.Builder();
            attributes.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY);
            builder.setAudioAttributes(attributes.build())
                    .setMaxStreams(5);
            soundPool = builder.build();
        } else {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }

        flySound = soundPool.load(getContext(), R.raw.jump, 1);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        cloudWidth = MeasureSpec.getSize(widthMeasureSpec) / 6;
        cloudHeight = MeasureSpec.getSize(heightMeasureSpec) / 12;
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

        cloudBitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(getResources(), R.drawable.game02_cloud)
                , getMeasuredWidth() / 6, getMeasuredHeight() / 10, true);

        platformBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.game02_brick);


        init();
    }

    private void init() {

        gamepaused = false;

        imagePlayer = BitmapFactory.decodeResource(getResources(), R.drawable.game02_player);

        fallspeed = 0;

        isjumping = true;
        isfalling = false;

        gameover = false;
        score = 0;
        imagex = getMeasuredWidth() / 2;

        jumpspeedvalue = getMeasuredHeight() / 30;
        jumpspeed = jumpspeedvalue;
        platformdistance = getMeasuredHeight() / 8;
        imagey = getMeasuredHeight() - 100;

        platformWidth = getMeasuredWidth() / 5;

    }

    private void firstSet(){
        firstDraw = false;
        for (int i = 0; i < countCloud; i++) {
            int x = new Random().nextInt(getMeasuredWidth());
            int y = new Random().nextInt(getMeasuredHeight());
            cloud[i][0] = x;
            cloud[i][1] = y;
            cloud[i][2] = 1; // 纵坐标的变化
            if (new Random().nextInt(5) > 3)
                cloud[i][3] = new Random().nextInt(3); //横坐标的变化
            else
                cloud[i][3] = -new Random().nextInt(3);
        }

        for (int i = 0; i < totalplatforms; i++) {
            //--0:基础宽度  left
            //--1：位置 top
            //--2：实际宽度 right
            //--3: 位置 bottom
            //--4: 横向移动距离变化
            //--5: 人物跳上板子的速度变化
            //--6: 板子长度
            platform[i][6] = Math.random() > 0.7 ? new Random().nextInt(platformWidth) : 0;
            temp = platform[i][0] = temp = new Random().nextInt(getMeasuredWidth());
            platform[i][1] = newplatformheight;
            platform[i][2] = temp + platformWidth + platform[i][6];
            platform[i][3] = newplatformheight + 50;
            platform[i][4] = 0;
            platform[i][5] = 0;
            newplatformheight = newplatformheight + platformdistance;
        }
    }

    private void drawCloud(Canvas canvas){
        //--------------绘制云

        for (int i = 0; i < countCloud; i++) {

            cloud[i][0] = cloud[i][0] + cloud[i][3];//横坐标修改

            if (imagey > canvas.getHeight() * 0.4) {
                cloud[i][1] = cloud[i][1] + cloud[i][2];
                canvas.drawBitmap(cloudBitmap, cloud[i][0] - cloudWidth / 2, cloud[i][1] - cloudHeight / 2, new Paint());

            } else {
                cloud[i][1] = cloud[i][1] + cloud[i][2] + jumpspeed;
                canvas.drawBitmap(cloudBitmap, cloud[i][0] - cloudWidth / 2, cloud[i][1] - cloudHeight / 2, new Paint());
            }
            if (cloud[i][1] > canvas.getHeight() || cloud[i][0] < 0 || cloud[i][0] > canvas.getWidth()) { //如果超出了屏幕
                cloud[i][0] = new Random().nextInt(canvas.getWidth());
                cloud[i][1] = 0;
                cloud[i][2] = 1;
                if (new Random().nextInt(5) > 3) cloud[i][3] = new Random().nextInt(3);
                else
                    cloud[i][3] = -new Random().nextInt(3);
            }
        }
        //--------------------
    }

    private void drawPlatforms(Canvas canvas){
        for (int i = 0; i < totalplatforms; i++) {

            Rect platRect = new Rect(platform[i][0] + platform[i][4], platform[i][1],
                    platform[i][2] + platform[i][4], platform[i][3]);
            canvas.drawBitmap(platformBitmap,
                    new Rect(0, 0, platformBitmap.getWidth(), platformBitmap.getHeight()),
                    platRect, new Paint());
            platform[i][0] = platform[i][0] + platform[i][4];
            platform[i][2] = platform[i][2] + platform[i][4];

            //----------改变横向距离移动方向
            if (platform[i][2] > canvas.getWidth()) {
                platform[i][0] = canvas.getWidth() - platformWidth - platform[i][6];
                platform[i][2] = canvas.getWidth();
                platform[i][4] = -platform[i][4];
            }
            if (platform[i][0] < 0) {
                platform[i][0] = 0;
                platform[i][2] = platformWidth + platform[i][6];
                platform[i][4] = -platform[i][4];
            }
            //////collision between doll and platform///////
            if (isfalling &&
                    platRect.contains(imagex + imagePlayer.getWidth() / 2, imagey + imagePlayer.getHeight())) {
                isfalling = false;
                fallspeed = 0;
                isjumping = true;
                soundPool.play(flySound, 1, 1, 0, 0, 1);
                if (platform[i][5] == 1)
                    jumpspeed = 60;
                else
                    jumpspeed = jumpspeedvalue;
            }
        }
    }


    private void drawPlayer(Canvas canvas){
        //-----------绘制人物

        if (isjumping) {
            canvaswidth = canvas.getWidth() - imagePlayer.getWidth();
            if (imagey > canvas.getHeight() * 0.4) {
                canvas.drawBitmap(imagePlayer, imagex, imagey - jumpspeed, null);
                imagey = imagey - jumpspeed;
            }
            ///////////Moving Platforms////////////
            else {
                for (int i = 0; i < totalplatforms; i++) {
                    platform[i][1] = platform[i][1] + jumpspeed;
                    platform[i][3] = platform[i][3] + jumpspeed; //向下移动改变纵坐标
                    if (platform[i][1] >= canvas.getHeight()) { // 如果超出屏幕
                        temp = platform[i][0] = new Random().nextInt(canvas.getWidth());
                        platform[i][6] = Math.random() > 0.7 ? new Random().nextInt(platformWidth) : 0;
                        platform[i][1] = platform[i][1] - canvas.getHeight();
                        platform[i][2] = temp + platformWidth + platform[i][6];
                        platform[i][3] = platform[i][1] + 50;
                        platform[i][4] = 0;

                        if (score > 2000)
                            platform[i][5] = new Random().nextInt(10);
                        if (score > 30000) {  //5000分后开始移动板子
                            if (new Random().nextInt(2) == 1)
                                platform[i][4] = -(new Random().nextInt(score / 3000) + 1);
                            else
                                platform[i][4] = new Random().nextInt(score / 3000) + 1;
                        }
                    }
                    score = score + 1;
                }
            }
            canvas.drawBitmap(imagePlayer, imagex, imagey, null);
            jumpspeed--;

            if (jumpspeed <= 0) {
                isjumping = false;
                isfalling = true;
                fallspeed = 1;
            }
        }


        if (isfalling) {
            if (imagey < canvas.getHeight() - imagePlayer.getHeight()) {

                canvas.drawBitmap(imagePlayer, imagex, imagey + fallspeed, null);
                imagey = imagey + fallspeed;
                fallspeed++;

            } else if (imagey > canvas.getHeight() - imagePlayer.getHeight() && score > 60) {
                gameover = true;
            } else {
                isjumping = true;
                isfalling = false;
                fallspeed = 0;
                jumpspeed = jumpspeedvalue;
            }
        }


        if (imagex <= 0)
            imagex = 0;
        if (imagex > canvas.getWidth() - imagePlayer.getWidth())
            imagex = canvas.getWidth() - imagePlayer.getWidth();
        //------------
    }
    @SuppressLint({"DrawAllocation", "CanvasSize"})
    @Override
    public void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub

        super.onDraw(canvas);

        if(!gamepaused) {
            if (firstDraw)
                firstSet();
            drawCloud(canvas);
            drawPlatforms(canvas);
            drawPlayer(canvas);
        }
        if (gameover) {
            context.stopService(new Intent(context,GameMusic.class));
            Intent nextintent = new Intent(context, transActivity.class);
            nextintent.putExtra("touchCount",1);
            nextintent.putExtra("gameState",3);
            context.startActivity(nextintent);
        }
        else if(score>66666){
            context.stopService(new Intent(context,GameMusic.class));
            Intent nextintent = new Intent(context, transActivity.class);
            nextintent.putExtra("touchCount",2);
            nextintent.putExtra("nextActivity",3);
            context.startActivity(nextintent);
        }
        else {
            try {
                Thread.sleep(20);
                invalidate();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

}
