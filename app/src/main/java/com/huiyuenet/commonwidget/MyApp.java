package com.huiyuenet.commonwidget;

import android.app.Application;
import android.util.Log;

import com.xuexiang.xaop.XAOP;
import com.xuexiang.xaop.logger.XLogger;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        XAOP.init(this); //初始化插件
        XAOP.debug(true); //日志打印切片开启
        XAOP.setPriority(Log.DEBUG); //设置日志打印的等级,默认为0
    }
}
