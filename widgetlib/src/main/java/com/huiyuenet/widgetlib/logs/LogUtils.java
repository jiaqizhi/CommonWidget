package com.huiyuenet.widgetlib.logs;

import com.xuexiang.xutil.XUtil;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.common.logger.Logger;
import com.xuexiang.xutil.data.DateUtils;
import com.xuexiang.xutil.file.FileIOUtils;
import com.xuexiang.xutil.file.FileUtils;

import java.io.File;

/**
 * 日志打印类
 * @作者 liuzhiwei
 * @时间 2021/3/8 10:19
 */
public class LogUtils {
    private static String logDir = "";
    private static String TAG = "CW";
    private static boolean saveLog = false;
    static {
        Logger.setTag(TAG);
    }

    /**
     * 设置日志的存放路径
     * @param logDir
     */
    public static void setLogDir(String logDir) {
        LogUtils.logDir = logDir;
    }

    /**
     * 设置日志tag
     * @param TAG
     */
    public static void setTAG(String TAG) {
        LogUtils.TAG = TAG;
    }

    /**
     * 设置是否保存日志
     * @param saveLog
     */
    public static void setSaveLog(boolean saveLog) {
        LogUtils.saveLog = saveLog;
    }

    public static void d (String log) {
        Logger.d(log);
        saveLog(log);
    }

    public static void e (String log) {
        Logger.e(log);
        saveLog(log);
    }

    /**
     * 保存日志
     * @param log
     * @return
     */
    private static boolean saveLog (String log) {
        if (!saveLog) {
            return false;
        }
        if (StringUtils.isEmpty(logDir)) {
            e("日志存放地址为空");
            return false;
        }
        //判断文件是否存在，不存在则创建，创建失败返回false
        if (!FileUtils.createOrExistsDir(logDir)) {
            e("文件夹创建失败");
            return false;
        }
        String fileName = DateUtils.date2String(DateUtils.getNowDate(), DateUtils.yyyyMMdd.get())+".txt";
        String logPath = logDir+ File.separator+fileName;

        //判断文件是否存在并创建
        if (!FileUtils.createOrExistsFile(logPath)) {
            e("日志文件创建失败");
            return false;
        }

        //判断日志是否写成功
        if (!FileIOUtils.writeFileFromString(logPath, log, true)) {
            e("日志写入失败");
            return false;
        }

        return true;
    }
}
