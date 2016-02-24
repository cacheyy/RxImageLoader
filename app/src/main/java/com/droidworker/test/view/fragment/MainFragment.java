package com.droidworker.test.view.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.droidworker.rximageloader.core.ImageLoader;
import com.droidworker.test.R;
import com.droidworker.test.model.ImageManager;
import com.droidworker.test.model.bean.ImageBean;
import com.droidworker.test.view.adapter.MainAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {
    @Bind(R.id.rv_image_list)
    RecyclerView imageList;
    private MainAdapter mAdapter;
    private List<ImageBean> mList = new ArrayList<>();

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2,
                GridLayoutManager.VERTICAL, false);
        imageList.setLayoutManager(gridLayoutManager);
        ImageLoader.with(this).addOnScrollListener(imageList);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mAdapter = new MainAdapter(mList);
        imageList.setAdapter(mAdapter);

        ImageManager.getInstance(getContext()).getImageList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<ImageBean>>() {
            @Override
            public void call(List<ImageBean> imageBeans) {
                mList.clear();
//                mList.addAll(imageBeans);
                mList.addAll(getGifList());
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private ArrayList<ImageBean> getGifList(){
        ArrayList<ImageBean> list = new ArrayList<>();
        list.add(new ImageBean(Environment.getExternalStorageDirectory().getAbsolutePath() + "/f048.gif"));
        list.add(new ImageBean("http://assets.materialup.com/uploads/5675f7a6-6bfb-4a5f-96fa-8fd68c7fec2d/gooey_effect-spinner-loader.gif"));
        return list;
    }
}
