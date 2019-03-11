package com.desktop.ultraman;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

public class WindowService extends Service {
    private Pet pet;
    private View petView;
    private List<String> homeList; //桌面应用程序包名列表

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        homeList = EUtil.getHomes(this);
        petView = LayoutInflater.from(this).inflate(R.layout.pet, null);
        ImageView petbody = petView.findViewById(R.id.ultraman);
        pet = new Pet(getApplicationContext(), petbody, petView);
        pet.setSitPath(new String[]{"pig.gif","pig_01.gif",
                "pig_02.gif","pig_03.gif"});
        pet.setFlyPath("fly.gif");
        pet.setDropdownPath("fall.gif");
        pet.setPushPath("hangup.gif");
        pet.setWalkToLeftPath("toleft.gif");
        pet.setWalkToRightPath("toright.gif");
        pet.setStayPath("pig.gif");
        pet.setLeftHidePath("stay_left.gif");
        pet.setRighthidePath("stay_right.gif");
        pet.Run();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
