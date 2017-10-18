package com.example.machenike.myfindtreatrue.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.machenike.myfindtreatrue.R;
import com.example.machenike.myfindtreatrue.commons.ActivityUtils;
import com.example.machenike.myfindtreatrue.treatrue.HomeActivity;
import com.example.machenike.myfindtreatrue.user.login.LoginActivity;
import com.example.machenike.myfindtreatrue.user.register.RegisterActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    public static final String MAIN_ACTION = "navigate_to_main";

    @BindView(R.id.btn_Register)
    Button mBtnRegister;
    @BindView(R.id.btn_Login)
    Button mBtnLogin;
    private Unbinder mUnbinder;
    private ActivityUtils mActivityUtils;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        // 接收到广播之后处理
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
        mActivityUtils = new ActivityUtils(this);
        //自动登录
        //判断是否已经登陆
        SharedPreferences preferences = getSharedPreferences("user_info", MODE_PRIVATE);
        if (preferences!=null){
            // 如果登录了
            if (preferences.getInt("key_tokenid",0)==UserPrefs.getInstance().getTokenid()){
                mActivityUtils.startActivity(HomeActivity.class);
                finish();
            }
        }

        // 注册本地广播
        IntentFilter fliter = new IntentFilter(MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,fliter);

    }

    @OnClick({R.id.btn_Register, R.id.btn_Login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_Register:
                mActivityUtils.startActivity(RegisterActivity.class);
                break;
            case R.id.btn_Login:
                mActivityUtils.startActivity(LoginActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
