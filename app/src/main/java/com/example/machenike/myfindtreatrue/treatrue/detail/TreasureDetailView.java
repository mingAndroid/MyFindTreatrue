package com.example.machenike.myfindtreatrue.treatrue.detail;

import java.util.List;

/**
 * Created by gqq on 2017/4/6.
 */

// 宝藏详情的视图接口
public interface TreasureDetailView {

    void showMessage(String msg);// 显示信息
    void setDetailData(List<TreasureDetailResult> list);// 设置数据

}
