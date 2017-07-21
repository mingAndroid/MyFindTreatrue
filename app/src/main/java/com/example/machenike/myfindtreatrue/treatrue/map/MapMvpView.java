package com.example.machenike.myfindtreatrue.treatrue.map;

import com.example.machenike.myfindtreatrue.treatrue.Treasure;

import java.util.List;

/**
 * Created by MACHENIKE on 2017/7/17.
 */

public interface MapMvpView {

    void showMessage(String message);//显示信息

    void setTreatrueData(List<Treasure> treasureList);//设置宝物数据

}
