package com.huiyuenet.commonwidget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.huiyuenet.commonwidget.databinding.ActivityDrawViewBinding;

public class DrawViewActivity extends Activity {
    private ActivityDrawViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_draw_view);
        binding.clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.draw.clear();
            }
        });

        binding.revokeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.draw.revoke();
            }
        });
    }


}