package com.example.machenike.myfindtreatrue.user.login;

import com.example.machenike.myfindtreatrue.net.NetClient;
import com.example.machenike.myfindtreatrue.user.User;
import com.example.machenike.myfindtreatrue.user.UserPrefs;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MACHENIKE on 2017/7/13.
 */

public class LoginPresenter {

    private LoginView mLoginView;

    public LoginPresenter(LoginView loginView) {
        mLoginView = loginView;
    }

    //登录的业务
    public void login(User user){
        //显示进度条
        mLoginView.showProgress();
        NetClient.getInstance().getTreatrueApi().login(user).enqueue(new Callback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                //隐藏进度条
                mLoginView.hideProgress();
                if (response.isSuccessful()){
                    LoginResult loginResult = response.body();
                    if (loginResult==null){
                        //吐司
                        mLoginView.showMessage("未知错误");
                        return;
                    }
                    if (loginResult.getErrcode()==1){
                        //真正的登陆成功了
                        //保存头像和tokenid
                        UserPrefs.getInstance().setPhoto(NetClient.BACE_URL+loginResult.getHeadpic());
                        UserPrefs.getInstance().setTokenid(loginResult.getTokenid());
                        //跳转到home界面
                        mLoginView.navigateToHome();

                    }
                    //吐司
                    //mLoginView.showMessage(loginResult.getErrmsg());
                }

            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                //隐藏进度条
                mLoginView.hideProgress();
                //吐司
                mLoginView.showMessage("请求失败"+t.getMessage());
            }
        });
    }
}
