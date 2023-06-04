package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.myapplication.base.BaseRecyclerAdapter;
import com.example.myapplication.base.BaseViewHolder;
import com.example.myapplication.databinding.ItemFanRowBinding;
import com.example.myapplication.databinding.ItemFanUpdateRowBinding;

public class FanUpdateAdapter extends BaseRecyclerAdapter<FanItem, FanUpdateAdapter.ViewHolder> {


    public FanUpdateAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    static class ViewHolder extends BaseViewHolder<ItemFanUpdateRowBinding, FanItem> {

        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_fan_update_row);
        }

        //TODO
        //사용자 만들 때
        @Override
        public void bind(int position, FanItem data) {
            binding.nameText.setText("'" + data.getFanName() + "'" + "선풍기는 ");

            HeatLevel heatType;

            if (data.getFanHeat() == 0) {
                heatType = HeatLevel.LEVEL1;
            } else if (data.getFanHeat() == 1) {
                heatType = HeatLevel.LEVEL2;
            } else {
                heatType = HeatLevel.LEVEL3;
            }
            binding.contentText.setText("'" + heatType.content + "'" + "를 선택하였습니다.");

        }
    }
}
