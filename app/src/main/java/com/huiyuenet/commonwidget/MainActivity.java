package com.huiyuenet.commonwidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;

import com.huiyuenet.commonwidget.databinding.ActivityMainBinding;
import com.huiyuenet.widgetlib.view.camera.CWCamera;

public class MainActivity extends Activity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //binding.mainCamera.changeCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        binding.mainCamera.setCameraPrepareListener(new CWCamera.CameraPrepareListener() {
            @Override
            public void prepare() {
                binding.mainCamera.startPreview();
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