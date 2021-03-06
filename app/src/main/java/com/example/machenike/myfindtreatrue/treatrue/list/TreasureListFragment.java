package com.example.machenike.myfindtreatrue.treatrue.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.machenike.myfindtreatrue.R;
import com.example.machenike.myfindtreatrue.treatrue.TreasureRepo;

/**
 * Created by MACHENIKE on 2017/7/19.
 */

public class TreasureListFragment extends Fragment{
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 使用RecyclerView完成列表视图的展示
        /**
         * 1. 创建RecyclerView
         * 2. 设置展示的样式：设置布局管理器
         * 3. 设置背景、动画、分割线等
         * 4. 设置适配器、设置数据
         */
        mRecyclerView = new RecyclerView(container.getContext());
        //设置布局管理器:GridLayoutManager,LinearLayoutManager,StaggeredGridLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        // 设置动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置背景
        mRecyclerView.setBackgroundResource(R.mipmap.screen_bg);

        return mRecyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置适配器和数据
        TreasureListAdapter adapter = new TreasureListAdapter();
        mRecyclerView.setAdapter(adapter);

        // 数据：从缓存里面拿到
        adapter.addItemData(TreasureRepo.getInstance().getTreasure());
    }
}
