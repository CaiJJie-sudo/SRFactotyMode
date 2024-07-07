package com.sagereal.factorymode;
import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.List;

public class SingleTestItemAdapter extends RecyclerView.Adapter<SingleTestItemAdapter.ViewHolder> {
    private List<String> singleTestItemName; // 数据源

    public SingleTestItemAdapter(List<String> singleTestItemName){
        this.singleTestItemName = singleTestItemName;
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
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_test_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 对子项数据进行赋值，会在每个子项被滚动到屏幕内时执行，position 得到当前项的实例
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String singleTestItem = singleTestItemName.get(position);
        holder.bind(singleTestItem);
        // 测试通过显示绿色，测试失败显示红色，否则默认没有颜色
        SharedPreferences sharePreference = holder.itemView.getContext().getSharedPreferences(holder.itemView.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        int singleTestStatus = sharePreference.getInt(holder.itemView.getResources().getString(R.string.single_item_position) + position, EnumSingleTest.UNTESTED.getValue());
        if (singleTestStatus == EnumSingleTest.TESTED_PASS.getValue()){
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.green));
        } else if (singleTestStatus == EnumSingleTest.TESTED_FAIL.getValue()) {
            holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.red));
        }
        // 设置每一项的点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == EnumSingleTest.BATTERY_POSITION.getValue()){
                    BatteryTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.VIBRATION_POSITION.getValue()){
                    VibrationTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.MIKE_POSITION.getValue()){
                    MikeTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.HEADPHONES_POSITION.getValue()){
                    HeadphonesTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.LCD_POSITION.getValue()){
                    LcdTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.SPEAKER_POSITION.getValue()){
                    SpeakerTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.RECEIVER_POSITION.getValue()){
                    ReceiverTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() ==  EnumSingleTest.CAMERA_POSITION.getValue()){
                    CameraTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.FLASH_POSITION.getValue()){
                    FlashTestActivity.openActivity(holder.itemView.getContext());
                }
                if (holder.getAdapterPosition() == EnumSingleTest.KEY_POSITION.getValue()){
                    KeysTestActivity.openActivity(holder.itemView.getContext());
                }
            }
        });
    }

    /**
     * 返回 RecyclerView 的子项数目
     * @return
     */
    @Override
    public int getItemCount() {
        return singleTestItemName.size();
    }
}
