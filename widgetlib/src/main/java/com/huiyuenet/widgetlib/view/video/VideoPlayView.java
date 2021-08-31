package com.huiyuenet.widgetlib.view.video;

import android.content.Context;
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
import android.widget.FrameLayout;
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
    private View view;
    private MediaUtils mediaUtils;
    private MediaControllerPopWindow popWindow;
    private boolean popWindowIsShow = false;
    private String url;
    private onVideoSizeChangeListener onVideoSizeChangeListener;
    private Surface mSurface;

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

    private Handler handler = new Handler(){
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
                    mediaUtils.setSurface(mSurface);
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

    public MediaControllerPopWindow getPopWindow() {
        return popWindow;
    }

    public VideoPlayView(@NonNull Context context) {
        super(context);
    }

    public VideoPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_video_play, this, true);

        textureView = view.findViewById(R.id.textureView);
        mediaUtils = MediaUtils.getInstance();
        popWindow = new MediaControllerPopWindow(context, mediaUtils);
        setOnListener();
    }



    public void playVideo(String url) {
        this.url = url;
        handler.sendEmptyMessageDelayed(PLAYVIDEO, 500);
    }

    private void setOnListener() {
        textureView.setSurfaceTextureListener(this);

        mediaUtils.setOnVideoCompleteListener(new MediaUtils.OnVideoCompleteListener() {
            @Override
            public void onVideoComplete() {

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
                if (onVideoSizeChangeListener != null) {
                    onVideoSizeChangeListener.onVideoSizeChange(width, height);
                }
                //stretching(width, height);
            }
        });

        textureView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!popWindowIsShow)  {
                    popWindowIsShow = true;
                    popWindow.show(view);
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

    public void stretching (float videoWidth, float videoHeight) {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        Matrix matrix = new Matrix();
        //获得最佳缩放比
        float sx = viewWidth / videoWidth;
        float sy = viewHeight / videoHeight;
        //先将视频变回原来大小
        float sx1 = videoWidth / viewWidth;
        float sy1 = videoHeight / viewHeight;
        matrix.preScale(sx1, sy1);

        //判断最佳比例，满足一遍能够填满
        if (sx >= sy) {
            matrix.preScale(sy, sy);
            float leftX = (viewWidth - viewWidth * sy) / 2;
            matrix.postTranslate(0, leftX);
        } else {
            matrix.postScale(sx,sx);
            float leftY = (viewHeight - videoHeight * sx) / 2;
            matrix.postTranslate(0, leftY);
        }
        textureView.setTransform(matrix);
        textureView.postInvalidate();
    }


    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        LogUtils.d("----------------------------------onSurfaceTextureAvailable");
        if (mediaUtils == null) {
            return;
        }
        mSurface = new Surface(surface);
        isSurfaceCreate = true;

    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        LogUtils.d("----------------------------------onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        LogUtils.d("----------------------------------onSurfaceTextureDestroyed");
        if (mediaUtils != null) {
            mediaUtils.release();
            mediaUtils = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        LogUtils.d("----------------------------------onSurfaceTextureUpdated");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }

    public interface onVideoSizeChangeListener {
        void onVideoSizeChange(int width, int height);
    }
}
