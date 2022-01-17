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
import android.widget.Toast;

import com.huiyuenet.commonwidget.databinding.ActivityVideoTestBinding;
import com.huiyuenet.widgetlib.view.video.MediaUtils;
import com.huiyuenet.widgetlib.view.video.VideoPlayView;

public class VideoTestActivity extends Activity {
    private ActivityVideoTestBinding binding;
    private int videoWidth, videoHeight;
    private boolean isCompletedZoom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_test);
//        binding.video.playVideo("http://video.huiyuenet.cn/sv/2deb74cc-177c861ef6b/2deb74cc-177c861ef6b.mp4");//慧学课程
//        binding.video.playVideo("http://online-training.oss-cn-beijing.aliyuncs.com/huixin/1615451289741441433.mp4");//不带字幕横屏
//        binding.video.playVideo("http://video.huiyuenet.cn/sv/267869f4-17b2f7fc176/267869f4-17b2f7fc176.mp4");//小尺寸竖屏
//        binding.video.playVideo("http://video.huiyuenet.cn/sv/287e0510-17b2f8b4bcb/287e0510-17b2f8b4bcb.mp4");//大尺寸竖屏
        binding.video.playVideo("http://video.huiyuenet.cn/sv/28c5ffbf-17b29a88f92/28c5ffbf-17b29a88f92.mp4");//带字幕横屏
        //http://video.huiyuenet.cn/sv/17eae6d7-17b28c1fa12/17eae6d7-17b28c1fa12.mp4  //溺亡
        binding.video.setOnVideoSizeChangeListener(new VideoPlayView.onVideoSizeChangeListener() {
            @Override
            public void onVideoSizeChange(int width, int height) {
                videoWidth = width;
                videoHeight = height;
            }
        });
        binding.video.setVideoStatusListener(new MediaUtils.onVideoStatusListener() {
            @Override
            public void onVideoLag() {
                Toast.makeText(VideoTestActivity.this, "视频缓冲中", Toast.LENGTH_SHORT);
            }

            @Override
            public void onVideoUnobstructed() {
                Toast.makeText(VideoTestActivity.this, "视频开始播放", Toast.LENGTH_SHORT);
            }
        });


        //binding.video.getPopWindow().getScreenBtn().setVisibility(View.GONE);
    }

    public void btnClick (View v) {
        int vid = v.getId();
        if (vid == R.id.hxkc) {
            binding.video.playVideo("http://video.huiyuenet.cn/sv/2deb74cc-177c861ef6b/2deb74cc-177c861ef6b.mp4");
        } else if (vid == R.id.budaizimu) {
//            binding.video.playVideo("http://online-training.oss-cn-beijing.aliyuncs.com/huixin/1615451289741441433.mp4");
            binding.video.playVideo("http://video.huiyuenet.cn/sv/42986c94-17b29a88f5b/42986c94-17b29a88f5b.mp4");
        } else if (vid == R.id.small) {
            binding.video.playVideo("http://video.huiyuenet.cn/sv/267869f4-17b2f7fc176/267869f4-17b2f7fc176.mp4");
        } else if (vid == R.id.big) {
            binding.video.playVideo("http://video.huiyuenet.cn/sv/287e0510-17b2f8b4bcb/287e0510-17b2f8b4bcb.mp4");
        } else if (vid == R.id.daizimu) {
            binding.video.playVideo("http://video.huiyuenet.cn/sv/28c5ffbf-17b29a88f92/28c5ffbf-17b29a88f92.mp4");
        }
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