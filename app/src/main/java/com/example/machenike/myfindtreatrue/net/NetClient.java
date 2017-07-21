package com.example.machenike.myfindtreatrue.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by MACHENIKE on 2017/7/13.
 * 网络客户端的单例类
 */

public class NetClient {

    public static final String BACE_URL = "http://admin.syfeicuiedu.com";
    private static NetClient mNetClient;
    private final Retrofit mRetrofit;
    private TreatrueApi mTreatrueApi;

    private NetClient(){
        //创建日志拦截器
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //创建OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
        // 设置GSON的非严格模式setLenient()
        Gson gson = new GsonBuilder().setLenient().create();
        //
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BACE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


    }
    
    public static synchronized NetClient getInstance(){
        if (mNetClient==null){
            mNetClient = new NetClient();
        }
        return mNetClient;
    }
    public TreatrueApi getTreatrueApi(){
        if (mTreatrueApi==null){
            mTreatrueApi = mRetrofit.create(TreatrueApi.class);
        }
        return mTreatrueApi;
    }
    
}
