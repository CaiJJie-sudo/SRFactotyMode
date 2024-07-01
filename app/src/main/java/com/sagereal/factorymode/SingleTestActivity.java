package com.sagereal.factorymode;

import android.os.Bundle;
import android.text.NoCopySpan;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.sagereal.factorymode.databinding.ActivityMainBinding;
import com.sagereal.factorymode.databinding.ActivitySingleTestBinding;

public class SingleTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySingleTestBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_test);
        // 显示系统导航键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView rvSingleTest = binding.rvSingleTest;
        findViewById(R.id.ib_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}
