package com.droidworker.test.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.droidworker.test.R;
import com.droidworker.rximageloader.core.ImageLoader;
import com.droidworker.test.model.bean.ImageBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author DroidWorkerLYF
 */
public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ImageBean> mList = new ArrayList<>();

    public MainAdapter(List<ImageBean> list){
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageBean bean = mList.get(position);
        ImageLoader.with(holder.itemView.getContext()).load(bean.path).error(R.drawable.error)
                .placeholder(R.drawable.loading).into(((ItemViewHolder)holder).mImageView);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_image)
        ImageView mImageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}