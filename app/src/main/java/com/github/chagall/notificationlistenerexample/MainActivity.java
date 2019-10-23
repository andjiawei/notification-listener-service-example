package com.github.chagall.notificationlistenerexample;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * MIT License
 *
 *  Copyright (c) 2016 Fábio Alves Martins Pereira (Chagall)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class MainActivity extends AppCompatActivity {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    private Context context=this;

    private ImageChangeBroadcastReceiver imageChangeBroadcastReceiver;
    private AlertDialog enableNotificationListenerAlertDialog;
    private EditText et_email;
    private EditText et_password;
    private EditText et_receiver;
    private UserManager userManager;
    private AppCompatCheckBox weChatCheckBox;

    public static void showToast(String message){
//        Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
    }
    SpeechSynthesizer mTts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userManager = UserManager.getInstance();
        VToast.init(this);

        Intent intent=new Intent(this,KeepLiveService.class);
        startService(intent);

        initView();
        getSpData();

        // If the user did not turn the notification listener service on we prompt him to do so
//        if(!isNotificationServiceEnabled()){
//            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
//            enableNotificationListenerAlertDialog.show();
//        }else{
//        }

        // Finally we register a et_receiver to tell the MainActivity when a notification has been received
        imageChangeBroadcastReceiver = new ImageChangeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.github.chagall.notificationlistenerexample");
        registerReceiver(imageChangeBroadcastReceiver,intentFilter);
        saveStore();//请求存储权限
       String applist= FileUtils.getFile();
        userManager.applist=applist;
        Log.e("applist","本地文件："+applist);
//        NetUtils.startSend("15939163333","9503944cecfc4e63bbdc3adfd01acc62");
         mTts = SpeechSynthesizer.createSynthesizer(MainActivity.this, mTtsInitListener);
        Log.e("mTts",mTts.toString());
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d("科大讯飞", "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
//                showTip("初始化失败,错误码："+code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                Toast.makeText(getApplicationContext(), "\"初始化失败,错误码：\"+code+\",请点击网址https://www.xfyun.cn/document/error-code查询解决方案\"", Toast.LENGTH_SHORT);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    private void initView() {
        et_email = (EditText) findViewById(R.id.email);
        et_password = (EditText) findViewById(R.id.password);
        et_receiver = (EditText) findViewById(R.id.receiver);
        weChatCheckBox= (AppCompatCheckBox) findViewById(R.id.cb_wechat);

        // 通知栏监控器开关
        Button notificationMonitorOnBtn = (Button)findViewById(R.id.notification_monitor_on_btn);
        notificationMonitorOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (!isNotificationServiceEnabled()) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "监控器开关已打开", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        Button notificationMonitorOffBtn = (Button)findViewById(R.id.notification_monitor_off_btn);
        notificationMonitorOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (isNotificationServiceEnabled()) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "监控器开关已关闭", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=et_email.getText().toString();
                String password=et_password.getText().toString();
                String receiver=et_receiver.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(receiver)){
                    Toast.makeText(MainActivity.this,"请补全信息",Toast.LENGTH_SHORT).show();
                }else{
                    userManager.email=email;
                    userManager.password=password;
                    userManager.receiver=receiver;
                    userManager.isAllow=true;

                    PrefsAccessor.getInstance(MainActivity.this).saveString("email",email);
                    PrefsAccessor.getInstance(MainActivity.this).saveString("password",password);
                    PrefsAccessor.getInstance(MainActivity.this).saveString("receiver",receiver);

                    Toast.makeText(MainActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                }
            }
        });

        weChatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Toast.makeText(MainActivity.this,"只接受微信消息",Toast.LENGTH_SHORT).show();
                    userManager.isWechat=true;
                }else{
                    Toast.makeText(MainActivity.this,"接受所有消息",Toast.LENGTH_SHORT).show();
                    userManager.isWechat=false;
                }
            }
        });


        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userManager.isText=true;
                Toast.makeText(MainActivity.this,"已开启文本模式",Toast.LENGTH_SHORT).show();

            }
        });

        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSureDialog();

//               boolean flag= FileUtils.deletefile();
//                if (flag){
//                    VToast.show("删除成功");
//                }else{
//                    VToast.show("删除失败");
//                }
//                Toast.makeText(MainActivity.this,"清空文本",Toast.LENGTH_SHORT).show();

            }
        });

        findViewById(R.id.voice_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String text="测试的语音消息哈哈哈哈哈啊";
                int code = mTts.startSpeaking(text, mTtsListener);
                if (code != ErrorCode.SUCCESS) {
                    VToast.show("语音合成失败,错误码: " + code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                }

            }
        });

    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            VToast.show("开始播放");
        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {

        }

        @Override
        public void onCompleted(SpeechError error) {

        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}

            //当设置SpeechConstant.TTS_DATA_NOTIFY为1时，抛出buf数据
			/*if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
						byte[] buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER);
						Log.e("MscSpeechLog", "buf is =" + buf);
					}*/

        }
    };

    private void showSureDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage("确定清空本地消息吗？");
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean flag= FileUtils.deletefile();
                         if (flag){
                               VToast.show("删除成功");
                          }else{
                              VToast.show("重复删除");
                          }
                    }
                });
        builder.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        VToast.show("已取消");
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();

    }

    private void getSpData() {
        String email = PrefsAccessor.getInstance(MainActivity.this).getString("email");
        String password = PrefsAccessor.getInstance(MainActivity.this).getString("password");
        String receiver = PrefsAccessor.getInstance(MainActivity.this).getString("receiver");

        if(!TextUtils.isEmpty(email)){
            et_email.setText(email);
        }

        if(!TextUtils.isEmpty(password)){
            et_password.setText(password);
        }

        if(!TextUtils.isEmpty(receiver)){
            et_receiver.setText(receiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(imageChangeBroadcastReceiver);
    }

    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     * @return True if eanbled, false otherwise.
     */
    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Image Change Broadcast Receiver.
     * We use this Broadcast Receiver to notify the Main Activity when
     * a new notification has arrived, so it can properly change the
     * notification image
     * */
    public class ImageChangeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int receivedNotificationCode = intent.getIntExtra("Notification Code",-1);
        }
    }


    /**
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     * @return An alert dialog which leads to the notification enabling screen
     */
    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.notification_listener_service);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                        VToast.show("点击了确定");
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                        VToast.show("点击了取消");
                    }
                });
        return(alertDialogBuilder.create());
    }

    //检查是否有权限
    public boolean hasPermission(String... permissions){
        for (String permission:permissions){
            if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }
    //请求权限
    public void requestPermission(int code,String... permissions){
        ActivityCompat.requestPermissions(this,permissions,code);
    }

    //请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case 100:

                VToast.show("获取外部存储权限");
                break;

        }
    }
    //保存为本地文件
    private void saveStore(){
        if(hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){

        }else{
            requestPermission(100,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }




}
