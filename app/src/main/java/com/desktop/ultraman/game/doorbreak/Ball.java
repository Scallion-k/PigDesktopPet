package com.desktop.ultraman.game.doorbreak;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;

import com.desktop.ultraman.R;

public class Ball{

    private int ballX,ballY;
    private float angle;
    private Bitmap ballBitmap;
    private int rotation;
    private int stepLength;
    private int rectWidth,rectHeight;
    private double move_X=0;
    private double move_Y=0;


    public Ball(SurfaceView v,int sh,int sw){
        this.ballX=0;
        this.ballY=0;
        this.angle= (float) (Math.random()*40+70);
        this.rotation=10;
        this.stepLength=7;

        Bitmap bitmap=BitmapFactory.decodeResource(v.getResources(), R.drawable.ball);
        this.ballBitmap=Bitmap.createScaledBitmap(bitmap,
                sw/10,sw/10,true);
        this.rectWidth=sw/10;
        this.rectHeight=sw/10;

        ballX=sw/2;
        ballY=sh-sw/5;
    }
    @SuppressLint("DrawAllocation")
    public void onDraw(Canvas canvas){ //绘制
        Matrix matrix=new Matrix();
    //    matrix.postScale(1/2f,1/2f,);
        matrix.postTranslate(-getRectWidth()/2,-getRectHeight()/2);
        matrix.postRotate(rotation);
        matrix.postTranslate(getRectWidth()/2,getRectHeight()/2);
        canvas.save();
        canvas.translate(ballX-getRectWidth()/2,ballY-getRectHeight()/2);
        canvas.drawBitmap(ballBitmap,matrix,new Paint());
        matrix.reset();
        canvas.restore();
    }
    void move(){ //球的移动
        rotation=(rotation+10)%360;
        move_X=stepLength*Math.cos(Math.toRadians(angle));
        move_Y=stepLength*Math.sin(Math.toRadians(angle));
        ballX= (int) (ballX+Math.ceil(move_X));
        ballY= (int) (ballY-Math.ceil(move_Y));
    }
    void judgeBoundary(int boundTop,int boundLeft,int boundRight,int boundBottom){ //边界检测

        if(move_X<0&&move_Y>0){ //-----左上方运动
            if(ballY-getRectHeight()/2<=boundTop){ //碰到上边界
                ballY=getRectHeight()/2+boundTop;
                angle=-angle;
            }
            if (ballX-getRectWidth()/2<=boundLeft){//碰到左边界
                ballX=boundLeft+getRectWidth()/2;
                angle=180-angle;
            }
        } else if (move_X>0&&move_Y>0){//-----右上方运动
            if(ballX+getRectWidth()/2>=boundRight){ //碰到右边界
                ballX=boundRight-getRectWidth()/2;
                angle=180-angle;
            }
            if(ballY-getRectHeight()/2<=boundTop){ //碰到上边界
                ballY=getRectHeight()/2+boundTop;
                angle=-angle;
            }
        }else if(move_X<0&&move_Y<0){//-----左下方运动
            if(ballX-getRectWidth()/2<=boundLeft){
                ballX=getRectWidth()/2+boundLeft;
                angle=-angle-180;
            }
            if(ballY+getRectHeight()/2>=boundBottom){
                ballY=boundBottom-getRectHeight()/2;
                angle=-angle;
            }
        }else if(move_X>0&&move_Y<0){//-----右下方运动
            if(ballX+getRectWidth()/2>=boundRight){ //碰到右边界
                ballX=boundRight-getRectWidth()/2;
                angle=180-angle;
            }
            if(ballY+getRectHeight()/2>=boundBottom){ //碰到下边界
                ballY=boundBottom-getRectHeight()/2;
                angle=-angle;
            }
        }
        angle=(angle+360)%360;
    }


    int getRectWidth() {
        return rectWidth;
    }

    int getRectHeight() {
        return rectHeight;
    }

    int getBallX() {
        return ballX;
    }

    int getBallY() {
        return ballY;
    }

    void changeAngle(float speed){
        Log.i("debug.angle","angle: "+angle);
        angle=(angle+angle*(speed/10f))%180;
        Log.i("debug.angle","angle: "+angle+" duo:"+speed/10f);
        if(angle>=0&&angle<=15) angle=15;
        if(angle>=165&&angle<=180) angle=165;
    }
}
