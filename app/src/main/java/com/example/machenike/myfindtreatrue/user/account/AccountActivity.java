package com.example.machenike.myfindtreatrue.user.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.machenike.myfindtreatrue.R;
import com.example.machenike.myfindtreatrue.commons.ActivityUtils;
import com.example.machenike.myfindtreatrue.custom.IconSelectWindow;
import com.example.machenike.myfindtreatrue.user.UserPrefs;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountActivity extends AppCompatActivity implements AccountView{

    @BindView(R.id.account_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_usericon)
    CircularImageView mIvUsericon;
    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.linearLayout)
    RelativeLayout mLinearLayout;
    private IconSelectWindow mIconSelectWindow;
    private ActivityUtils mActivityUtils;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        mActivityUtils = new ActivityUtils(this);

        // toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(R.string.account_msg);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        String photo = UserPrefs.getInstance().getPhoto();
        if (photo!=null){
            // 加载头像
            Picasso.with(this)
                    .load(photo)
                    .into(mIvUsericon);
        }
    }

    //点击头像弹出popuwindow
    @OnClick(R.id.iv_usericon)
    public void showPopuwindow(){
        if (mIconSelectWindow==null){
            mIconSelectWindow = new IconSelectWindow(this,mListener);
            return;
        }
        if (mIconSelectWindow.isShowing()){
            mIconSelectWindow.dismiss();
            return;
        }
        mIconSelectWindow.show();
    }

    //跳转的监听
    private IconSelectWindow.Listener mListener = new IconSelectWindow.Listener() {
        @Override
        public void toGallery() {  //到相册
            // 清除缓存
            CropHelper.clearCachedCropFile(mCropHandler.getCropParams().uri);

            Intent intent = CropHelper.buildCropFromGalleryIntent(mCropHandler.getCropParams());
            startActivityForResult(intent, CropHelper.REQUEST_CROP);
        }

        @Override
        public void toCamera() {  //到相机
            // 清除之前剪切的图片的缓存
            CropHelper.clearCachedCropFile(mCropHandler.getCropParams().uri);

            // 跳转
            Intent intent = CropHelper.buildCaptureIntent(mCropHandler.getCropParams().uri);
            startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
        }
    };

    // 返回箭头的处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //负责图片的处理的处理器
    private CropHandler mCropHandler = new CropHandler(){
        // 图片剪切之后：参数Uri代表剪切后的图片
        @Override
        public void onPhotoCropped(Uri uri) {
            // 拿到剪切之后的图片
            File file = new File(uri.getPath());
            //进行网络请求将图片上传
            // TODO: 2017/7/20
            Log.e("URI",uri.getPath());
            new AccountPresenter(AccountActivity.this).upLoadPhoto(file);
        }
        // 取消
        @Override
        public void onCropCancel() {
            mActivityUtils.showToast("剪切取消");
        }
        // 剪切失败
        @Override
        public void onCropFailed(String message) {
            mActivityUtils.showToast(message);
        }
        //剪切的参数
        @Override
        public CropParams getCropParams() {
            // 默认的剪切设置
            CropParams cropParams = new CropParams();
            return cropParams;
        }

        @Override
        public Activity getContext() {
            return AccountActivity.this;
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropHelper.handleResult(mCropHandler,requestCode,resultCode,data);
    }

    @Override
    protected void onDestroy() {
        if (mCropHandler.getCropParams() != null)
            CropHelper.clearCachedCropFile(mCropHandler.getCropParams().uri);
        super.onDestroy();
    }
    //-------------------------实现自视图接口的方法---------------------------------
    @Override
    public void showProgress() {
        mProgressDialog = ProgressDialog.show(this, "头像上传", "正在上传中~");
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
    public void updatePhoto(String photoUrl) {
        if (photoUrl!=null){
            Picasso.with(this)
                    .load(photoUrl)
                    .error(R.mipmap.user_icon)// 加载错误显示的视图
                    .placeholder(R.mipmap.user_icon)// 占位视图
                    .into(mIvUsericon);
        }
    }
}
