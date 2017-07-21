package com.example.machenike.myfindtreatrue;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.example.machenike.myfindtreatrue.user.UserPrefs;

/**
 * Created by MACHENIKE on 2017/7/13.
 */

public class TreatrueApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        // 用户仓库的初始化
        UserPrefs.init(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());
    }
}
