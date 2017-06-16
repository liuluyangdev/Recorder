package com.test.recorder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    MediaRecorder recorder = null;
    private TextView tv_timer;
    private Handler mHandler = new Handler();
    private long timer = 0;
    private String timeStr = "";
    MediaPlayer player;
    String filePath;
    File path1;
    String path2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }


    public void click(View v) {

        filePath = getSDCardPath() + "/Record";
        path1 = new File(filePath);
        path2 = filePath +"/"+getFileName()+".3gp";
        File path3 = new File(path2);
        if (!path1.exists()) {
            path1.mkdirs();
        }
//        if (path1.exists()) {
//            path3.delete();
//        }

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(path2);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();   // Recording is now started

        View  view =  getLayoutInflater().inflate(R.layout.timer,null);
        tv_timer = (TextView) view.findViewById(R.id.tv_timer);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("录音")
                .setMessage("正在录音。。。")
                .setView(view)
                .setPositiveButton("停止录音", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recorder.stop();
                recorder.reset();   // You can reuse the object by going back to setAudioSource() step
                recorder.release(); // Now the object cannot be reused
                recorder = null;
                timer = 0;
                timeStr = TimeUtil.getFormatTime(timer);
                tv_timer.setText(timeStr);
                mHandler.removeCallbacks(TimerRunnable);


            }
        }).show();

        countTimer();

    }
    private Runnable TimerRunnable = new Runnable() {

        @Override
        public void run() {
                timer += 1000;
                timeStr = TimeUtil.getFormatTime(timer);
                tv_timer.setText(timeStr);

            countTimer();
        }
    };

    private void countTimer(){
        mHandler.postDelayed(TimerRunnable, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(TimerRunnable);
    }

    //播放录音
    public void click1(View v) {
        player = new MediaPlayer();
        try {
            player.setDataSource(path2);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();

    }

    //获取当前时间
    public static String getCharacterAndNumber() {
        String rel="";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        return rel;
    }

    //获取时间命名的文件名
    public static String getFileName() {

        String fileNameRandom = getCharacterAndNumber();
        return fileNameRandom;
    }

    private String getSDCardPath() {
        File sdcardDir = null;
        //判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        }
        return sdcardDir.toString();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "你取消的授权!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}
