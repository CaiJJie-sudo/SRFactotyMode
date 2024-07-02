package com.sagereal.factorymode.singletest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.utils.EnumData;

public class LcdTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lcd_test);
    }
    public static void openActivity(Context context) {
        Intent intent = new Intent(context, LcdTestActivity.class);
        intent.getIntExtra(String.valueOf(EnumData.LCD_POSITION), 0);
        context.startActivity(intent);
    }
}
