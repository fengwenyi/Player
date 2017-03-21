package com.xfsy.administrator.player;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xfsy.administrator.util.FontDisplayUtil;

/**
 * Created by Administrator on 2017/3/21 0021.
 */

public class PlayerActivity extends AppCompatActivity {

    private CustomVideoView videoView;  //播放器
    private LinearLayout controllerLayout;  //控制布局
    private ImageView play_controller_img;  //播放/暂停控制
    private TextView time_current_tv;  //当前播放时间
    private TextView time_total_tv;  //视频播放总时间
    private SeekBar play_seek;  //播放进度
    private SeekBar volumn_seek;  //音量进度
    private ImageView screen_img;   //全屏
    private int screen_width; //屏幕宽度
    private int screen_height; //屏幕高度
    private RelativeLayout videoLayout;
    private AudioManager audioManager; //音频管理器
    private ImageView volume_img; //音量
    private boolean isFullScreen = false; //竖屏
    private boolean isVisible = true; //全屏时，播放按钮等全部隐藏
    private RelativeLayout option_layout; //操作布局

    //常量
    public static final int UPDATE_UI = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //初始化布局页面
        initView();
        initUI(); //

        //点击事件处理
        setPlayerEvent();


        //网络
        String videoPath = "http://162.250.97.90/file/PLAY/61213.mp4";
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        videoView.start();
        UIHandler.sendEmptyMessage(UPDATE_UI); //通过 handler 刷新进度条


    }


    /**
     * initView
     * 初始化布局页面
     */
    private void initView() {
        videoView = (CustomVideoView) findViewById(R.id.my_videoView);
        controllerLayout = (LinearLayout) findViewById(R.id.controllerbr_layout);
        play_controller_img = (ImageView) findViewById(R.id.pause_img);
        time_current_tv = (TextView) findViewById(R.id.time_current_tv);
        time_total_tv = (TextView) findViewById(R.id.time_total_tv);
        play_seek = (SeekBar) findViewById(R.id.play_seek);
        volumn_seek = (SeekBar) findViewById(R.id.volumn_seek);
        videoLayout = (RelativeLayout) findViewById(R.id.videoLayout);
        volume_img = (ImageView) findViewById(R.id.volume_img);
        screen_img = (ImageView) findViewById(R.id.screen_img);
        option_layout = (RelativeLayout) findViewById(R.id.option_layout);
    }

    /**
     * initUI
     * 获取界面的值
     */
    private void initUI() {
        screen_width = getResources().getDisplayMetrics().widthPixels;
        screen_height = getResources().getDisplayMetrics().heightPixels;
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE); //获取音频服务

        int streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); //当前设备的最大音量
        int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC); //当前设备的当前音量

        volumn_seek.setMax(streamMaxVolume); //音量进度条的最大值
        volumn_seek.setProgress(streamVolume); //音量进度条的当前值

    }

    /**
     * setPlayerEvent
     * 点击事件处理
     */
    private void setPlayerEvent() {
        /**
         * 控制视频的播放和暂停
         * 切换图片，并改变播放状态
         */
        play_controller_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    play_controller_img.setImageResource(R.drawable.play_btn_style);
                    //暂停播放
                    videoView.pause();
                } else {
                    play_controller_img.setImageResource(R.drawable.pause_btn_style);
                    //继续播放
                    videoView.start();
                    UIHandler.sendEmptyMessage(UPDATE_UI); //通过 handler 刷新进度条
                }
            }
        });

        /**
         * 视频播放进度条拖动事件处理
         * 1.UIHandler停止刷新
         * 2.获取拖动的位置
         * 3.从拖动的位置，开始播放，让UIHandler刷新
         */
        play_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updataTextViewWithTimeFormat(time_current_tv, progress); //
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                UIHandler.removeMessages(UPDATE_UI); //

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress(); //
                videoView.seekTo(progress); //令视频播放的进度遵循seekBar停止拖动的这一刻的进度
                UIHandler.sendEmptyMessage(UPDATE_UI);
            }
        });
        /**
         * 音频进度条拖动事件处理
         */
        volumn_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //设置当前设备的音量
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0); //参数：类型，当前值，标记
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        /**
         * 全屏与半屏的切换
         */
        screen_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) { //横屏状态
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //改变为竖屏状态
                } else { //竖屏状态
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //改变为横屏状态
                }
            }
        });
        //
        videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisible) {//显示时
                    controllerLayout.setVisibility(View.GONE);
                    isVisible = false;
                } else {
                    isVisible = true;
                    controllerLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    /**
     * updataTextViewWithTimeFormat
     * 对时间进行格式化处理
     * @param textView  TextView控件
     * @param millisecond  时间，毫秒
     */
    private void updataTextViewWithTimeFormat(TextView textView, int millisecond) {
        int second = millisecond / 1000;
        int hh = second / 3600;  //时
        int mm = second % 3600 / 60;  //分钟
        int ss = second % 60;  //秒
        String str = null;
        if (hh != 0) {
            str = String.format("%02d:%02d:%02d", hh, mm, ss); //格式化
            //%02d  如果只有个位，那么，十位就会用0填充
        } else {
            str = String.format("%02d:%02d", mm, ss);
        }
        textView.setText(str);
    }

    /**
     * UIHandler
     * 通过 handler 刷新自己，实现进度条更新的效果
     */
    private Handler UIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == UPDATE_UI) { //标识
                int currentPosition = videoView.getCurrentPosition(); //视频当前播放时间，毫秒
                int totalDuration = videoView.getDuration(); //视频总时间，毫秒

                //格式化视频播放时间
                updataTextViewWithTimeFormat(time_current_tv, currentPosition);
                updataTextViewWithTimeFormat(time_total_tv, totalDuration);

                //播放进度条
                play_seek.setMax(totalDuration);
                play_seek.setProgress(currentPosition);

                UIHandler.sendEmptyMessageDelayed(UPDATE_UI, 500); //自己刷新自己
                //达到刷新的效果
            }
        }
    };

    /**
     * 暂停
     */
    @Override
    protected void onPause() {
        super.onPause();
        UIHandler.removeMessages(UPDATE_UI); //停止 handler 自动刷新
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 监听到屏幕方向的改变
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /**
         * 当屏幕方向为横屏的时候
         */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //横屏时，音频控制可见
            volume_img.setVisibility(View.VISIBLE);
            volumn_seek.setVisibility(View.VISIBLE);
            isFullScreen = true;
        } else {/** 当屏幕方向为竖屏的时候 */
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, FontDisplayUtil.dip2px(this, 240));
            //竖屏时，音频控制不可见
            volume_img.setVisibility(View.GONE);
            volumn_seek.setVisibility(View.GONE);
            isFullScreen = false;
        }
    }

    /**
     * setVideoViewScale
     * 横屏竖屏转换时，视频大小处理
     * @param width
     * @param height
     */
    private void setVideoViewScale(int width, int height) { //像素，需要转换
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        videoView.setLayoutParams(layoutParams);

        ViewGroup.LayoutParams layoutParams1 = videoLayout.getLayoutParams();
        layoutParams1.width = width;
        layoutParams1.height = height;
        videoLayout.setLayoutParams(layoutParams1);
    }
}
