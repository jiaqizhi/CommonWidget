package com.huiyuenet.widgetlib.view.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Size;

import java.util.List;

/**
 * 摄像头工具类
 *
 * @作者 liuzhiwei
 * @时间 2021/3/8 11:13
 */
public class CameraUtils {
    private static CameraUtils mInstance;
    /**
     * 相机参数对象
     */
    private Camera.Parameters mParameters;
    /**
     * 闪光灯自动
     */
    public static final int FLASH_AUTO = 0;
    /**
     * 闪光灯关闭
     */
    public static final int FLASH_OFF = 1;
    /**
     * 闪光灯开启
     */
    public static final int FLASH_ON = 2;

    private Camera mCamera;

    /**
     * 预览分辨率
     */
    private Camera.Size preSize;

    private CameraUtils() {
    }

    public Camera.Size getPreSize() {
        return preSize;
    }

    /**
     * 同步对象
     */
    private static final Object o = new Object();

    public static CameraUtils getInstance() {
        if (mInstance == null) {
            synchronized (o) {//锁住o，保证只实例化一个CameraUtils
                if (mInstance == null) {
                    mInstance = new CameraUtils();
                }
            }
        }
        return mInstance;
    }

    public Camera openCamera(int orientation) {
        // 0 表示开启后置相机
        return openCamera(0, orientation);
    }

    public Camera openCamera(int id, int orientation) {
        if (mCamera == null) {
            mCamera = Camera.open(id);
        }
        setProperty(orientation);
        return mCamera;
    }

    /**
     * 相机属性设置  0==竖屏  1==横屏
     */
    private void setProperty(int orientation) {
        //设置相机预览页面旋转90°，（默认是横屏）
        if (orientation == 0) {
            mCamera.setDisplayOrientation(90);
        } else {
            mCamera.setDisplayOrientation(0);
        }

        mParameters = mCamera.getParameters();
        //设置将保存的图片旋转90°（竖着拍摄的时候）
        mParameters.setRotation(90);
        mParameters.setPreviewSize(1920, 1080);
        mParameters.setPictureSize(1920, 1080);
        mParameters.setPictureFormat(ImageFormat.JPEG);
        //mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(mParameters);
    }

    /**
     * 选装图片的角度
     */
    public void setRotateOrientation(int cameraid, Context context) {
        if (mCamera != null) {
            mParameters = mCamera.getParameters();
            if (cameraid == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mParameters.setRotation(270);
            } else {
                mParameters.setRotation(90);
            }
            mCamera.setParameters(mParameters);
        }
    }

    /**
     * 获取支持的预览分辨率
     */
    public List<Camera.Size> getPreviewSizeList() {
        if (mCamera == null) {
            throw new NullPointerException("Camera can not be null");
        }
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    /**
     * 获取保存图片支持的分辨率
     */
    public List<Camera.Size> getPictureSizeList() {
        if (mCamera == null) {
            throw new NullPointerException("Camera can not be null");
        }
        return mCamera.getParameters().getSupportedPictureSizes();
    }

    /**
     * 设置闪光灯模式
     */
    public void setFlashMode(int mode) {
        mParameters = mCamera.getParameters();
        String flashMode = mParameters.getFlashMode();
        switch (mode) {
            case FLASH_AUTO:
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                break;
            case FLASH_OFF:
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                break;
            case FLASH_ON:
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                break;
            default:
                break;
        }
        mCamera.setParameters(mParameters);
    }

    /**
     * 释放相机资源
     */
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }
    /**
     * 设置保存图片的分辨率
     */
    public void setSaveSize(Camera.Size saveSize) {
        mParameters.setPictureSize(saveSize.width, saveSize.height);
        mCamera.setParameters(mParameters);
    }

    /**
     * 设置最佳预览尺寸
     * @param context
     */
    public void setPreviewSize (Context context) {
        DisplayMetrics dms = context.getResources().getDisplayMetrics();
        int screenWidth = dms.heightPixels;
        int screenHeight = dms.widthPixels;
        float temp = 10f;
        List<Camera.Size> preSizes = getPreviewSizeList();
        preSize = preSizes.get(0);
        Camera.Size tempSize;
        for (int i = 0; i < preSizes.size(); i++) {
            tempSize = preSizes.get(i);
            float abs = Math.abs(Float.parseFloat(tempSize.width+"") / Float.parseFloat(tempSize.height+"") -
                    Float.parseFloat(screenWidth+"") / Float.parseFloat(screenHeight+""));
            if (temp > abs) {
                temp = abs;
                preSize = tempSize;
            }
        }
        mParameters.setPreviewSize(preSize.width, preSize.height);
        mCamera.setParameters(mParameters);
    }

    public void setPreviewSize (Camera.Size size) {
        preSize = size;
        mParameters.setPreviewSize(preSize.width, preSize.height);
        mCamera.setParameters(mParameters);
    }
}
