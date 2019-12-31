package com.example.connectservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import javax.security.auth.callback.Callback;

public class MyService extends Service {
    private  static  boolean  runningflag = false;
    private  static String data = "这是默认信息";
    private static Callback callback = null;
    
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public class Binder extends  android.os.Binder{
        public  Binder(){
            super();
        }
        public void setData(String data){
            MyService.this.data = data;
        }
        public MyService getService(){
            return MyService.this;
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();
        runningflag = true;
        
        new Thread(){
            @Override
            public void run() {
                super.run();
                int i = 0;
                while (runningflag){
                    i++;
                    String str = i +":"+ data;
                    System.out.println(str);
                    if(callback != null){
                        callback.onDataChange(str);//回调函数不为空传输数据到与此服务绑定的活动中
                    }
                    try {
                        sleep(1000);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
                
            }
        }.start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        data = intent.getStringExtra("data");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        runningflag = false;
    }



    public static Callback getCallback() {
        return callback;
    }

    public static void setCallback(Callback callback) {
        MyService.callback = callback;
    }

    public static interface Callback{  //定义回调接口处理逻辑，其具体实现在各活动中
        void onDataChange(String data);
    }
}
