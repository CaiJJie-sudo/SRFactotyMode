package com.sagereal.factorymode.singletest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityMikeTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.ToastUtils;
import com.sagereal.factorymode.utils.PermissionRequestUtil;
import com.sagereal.factorymode.utils.SharePreferenceUtil;

import java.io.IOException;

public class MikeTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMikeTestBinding mBinding;
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;
    private String mMikeAudioFilePath; // 录音文件存放位置
    private boolean mIsRecording = false; // 是否正在录音
    private boolean mIsPlaying = false; // 是否正在播放录音
    private boolean mPlugHeadphones = false; // 耳机插拔状态
    private BroadcastReceiver mHeadphonesBroadcastReceiver;
    private boolean mTestOver = false; // 是否测试完毕
    private AudioManager mAudioManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_mike_test);
        setOnClickListeners(mBinding.btnMikeRecord, mBinding.btnPass, mBinding.btnFail);

        mMikeAudioFilePath = getExternalCacheDir().getAbsolutePath() + "/mikeTestRecording.mp4";
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 注册耳机插拔状态变化的广播接收器
        registerHeadphonesReceiver();
    }

    public static void openActivity(Context context) {
        // 检查录音权限
        if (!PermissionRequestUtil.requestSinglePermission(context, Manifest.permission.RECORD_AUDIO)) {
            PermissionRequestUtil.showPermissionDialog(context);
        } else {
            context.startActivity(new Intent(context, MikeTestActivity.class));
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
                    checkHeadphones();
                }
            }
        };
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(mHeadphonesBroadcastReceiver, filter);
    }

    /**
     * 检查耳机状态
     */
    private void checkHeadphones() {
        if (mAudioManager != null) {
            // 判断是否插入耳机并进行提示
            if (mAudioManager.isWiredHeadsetOn()) {
                ToastUtils.showToast(this, getString(R.string.speaker_test_headphones), Toast.LENGTH_SHORT);
                mPlugHeadphones = true;
                // 若插入了耳机且在测试中，则刷新该页面
                if (!mBinding.btnMikeRecord.isEnabled()) {
                    recreate();
                }
            } else if (!mAudioManager.isWiredHeadsetOn() && mPlugHeadphones) {
                ToastUtils.showToast(this, getString(R.string.speaker_test_no_headphones), Toast.LENGTH_SHORT);
                mPlugHeadphones = false;
            }
        }
    }

    /**
     * 开始录音
     */
    private void startRecording() {
        mTestOver = false;
        mIsRecording = true;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding.tvMikeRecordTip.setVisibility(View.VISIBLE);
        mBinding.tvMikeRecordTip.setText(getString(R.string.recording));
        mBinding.btnMikeRecord.setText(getString(R.string.testing));
        mBinding.btnMikeRecord.setEnabled(false);

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setOutputFile(mMikeAudioFilePath);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRecording();
                }
            }, 5000); // 5秒后停止录音
        } catch (IOException e) {
            e.printStackTrace();
            // 添加日志输出或其他处理
        }
    }

    /**
     * 停止录音
     */
    private void stopRecording() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            // 停止录音后播放录音
            playRecording();
        }
    }

    /**
     * 播放录音
     */
    private void playRecording() {
        // 若录音未录完中途退出，则不播放录音
        if(!mIsRecording){
            return;
        }
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mMikeAudioFilePath);
            mMediaPlayer.prepare();

            // 检查音量是否为零，如果是，设置音量为最大值的一半
            if (mAudioManager != null && mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2,
                        AudioManager.FLAG_SHOW_UI);
            }

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            mIsPlaying = true;
            mMediaPlayer.start();
            mBinding.tvMikeRecordTip.setText(R.string.record_playing);
            // 播放完成
            mMediaPlayer.setOnCompletionListener(mp -> {
                mMediaPlayer.release();
                mMediaPlayer = null;
                mBinding.tvMikeRecordTip.setText(R.string.record_test_finish);
                mBinding.btnMikeRecord.setText(R.string.retest);
                mBinding.btnMikeRecord.setEnabled(true);
                mTestOver = true;
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 页面不可见时停止录音或停止播放录音并恢复页面状态
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mIsRecording) {
            stopRecording();
            mIsRecording = false;
            resetUI();
        }
        if (mIsPlaying && mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mIsPlaying = false;
            resetUI();
        }
    }


    /**
     * 重置UI和测试状态
     */
    private void resetUI() {
        mBinding.tvMikeRecordTip.setVisibility(View.INVISIBLE);
        mBinding.btnMikeRecord.setText(R.string.record);
        mBinding.btnMikeRecord.setEnabled(true);
        mTestOver = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销耳机插拔状态变化的广播接收器
        unregisterReceiver(mHeadphonesBroadcastReceiver);
    }

    /**
     * 设置点击事件监听器
     */
    private void setOnClickListeners(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_mike_record) { // 点击录音
            checkHeadphones();
            if (mPlugHeadphones) {
                return;
            }
            startRecording();
            return;
        } else if (v.getId() == R.id.btn_pass) {
            // 未测试或测试中不能点击通过
            if (!mTestOver) {
                ToastUtils.showToast(this, getString(R.string.test_unfinished), Toast.LENGTH_SHORT);
                return;
            } else {
                SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_MIKE.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            }
        } else if (v.getId() == R.id.btn_fail) { // 点击测试失败
            SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_MIKE.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
        }
        // 跳转至单项测试列表页面
        finish();
    }
}
