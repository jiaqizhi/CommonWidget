package com.huiyuenet.widgetlib.view.video;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.huiyuenet.widgetlib.R;
import com.huiyuenet.widgetlib.logs.LogUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 视频播放
 *
 * @作者 liuzhiwei
 * @时间 2021/8/26 10:44
 */
public class VideoPlayView extends FrameLayout implements TextureView.SurfaceTextureListener {
    private TextureView textureView;
    private Context mContext;
    private FrameLayout view;
    private MediaUtils mediaUtils;
    private MediaControllerPopWindow popWindow;
    private boolean popWindowIsShow = false;
    private String url;
    private onVideoSizeChangeListener onVideoSizeChangeListener;
    private onVideoCompletionListener onVideoCompletionListener;
    private SurfaceTexture mSurface;

    private int videoWidth;
    private int videoHeight;

    /**
     * 全屏
     */
    public final static int FULLSCREEN = 0;
    /**
     * 小屏
     */
    public final static int SMALLSCREEN = 1;
    private int screenStatus = SMALLSCREEN;

    /**
     * 控制器显示时间
     */
    private final static int POPWINDOWSSHOWTIME = 1;
    private static int popWindowsShowTime = 5;
    /**
     * 播放视频
     */
    private final static int PLAYVIDEO = 2;
    private boolean isSurfaceCreate = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == POPWINDOWSSHOWTIME) {
                popWindowsShowTime--;
                if (popWindowsShowTime < 0) {
                    if (popWindowIsShow) {
                        popWindow.dismiss();
                        popWindowIsShow = false;
                        popWindowsShowTime = 5;
                    }
                } else {
                    sendEmptyMessageDelayed(1, 1000);
                }
            } else if (msg.what == PLAYVIDEO) {
                if (isSurfaceCreate) {
                    mediaUtils.setSurface(new Surface(mSurface));
                    mediaUtils.play(url);
                } else {
                    sendEmptyMessageDelayed(PLAYVIDEO, 500);
                }
            }
        }
    };

    public void setOnVideoSizeChangeListener(VideoPlayView.onVideoSizeChangeListener onVideoSizeChangeListener) {
        this.onVideoSizeChangeListener = onVideoSizeChangeListener;
    }

    public void setOnVideoCompletionListener(VideoPlayView.onVideoCompletionListener onVideoCompletionListener) {
        this.onVideoCompletionListener = onVideoCompletionListener;
    }

    public int getScreenStatus() {
        return screenStatus;
    }

    public MediaControllerPopWindow getPopWindow() {
        return popWindow;
    }

    public View getView() {
        return view;
    }

    public TextureView getTextureView() {
        return textureView;
    }

    public MediaUtils getMediaUtils() {
        return mediaUtils;
    }

    public VideoPlayView(@NonNull Context context) {
        super(context);
    }

    public VideoPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        view = inflater.inflate(R.layout.layout_video_play, this, true);
//
//        textureView = view.findViewById(R.id.textureView);

        view = new FrameLayout(mContext);
        view.setBackgroundColor(Color.BLACK);
        textureView = new TextureView(mContext);

        LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        view.addView(textureView, params);

        this.addView(view, params);

        mediaUtils = MediaUtils.getInstance();
        popWindow = new MediaControllerPopWindow(mContext, mediaUtils, this);
        setOnListener();

        this.setKeepScreenOn(true);
    }


    /**
     * 播放视频
     *
     * @param url
     */
    public void playVideo(String url) {
        if (isSurfaceCreate) {
            mediaUtils.play(url);
        } else {
            this.url = url;
            handler.sendEmptyMessageDelayed(PLAYVIDEO, 500);
        }

    }

    /**
     * 设置监听
     */
    private void setOnListener() {
        textureView.setSurfaceTextureListener(this);

        mediaUtils.setOnVideoCompleteListener(new MediaUtils.OnVideoCompleteListener() {
            @Override
            public void onVideoComplete() {
                if (onVideoCompletionListener != null) {
                    onVideoCompletionListener.onVideoCompletion();
                }
            }
        });

        mediaUtils.setOnVideoErrorListener(new MediaUtils.onVideoErrorListener() {
            @Override
            public void onVideoError(MediaPlayer mp, int what, int extra) {
                LogUtils.d("播放错误===" + extra);
            }
        });

        mediaUtils.setOnVideoSizeChangeListener(new MediaUtils.onVideoSizeChangeListener() {
            @Override
            public void onVideoSizeChange(int width, int height) {
                videoWidth = width;
                videoHeight = height;
                if (onVideoSizeChangeListener != null) {
                    onVideoSizeChangeListener.onVideoSizeChange(width, height);
                }
                stretching(width, height, VideoPlayView.this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });

        textureView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!popWindowIsShow) {
                    popWindowIsShow = true;
                    popWindow.show(textureView);
                    handler.sendEmptyMessageDelayed(POPWINDOWSSHOWTIME, 1000);
                }
            }
        });

        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                handler.removeMessages(POPWINDOWSSHOWTIME);
                popWindowIsShow = false;
            }
        });
    }

    public void startVideo() {
        mediaUtils.start();
    }

    public void pauseVideo() {
        mediaUtils.pause();
    }

    public void stopVideo() {
        mediaUtils.stop();
    }

    public void releaseVideo() {
        mediaUtils.release();
    }

    public void fullScreen() {
        screenStatus = FULLSCREEN;
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        viewGroup.removeView(view);
        Activity ac = (Activity) mContext;
        ViewGroup contentView = ac.findViewById(android.R.id.content);
        if (videoWidth > videoHeight)
            ac.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //textureView.setTransform(null);
        contentView.addView(view, params);
        popWindow.setScreenStatus(true);
        stretching(videoWidth, videoHeight, contentView, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void exitFullScreen() {
        screenStatus = SMALLSCREEN;
        Activity ac = (Activity) mContext;
        ViewGroup contentView = ac.findViewById(android.R.id.content);
        ac.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        contentView.removeView(view);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(view, params);
        popWindow.setScreenStatus(false);
        stretching(videoWidth, videoHeight, this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 按比例缩放视频控件
     * @param videoWidth
     * @param videoHeight
     * @param rootView
     * @param screen 屏幕方向
     */
    public void stretching(float videoWidth, float videoHeight, View rootView, int screen) {
        int viewWidth = rootView.getWidth();
        int viewHeight = rootView.getHeight();

        if (screen == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            viewWidth = rootView.getHeight();
            viewHeight = rootView.getWidth();
        }
        Matrix matrix = new Matrix();
        //获得最佳缩放比
        float sx = viewWidth / videoWidth;
        float sy = viewHeight / videoHeight;
        //先将视频变回原来大小
        float sx1 = videoWidth / viewWidth;
        float sy1 = videoHeight / viewHeight;
        matrix.preScale(sx1, sy1);

        //判断最佳比例，满足一遍能够填满
        if (videoWidth <= videoHeight) {
            matrix.preScale(sy, sy);
//            float dy = (viewWidth - viewWidth * sy) / 2;
//            LogUtils.d("-----------------------------------------------viewWidth="+viewWidth+",viewWidth * sy="+(viewWidth * sy) + ",dy="+dy + ",videoWidth="+videoWidth);
            float dx = (viewWidth - videoWidth) / 2;
            if (videoWidth < viewWidth) {
                dx = (viewWidth - videoWidth*sy) / 2;
            }
            matrix.postTranslate(dx, 0);
        } else {
            matrix.postScale(sx, sx);
            float leftY = (viewHeight - videoHeight * sx) / 2;
            matrix.postTranslate(0, leftY);
        }
        textureView.setTransform(matrix);
        textureView.postInvalidate();
    }


    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
//        LogUtils.d("----------------------------------onSurfaceTextureAvailable");
        if (mSurface == null) {
            mSurface = surface;
            isSurfaceCreate = true;
        } else {
            textureView.setSurfaceTexture(mSurface);
        }


    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
//        LogUtils.d("----------------------------------onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
//        LogUtils.d("----------------------------------onSurfaceTextureDestroyed");
//        if (mediaUtils != null) {
//            mediaUtils.release();
//            mediaUtils = null;
//        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
//        LogUtils.d("----------------------------------onSurfaceTextureUpdated");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }

    public interface onVideoSizeChangeListener {
        void onVideoSizeChange(int width, int height);
    }

    public interface onVideoCompletionListener {
        void onVideoCompletion();
    }

}
