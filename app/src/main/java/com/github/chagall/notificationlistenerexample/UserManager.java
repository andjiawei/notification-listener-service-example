package com.github.chagall.notificationlistenerexample;

/**
 * Created by jiawei on 2017/9/7.
 */

public class UserManager {

    private static UserManager userManager;
    
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
}
