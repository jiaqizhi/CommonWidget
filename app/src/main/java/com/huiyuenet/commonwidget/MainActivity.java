package com.huiyuenet.commonwidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;

import com.huiyuenet.commonwidget.databinding.ActivityMainBinding;
import com.xuexiang.xaop.util.PermissionUtils;

import java.security.Permission;

public class MainActivity extends Activity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        PermissionUtils.permission(Manifest.permission.CAMERA);
    }
}