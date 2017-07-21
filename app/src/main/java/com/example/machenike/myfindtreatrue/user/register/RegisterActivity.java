package com.example.machenike.myfindtreatrue.user.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

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

public class RegisterActivity extends AppCompatActivity implements RegisterView{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_Username)
    EditText mEtUsername;
    @BindView(R.id.et_Password)
    EditText mEtPassword;
    @BindView(R.id.et_Confirm)
    EditText mEtConfirm;
    @BindView(R.id.btn_Register)
    Button mBtnRegister;
    private Unbinder mUnbinder;
    private ActivityUtils mActivityUtils;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }

    //当执行完setContentView(R.layout.activity_register)后执行
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mUnbinder = ButterKnife.bind(this);
        mActivityUtils = new ActivityUtils(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.register);
        }

        //输入框监听
        mEtUsername.addTextChangedListener(mTextWatcher);
        mEtPassword.addTextChangedListener(mTextWatcher);
        mEtConfirm.addTextChangedListener(mTextWatcher);
    }

    private String mUserName;
    private String mPassWord;
    private String mConfirm;
    TextWatcher mTextWatcher = new TextWatcher() {
        //改变之前
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        //正在改变
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        //改变之后
        @Override
        public void afterTextChanged(Editable s) {
            mUserName = mEtUsername.getText().toString();
            mPassWord = mEtPassword.getText().toString();
            mConfirm = mEtConfirm.getText().toString();
            /*
            * 什么时候按钮可点击
            * 1、mUserName、mPassWord、mConfirm都不为空
            * 2、mPassWord和mConfirm内容相同
            * */
            boolean canRegister = !TextUtils.isEmpty(mUserName)&&!TextUtils.isEmpty(mPassWord)&&!TextUtils.isEmpty(mConfirm)&&mPassWord.equals(mConfirm);
            mBtnRegister.setEnabled(canRegister);
        }
    };
    @OnClick(R.id.btn_Register)
    public void onViewClicked() {
        /*
        * 注册成功的条件
        * 1、账号为中文，字母或数字，长度为4~20，一个中文算2个长度
        * 2、长度在6~18之间，只能包含字符、数字和下划线
        * */
        if (RegexUtils.verifyUsername(mUserName)!=RegexUtils.VERIFY_SUCCESS){
            AlertDialogeFragment.getInstance(getString(R.string.username_error),getString(R.string.username_rules))
                    .show(getSupportFragmentManager(),"userName_error");
            return;
        }
        if (RegexUtils.verifyPassword(mPassWord)!=RegexUtils.VERIFY_SUCCESS){
            AlertDialogeFragment.getInstance(getString(R.string.password_error),getString(R.string.password_rules))
                    .show(getSupportFragmentManager(),"passWord_error");
            return;
        }
        new RegisterPresenter(this).register(new User(mUserName,mPassWord));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //toolbar返回箭头点击处理
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
//-------------------------------视图接口重写的方法----------------------------------
    @Override
    public void showProgress() {
        mProgressDialog = ProgressDialog.show(this, "注册", "正在注册中，请稍后~");
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    @Override
    public void navigateToHome() {
        mActivityUtils.startActivity(HomeActivity.class);
        finish();

        // Main页面关闭
        Intent intent = new Intent(MainActivity.MAIN_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
