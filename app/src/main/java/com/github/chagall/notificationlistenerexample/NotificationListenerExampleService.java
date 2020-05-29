package com.github.chagall.notificationlistenerexample;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * MIT License
 * <p>
 * Copyright (c) 2016 Fábio Alves Martins Pereira (Chagall)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class NotificationListenerExampleService extends NotificationListenerService {

    long time = 0;

    StringBuffer sb = new StringBuffer();
    StringBuffer samesb = new StringBuffer();
    UserManager userManager = UserManager.getInstance();
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (sb.length() > 0) {
                if (UserManager.getInstance().isAllow) {
                    sendToEmail("", sb.toString(), "");

                }
//                if(UserManager.getInstance().isText){
//                    saveLocalMessage(sb.toString());
//                }
            }
        }
    };


    /*
            These are the package names of the apps. for which we want to
            listen the notifications
         */
    private static final class ApplicationPackageNames {
        public static final String FACEBOOK_PACK_NAME = "com.facebook.katana";
        public static final String FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca";
        public static final String WHATSAPP_PACK_NAME = "com.whatsapp";
        public static final String INSTAGRAM_PACK_NAME = "com.instagram.android";
    }

    /*
        These are the return codes we use in the method which intercepts
        the notifications, to decide whether we should do something or not
     */
    public static final class InterceptedNotificationCode {
        public static final int FACEBOOK_CODE = 1;
        public static final int WHATSAPP_CODE = 2;
        public static final int INSTAGRAM_CODE = 3;
        public static final int OTHER_NOTIFICATIONS_CODE = 4; // We ignore all notification with code == 4
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
//        Log.e("XSL_Test", "消息的抬头 " + notificationTitle + "接收消息的内容 " + notificationText);


        if (TextUtils.isEmpty(notificationText) || TextUtils.isEmpty(notificationTitle)) {
            return;
        }

        if ("null".equalsIgnoreCase(notificationText) || "null".equalsIgnoreCase(notificationTitle)) {
            return;
        }

        if (notificationTitle.contains("小米")) {
            Log.e("小米手环", "拦截小米手环消息 " + notificationTitle);
            return;
        }

        String replace = userManager.applist.replace("，", ",");
        String[] split = replace.split(",");
        for (int i = 0; i < split.length; i++) {
            if (notificationTitle.contains(split[i]) && !TextUtils.isEmpty(split[i])) {
                Log.e("applist", "收到" + notificationTitle + "的无效通知");
                return;
            }
        }

        //过滤连续相同的通知
        if (samesb.toString().equals(notificationTitle + notificationText)) {
            Log.e("same", "拦截重复消息");
            return;
        }
        samesb.setLength(0);
        samesb.append(notificationTitle);
        samesb.append(notificationText);

        if (UserManager.getInstance().isWechat) {
            if (notificationPkg.contains("com.tencent.mm")) {
                Log.e("接受微信消息", "接受微信消息 ");
                if(UserManager.getInstance().isPlay){
                    startSpeak(notificationTitle+","+notificationText);
                }

                saveLocalMessage(notificationTitle + "&&" + notificationText);
            } else {
                Log.e("拦截非微信的消息", "拦截此次消息");
//                    return;
            }
        } else {
            Log.e("接受所有消息", "接受所有消息");
        }

        if ((System.currentTimeMillis() - time) > 10 * 1000) {
            time = System.currentTimeMillis();
            sb.append(notificationText);
            Log.e("将要发送的消息", sb.toString());
            if (UserManager.getInstance().isAllow) {
                sendToEmail(notificationTitle, sb.toString(), notificationPkg);
            } else {
                Log.e("不允许推送", "不允许发送邮件");
            }


        } else {
            handler.removeCallbacksAndMessages(null);
            handler.sendEmptyMessageDelayed(100, 10 * 1000);
            sb.append(notificationText);
            sb.append(System.getProperty("line.separator"));
        }


        NetUtils.startSend(notificationTitle + "&&" + notificationText, notificationPkg);
        sb.setLength(0);

    }

    private void startSpeak(String s) {
        if(TextUtils.isEmpty(s)){
            UserManager.getInstance().xunfei.startSpeaking("空消息",null);
        }else{
            if(s.contains("条]")){
                UserManager.getInstance().xunfei.startSpeaking(s.split("条]")[1],null);
            }else{
                UserManager.getInstance().xunfei.startSpeaking(s,null);
            }
        }

    }

    private void saveLocalMessage(String message) {
        String nowDate = getNowDate();
        FileUtils.mCreatFile(message, nowDate);
    }

    private String getNowDate() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");// HH:mm:ss
//获取当前时间
        Date date = new Date(System.currentTimeMillis());


        return simpleDateFormat.format(date);
    }

    private void sendToEmail(String notificationTitle, final String text, final String packageName) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //当你从github上下载下来代码后，你需要在这里设置自己的邮箱和密码，这样才能发送信息
                    //此处的密码是163开通第三方服务的授权码，在设置选项卡的pop3 smtp这些打开后，设置的
                    GMailSender sender = new GMailSender(UserManager.getInstance().email, UserManager.getInstance().password);
                    //设置你的邮箱和接收者的邮箱，我这里填写的是个例子
                    sender.sendMail(text,
                            text,
                            UserManager.getInstance().email,
                            UserManager.getInstance().receiver);

                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }
        }).start();

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        Log.i("XSL_Test", "Notification removed " + notificationTitle + " & " + notificationText);

    }

    private int matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if (packageName.equals(ApplicationPackageNames.FACEBOOK_PACK_NAME)
                || packageName.equals(ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME)) {
            return (InterceptedNotificationCode.FACEBOOK_CODE);
        } else if (packageName.equals(ApplicationPackageNames.INSTAGRAM_PACK_NAME)) {
            return (InterceptedNotificationCode.INSTAGRAM_CODE);
        } else if (packageName.equals(ApplicationPackageNames.WHATSAPP_PACK_NAME)) {
            return (InterceptedNotificationCode.WHATSAPP_CODE);
        } else {
            return (InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }

    @Override
    public void onDestroy() {
        if (!TextUtils.isEmpty(sb.toString())) {

            if (UserManager.getInstance().isAllow) {
                sendToEmail("", sb.toString(), "");
            }
//            if(UserManager.getInstance().isText){
//                saveLocalMessage(sb.toString());
//            }

        }
        super.onDestroy();
    }
}
