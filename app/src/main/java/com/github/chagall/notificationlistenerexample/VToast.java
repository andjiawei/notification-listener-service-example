package com.github.chagall.notificationlistenerexample;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by jiawei on 2017/9/7.
 */

public class VToast {
    public static Context mContext;
    public static void init(Context context){
        mContext=context;
    }

    public static void show(final String message){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
            }
        });

    }
}
