package com.desktop.ultraman.game.doorbreak;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.desktop.ultraman.R;
import com.desktop.ultraman.function.GameMusic;


public class GameActivity extends Activity {

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new GameSurfaceView(this));

        Intent intent=new Intent(this,GameMusic.class);
        intent.putExtra("musicID",1);
        intent.putExtra("musicSource",1);
        startService(intent);
    }

    @Override
    protected void onStop() {
        Intent intent=new Intent(this,GameMusic.class);
        stopService(intent);
        finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Intent intent=new Intent(this,GameMusic.class);
        stopService(intent);
        finish();
        super.onDestroy();
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
        GameSurfaceView.gamepaused=true;
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
                GameSurfaceView.gamepaused=false;
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
