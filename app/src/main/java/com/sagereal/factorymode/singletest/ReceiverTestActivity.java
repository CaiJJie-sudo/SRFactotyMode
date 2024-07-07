package com.sagereal.factorymode.singletest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityReceiverTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.SharePreferenceUtils;

public class ReceiverTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityReceiverTestBinding binding;
    private MediaPlayer mediaPlayer;
    private BroadcastReceiver headphonesReceiver;
    private boolean plugHeadphones = false;// 耳机插拔状态
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_receiver_test);
        // 注册耳机插拔状态变化的广播接收器
        registerHeadphonesReceiver();
        binding.btnPass.setOnClickListener(this);
        binding.btnFail.setOnClickListener(this);
    }
    public static void openActivity(Context context) {
        Intent intent = new Intent(context, ReceiverTestActivity.class);
        context.startActivity(intent);
    }

    /**
     * 播放音乐
     */
    private void playMusic() {
        releaseMediaPlayer();
        if (!plugHeadphones){
            return;
        }
        // 创建MediaPlayer并设置要播放的音乐文件
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        if (mediaPlayer != null) {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            // 播放音乐
            mediaPlayer.start();
        }
    }
    /**
     * 监听并更改耳机插拔状态变化的广播接收器
     */
    private void registerHeadphonesReceiver() {
        headphonesReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pluggedHeadphones();
                if(plugHeadphones){
                    playMusic();
                }else {
                    releaseMediaPlayer();
                }
            }
        };
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(headphonesReceiver, filter);
    }
    /**
     * 检查耳机插入状态
     * @return true: 有耳机 false : 无耳机
     */
    private void pluggedHeadphones() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (!audioManager.isWiredHeadsetOn()) {
            Toast.makeText(this, getString(R.string.not_plugged_headphones), Toast.LENGTH_SHORT).show();
            plugHeadphones = false;
        } else if (audioManager.isWiredHeadsetOn() && plugHeadphones == false) {
            Toast.makeText(this, getString(R.string.plugged_headphones), Toast.LENGTH_SHORT).show();
            plugHeadphones = true;
        }
    }
    /**
     * 当页面可见时播放音乐
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer == null) {
            playMusic();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        // 注销耳机插拔状态变化的广播接收器
        unregisterReceiver(headphonesReceiver);
    }
    /**
     * 释放MediaPlayer资源
     */
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_pass) {
            // 当未插入耳机，则点击PASS时进行提示
            pluggedHeadphones();
            if (!plugHeadphones) {
                return;
            } else {
                SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.RECEIVER_POSITION.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            }
        }else if (v.getId() == R.id.btn_fail) {
            SharePreferenceUtils.saveData(v.getContext(), EnumSingleTest.RECEIVER_POSITION.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
        }
        // 跳转至单项测试列表页面
        onBackPressed();
    }
}
