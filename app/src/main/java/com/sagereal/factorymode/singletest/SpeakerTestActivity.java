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
import com.sagereal.factorymode.databinding.ActivitySpeakerTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.ToastUtils;
import com.sagereal.factorymode.utils.SharePreferenceUtil;

public class SpeakerTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySpeakerTestBinding mBinding;
    private MediaPlayer mMediaPlayer;
    private BroadcastReceiver mHeadphonesBroadcastReceiver;
    private boolean mPlugHeadphones = false; // 耳机插拔状态
    private AudioManager mAudioManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_speaker_test);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 注册耳机插拔状态变化的广播接收器
        registerHeadphonesReceiver();
        mBinding.btnPass.setOnClickListener(this);
        mBinding.btnFail.setOnClickListener(this);
    }

    public static void openActivity(Context context) {
        context.startActivity(new Intent(context, SpeakerTestActivity.class));
    }

    private void playMusic() {
        releaseMediaPlayer();
        if (mPlugHeadphones) {
            return;
        }

        // 检查音量是否为零，如果是，设置音量为最大值的一半
        if (mAudioManager != null) {
            if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2,
                        AudioManager.FLAG_SHOW_UI);
            }
        }

        // 创建MediaPlayer并设置要播放的音乐文件
        mMediaPlayer = MediaPlayer.create(this, R.raw.music);
        if (mMediaPlayer != null) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            // 播放音乐
            mMediaPlayer.start();
        }
    }

    /**
     * 监听并更改耳机插拔状态变化的广播接收器
     */
    private void registerHeadphonesReceiver() {
        mHeadphonesBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("state")) {
                    pluggedHeadphones();
                    if (!mPlugHeadphones) {
                        playMusic();
                    } else {
                        releaseMediaPlayer();
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(mHeadphonesBroadcastReceiver, filter);
    }

    /**
     * 检查耳机插入状态
     * @return true: 有耳机 false : 无耳机
     */
    private void pluggedHeadphones() {
        if (mAudioManager != null) {
            if (!mAudioManager.isWiredHeadsetOn() && mPlugHeadphones) {
                ToastUtils.showToast(this, getString(R.string.speaker_test_no_headphones), Toast.LENGTH_SHORT);
                mPlugHeadphones = false;
            } else if (mAudioManager.isWiredHeadsetOn()) {
                ToastUtils.showToast(this, getString(R.string.speaker_test_headphones), Toast.LENGTH_SHORT);
                mPlugHeadphones = true;
            }
        }
    }

    /**
     * 当页面可见时播放音乐
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaPlayer == null) {
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
        unregisterReceiver(mHeadphonesBroadcastReceiver);
    }

    /**
     * 释放MediaPlayer资源
     */
    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_pass) {
            pluggedHeadphones();
            // 当耳机被插入，则点击PASS时进行提示
            if (mPlugHeadphones) {
                return;
            } else {
                SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_SPEAKER.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            }
        } else if (v.getId() == R.id.btn_fail) {
            SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_SPEAKER.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
        }
        // 跳转至单项测试列表页面
        finish();
    }
}
