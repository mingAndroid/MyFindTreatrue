package com.example.machenike.myfindtreatrue.user.login;

/**
 * Created by MACHENIKE on 2017/7/13.
 */

public interface LoginView {

    void showProgress();//显示进度条

    void hideProgress();//隐藏进度条

    void showMessage(String message);//显示信息

    void navigateToHome();//跳转至主页面
}
