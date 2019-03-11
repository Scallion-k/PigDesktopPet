package com.desktop.ultraman.game.papersumo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.desktop.ultraman.R;
import com.desktop.ultraman.function.GameMusic;
import com.desktop.ultraman.game.transActivity;

public class PaperSumoActivity extends Activity {
    private PaperSumoView view;
    private int pointerIndex;
    Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    /**
     * 初期化
     */
    public void init() {
        Intent intent=new Intent(this,GameMusic.class);
        intent.putExtra("musicID",3);
        intent.putExtra("musicSource",1);
        startService(intent);

        view = new PaperSumoView(this);
        setContentView(view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:// primary以外(2本目以降)のtouch
                pointerIndex = event.getActionIndex();
                break;
            default:
                return false;
        }

        // playerのtap領域を触れたか
        for (Rikishi player : view.getPlayers()) {
            if (player.getTapRect().contains(event.getX(pointerIndex),
                    event.getY(pointerIndex))) {
                view.onTap(player.getId());
            }
        }
        if (view.isFin()) {
            stopService(new Intent(this,GameMusic.class));
            Intent nextintent = new Intent(this, transActivity.class);
            nextintent.putExtra("touchCount",1);
            int ifWin=view.ifWin()?1:4;
            nextintent.putExtra("gameState",ifWin);
            startActivity(nextintent);
            // 終了画面でのボタンタップの判定
            /*
            if (view.getRetryRect().contains((int) event.getX(pointerIndex),
                    (int) event.getY(pointerIndex))) {
                init();
            } else if (view.getEndRect().contains(
                    (int) event.getX(pointerIndex),
                    (int) event.getY(pointerIndex))) {
                finish();
            }
            */
        }
        return true;
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
        PaperSumoView.gamepaused=true;
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        dialog.setCancelable(false);
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
                PaperSumoView.gamepaused=false;
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}