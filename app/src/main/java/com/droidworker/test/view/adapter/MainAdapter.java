package com.droidworker.test.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.droidworker.rximageloader.core.ImageLoader;
import com.droidworker.rximageloader.core.transformation.RoundCornerTransform;
import com.droidworker.rximageloader.core.transition.CrossFade;
import com.droidworker.test.R;
import com.droidworker.test.model.bean.ImageBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * @author DroidWorkerLYF
 */
public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ImageBean> mList = new ArrayList<>();

    public MainAdapter(List<ImageBean> list) {
        this.mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .item_image, parent, false));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageBean bean = mList.get(position);
        final ImageView view = ((ItemViewHolder) holder).mImageView;
        ImageLoader.with(holder.itemView.getContext()).load(bean.path)
                .scaleType(ImageView.ScaleType.FIT_XY)
                .transform(new RoundCornerTransform(30, 30))
                .transition(new CrossFade(view))
                .error(R.drawable.error)
                .placeholder(R.drawable.loading)
                .progress(new Action1<Float>() {
                    @Override
                    public void call(Float aFloat) {
                        Log.e("lyf", "progress " + aFloat);
                    }
                })
                .into(view);
    }

    @Override
    public int getItemCount() {
        return mList.size() ;//>= 1 ? 1 :0;
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
