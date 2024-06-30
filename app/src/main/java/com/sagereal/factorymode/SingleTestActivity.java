package com.sagereal.factorymode;

import android.os.Bundle;
import android.text.NoCopySpan;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SingleTestActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_test);
        findViewById(R.id.ib_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}
