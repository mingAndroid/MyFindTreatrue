package com.example.machenike.myfindtreatrue.user.account;

/**
 * Created by MACHENIKE on 2017/7/21.
 */

public interface AccountView {

    void showProgress();  //显示进度

    void hideProgress(); //隐藏进度

    void showMessage(String msg); //展示信息

    void updatePhoto(String photoUrl); //返回数据，展示头像
}
