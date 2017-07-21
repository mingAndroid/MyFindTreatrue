package com.example.machenike.myfindtreatrue.treatrue.map;

import com.example.machenike.myfindtreatrue.net.NetClient;
import com.example.machenike.myfindtreatrue.treatrue.Area;
import com.example.machenike.myfindtreatrue.treatrue.Treasure;
import com.example.machenike.myfindtreatrue.treatrue.TreasureRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by MACHENIKE on 2017/7/17.
 */

//获取宝藏数据的业务类
public class MapPresenter {
    private Area mArea;
    private MapMvpView mMapMvpView;

    public MapPresenter(MapMvpView mapMvpView) {
        mMapMvpView = mapMvpView;
    }

    //获取宝藏数据
    public void getTreatrue(Area area){

        // 当前区域已经缓存过，就不再去请求
        if (TreasureRepo.getInstance().isCached(area)){
            return;
        }

        mArea = area;
        //网络请求的调用
        NetClient.getInstance().getTreatrueApi().getTreatrue(area).enqueue(new Callback<List<Treasure>>() {
            @Override
            public void onResponse(Call<List<Treasure>> call, Response<List<Treasure>> response) {

                if (response.isSuccessful()){
                    List<Treasure> treasureList = response.body();
                    if (treasureList==null){
                        //弹个吐司说明一下
                        mMapMvpView.showMessage("未知错误");
                        return;
                    }
                    //做一个数据缓存，缓存请求的数据和区域
                    TreasureRepo.getInstance().addTreasure(treasureList);
                    TreasureRepo.getInstance().cache(mArea);

                    //拿到数据在MapFragment上进行展示

                    mMapMvpView.setTreatrueData(treasureList);
                }
            }

            @Override
            public void onFailure(Call<List<Treasure>> call, Throwable t) {
                //弹个吐司
                mMapMvpView.showMessage("请求失败"+t.getMessage());
            }
        });
    }
}
