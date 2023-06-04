package com.example.myapplication.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder<B extends ViewDataBinding, D> extends RecyclerView.ViewHolder {
    protected B binding;
    private Context context;

    public BaseViewHolder(ViewGroup parent, @LayoutRes int layoutResId) {
        super(LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false));
        this.binding = DataBindingUtil.bind(itemView);
        context = itemView.getContext();
    }

    protected abstract void bind(int index,D data);

    protected void recycled() {
        // no-op
    }

    protected Context getContext() {
        return context;
    }
}