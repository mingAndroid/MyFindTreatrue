package com.example.machenike.myfindtreatrue.user.account;

import com.example.machenike.myfindtreatrue.net.NetClient;
import com.example.machenike.myfindtreatrue.user.UserPrefs;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MACHENIKE on 2017/7/21.
 */

public class AccountPresenter {

    private AccountView mAccountView;

    public AccountPresenter(AccountView accountView) {
        mAccountView = accountView;
    }

    public void upLoadPhoto(File file){

        //显示进度
        mAccountView.showProgress();
//        RequestBody requestBody = RequestBody.create(null, file);
        // 构建上传的文件的部分
        MultipartBody.Part part = MultipartBody.Part.createFormData("file","photo.png", RequestBody.create(null,file));
        NetClient.getInstance().getTreatrueApi().upload(part).enqueue(new Callback<UploadResult>() {
            @Override
            public void onResponse(Call<UploadResult> call, Response<UploadResult> response) {
                mAccountView.hideProgress();
                if (response.isSuccessful()){
                    UploadResult uploadResult = response.body();
                    if (uploadResult==null){
                        //显示信息
                        mAccountView.showMessage("uploadResult为空！");
                        return;
                    }

                    if (uploadResult.getCount()!=1){
                        //显示信息
                        mAccountView.showMessage("aaaaa"+response.message());
                        return;
                    }
                    //拿到头像地址数据
                    final String photoUrl = uploadResult.getUrl();

                    //上传成功，更新数据，数据的Url需要截取
                    String substring = photoUrl.substring(photoUrl.lastIndexOf("/") + 1, photoUrl.length());
                    Update update = new Update(UserPrefs.getInstance().getTokenid(),substring);
                    NetClient.getInstance().getTreatrueApi().update(update).enqueue(new Callback<UpdateResult>() {
                        @Override
                        public void onResponse(Call<UpdateResult> call, Response<UpdateResult> response) {
                            //隐藏进度
                            mAccountView.hideProgress();
                            if (response.isSuccessful()){
                                UpdateResult updateResult = response.body();
                                if (updateResult==null){
                                    //显示信息
                                    mAccountView.showMessage("updateResult为空！");
                                    return;
                                }
                                if (updateResult.getCode()==1){
                                    //彻底成功
                                    //保存到用户仓库
                                    UserPrefs.getInstance().setPhoto(NetClient.BACE_URL+photoUrl);
                                    //展示图片
                                    mAccountView.updatePhoto(NetClient.BACE_URL+photoUrl);
                                }
                                //显示信息
                                mAccountView.showMessage("bbbbb"+response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<UpdateResult> call, Throwable t) {
                            //隐藏进度
                            mAccountView.hideProgress();
                            //显示信息
                            mAccountView.showMessage("更新失败"+t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<UploadResult> call, Throwable t) {
                //隐藏进度
                mAccountView.hideProgress();
                //显示信息
                mAccountView.showMessage("上传失败"+t.getMessage());
            }
        });
    }
}
