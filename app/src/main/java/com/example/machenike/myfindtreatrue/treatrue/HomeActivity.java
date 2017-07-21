package com.example.machenike.myfindtreatrue.treatrue;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.machenike.myfindtreatrue.R;
import com.example.machenike.myfindtreatrue.commons.ActivityUtils;
import com.example.machenike.myfindtreatrue.treatrue.list.TreasureListFragment;
import com.example.machenike.myfindtreatrue.treatrue.map.MapFragment;
import com.example.machenike.myfindtreatrue.user.MainActivity;
import com.example.machenike.myfindtreatrue.user.UserPrefs;
import com.example.machenike.myfindtreatrue.user.account.AccountActivity;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation)
    NavigationView mNavigation;
    @BindView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    private ActivityUtils mActivityUtils;
    private ImageView mIvIcon;
    private MapFragment mMapFragment;
    private TreasureListFragment mTreasureListFragment;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mActivityUtils = new ActivityUtils(this);
        mFragmentManager = getSupportFragmentManager();
        mMapFragment = (MapFragment)mFragmentManager.findFragmentById(R.id.mapFragment);
        //设置Toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar()!=null){
            //不显示默认的标题，显示自己在布局中添加的TextView
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //DrawerLayout的侧滑监听
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        actionBarDrawerToggle.syncState();//让actionBarDrawerToggle跟DrawerLayout的开关状态同步
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);

        //侧滑菜单item的选择监听
        mNavigation.setNavigationItemSelectedListener(this);

        //找到侧滑页面的头像控件，设置点击事件点击头像跳转个人信息页面
        mIvIcon = (ImageView) mNavigation.getHeaderView(0).findViewById(R.id.iv_usericon);
        mIvIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到用户详情界面
                mActivityUtils.startActivity(AccountActivity.class);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //加载更新头像
        // 更新头像信息
        String photo = UserPrefs.getInstance().getPhoto();
        if (photo != null) {
            // 加载头像：采用Picasso实现
            Picasso
                    .with(this)
                    .load(photo)
                    .into(mIvIcon);
        }
    }

    //侧滑菜单item的选择监听
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_hide:
                //埋藏宝藏
                //mActivityUtils.showToast("埋藏宝藏");
                mMapFragment.changeUIMode(2);   // 切换到埋藏宝藏的视图
                break;
            case R.id.menu_my_list:
                //我的列表
                mActivityUtils.showToast("我的列表");
                break;
            case R.id.menu_help:
                //帮助
                mActivityUtils.showToast("帮助");
                break;
            case R.id.menu_logout:
                // 清空用户信息数据
                UserPrefs.getInstance().clearUser();
                // 返回到Main页面
                mActivityUtils.startActivity(MainActivity.class);
                finish();
                break;
        }
        mDrawerLayout.closeDrawer(Gravity.START);
        return true;
    }

    // 准备工作：完成选项菜单的图标的切换等
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // item的图标的变化处理
        MenuItem item = menu.findItem(R.id.action_toggle);
        // 根据显示的视图不一样，设置不一样的图标
        if (mTreasureListFragment != null && mTreasureListFragment.isAdded()) {
            item.setIcon(R.drawable.ic_map);
        } else {
            item.setIcon(R.drawable.ic_view_list);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    // 创建：选项菜单的布局填充
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 菜单的填充
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    // 某一个选项菜单被选择的时候(点击)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle:

                // 切换视图：地图的视图和列表的视图进行切换
                showListFragment();

                // 更新选项菜单的视图：触发onPrepareOptionsMenu
                invalidateOptionsMenu();

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // 显示或隐藏列表视图
    private void showListFragment() {

        if (mTreasureListFragment != null && mTreasureListFragment.isAdded()) {

            // 将ListFragment弹出回退栈
            mFragmentManager.popBackStack();
            // 移除ListFragment
            mFragmentManager.beginTransaction().remove(mTreasureListFragment).commit();
            return;
        }

        mTreasureListFragment = new TreasureListFragment();

        // 在布局中展示(FrameLayout作为占位)
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mTreasureListFragment)
                // 添加回退栈
                .addToBackStack(null)
                .commit();
    }

    // 处理返回键
    @Override
    public void onBackPressed() {

        // 侧滑打开的，就先关闭
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // 如果MapFragment里面的视图是普通视图的话，可以退出
            if (mMapFragment.clickBackPressed()) {
                super.onBackPressed();
            }
        }
    }

}
