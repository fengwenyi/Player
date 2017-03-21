package com.xfsy.administrator.player;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();//





        //本地
       /* String path = "";
        videoView.setVideoPath(path);*/


        //网络
        String videoPath = "http://play.68mtv.com:1010/play9/29079.mp4";
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        // 控制视频播放
        MediaController mediaController = new MediaController(this);

        // videoView 和 mediaController 建立关联
        videoView.setMediaController(mediaController);

        //mediaController 与 videoView 建立关联
        mediaController.setMediaPlayer(videoView);


    }

    /**
     * initView
     * 初始化 view
     */
    private void initView() {
        videoView = (VideoView) findViewById(R.id.videoView);
    }

    /**
     * MP3
     * 音频播放
     */
    public void MP3(View view) {
        String path = "http://fm111.img.xiaonei.com/tribe/20070613/10/52/A314269027058MUS.mp3"; //MP3的网络地址
        Uri uri = Uri.parse(path);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
    }
}
