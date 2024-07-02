package com.sagereal.factorymode.singletest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.utils.EnumData;

public class ReceiverTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_test);
    }
    public static void openActivity(Context context) {
        Intent intent = new Intent(context, ReceiverTestActivity.class);
        intent.getIntExtra(String.valueOf(EnumData.RECEIVER_POSITION), 0);
        context.startActivity(intent);
    }
}
