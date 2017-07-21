package com.example.machenike.myfindtreatrue.user;

import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.example.machenike.myfindtreatrue.commons.ActivityUtils;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by MACHENIKE on 2017/7/9.
 */

public class MainMp4Fragment extends Fragment implements TextureView.SurfaceTextureListener {

    private TextureView mTextureView;
    private ActivityUtils mActivityUtils;
    private MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //用于展示视频的控件
        mTextureView = new TextureView(getContext());
        mActivityUtils = new ActivityUtils(this);
        return mTextureView;
    }

    //当onCreateView方法执行完毕后执行
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*
        * 何时可以播放视频？
        * 当视频展示控件准备好的时候执行
        * 1.视频控件何时准备好？
        * 设置监听
        * */
        mTextureView.setSurfaceTextureListener(this);
    }
//----------------------------------监听重写的方法-------------------------------------------
  //控件准备好了
    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, int width, int height) {
        /*
        * 视频展示控件准备好了，何时可以播放视频
        * 1、视频资源被找到
        * 2、视频播放播放控件MediaPlayer准备好的时候
        * 3、视频要展示到哪个控件上
        * */
        try {
            AssetFileDescriptor openFd = getContext().getAssets().openFd("welcome.mp4");
            //得到MediaPlayer需要的文件类型
            FileDescriptor fileDescriptor = openFd.getFileDescriptor();
            //初始化视频播放控件
            mMediaPlayer = new MediaPlayer();
            //设置视频资源
            mMediaPlayer.setDataSource(fileDescriptor,openFd.getStartOffset(),openFd.getLength());
            //设置完资源看何时可以播放 1.异步准备 2.设置监听
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                //准备好了，可以播放了
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Surface mSurface = new Surface(surface);
                    mMediaPlayer.setSurface(mSurface);//设置视频展示控件
                    mMediaPlayer.setLooping(true);//设置循环播放
                    mMediaPlayer.start();//开始播放
                }
            });


        } catch (IOException e) {
            mActivityUtils.showToast("视频播放失败");
        }
    }
    //当尺寸发生变化的时候
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }
    //销毁的时候
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }
    //更新
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMediaPlayer!=null){
            mMediaPlayer.release();//释放资源
            mMediaPlayer=null;
        }

    }
}
