package com.sagereal.factorymode.singletest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityLcdTestBinding;
import com.sagereal.factorymode.utils.EnumData;

public class LcdTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityLcdTestBinding binding;
    private int screenColorIndex = 0;
    private int[] screenColors = {R.color.red, R.color.green, R.color.blue, R.color.gray};
    private int currentColor = screenColors[screenColorIndex];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lcd_test);
        binding.btnBegin.setOnClickListener(this);
        binding.lcdLayout.setOnClickListener(this);
    }

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, LcdTestActivity.class);
        intent.getIntExtra(String.valueOf(EnumData.LCD_POSITION), 0);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_begin) {
            binding.btnBegin.setVisibility(View.GONE);
            binding.lcdLayout.setBackgroundColor(getResources().getColor(currentColor));
        } else if (v.getId() == R.id.lcd_layout) {
            if(screenColorIndex == screenColors.length - 1) {
                binding.lcdLayout.setBackgroundColor(getResources().getColor(R.color.white));
                binding.linearPassFail.setVisibility(View.VISIBLE);
            } else {
                screenColorIndex = screenColorIndex + 1;
                currentColor = screenColors[screenColorIndex];
                binding.lcdLayout.setBackgroundColor(getResources().getColor(currentColor));
            }

        } else {
            // Handle further clicks or additional logic here
        }
    }

}
