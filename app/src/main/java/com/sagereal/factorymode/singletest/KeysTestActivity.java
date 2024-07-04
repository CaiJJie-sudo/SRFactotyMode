package com.sagereal.factorymode.singletest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityKeysTestBinding;
import com.sagereal.factorymode.utils.EnumData;

public class KeysTestActivity extends AppCompatActivity {
    private ActivityKeysTestBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_keys_test);

    }
    public static void openActivity(Context context) {
        Intent intent = new Intent(context, KeysTestActivity.class);
        intent.getIntExtra(String.valueOf(EnumData.KEY_POSITION), 0);
        context.startActivity(intent);
    }
}
