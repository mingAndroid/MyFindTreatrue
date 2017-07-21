package com.example.machenike.myfindtreatrue.treatrue.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.machenike.myfindtreatrue.R;
import com.example.machenike.myfindtreatrue.commons.ActivityUtils;
import com.example.machenike.myfindtreatrue.custom.TreasureView;
import com.example.machenike.myfindtreatrue.treatrue.Area;
import com.example.machenike.myfindtreatrue.treatrue.Treasure;
import com.example.machenike.myfindtreatrue.treatrue.TreasureRepo;
import com.example.machenike.myfindtreatrue.treatrue.detail.TreasureDetailActivity;
import com.example.machenike.myfindtreatrue.treatrue.hide.HideTreasureActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by MACHENIKE on 2017/7/13.
 */

public class MapFragment extends Fragment implements MapMvpView {
    private static final int LOCATION_REQUEST_CODE = 100;
    @BindView(R.id.iv_located)
    ImageView mIvLocated;
    @BindView(R.id.btn_HideHere)
    Button mBtnHideHere;
    @BindView(R.id.centerLayout)
    RelativeLayout mCenterLayout;
    @BindView(R.id.iv_scaleUp)
    ImageView mIvScaleUp;
    @BindView(R.id.iv_scaleDown)
    ImageView mIvScaleDown;
    @BindView(R.id.tv_located)
    TextView mTvLocated;
    @BindView(R.id.tv_satellite)
    TextView mTvSatellite;
    @BindView(R.id.tv_compass)
    TextView mTvCompass;
    @BindView(R.id.ll_locationBar)
    LinearLayout mLlLocationBar;
    @BindView(R.id.tv_currentLocation)
    TextView mTvCurrentLocation;
    @BindView(R.id.iv_toTreasureInfo)
    ImageView mIvToTreasureInfo;
    @BindView(R.id.et_treasureTitle)
    EditText mEtTreasureTitle;
    @BindView(R.id.cardView)
    CardView mCardView;
    @BindView(R.id.layout_bottom)
    FrameLayout mLayoutBottom;
    @BindView(R.id.map_frame)
    FrameLayout mMapFrame;
    Unbinder unbinder;
    @BindView(R.id.treasureView)
    TreasureView mTreasureView;
    @BindView(R.id.hide_treasure)
    RelativeLayout mHideTreasure;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private static LatLng mCurrentLocation;
    private boolean isFirst = true;
    private LatLng mCurrentStatus;
    private ActivityUtils mActivityUtils;
    private MapPresenter mMapPresenter;
    private Marker mCurrentMarker;
    private GeoCoder mGeoCoder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);

        //检测权限有没有成功
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //没有成功，需要向用户动态申请
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        mActivityUtils = new ActivityUtils(this);
        // 清空缓存的数据
        TreasureRepo.getInstance().clear();

        mMapPresenter = new MapPresenter(this);

        //初始化百度地图
        initMapView();

        //初始化地图定位相关
        initLocation();

        // 初始化地理编码相关
        initGeoCoder();

    }

    private void initGeoCoder() {
        // 第一步，创建地理编码检索实例；
        mGeoCoder = GeoCoder.newInstance();

        // 第二步，设置地理编码检索监听者；
        mGeoCoder.setOnGetGeoCodeResultListener(mGeoCoderResultListener);

        // 地图状态变化之后发起
    }

    private String mGeoCoderAddr;
    // 地理编码的监听者
    private OnGetGeoCoderResultListener mGeoCoderResultListener = new OnGetGeoCoderResultListener() {

        // 获取地理编码结果:geoCodeResult拿到的结果
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }

        // 获取反向地理编码结果：reverseGeoCodeResult拿到的结果
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            // 判断结果是否正确拿到
            if (reverseGeoCodeResult==null||reverseGeoCodeResult.error!= SearchResult.ERRORNO.NO_ERROR){
                // 没有拿到检索的结果
                mGeoCoderAddr = "未知的地址";
                mTvCurrentLocation.setText(mGeoCoderAddr);
                return;
            }
            // 拿到地址信息
            mGeoCoderAddr = reverseGeoCodeResult.getAddress();

            // 将地址信息设置给TextView
            mTvCurrentLocation.setText(mGeoCoderAddr);
        }
    };

    private void initLocation() {

        //激活定位图层
        mBaiduMap.setMyLocationEnabled(true);

        //第一步，初始化LocationClient类
        mLocationClient = new LocationClient(getContext().getApplicationContext());

        //第二步，配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开GPS
        option.setCoorType("bd09ll"); //坐标系类型
        option.setIsNeedAddress(true);//是否需要返回地址信息
        //将参数设置给LocationClient
        mLocationClient.setLocOption(option);

        //第三步，实现BDLocationListener接口，注册监听
        mLocationClient.registerLocationListener(mBDLocationListener);

        //第四步，开始定位
        mLocationClient.start();

    }

    private static String mCurrentAddress;
    private BDLocationListener mBDLocationListener = new BDLocationListener() {
        //当获取到定位数据时触发
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            if (bdLocation == null) {
                //没有拿到数据,再次请求
                mLocationClient.requestLocation();
                return;
            }

            //拿到定位的经纬度
            double latitude = bdLocation.getLatitude();//纬度
            double longitude = bdLocation.getLongitude();//经度

            //定位的位置和地址
            mCurrentLocation = new LatLng(latitude, longitude);
            mCurrentAddress = bdLocation.getAddrStr();

           // Log.i("TAG", "当前的位置是：" + mCurrentAddress + "经纬度：" + latitude + "," + longitude);

            //地图上设置定位数据
            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(100f) //定位的精度，其实就是定位的那个光圈的大小
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
            mBaiduMap.setMyLocationData(locationData);

            //判断是否是第一次将地图自动移动到定位的位置
            if (isFirst) {
                //自动移动到定位处
                moveToLocation();
                isFirst = false;
            }
        }
    };

    private void initMapView() {

        //地图的状态
        MapStatus mapStatus = new MapStatus.Builder()
                .overlook(0) //俯仰角度
                .rotate(0) //旋转
                .zoom(15)  //地图缩放级别，默认12，范围3-21
                .build();

        //创建百度地图的操作类，设置地图的信息
        BaiduMapOptions baiduMapOptions = new BaiduMapOptions()
                .mapStatus(mapStatus)
                .compassEnabled(true)  //是否显示指南针，默认显示
                .zoomControlsEnabled(false) //不显示缩放控件
                .zoomGesturesEnabled(true)  //允许手势缩放
                .scaleControlEnabled(false) //不显示比例尺
                ;
        //创建地图控件
        MapView mapView = new MapView(getContext(), baiduMapOptions);
        //在帧布局中添加地图控件，设置index为0，将其放置在布局的最底部
        mMapFrame.addView(mapView, 0);
        //拿到百度地图的对象
        mBaiduMap = mapView.getMap();

        //给地图地图状态监听，实时获取地图中心位置
        mBaiduMap.setOnMapStatusChangeListener(mOnMapStatusChangeListener);

        //设置覆盖物的点击监听
        mBaiduMap.setOnMarkerClickListener(mOnMarkerClickListener);
    }

    //地图覆盖物的点击监听
    private BaiduMap.OnMarkerClickListener mOnMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            // 当前点击的Marker先管理判断
            if (mCurrentMarker != null) {
                if (mCurrentMarker != marker) {
                    mCurrentMarker.setVisible(true);// 点击了其他的，把之前的显示出来
                }
            }
            mCurrentMarker = marker;

            //点击展示infoWindow,当前覆盖物不可见
            mCurrentMarker.setVisible(false);

            //1.创建infoWindow
            InfoWindow infoWindow = new InfoWindow(dot_expand, marker.getPosition(), 0, new InfoWindow.OnInfoWindowClickListener() {
                // InfoWindow的点击监听
                @Override
                public void onInfoWindowClick() {
//                    if (mCurrentMarker != null) {
//                        mCurrentMarker.setVisible(true);
//                    }
//                    //隐藏infoWindow
//                    mBaiduMap.hideInfoWindow();

                    //切换回普通视图
                    changeUIMode(UI_MODE_NORMAL);
                }
            });

            // 2. 地图上展示nfoWindow
            mBaiduMap.showInfoWindow(infoWindow);

            //显示TreasureView(即宝物信息卡片)
           // mLayoutBottom.setVisibility(View.VISIBLE);
            int id = marker.getExtraInfo().getInt("id");
            Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
            mTreasureView.bindTreasure(treasure);
            //视图切换为宝藏选中的视图
            changeUIMode(UI_MODE_SELECT);
            return false;
        }
    };
    //视图切换的方法，根据各种空间的显示和隐藏来控制试图的显示
    private static final int UI_MODE_NORMAL=0;//普通视图
    private static final int UI_MODE_SELECT=1;//宝藏选中的视图
    private static final int UI_MODE_HIDE=2;//埋藏宝藏的视图

    private static int mUIMode = UI_MODE_NORMAL;//当前视图

    public void changeUIMode(int uiMode) {

        if (mUIMode==uiMode){
            return;
        }
        mUIMode=uiMode;
        switch (uiMode){
            case UI_MODE_NORMAL://切换为普通视图
                if (mCurrentMarker!=null){
                    mCurrentMarker.setVisible(true);
                }
                mBaiduMap.hideInfoWindow();
                mLayoutBottom.setVisibility(View.GONE);
                mCenterLayout.setVisibility(View.GONE);
                break;
            case UI_MODE_SELECT://切换为宝藏选中的视图
                mLayoutBottom.setVisibility(View.VISIBLE);
                mTreasureView.setVisibility(View.VISIBLE);
                mCenterLayout.setVisibility(View.GONE);
                mHideTreasure.setVisibility(View.GONE);//切记此处要手动绑定视图
                break;
            case UI_MODE_HIDE://切换为埋藏宝藏的视图
                if (mCurrentMarker!=null){
                    mCurrentMarker.setVisible(true);
                }
                mBaiduMap.hideInfoWindow();
                mCenterLayout.setVisibility(View.VISIBLE);
                mLayoutBottom.setVisibility(View.GONE);
                mBtnHideHere.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLayoutBottom.setVisibility(View.VISIBLE);
                        mTreasureView.setVisibility(View.GONE);
                        mHideTreasure.setVisibility(View.VISIBLE);
                    }
                });
                break;
        }


    }

    //给地图地图状态监听，实时获取地图中心位置
    private BaiduMap.OnMapStatusChangeListener mOnMapStatusChangeListener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {

            //拿到当前移动后的地图状态所在的位置
            LatLng target = mapStatus.target;

            //判断地图状态是否发生变化（该变化指的是地图中心点的移动）
            if (target != mCurrentStatus) {
                // 根据当前的地图的状态来获取当前的区域内的宝藏数据
                updateMapArea();

                // 在埋藏宝藏的情况下
                if (mUIMode==UI_MODE_HIDE){

                    // 设置反地理编码的参数：位置(当前的经纬度)
                    ReverseGeoCodeOption option = new ReverseGeoCodeOption();
                    option.location(target);

                    // 发起反地理编码：经纬度-->地址
                    mGeoCoder.reverseGeoCode(option);
                }

                // 当前地图的位置
                mCurrentStatus = target;
            }
        }
    };

    //区域的确定和宝藏数据的获取
    private void updateMapArea() {

        // 拿到当前的地图状态
        MapStatus mapStatus = mBaiduMap.getMapStatus();

        // 从中拿到当前地图的经纬度
        double longitude = mapStatus.target.longitude;
        double latitude = mapStatus.target.latitude;

        // 根据当前的经纬度来确定区域
        Area area = new Area();

        // 根据当前经纬度向上和向下取整得到的区域
        area.setMaxLat(Math.ceil(latitude));
        area.setMaxLng(Math.ceil(longitude));
        area.setMinLng(Math.floor(longitude));
        area.setMinLat(Math.floor(latitude));

        //根据当前的位置进行网络请求，获取宝藏
//        Log.i("TAG","我执行了");
        mMapPresenter.getTreatrue(area);
//        Log.i("TAG","我也执行了");

    }

    //---------------------------------给地图上的一些控件设置点击事件--------------------
    //移动到定位的地方
    @OnClick(R.id.tv_located)
    public void moveToLocation() {

        MapStatus mapStatus = new MapStatus.Builder()
                .target(mCurrentLocation)
                .rotate(0)
                .zoom(19)
                .overlook(0)
                .build();

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);

        //更新地图的状态
        mBaiduMap.animateMapStatus(mapStatusUpdate);
    }

    //卫星视图和普通视图的切换
    @OnClick(R.id.tv_satellite)
    public void switchMapType() {
        //先拿到当前视图的类型
        int mapType = mBaiduMap.getMapType();
        //切换地图类型
        mapType = mapType == BaiduMap.MAP_TYPE_NORMAL ? BaiduMap.MAP_TYPE_SATELLITE : BaiduMap.MAP_TYPE_NORMAL;
        //文字的变化
        String s = mapType == BaiduMap.MAP_TYPE_NORMAL ? "卫星" : "普通";
        mBaiduMap.setMapType(mapType);
        mTvSatellite.setText(s);
    }

    //指南针
    @OnClick(R.id.tv_compass)
    public void switchCompass() {
        //当前地图指南针有没有在显示
        boolean enabled = mBaiduMap.getUiSettings().isCompassEnabled();
        mBaiduMap.getUiSettings().setCompassEnabled(!enabled);
    }

    //地图的缩放
    @OnClick({R.id.iv_scaleDown, R.id.iv_scaleUp})
    public void switchMapScale(View view) {
        switch (view.getId()) {
            case R.id.iv_scaleDown:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                break;
            case R.id.iv_scaleUp:
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                break;
        }
    }

    // 宝藏信息卡片的点击事件
    @OnClick(R.id.treasureView)
    public void clickTreasureView(){
        // 跳转到详情页，展示宝藏信息，将宝藏数据传递过去
        int id = mCurrentMarker.getExtraInfo().getInt("id");
        Treasure treasure = TreasureRepo.getInstance().getTreasure(id);
        TreasureDetailActivity.open(getContext(),treasure);
    }

    @OnClick(R.id.hide_treasure)
    public void hideTreasure(){
        // 拿到录入的标题
        String title = mEtTreasureTitle.getText().toString();
        // 判断
        if (TextUtils.isEmpty(title)){
            mActivityUtils.showToast("请输入宝藏标题");
            return;
        }
        // 输入了标题：跳转到埋藏宝藏详细页面
        LatLng latLng = mBaiduMap.getMapStatus().target;
        HideTreasureActivity.open(getContext(),title,mGeoCoderAddr,latLng,0);
    }

    //处理权限的回掉
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                //判断用户是否授权成功
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //定位
                    mLocationClient.requestLocation();
                } else {
                    Toast.makeText(getContext(), "权限不足", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    //---------------------------视图接口的实现方法---------------------------
    @Override
    public void showMessage(String message) {
        mActivityUtils.showToast(message);
    }

    @Override
    public void setTreatrueData(List<Treasure> treasureList) {
        // 再次进行网络请求的时候，之前的覆盖物都清除一下
        mBaiduMap.clear();

        //Log.i("TAG","数据："+treasureList.size());

        // 拿到每一个宝藏数据、将宝藏信息以覆盖物的形式添加到地图上
        for (Treasure treasure :
                treasureList) {
            //创建一个方法添加地图覆盖物
            LatLng latLng = new LatLng(treasure.getLatitude(), treasure.getLongitude());
            int id = treasure.getId();
            addMarKer(latLng, id);
        }
    }

    //覆盖物的图标
    private BitmapDescriptor dot = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_dot);
    private BitmapDescriptor dot_expand = BitmapDescriptorFactory.fromResource(R.mipmap.treasure_expanded);

    //添加地图覆盖物
    private void addMarKer(LatLng latLng, int treasureId) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)//覆盖物的位置
                .icon(dot)//覆盖物的图标
                .anchor(0.5f, 0.5f)//锚点，居中显示
                ;
        //设置覆盖物的额外信息，将宝藏ID信息存储到覆盖物中
        Bundle bundle = new Bundle();
        bundle.putInt("id", treasureId);
        markerOptions.extraInfo(bundle);
        //添加覆盖物
        mBaiduMap.addOverlay(markerOptions);
    }

    //返回定位到的位置，给其他人使用
    public static LatLng getMyLocation() {
        return mCurrentLocation;
    }
    //返回定位到的地址，给其他人使用
    public static String getLocationAddr() {
        return mCurrentAddress;
    }

    // 对外提供一个方法，什么时候可以退出
    public boolean clickBackPressed(){

        if (mUIMode!=UI_MODE_NORMAL){
            changeUIMode(UI_MODE_NORMAL);
            return false;
        }
        return true;
    }
}
