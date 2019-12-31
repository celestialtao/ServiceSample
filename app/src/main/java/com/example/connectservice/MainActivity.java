package com.example.connectservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    private String  data;
    private Intent serintent;
    private MyService.Binder binder;
    private TextView tvout;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            //msg携带子进程传输的Bundle并携带数据，在主进程中绘制UI界面
            super.handleMessage(msg);
            tvout.setText(msg.getData().getString("data"));
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    binder = (MyService.Binder) iBinder;
                    binder.getService().setCallback(new MyService.Callback() {
                        @Override
                        public void onDataChange(String data) {
                          //tvout.setText(data);此回调callback在MyService的新线程调用，会违背Android在主线程更新UI的原则
                            //此处利用handler通过向主线程发送Message完成数据的传输
                            Message msg = new Message();
                            Bundle b = new Bundle(); //通过Bundle实现数据的传输
                            b.putString("data",data);
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }
                    });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvout = findViewById(R.id.textView);
        serintent = new Intent(this,MyService.class);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        editText = findViewById(R.id.editText);
        findViewById(R.id.startservice).setOnClickListener(this);
        findViewById(R.id.stopservice).setOnClickListener(this);
        findViewById(R.id.bindservice).setOnClickListener(this);
        findViewById(R.id.unbindservice).setOnClickListener(this);
        findViewById(R.id.syncdata).setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.startservice:
                data = editText.getText().toString();
                serintent.putExtra("data",data); //注意最后的toString调用
                startService(serintent);
                break;
            case R.id.stopservice:
                try {
                    stopService(serintent);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.bindservice:
                bindService(serintent,conn,BIND_AUTO_CREATE);
                break;
            case R.id.unbindservice:
                try {
                    unbindService(conn);
                }
                catch (IllegalArgumentException e){
                    e.printStackTrace();
                }

                break;
            case R.id.syncdata:
                 if(binder != null){
                        binder.setData(editText.getText().toString());
                 }
                break;

        }
    }



}
