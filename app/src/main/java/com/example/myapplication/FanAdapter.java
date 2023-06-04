package com.example.myapplication;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.myapplication.base.BaseRecyclerAdapter;
import com.example.myapplication.base.BaseViewHolder;
import com.example.myapplication.databinding.ItemFanRowBinding;

public class FanAdapter extends BaseRecyclerAdapter<FanItem, FanAdapter.ViewHolder> {


    public FanAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    static class ViewHolder extends BaseViewHolder<ItemFanRowBinding, FanItem> {

        public ViewHolder(ViewGroup parent) {
            super(parent, R.layout.item_fan_row);
        }

        @Override
        protected void bind(int position, FanItem data) {
           binding.nameText.setText(data.getFanName());

        }
    }
}
