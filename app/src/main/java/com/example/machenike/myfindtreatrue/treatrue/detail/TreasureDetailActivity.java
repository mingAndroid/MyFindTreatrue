package com.example.machenike.myfindtreatrue.treatrue.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.utils.OpenClientUtil;
import com.example.machenike.myfindtreatrue.R;
import com.example.machenike.myfindtreatrue.commons.ActivityUtils;
import com.example.machenike.myfindtreatrue.custom.TreasureView;
import com.example.machenike.myfindtreatrue.treatrue.Treasure;
import com.example.machenike.myfindtreatrue.treatrue.map.MapFragment;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TreasureDetailActivity extends AppCompatActivity implements TreasureDetailView {

    private static final String KEY_TREASURE = "key_treasure";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.frameLayout)
    FrameLayout mFrameLayout;
    @BindView(R.id.detail_treasure)
    TreasureView mDetailTreasure;
    @BindView(R.id.tv_detail_description)
    TextView mTvDetailDescription;
    private ActivityUtils mActivityUtils;
    private TreasureDetailPresenter mTreasureDetailPresenter;
    private Treasure mTreasure;

    /**
     * 对外提供一个方法，跳转到本页面
     * 规范一下传递的数据：需要什么参数就必须要传入
     */
    public static void open(Context context, Treasure treasure) {
        Intent intent = new Intent(context, TreasureDetailActivity.class);
        intent.putExtra(KEY_TREASURE, treasure);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treasure_detail);
        ButterKnife.bind(this);

        mActivityUtils = new ActivityUtils(this);

        mTreasureDetailPresenter = new TreasureDetailPresenter(this);

        // 拿到传递过来的数据
        mTreasure = (Treasure) getIntent().getSerializableExtra(KEY_TREASURE);

        // toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            // 设置标题和返回箭头
            getSupportActionBar().setTitle(mTreasure.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // 地图的展示
        initMapView();


        // 宝藏信息卡片的展示
        mDetailTreasure.bindTreasure(mTreasure);

        // 网络获取宝藏的详情数据
        TreasureDetail treasureDetail = new TreasureDetail(mTreasure.getId());
        mTreasureDetailPresenter.getTreasureDetail(treasureDetail);
    }

    private void initMapView() {
        // 宝藏的位置
        LatLng latlng = new LatLng(mTreasure.getLatitude(), mTreasure.getLongitude());

        MapStatus mapStatus = new MapStatus.Builder()
                .target(latlng)
                .zoom(18)
                .rotate(0)
                .overlook(-20)
                .build();

        // 地图只是用于展示，没有任何操作
        BaiduMapOptions options = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(false)
                .scrollGesturesEnabled(false)
                .scaleControlEnabled(false)
                .zoomControlsEnabled(false)
                .zoomGesturesEnabled(false)
                .rotateGesturesEnabled(false);

        // 创建的地图控件
        MapView mapView = new MapView(this, options);

        // 放到布局中
        mFrameLayout.addView(mapView);

        // 拿到地图的操作类
        BaiduMap map = mapView.getMap();

        // 添加一个覆盖物
        BitmapDescriptor dot_expand = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);
        MarkerOptions option = new MarkerOptions()
                .position(latlng)
                .icon(dot_expand)
                .anchor(0.5f, 0.5f);
        map.addOverlay(option);
    }

    // 处理返回箭头的事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // toolbar的图标的点击事件
    @OnClick(R.id.iv_navigation)
    public void showPopupMenu(View view) {
        // 展示出来一个PopupMenu
        /**
         * 1. 创建一个弹出式菜单
         * 2. 菜单项的填充(布局)
         * 3. 设置菜单项的点击监听
         * 4. 显示
         */
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_navigation);
        popupMenu.setOnMenuItemClickListener(mMenuItemClickListener);
        popupMenu.show();
    }

    // 菜单项的点击监听
    private PopupMenu.OnMenuItemClickListener mMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {

        // 点击菜单项会触发：具体根据item的id来判断
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            // 不管进行骑行还是步行，都需要起点和终点：坐标和地址
            // 起点：我们定位的位置和地址
            LatLng start = MapFragment.getMyLocation();
            String startAddr = MapFragment.getLocationAddr();

            // 终点：宝藏的位置和地址
            LatLng end = new LatLng(mTreasure.getLatitude(),mTreasure.getLongitude());
            String endAddr = mTreasure.getLocation();

            // 步行和骑行的点击事件
            switch (item.getItemId()){
                case R.id.walking_navi:
                    // 开始步行导航
                    startWalkingNavi(start,startAddr,end,endAddr);
                    break;
                case R.id.biking_navi:
                    // 开始骑行导航
                    startBikingNavi(start,startAddr,end,endAddr);
                    break;
            }

            return false;
        }
    };

    private void startBikingNavi(LatLng startPoint, String startAddr, LatLng endPoint, String endAddr) {

        // 起点和终点的设置
        NaviParaOption option = new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);
        // 打开骑行导航
        boolean bikeNavi = BaiduMapNavigation.openBaiduMapBikeNavi(option, this);

        // 未开启成功
        if (!bikeNavi){
            startWebNavi(startPoint, startAddr, endPoint, endAddr);
        }
    }
    // 打开网页进行导航
    private void startWebNavi(LatLng startPoint, String startAddr, LatLng endPoint, String endAddr) {
        // 起点和终点的设置
        NaviParaOption option = new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);
        BaiduMapNavigation.openWebBaiduMapNavi(option,this);
    }

    private void startWalkingNavi(LatLng startPoint, String startAddr, LatLng endPoint, String endAddr) {

        // 起点和终点的设置
        NaviParaOption option = new NaviParaOption()
                .startName(startAddr)
                .startPoint(startPoint)
                .endName(endAddr)
                .endPoint(endPoint);

        // 开启步行导航
        boolean walkNavi = BaiduMapNavigation.openBaiduMapWalkNavi(option, this);

        // 未开启成功
        if (!walkNavi){
            // 可以到网页导航
//            startWebNavi(startPoint, startAddr, endPoint, endAddr);
            showDialog();
        }
    }

    private void showDialog() {

        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("您未安装百度地图客户端或版本过低，要不要安装？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 打开最新版的客户端下载页面
                        OpenClientUtil.getLatestBaiduMapApp(TreasureDetailActivity.this);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    // -----------------------------详情的视图实现----------------------------
    @Override
    public void showMessage(String msg) {
        mActivityUtils.showToast(msg);
    }

    @Override
    public void setDetailData(List<TreasureDetailResult> list) {
        // 请求数据的展示
        if (list.size() >= 1) {
            TreasureDetailResult result = list.get(0);
            mTvDetailDescription.setText(result.description);
            return;
        }
        mTvDetailDescription.setText("当前宝藏没有详情信息");
    }
}
