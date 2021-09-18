package com.huiyuenet.commonwidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;

import com.huiyuenet.commonwidget.databinding.ActivityMainBinding;
import com.huiyuenet.widgetlib.logs.LogUtils;
import com.huiyuenet.widgetlib.view.camera.CWCamera;
import com.huiyuenet.widgetlib.view.camera.CameraUtils;

import java.util.List;

public class MainActivity extends Activity {
    private ActivityMainBinding binding;
    private boolean selfSize = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //binding.mainCamera.changeCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        binding.mainCamera.setCameraPrepareListener(new CWCamera.CameraPrepareListener() {
            @Override
            public void prepare() {
                if (selfSize) {
                    binding.mainCamera.startPreview();
                    LogUtils.d("width="+binding.mainCamera.getPreviewSize().width+",height="+binding.mainCamera.getPreviewSize().height+"--------------------------------");
                } else {
                    selfSize = true;
                    binding.mainCamera.releaseCamera();
                    binding.mainCamera.openCamera();
                    List<Camera.Size> sizes = CameraUtils.getInstance().getPreviewSizeList();
                    Camera.Size setSize = null;
                    for (Camera.Size size : sizes) {
                        if (size.width == 640 && size.height == 480) {
                            setSize = size;
                            break;
                        }
                    }
                    if (setSize == null) {
                        setSize = sizes.get(0);
                    }
                    binding.mainCamera.initCamera(setSize);

                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //binding.mainCamera.startPreview();
    }

    public void changeCamera (View v) {
        binding.mainCamera.changeCamera(binding.mainCamera.getCameraid() == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK);
        binding.mainCamera.stopPreview();
        binding.mainCamera.releaseCamera();
        binding.mainCamera.openCamera();
        binding.mainCamera.initCamera();
        binding.mainCamera.startPreview();
    }
}