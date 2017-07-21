package com.example.machenike.myfindtreatrue.user.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.machenike.myfindtreatrue.R;
import com.example.machenike.myfindtreatrue.commons.ActivityUtils;
import com.example.machenike.myfindtreatrue.commons.RegexUtils;
import com.example.machenike.myfindtreatrue.custom.AlertDialogeFragment;
import com.example.machenike.myfindtreatrue.treatrue.HomeActivity;
import com.example.machenike.myfindtreatrue.user.MainActivity;
import com.example.machenike.myfindtreatrue.user.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity implements LoginView{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_Username)
    EditText mEtUsername;
    @BindView(R.id.et_Password)
    EditText mEtPassword;
    @BindView(R.id.tv_forgetPassword)
    TextView mTvForgetPassword;
    @BindView(R.id.btn_Login)
    Button mBtnLogin;
    private Unbinder mUnbinder;
    private ActivityUtils mActivityUtils;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mUnbinder = ButterKnife.bind(this);
        mActivityUtils = new ActivityUtils(this);
        //设置标题栏
        setSupportActionBar(mToolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(R.string.login);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //给输入框设置监听
        mEtUsername.addTextChangedListener(mTextWatcher);
        mEtPassword.addTextChangedListener(mTextWatcher);
    }

    private String mPassWord;
    private String mUserName;
    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mPassWord = mEtPassword.getText().toString();
            mUserName = mEtUsername.getText().toString();

            boolean canLogin = !TextUtils.isEmpty(mPassWord)&&!TextUtils.isEmpty(mUserName);
            mBtnLogin.setEnabled(canLogin);
        }
    };
    //给返回箭头添加点击事件

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_Login)
    public void onViewClicked() {

        if (RegexUtils.verifyUsername(mUserName)!=RegexUtils.VERIFY_SUCCESS){
            AlertDialogeFragment.getInstance(getString(R.string.username_error),getString(R.string.username_rules))
                    .show(getSupportFragmentManager(),"username_error");
            return;
        }

        if (RegexUtils.verifyPassword(mPassWord)!=RegexUtils.VERIFY_SUCCESS){
            AlertDialogeFragment.getInstance(getString(R.string.password_error),getString(R.string.password_rules))
                    .show(getSupportFragmentManager(),"password_error");
            return;
        }
        new LoginPresenter(this).login(new User(mUserName,mPassWord));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("=========","==============");
        mUnbinder.unbind();
    }
//-----------------------登陆过程中涉及的视图操作---------------------------
    @Override
    public void showProgress() {
        mProgressDialog = ProgressDialog.show(this, "登录", "正在登录中...");
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String message) {
        mActivityUtils.showToast(message);
    }

    @Override
    public void navigateToHome() {

        mActivityUtils.startActivity(HomeActivity.class);
        finish();

        // MainActivity是不是也需要关闭：发个本地广播的形式关闭
        Intent intent = new Intent(MainActivity.MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
}
