package com.desktop.ultraman.game.papersumo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.desktop.ultraman.R;

public class PaperSumoView extends SurfaceView implements
        SurfaceHolder.Callback, Runnable {
    private int width, height;
    private int finCount;
    private Thread thread;
    private Paint paint;
    private Canvas canvas;
    private Rikishi[] players;
    private final float dohyoRange = 0.50f;
    private RectF canvasRect;

    private Bitmap bgImage;

    private Bitmap dohyoImage;
    private RectF dohyoRect;


    static boolean gamepaused;

    private SoundPool soundPool;
    int collsionSound;

    public PaperSumoView(Context context) {
        super(context);
        getHolder().addCallback(this);

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
        collsionSound = soundPool.load(getContext(), R.raw.collsion, 1);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        width = getWidth();
        height = getHeight();
        gamepaused = false;

        // paint初期化
        paint = new Paint();
        paint.setStrokeWidth(2.0f);
        paint.setAntiAlias(true);

        // 画面
        canvasRect = new RectF(0, 0, width, height);
        bgImage = BitmapFactory.decodeResource(getResources(), R.drawable.gamebackground_03);

        // 战斗舞台

        dohyoImage = BitmapFactory.decodeResource(getResources(), R.drawable.battle);
        dohyoRect = new RectF(width/16, height / 2, width*15/16,height * 2 / 3);


        //人物初始化
        players = new Rikishi[]{
                new Rikishi(0, BitmapFactory.decodeResource(getResources(),
                        R.drawable.game03_player), new PointF(width / 2 - 50.0f,
                        dohyoRect.top), dohyoRect,
                        false, new PointF(5.0f, 3.0f)),
                new Rikishi(1, BitmapFactory.decodeResource(getResources(),
                        R.drawable.game03_devil), new PointF(width / 2 + 50.0f,
                        dohyoRect.top), new RectF(0, 0, 0, 0),
                        true, new PointF(0.7f, 0.7f))};

        if (thread == null) {
            finCount = 0;
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }

    @Override
    public void run() {
        while (thread != null) {
            if (!gamepaused) {
                update();
                doDraw();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doDraw() {
        canvas = getHolder().lockCanvas();
        canvas.drawBitmap(bgImage, null, canvasRect, paint);

        Log.i("debug.game03", "game03 run");
        // player描画
        for (Rikishi player : players) {
            player.draw(canvas);
        }
        canvas.drawBitmap(dohyoImage, null, dohyoRect, paint);
        getHolder().unlockCanvasAndPost(canvas);
    }

    private void update() {
        players[1].jump();
        for (Rikishi player : players) {
            player.update();
            // 終了判定
            if (player.getPositionRect().right < width * (1 - dohyoRange) / 2
                    || width * (1 + dohyoRange) / 2 < player.getPositionRect().left) {
                player.drop();
                finCount++;
            }
        }

        // 判断碰撞
        for (int i = 0; i < players.length - 1; i++) {
            for (int j = i + 1; j < players.length; j++) {
                if (RectF.intersects(players[i].getPositionRect(),
                        players[j].getPositionRect())) {
                    float v = players[i].getVelocity().x;
                    soundPool.play(collsionSound, 1, 1, 0, 0, 1);
                    // 判断碰撞位置
                    float collisionX;
                    if (players[i].getPositionRect().centerX() < players[j]
                            .getPositionRect().centerX()) {
                        collisionX = (players[i].getPositionRect().right + players[j]
                                .getPositionRect().left) / 2.0f;
                    } else {
                        collisionX = (players[i].getPositionRect().left + players[j]
                                .getPositionRect().right) / 2.0f;
                    }

                    // 计算碰撞位移
                    players[i].onCollision(players[j].getVelocity().x,
                            collisionX);
                    players[j].onCollision(v, collisionX);
                }
            }
        }
    }

    public void onTap(int playerId) {
        players[playerId].jump();
    }

    public Rikishi[] getPlayers() {
        return players;
    }

    public boolean isFin() {
        return finCount > 5;
    }

    public boolean ifWin(){ return !players[0].isLose(); }
}
