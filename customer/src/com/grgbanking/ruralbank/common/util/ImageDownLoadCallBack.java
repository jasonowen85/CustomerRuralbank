package com.grgbanking.ruralbank.common.util;

import java.io.File;

/**
 * Created by Think on 2016/8/31.
 */
public interface ImageDownLoadCallBack {

    void onDownLoadSuccess(File file);

    void onDownLoadFailed();
}