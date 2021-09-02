package com.huiyuenet.commonwidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.huiyuenet.commonwidget.databinding.ActivityVideoTestBinding;
import com.huiyuenet.widgetlib.view.video.VideoPlayView;

public class VideoTestActivity extends Activity {
    private ActivityVideoTestBinding binding;
    private int videoWidth, videoHeight;
    private boolean isCompletedZoom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_test);
//        binding.video.playVideo("http://video.huiyuenet.cn/sv/2deb74cc-177c861ef6b/2deb74cc-177c861ef6b.mp4");
        binding.video.playVideo("http://video.huiyuenet.cn/sv/267869f4-17b2f7fc176/267869f4-17b2f7fc176.mp4");
//        binding.video.playVideo("http://video.huiyuenet.cn/sv/287e0510-17b2f8b4bcb/287e0510-17b2f8b4bcb.mp4");
        binding.video.setOnVideoSizeChangeListener(new VideoPlayView.onVideoSizeChangeListener() {
            @Override
            public void onVideoSizeChange(int width, int height) {
                videoWidth = width;
                videoHeight = height;
            }
        });

        //binding.video.getPopWindow().getScreenBtn().setVisibility(View.GONE);
    }

    public void btnClick (View v) {
        binding.video.playVideo("http://video.huiyuenet.cn/sv/287e0510-17b2f8b4bcb/287e0510-17b2f8b4bcb.mp4");
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (!isCompletedZoom) {
//            //binding.video.stretching(videoWidth, videoHeight);
//            isCompletedZoom = true;
//        }
    }

    //判定当前的屏幕是竖屏还是横屏
    public int ScreenOrient(Activity activity)
    {
        int orient = activity.getRequestedOrientation();
//        if(orient != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && orient != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//            WindowManager windowManager = activity.getWindowManager();
//            Display display = windowManager.getDefaultDisplay();
//            int screenWidth  = display.getWidth();
//            int screenHeight = display.getHeight();
//            orient = screenWidth < screenHeight ?  ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//        }
        return orient;
    }
}