package com.sagereal.factorymode;
import android.content.Context;
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
import com.sagereal.factorymode.utils.EnumData;

import java.util.List;

public class SingleTestItemAdapter extends RecyclerView.Adapter<SingleTestItemAdapter.ViewHolder> {
    private List<String> singleTestItemDataName; // 数据源
    // 构造方法，赋值给 singleTestItemDataList
    public SingleTestItemAdapter(Context context, List<String> singleTestItemDataName){
        this.singleTestItemDataName = singleTestItemDataName;
    }

    // 静态内部类，每个测试项对应的布局
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

    // 创建 ViewHolder 实例，将加载的布局传入到 ViewHolder 的构造函数
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_test_item, parent, false);
        return new ViewHolder(view);
    }

    // 对子项数据进行赋值，会在每个子项被滚动到屏幕内时执行，position 得到当前项的实例
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String singleTestItemName = singleTestItemDataName.get(position);
        holder.bind(singleTestItemName);

        // 设置每一项的点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == EnumData.BATTERY_POSITION.getValue()){
                    BatteryTestActivity.openActivity(holder.itemView.getContext(), holder.getAdapterPosition());
                }
                if (holder.getAdapterPosition() == EnumData.VIBRATION_POSITION.getValue()){
                    VibrationTestActivity.openActivity(holder.itemView.getContext(), holder.getAdapterPosition());
                }
                if (holder.getAdapterPosition() == EnumData.MIKE_POSITION.getValue()){
                    MikeTestActivity.openActivity(holder.itemView.getContext(), holder.getAdapterPosition());
                }
                if (holder.getAdapterPosition() == EnumData.HEADPHONES_POSITION.getValue()){
                    HeadphonesTestActivity.openActivity(holder.itemView.getContext(), holder.getAdapterPosition());
                }
                if (holder.getAdapterPosition() == EnumData.LCD_POSITION.getValue()){
                    LcdTestActivity.openActivity(holder.itemView.getContext(), holder.getAdapterPosition());
                }
                if (holder.getAdapterPosition() == EnumData.SPEAKER_POSITION.getValue()){
                    SpeakerTestActivity.openActivity(holder.itemView.getContext(), holder.getAdapterPosition());
                }
                if (holder.getAdapterPosition() == EnumData.RECEIVER_POSITION.getValue()){
                    ReceiverTestActivity.openActivity(holder.itemView.getContext(), holder.getAdapterPosition());
                }
                if (holder.getAdapterPosition() ==  EnumData.CAMERA_POSITION.getValue()){
                    CameraTestActivity.openActivity(holder.itemView.getContext(), holder.getAdapterPosition());
                }
                if (holder.getAdapterPosition() == EnumData.FLASH_POSITION.getValue()){
                    FlashTestActivity.openActivity(holder.itemView.getContext(), holder.getAdapterPosition());
                }
                if (holder.getAdapterPosition() == EnumData.KEY_POSITION.getValue()){
                    KeysTestActivity.openActivity(holder.itemView.getContext(), holder.getAdapterPosition());
                }
            }
        });
    }

    // 返回 RecyclerView 的子项数目
    @Override
    public int getItemCount() {
        return singleTestItemDataName.size();
    }
}
