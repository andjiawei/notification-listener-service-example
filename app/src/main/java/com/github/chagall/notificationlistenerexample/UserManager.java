package com.github.chagall.notificationlistenerexample;

import com.iflytek.cloud.SpeechSynthesizer;

/**
 * Created by jiawei on 2017/9/7.
 */

public class UserManager {

    private static UserManager userManager;
    public SpeechSynthesizer xunfei;


    private UserManager(){
    }

    public static synchronized UserManager getInstance(){

        if(userManager==null){
            userManager=new UserManager();
        }
        return userManager;
    }

    public String email;
    public String password;
    public String receiver;
    public boolean isAllow=false;//是否允许发送
    public boolean isWechat=true;//默认只接受微信消息
    public boolean isText=false;//文本接收
    public String  applist="";
    public boolean isPlay=false;
}
