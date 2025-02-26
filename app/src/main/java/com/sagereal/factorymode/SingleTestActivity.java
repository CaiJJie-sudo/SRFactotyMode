package com.sagereal.factorymode;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sagereal.factorymode.databinding.ActivitySingleTestBinding;

import java.util.ArrayList;
import java.util.List;

public class SingleTestActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivitySingleTestBinding mBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_single_test);
        mBinding.ibBack.setOnClickListener(this);
    }

    /**
     * 初始化单项测试数据列表
     * @return
     */
    public List<String> initSingleTestItem(){
        List<String> singleTestItemList = new ArrayList<>();
        singleTestItemList.add(getString(R.string.battery_test));
        singleTestItemList.add(getString(R.string.vibration_test));
        singleTestItemList.add(getString(R.string.mike_test));
        singleTestItemList.add(getString(R.string.headphones_test));
        singleTestItemList.add(getString(R.string.lcd_test));
        singleTestItemList.add(getString(R.string.speaker_test));
        singleTestItemList.add(getString(R.string.receiver_test));
        singleTestItemList.add(getString(R.string.camera_test));
        singleTestItemList.add(getString(R.string.flash_test));
        singleTestItemList.add(getString(R.string.keys_test));
        return singleTestItemList;
    }

    /**
     * 当页面可见时刷新 singleTestItem 的状态
     */
    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView rvSingleTest = mBinding.rvSingleTest;
        SingleTestItemAdapter singleTestItemAdapter = new SingleTestItemAdapter(initSingleTestItem());
        rvSingleTest.setLayoutManager(new LinearLayoutManager(this));
        rvSingleTest.setAdapter(singleTestItemAdapter);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ib_back){
            finish();
        }
    }
}
