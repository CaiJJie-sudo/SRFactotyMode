package com.sagereal.factorymode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sagereal.factorymode.singletest.BatteryTestActivity;
import com.sagereal.factorymode.singletest.CameraTestActivity;
import com.sagereal.factorymode.singletest.FlashTestActivity;
import com.sagereal.factorymode.singletest.HeadphonesTestActivity;
import com.sagereal.factorymode.singletest.KeysTestActivity;
import com.sagereal.factorymode.singletest.LcdTestActivity;
import com.sagereal.factorymode.singletest.MikeTestActivity;
import com.sagereal.factorymode.singletest.ReceiverTestActivity;
import com.sagereal.factorymode.singletest.SpeakerTestActivity;
import com.sagereal.factorymode.singletest.VibrationTestActivity;
import com.sagereal.factorymode.utils.EnumSingleTest;
import com.sagereal.factorymode.utils.SharePreferenceUtil;

import java.util.List;

public class SingleTestItemAdapter extends RecyclerView.Adapter<SingleTestItemAdapter.ViewHolder> {
    private List<String> mSingleTestItemName; // 数据源

    public SingleTestItemAdapter(List<String> singleTestItemName){
        this.mSingleTestItemName = singleTestItemName;
    }

    /**
     * 静态内部类，每个测试项对应的布局
     */
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvSingleTestItems;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSingleTestItems = itemView.findViewById(R.id.tv_single_test_item);
        }

        public void bind(String singleTestName){
            tvSingleTestItems.setText(singleTestName);
        }
    }

    /**
     * 创建 ViewHolder 实例，将加载的布局传入到 ViewHolder 的构造函数
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_test_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 对子项数据进行赋值，会在每个子项被滚动到屏幕内时执行，position 得到当前项的实例
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String singleTestItem = mSingleTestItemName.get(position);
        holder.bind(singleTestItem);

        // 测试通过显示绿色，测试失败显示红色，否则默认没有颜色
        int singleTestStatus = SharePreferenceUtil.getData(holder.itemView.getContext(), position, EnumSingleTest.UNTESTED.getValue());
        if (singleTestStatus == EnumSingleTest.TESTED_PASS.getValue()){
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.green));
        } else if (singleTestStatus == EnumSingleTest.TESTED_FAIL.getValue()) {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.red));
        }


        // 设置每一项的点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == EnumSingleTest.POSITION_BATTERY.getValue()){
                    BatteryTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.POSITION_VIBRATION.getValue()){
                    VibrationTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.POSITION_MIKE.getValue()){
                    MikeTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.POSITION_HEADPHONES.getValue()){
                    HeadphonesTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.POSITION_LCD.getValue()){
                    LcdTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.POSITION_SPEAKER.getValue()){
                    SpeakerTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.POSITION_RECEIVER.getValue()){
                    ReceiverTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() ==  EnumSingleTest.POSITION_CAMERA.getValue()){
                    CameraTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.POSITION_FLASH.getValue()){
                    FlashTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.POSITION_KEY.getValue()){
                    KeysTestActivity.openActivity(holder.itemView.getContext());
                }
            }
        });
    }

    /**
     * 返回 RecyclerView 的子项数目
     */
    @Override
    public int getItemCount() {
        return mSingleTestItemName.size();
    }
}
