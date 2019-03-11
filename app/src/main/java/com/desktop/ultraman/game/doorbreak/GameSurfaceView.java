package com.desktop.ultraman.game.doorbreak;

import android.content.Intent;
import android.gesture.GestureUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.VelocityTracker;

import com.desktop.ultraman.R;
import com.desktop.ultraman.function.GameMusic;
import com.desktop.ultraman.game.transActivity;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, GestureDetector.OnGestureListener {

    static boolean gamepaused;

    private GestureDetector mGestureDetector;
    boolean gameOver = false;

    private Rect rectRound;
    private Bitmap hinder;
    private boolean[][] hinders;
    int hindersWidth, hindersHeight, hindersLeft, hindersTop;


    private Bitmap door01 = null, door02 = null, door03 = null;
    private Rect doorRect, doorDrawRect;

    private Bitmap eye = null;
    private Rect lefteyeRect;

    // private Bitmap rigtheye = null;
    private Rect righteyeRect;

    private Bitmap movingStick = null; //for moving bar
    private Rect stickRect;

    int batX;

    private Ball mBall;

    private int mHeight; //screen height
    private int mWidth;    //screen width

    private int ballMaxY;

    private Paint pnt = null;
    private int score;

    //   private Bitmap gmend = null;

    private boolean longTouch = false;

    private SoundPool soundPool;
    int brickSound, doorSound, collsionSound;

    Context context;
   // Intent nextintent;

    private boolean gamenext = false;

    public GameSurfaceView(Context context) {
        super(context);
        this.context = context;
        init();
        // TODO Auto-generated constructor stub
    }


    public GameSurfaceView(Context context, AttributeSet attrSet) {
        super(context);
        // TODO Auto-generated constructor stub
        init();

    }

    //   private Rect doorMesured;

    private void init() {

        score = 0;
        gamepaused = false;
        setFocusable(true);
        pnt = new Paint(Paint.ANTI_ALIAS_FLAG);
        pnt.setStyle(Paint.Style.STROKE);
        pnt.setColor(Color.BLACK);
        pnt.setTextSize(30);
        pnt.setStrokeWidth(10);

        getHolder().addCallback(this);
        setBackgroundResource(R.drawable.gamebackground_01);


        hinders = new boolean[2][5];

        for (int i = 0; i < hinders.length; i++)
            for (int j = 0; j < hinders[i].length; j++)
                hinders[i][j] = true;


        //     gmend = BitmapFactory.decodeResource(getResources(), R.drawable.gameover);

        door01 = BitmapFactory.decodeResource(getResources(), R.drawable.game01_door01);
        door02 = BitmapFactory.decodeResource(getResources(), R.drawable.game01_door02);
        door03 = BitmapFactory.decodeResource(getResources(), R.drawable.game01_door03);

        //  doorMesured=new Rect(0,0,door01.getWidth(),door01.getHeight());

        eye = BitmapFactory.decodeResource(getResources(), R.drawable.game01_eyes);

        movingStick = BitmapFactory.decodeResource(getResources(), R.drawable.car);

        hinder = BitmapFactory.decodeResource(getResources(), R.drawable.game01_brick);

        mGestureDetector = new GestureDetector(getContext(), this);


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

        brickSound = soundPool.load(getContext(), R.raw.brick, 1);
        doorSound = soundPool.load(getContext(), R.raw.door, 1);
        collsionSound = soundPool.load(getContext(), R.raw.brick, 1);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        mBall = new Ball(this, mHeight, mWidth);

        setMeasuredDimension(mWidth, mHeight);
        batX = (mWidth - movingStick.getWidth()) / 2;

        ballMaxY = mHeight - mHeight / 10;

        hindersWidth = mWidth / 2;
        hindersHeight = mHeight / 6;
        hindersLeft = mWidth / 4;
        hindersTop = mHeight / 2;
        rectRound = new Rect(hindersLeft - 10, hindersTop - 10,
                hindersLeft + hindersWidth + 10, hindersTop + hindersHeight + 10);

        doorRect = new Rect(mWidth / 2 - mWidth / 16, 0,
                mWidth / 2 + mWidth / 16, mHeight / 16);
        doorDrawRect = new Rect(mWidth / 2 - mWidth / 6, 0,
                mWidth / 2 + mWidth / 6, mHeight / 8);
        lefteyeRect = new Rect(mWidth / 8, mHeight / 4, mWidth * 3 / 8, mHeight / 4 + mHeight / 16);
        righteyeRect = new Rect(mWidth * 5 / 8, mHeight / 4, mWidth * 7 / 8, mHeight / 4 + mHeight / 16);

        stickRect = new Rect(mWidth / 2 - mWidth / 10, ballMaxY, mWidth / 2 + mWidth / 10, mHeight);
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        //canvas.drawPaint(pnt);

        //canvas.drawRect(rectRound, pnt);
        if (score < 50)
            canvas.drawBitmap(door01, null, doorDrawRect, pnt);
        else if (score < 80)
            canvas.drawBitmap(door02, null, doorDrawRect, pnt);
        else canvas.drawBitmap(door03, null, doorDrawRect, pnt);
        //canvas.drawRect(doorRect, pnt);
        //canvas.drawRect(doorDrawRect, pnt);

        canvas.drawBitmap(eye, null, lefteyeRect, pnt);
        //canvas.drawRect(lefteyeRect, pnt);
        canvas.drawBitmap(eye, null, righteyeRect, pnt);
        //canvas.drawRect(righteyeRect, pnt);

        canvas.drawBitmap(movingStick, null, stickRect, pnt);
        //canvas.drawRect(stickRect, pnt);

        for (int i = 0; i < hinders.length; i++)
            for (int j = 0; j < hinders[i].length; j++)
                if (hinders[i][j])
                    canvas.drawBitmap(hinder, null,
                            new Rect(hindersLeft + j * hindersWidth / hinders[i].length,
                                    hindersTop + i * hindersHeight / hinders.length,
                                    hindersLeft + (j + 1) * hindersWidth / hinders[i].length,
                                    hindersTop + (i + 1) * hindersHeight / hinders.length), pnt);

        mBall.onDraw(canvas);

/*
        if (gameOver) {
            canvas.drawBitmap(gmend, (mWidth - gmend.getWidth()) / 2, (mHeight - gmend.getHeight()) / 2, null);
        }
*/
        // canvas.drawBitmap(movingStick, batX, mHeight - 200, null);

    }

    private void onNext() {
        if (score > 100) {
            gameOver = true;
            gamenext = true;
        }


    }

    Thread t = new Thread(new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                while (!gameOver) {
                    if (!gamepaused) {
                        if (ifLeft) onLeft();
                        if (ifRight) onRight();
                        mBall.move();
                        changeCollision();
                        onNext();
                        postInvalidate();
                        Thread.sleep(20);
                    }
                    /*
                     */
                }
                if (gameOver) {
                    Intent nextintent = new Intent(context, transActivity.class);
                    if (!gamenext) {
                        context.stopService(new Intent(context, GameMusic.class));
                        nextintent.putExtra("touchCount", 1);
                        nextintent.putExtra("gameState", 2);
                        context.startActivity(nextintent);
                    } else {
                        context.stopService(new Intent(context, GameMusic.class));
                        nextintent.putExtra("touchCount", 2);
                        nextintent.putExtra("nextActivity", 2);
                        context.startActivity(nextintent);
                        System.exit(0);
                    }
                }

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    });

    @SuppressLint("DrawAllocation")
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    public void surfaceCreated(SurfaceHolder hokder) {
        // TODO Auto-generated method stub
        t.start();
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        t.interrupt();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                longTouch = false;
                ifLeft = false;
                ifRight = false;
                break;
            default:
                break;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    private void changeCollision() {
        //-----砖块碰撞检测
        for (int i = 0; i < hinders.length; i++)
            for (int j = 0; j < hinders[i].length; j++) {
                if (hinders[i][j]) {
                    float left = hindersLeft + j * hindersWidth / hinders[i].length;//左边坐标
                    float right = hindersLeft + (j + 1) * hindersWidth / hinders[i].length;//右边坐标
                    float top = hindersTop + i * hindersHeight / hinders.length;//上边坐标
                    float bottom = hindersTop + (i + 1) * hindersHeight / hinders.length;//下边坐标
                    boolean judgeflag = judgeCollision(left, top, right, bottom);
                    if (judgeflag) {
                        hinders[i][j] = false;
                        //score += Math.random() * 20 + 100;
                        soundPool.play(brickSound, 0.5f, 0.5f, 0, 0, 1);
                        break;
                    }

                }
            }
        //----门碰撞检测
        if (judgeCollision(doorRect.left, doorRect.top, doorRect.right, doorRect.bottom)) {
            soundPool.play(doorSound, 1, 1, 0, 0, 1);
            score += Math.random() * 20;
            Log.i("debug.gameBall", "sorce: " + score);
        }
        //----眼睛碰撞检测
        if (judgeCollision(lefteyeRect.left, lefteyeRect.top, lefteyeRect.right, lefteyeRect.bottom))
            soundPool.play(collsionSound, 0.5f, 0.5f, 0, 0, 1);
        if (judgeCollision(righteyeRect.left, righteyeRect.top, righteyeRect.right, righteyeRect.bottom))
            soundPool.play(collsionSound, 0.5f, 0.5f, 0, 0, 1);

        if (stickRect.contains(mBall.getBallX(), mBall.getBallY() + mBall.getRectHeight() / 2)) {
            mBall.judgeBoundary(0, 0, mWidth, ballMaxY);
            soundPool.play(collsionSound, 0.7f, 0.7f, 0, 0, 1);
            mBall.changeAngle((int) (Math.random() * 10));
        }
        if (mBall.getBallY() + mBall.getRectHeight() / 2 >= mHeight)
            gameOver = true;
        //----全局碰撞检测
        mBall.judgeBoundary(0, 0, mWidth, mHeight);
    }

    private boolean judgeCollision(float left, float top, float right, float bottom) {
        double ballTopX = mBall.getBallX(), ballTopY = mBall.getBallY() - mBall.getRectHeight() / 2;
        if (ballTopX > left && ballTopX < right && ballTopY > top && ballTopY < bottom) {
            mBall.judgeBoundary((int) bottom, 0, mWidth, ballMaxY);
            return true; //碰到砖块下面
        }
        double ballBottomX = mBall.getBallX(), ballBottomY = mBall.getBallY() + mBall.getRectHeight() / 2;
        if (ballBottomX > left && ballBottomX < right && ballBottomY > top && ballBottomY < bottom) {
            mBall.judgeBoundary(0, 0, mWidth, (int) top);
            return true; //碰到砖块上面
        }
        double ballLeftX = mBall.getBallX() - mBall.getRectWidth() / 2, ballLeftY = mBall.getBallY();
        if (ballLeftX > left && ballLeftX < right && ballLeftY > top && ballLeftY < bottom) {
            mBall.judgeBoundary(0, (int) right, 0, ballMaxY);
            return true; //碰到砖块右面
        }
        double ballRightX = mBall.getBallX() + mBall.getRectWidth() / 2, ballRightY = mBall.getBallY();
        if (ballRightX > left && ballRightX < right && ballRightY > top && ballRightY < bottom) {
            mBall.judgeBoundary(0, 0, (int) left, ballMaxY);
            return true; //碰到砖块左面
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        longTouch = true;
        int x = (int) motionEvent.getX();
        if (x < mWidth * 2 / 5) {
            if (stickRect.left > 0) {
                stickRect.left -= 20;
                stickRect.right -= 20;

            }
        } else if (x > mWidth * 3 / 5) {
            if (stickRect.right < mWidth) {
                stickRect.left += 20;
                stickRect.right += 20;

            }
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return true;
    }

    @Override
    public void onLongPress(final MotionEvent motionEvent) {
        // post(new Runnable() {
        //     @Override
        //     public void run() {
        if (longTouch) {
            if (motionEvent.getX() < mWidth * 2 / 5) ifLeft = true;
            else if (motionEvent.getX() > mWidth * 3 / 5) ifRight = true;
        }
        //          postDelayed(this, 50);
        //      }
        //  });
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return true;
    }


    private void onLeft() {
        if (stickRect.left > 0) {
            stickRect.left -= 7;
            stickRect.right -= 7;
        }
    }

    private void onRight() {
        if (stickRect.right < mWidth) {
            stickRect.left += 7;
            stickRect.right += 7;
        }
    }


    private boolean ifLeft = false, ifRight = false;
}
