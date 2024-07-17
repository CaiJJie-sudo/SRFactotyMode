package com.sagereal.factorymode.singletest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.sagereal.factorymode.R;
import com.sagereal.factorymode.databinding.ActivityHeadphonesTestBinding;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.ToastUtils;
import com.sagereal.factorymode.utils.PermissionRequestUtil;
import com.sagereal.factorymode.utils.SharePreferenceUtil;

import java.io.IOException;

public class HeadphonesTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityHeadphonesTestBinding mBinding;
    private MediaRecorder mMediaRecorder;
    private MediaPlayer mMediaPlayer;
    private String mHeadphonesTestFilePath; // 录音文件存放位置
    private boolean mPlugHeadphones = false; // 耳机插拔状态
    private boolean mIsRecording = false; // 是否正在录音
    private boolean mIsPlaying; // 是否正在播放录音
    private boolean mTestOver = false; // 是否测试完毕

    private BroadcastReceiver mHeadphonesBroadcastReceiver;
    private AudioManager mAudioManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_headphones_test);
        mHeadphonesTestFilePath = getExternalCacheDir().getAbsolutePath() + "/headphonesTestRecording.mp4";
        setOnClickListeners(mBinding.btnHeadphonesRecord, mBinding.btnPass, mBinding.btnFail);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // 注册耳机插拔状态变化的广播接收器
        registerHeadphonesReceiver();
    }

    public static void openActivity(Context context) {
        // 检查录音权限
        if (!PermissionRequestUtil.requestSinglePermission(context, Manifest.permission.RECORD_AUDIO)) {
            PermissionRequestUtil.showPermissionDialog(context);
        } else {
            context.startActivity(new Intent(context, HeadphonesTestActivity.class));
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
        // 判断是否插入耳机并进行提示
        if (mAudioManager != null) {
            if (!mAudioManager.isWiredHeadsetOn()) {
                ToastUtils.showToast(this, getString(R.string.not_plugged_headphones), Toast.LENGTH_SHORT);
                mPlugHeadphones = false;
                // 若没有插入耳机且在测试中，则刷新该页面
                if (!mBinding.btnHeadphonesRecord.isEnabled()) {
                    recreate();
                }
            } else if (mAudioManager.isWiredHeadsetOn() && !mPlugHeadphones) {
                ToastUtils.showToast(this, getString(R.string.plugged_headphones), Toast.LENGTH_SHORT);
                mPlugHeadphones = true;
            }
        }
    }

    /**
     * 开始录音
     */
    private void startRecording() {
        mTestOver = false;
        mIsRecording = true;
        mBinding.tvHeadphonesRecordTip.setVisibility(View.VISIBLE);
        mBinding.tvHeadphonesRecordTip.setText(getString(R.string.recording));
        mBinding.btnHeadphonesRecord.setText(getString(R.string.testing));
        mBinding.btnHeadphonesRecord.setEnabled(false);

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setOutputFile(mHeadphonesTestFilePath);

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
            // 停止录音后立即播放录音
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
            mMediaPlayer.setDataSource(mHeadphonesTestFilePath);
            mMediaPlayer.prepare();

            // 检查音量是否为零，如果是，设置音量为最大值的一半
            if (mAudioManager != null && mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2,
                        AudioManager.FLAG_SHOW_UI);
            }
            mIsPlaying = true;
            mMediaPlayer.start();
            mBinding.tvHeadphonesRecordTip.setText(R.string.record_playing);
            // 播放完成
            mMediaPlayer.setOnCompletionListener(mp -> {
                mMediaPlayer.release();
                mMediaPlayer = null;
                mBinding.tvHeadphonesRecordTip.setText(R.string.record_test_finish);
                mBinding.btnHeadphonesRecord.setText(R.string.retest);
                mBinding.btnHeadphonesRecord.setEnabled(true);
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
        mBinding.tvHeadphonesRecordTip.setVisibility(View.INVISIBLE);
        mBinding.btnHeadphonesRecord.setText(R.string.record);
        mBinding.btnHeadphonesRecord.setEnabled(true);
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
        if (v.getId() == R.id.btn_headphones_record) {  // 点击录音
            checkHeadphones();
            if (!mPlugHeadphones) {
                return;
            }
            startRecording();
            return;
        } else if (v.getId() == R.id.btn_pass){
            // 未测试或测试中不能点击通过
            if(!mTestOver) {
<<<<<<< HEAD
                ToastUtils.showToast(this, getString(R.string.test_unfinished), Toast.LENGTH_SHORT);
=======
                ToastUtils.showToast(this, getString(R.string.cannot_pass_fail), Toast.LENGTH_SHORT);
>>>>>>> 9f3c1ad69fd50ae07b351f4d3601d2965f149b22
                return;
            }else {
                SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_HEADPHONES.getValue(), EnumSingleTest.TESTED_PASS.getValue());
            }
        } else if (v.getId() == R.id.btn_fail) {
            SharePreferenceUtil.saveData(v.getContext(), EnumSingleTest.POSITION_HEADPHONES.getValue(), EnumSingleTest.TESTED_FAIL.getValue());
        }
        // 跳转至单项测试列表页面
        finish();
    }
}
