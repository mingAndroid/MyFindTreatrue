package com.example.machenike.myfindtreatrue.net;

import com.example.machenike.myfindtreatrue.treatrue.Area;
import com.example.machenike.myfindtreatrue.treatrue.Treasure;
import com.example.machenike.myfindtreatrue.treatrue.detail.TreasureDetail;
import com.example.machenike.myfindtreatrue.treatrue.detail.TreasureDetailResult;
import com.example.machenike.myfindtreatrue.treatrue.hide.HideTreasure;
import com.example.machenike.myfindtreatrue.treatrue.hide.HideTreasureResult;
import com.example.machenike.myfindtreatrue.user.User;
import com.example.machenike.myfindtreatrue.user.account.Update;
import com.example.machenike.myfindtreatrue.user.account.UpdateResult;
import com.example.machenike.myfindtreatrue.user.account.UploadResult;
import com.example.machenike.myfindtreatrue.user.login.LoginResult;
import com.example.machenike.myfindtreatrue.user.register.RegisterResult;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by MACHENIKE on 2017/7/13.
 */

public interface TreatrueApi {

    //登录的请求
    @POST("/Handler/UserHandler.ashx?action=login")
    Call<LoginResult> login(@Body User user);


    // 注册的请求
    @POST("/Handler/UserHandler.ashx?action=register")
    Call<RegisterResult> register(@Body User user);

    //区域内宝藏数据的获取
    @POST("/Handler/TreasureHandler.ashx?action=show")
    Call<List<Treasure>> getTreatrue(@Body Area area);

    // 宝藏详情的数据获取
    @POST("/Handler/TreasureHandler.ashx?action=tdetails")
    Call<List<TreasureDetailResult>> getTreasureDetail(@Body TreasureDetail treasureDetail);

    // 埋藏宝藏的请求
    @POST("/Handler/TreasureHandler.ashx?action=hide")
    Call<HideTreasureResult> hideTreasure(@Body HideTreasure hideTreasure);

    /*//头像上传的请求
    @Multipart
    @POST("Handler/UserLoadPicHandler1.ashx")
    Call<UploadResult> upload(@Part("photo")RequestBody requestBody);*/

    // 头像的上传
    @Multipart
    @POST("/Handler/UserLoadPicHandler1.ashx")
    Call<UploadResult> upload(@Part MultipartBody.Part part);

    // 用户头像的更新
    @POST("/Handler/UserHandler.ashx?action=update")
    Call<UpdateResult> update(@Body Update update);
}
