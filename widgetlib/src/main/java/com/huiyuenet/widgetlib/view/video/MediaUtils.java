package com.huiyuenet.widgetlib.view.video;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.Surface;

import java.io.IOException;

import androidx.annotation.NonNull;

/**
 * mediaPlayer工具类
 * @作者 liuzhiwei
 * @时间 2021/8/26 11:00
 */
public class MediaUtils implements MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnSeekCompleteListener{
    /**
     * 播放器
     */
    private static MediaPlayer mediaPlayer;
    private static MediaUtils mediaUtils;

    /****视频播放状态监听****/
    private OnVideoCompleteListener onVideoCompleteListener;
    private onVideoErrorListener onVideoErrorListener;
    private onVideoBufferingUpdateListener onVideoBufferingUpdateListener;
    private onVideoPlayProgressListener onVideoPlayProgressListener;
    private onVideoSizeChangeListener  onVideoSizeChangeListener;

    private final static int UPDATEBUFFERPROGRESS = 1;
    private final static int UPDATEVIDEOPROGRESS = 2;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == UPDATEBUFFERPROGRESS) {
                if (onVideoBufferingUpdateListener != null) {
                    onVideoBufferingUpdateListener.onVideoBufferingUpdate(duration, mPercent);
                    sendEmptyMessageDelayed(UPDATEBUFFERPROGRESS, 1000);
                }
            } else if (msg.what == UPDATEVIDEOPROGRESS) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    if (onVideoPlayProgressListener != null) {
                        onVideoPlayProgressListener.onVideoPlayProgress(duration, mediaPlayer.getCurrentPosition());
                        sendEmptyMessageDelayed(UPDATEVIDEOPROGRESS, 1000);
                    }
                }

            }
        }
    };

    public void setOnVideoCompleteListener(OnVideoCompleteListener onVideoCompleteListener) {
        this.onVideoCompleteListener = onVideoCompleteListener;
    }

    public void setOnVideoErrorListener(MediaUtils.onVideoErrorListener onVideoErrorListener) {
        this.onVideoErrorListener = onVideoErrorListener;
    }

    public void setOnVideoBufferingUpdateListener(MediaUtils.onVideoBufferingUpdateListener onVideoBufferingUpdateListener) {
        this.onVideoBufferingUpdateListener = onVideoBufferingUpdateListener;
    }

    public void setOnVideoPlayProgressListener(MediaUtils.onVideoPlayProgressListener onVideoPlayProgressListener) {
        this.onVideoPlayProgressListener = onVideoPlayProgressListener;
    }

    public void setOnVideoSizeChangeListener(MediaUtils.onVideoSizeChangeListener onVideoSizeChangeListener) {
        this.onVideoSizeChangeListener = onVideoSizeChangeListener;
    }

    /**
     * 视频总长度
     */
    private int duration;
    /**
     * 缓存进度
     */
    private int mPercent;

    public static MediaUtils getInstance () {
        if (mediaUtils == null) {
            mediaUtils = new MediaUtils();
        }
        return mediaUtils;
    }

    /**
     * 加载事件监听
     */
    private void initMediaPlayerListener () {
        mediaPlayer.setOnVideoSizeChangedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
    }

    /**
     * 设置surface
     * @param surface
     */
    public void setSurface (Surface surface) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setScreenOnWhilePlaying(true);//屏幕常亮
        }
        initMediaPlayerListener();
        mediaPlayer.setSurface(surface);
    }

    /**
     * 设置播放地址，并准备播放
     * @param url
     */
    public void play (String url) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放（可恢复播放）
     */
    public void pause () {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    /**
     * 开始播放/恢复播放
     */
    public void start () {
        handler.removeMessages(UPDATEVIDEOPROGRESS);
        mediaPlayer.start();
        handler.sendEmptyMessageDelayed(UPDATEVIDEOPROGRESS, 500);
    }

    /**
     * 停止播放（无法恢复播放）
     */
    public void stop () {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    /**
     * 停止并释放播放器
     */
    public void release () {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public boolean isPlaying () {
        return mediaPlayer.isPlaying();
    }


    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mPercent = percent;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (onVideoCompleteListener != null) {
            onVideoCompleteListener.onVideoComplete();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (onVideoErrorListener != null) {
            onVideoErrorListener.onVideoError(mp, what, extra);
        }
        return false;
    }

    /**
     * 视频准备完成
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        duration = mp.getDuration();
        if (onVideoSizeChangeListener != null) {
            onVideoSizeChangeListener.onVideoSizeChange(mp.getVideoWidth(), mp.getVideoHeight());
        }
        mp.start();
        handler.sendEmptyMessageDelayed(UPDATEVIDEOPROGRESS, 500);
        handler.sendEmptyMessageDelayed(UPDATEBUFFERPROGRESS, 1000);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    /**
     * 视频播放完成
     * @作者 liuzhiwei
     * @时间 2021/8/27 10:28
     */
    public interface OnVideoCompleteListener {
        void onVideoComplete ();
    }

    /**
     * 视频尺寸变化
     * @作者 liuzhiwei
     * @时间 2021/8/27 10:28
     */
    public interface onVideoSizeChangeListener {
        void onVideoSizeChange (int width, int height);
    }

    /**
     * 视频播放异常
     * @作者 liuzhiwei
     * @时间 2021/8/27 10:28
     */
    public interface onVideoErrorListener {
        void onVideoError (MediaPlayer mp, int what, int extra);
    }

    /**
     * 视频缓存进度
     * @作者 liuzhiwei
     * @时间 2021/8/27 10:28
     */
    public interface onVideoBufferingUpdateListener {
        void onVideoBufferingUpdate (int duration, int percent);
    }

    /**
     * 视频播放进度
     * @作者 liuzhiwei
     * @时间 2021/8/27 10:29
     */
    public interface onVideoPlayProgressListener {
        void onVideoPlayProgress(int duration, int currentPosition);
    }
}
