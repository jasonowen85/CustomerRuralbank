package com.grgbanking.ruralbank.common.util;

/**
 * Created by Think on 2016/9/2.
 */

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UPlayer implements IVoiceManager, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    private final String TAG = UPlayer.class.getName();
    private String path;
    private Handler mHandler;

    private MediaPlayer mPlayer;

    public UPlayer(String path, final Handler handler) {
        this.path = path;
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mHandler = handler;
    }

    @Override
    public boolean start() {
        try {
            //设置要播放的文件
            mPlayer.setDataSource(path);
            mPlayer.prepareAsync();
            //播放
            // mPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, "prepare() failed");
        }

        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Message msg = mHandler.obtainMessage();
        msg.what = 0;
        mHandler.sendMessage(msg);
        mPlayer.start();
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        Message msg = mHandler.obtainMessage();
        msg.what = 1;
        mHandler.sendMessage(msg);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Message msg = mHandler.obtainMessage();
        msg.what = -1;
        mHandler.sendMessage(msg);
        return false;
    }


    @Override
    public boolean stop() {
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        return false;
    }

}