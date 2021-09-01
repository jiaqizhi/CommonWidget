package com.huiyuenet.widgetlib.view.video;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huiyuenet.widgetlib.R;
import com.huiyuenet.widgetlib.logs.LogUtils;

/**
 * 视频控制器
 * @作者 liuzhiwei
 * @时间 2021/8/27 10:41
 */
public class MediaControllerPopWindow extends PopupWindow {
    private Context mContext;
    private MediaUtils mMediaUtils;

    private ImageButton playBtn, screenBtn;
    private TextView videoTime;
    private ProgressBar videoProgress;
    private View view;
    private VideoPlayView mVideoPlayView;

    public ImageButton getPlayBtn() {
        return playBtn;
    }

    public ImageButton getScreenBtn() {
        return screenBtn;
    }

    public MediaControllerPopWindow (Context context, MediaUtils mediaUtils, VideoPlayView videoPlayView) {
        super(context);
        mContext = context;
        mMediaUtils = mediaUtils;
        mVideoPlayView = videoPlayView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_media_controller_pop, null);
        this.playBtn = view.findViewById(R.id.play_btn);
        this.screenBtn = view.findViewById(R.id.screen_btn);
        this.videoTime = view.findViewById(R.id.video_time);
        this.videoProgress = view.findViewById(R.id.progress);

        videoProgress.setMax(100);

        initListener();
        initPopWindow();
    }





    /**
     * 加载监听
     */
    private void initListener () {
        mMediaUtils.setOnVideoBufferingUpdateListener(new MediaUtils.onVideoBufferingUpdateListener() {
            @Override
            public void onVideoBufferingUpdate(int duration, int percent) {
                //LogUtils.d("视频总长度="+duration+",当前缓存进度="+percent);
                videoProgress.setSecondaryProgress(percent);
            }
        });

        mMediaUtils.setOnVideoPlayProgressListener(new MediaUtils.onVideoPlayProgressListener() {
            @Override
            public void onVideoPlayProgress(int duration, int currentPosition) {
                //LogUtils.d("视频总长度="+duration+",当前播放进度="+currentPosition);
                double pro = (double)(currentPosition/1000) / (duration/1000);
                pro = pro * 100;
                videoProgress.setProgress((int)pro);

                int m = (duration/1000) / 60;
                String mStr = m > 9 ? m+"" : "0"+m;
                int s = (duration/1000) % 60;
                String sStr = s > 9 ? s+"" : "0"+s;
                int currentM = (currentPosition/1000) / 60;
                String currentMStr = currentM > 9 ? currentM+"" : "0"+currentM;
                int currentS = (currentPosition/1000) % 60;
                String currentSStr = currentS > 9 ? currentS+"" : "0"+currentS;
                videoTime.setText(mStr +":" + sStr + "/" + currentMStr + ":" + currentSStr);
            }
        });

        this.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mMediaUtils.isPlaying()) {
                    mMediaUtils.pause();
                    playBtn.setBackgroundResource(R.drawable.play);
                } else {
                    mMediaUtils.start();
                    playBtn.setBackgroundResource(R.drawable.pause);
                }

            }
        });

        this.screenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mVideoPlayView.getScreenStatus() == VideoPlayView.SMALLSCREEN) {
                    mVideoPlayView.fullScreen();
                } else {
                    mVideoPlayView.exitFullScreen();
                }
            }
        });

    }

    /**
     * 设置播放状态
     * @param isPlay
     */
    public void setPlayStatuss (boolean isPlay) {
        if (isPlay) {
            playBtn.setBackgroundResource(R.drawable.pause);
        } else {
            playBtn.setBackgroundResource(R.drawable.play);
        }
    }

    /**
     * 设置全屏按钮的状态
     * @param videoIsScreen
     */
    public void setScreenStatus (boolean videoIsScreen) {
        if (!videoIsScreen) {
            screenBtn.setBackgroundResource(R.drawable.full_screen);
        } else {
            screenBtn.setBackgroundResource(R.drawable.exit_full_screen);
        }
    }

    private void initPopWindow () {
        this.setContentView(view);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(100);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(0));//消除黑边框

//        ColorDrawable dw = new ColorDrawable(0x00FFFFFF);
//        this.setBackgroundDrawable(dw);

    }

    public void show (View v) {
        int[] local = new int[2];
        v.getLocationOnScreen(local);
        this.setWidth(v.getWidth());
        int y = v.getHeight()-this.getHeight();
        LogUtils.d("y========================="+y);
        this.showAtLocation(v, Gravity.NO_GRAVITY, local[0], y+local[1]);
        //this.dismiss();
    }
}
