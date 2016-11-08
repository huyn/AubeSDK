package com.aube.sdkdemo;

import android.app.Application;

import com.huyn.baseframework.utils.Constant;

/**
 * Created by huyaonan on 16/10/25.
 */
public class MyApplication extends Application {

    public static final String appKey = "thephone-android";
    public static final String appPrivateKey = "f5835541d842a30f3b7a03962c091950";

    @Override
    public void onCreate() {
        super.onCreate();

        Constant.setAppKeyAndSecret(appKey, appPrivateKey, null);
    }

}
