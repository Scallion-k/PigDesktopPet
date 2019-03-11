package com.desktop.ultraman.function;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.desktop.ultraman.R;

import java.util.Random;

public class GameMusic extends Service {
    @Nullable

    private MediaPlayer mediaPlayer;
    int musicID;
    int musicSource;
    //铃声音、游戏背景音
    int[] musicsGame=new int[]{R.raw.music_ring, // 0
    R.raw.game01_back,R.raw.game02_back,R.raw.game03_back, //1,2,3
            R.raw.trans_back, //4
            R.raw.game_win,R.raw.game_fall}; // 5,6
    int[] musicsOther=new int[]{R.raw.music01,R.raw.music02,R.raw.music03,R.raw.music04,R.raw.music05};
    int musicEgg=R.raw.egg;
    boolean ifEgg;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        musicSource=intent.getIntExtra("musicSource",0);
        musicID=intent.getIntExtra("musicID",0);
        ifEgg=intent.getBooleanExtra("musicEgg",false);
        if(mediaPlayer==null){
            if(musicSource==0){ //播放音乐
                if(ifEgg)
                    mediaPlayer=MediaPlayer.create(this,musicEgg);
                else mediaPlayer=MediaPlayer.create(this,musicsOther[new Random().nextInt(musicsOther.length)]);
                mediaPlayer.setLooping(false);
                mediaPlayer.start();
            }
            else if(musicSource==1){ //播放游戏背景音乐
                mediaPlayer=MediaPlayer.create(this,musicsGame[musicID]);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        }
        Log.i("debug.musicSer","music start");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            Log.i("debug.musicSer","music stop");
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
}
