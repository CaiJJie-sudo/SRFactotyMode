package com.sagereal.factorymode.singletest;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityReceiverTestBinding;
import com.sagereal.factorymode.utils.EnumData;

public class ReceiverTestActivity extends AppCompatActivity {
    private ActivityReceiverTestBinding binding;
    private MediaPlayer mediaPlayer;
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
    private void playMusic() {
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
}
