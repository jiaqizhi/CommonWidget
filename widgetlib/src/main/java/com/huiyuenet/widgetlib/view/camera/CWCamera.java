package com.huiyuenet.widgetlib.view.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.huiyuenet.widgetlib.R;
import com.huiyuenet.widgetlib.logs.LogUtils;
import com.huiyuenet.widgetlib.logs.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * 摄像头组件（预览、拍照、获取帧）
 *
 * @作者 liuzhiwei
 * @时间 2021/3/8 10:40
 */
public class CWCamera extends SurfaceView implements Camera.PreviewCallback, SurfaceHolder.Callback, Runnable, Camera.AutoFocusCallback {
    private Context context;
    private Camera camera;
    private int cameraid;
    private SurfaceHolder holder;
    private int screenHeight;
    private int screenWidth;
    private PreviewFrameListener previewFrameListener;
    private CameraPrepareListener cameraPrepareListener;
    private CameraPZListener cameraPZListener;
    private boolean captrueing = false;
    private boolean isSupportAutoFocus = false;
    private byte[] pic;

    public CWCamera(Context context) {
        super(context);
    }

    public CWCamera(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = this.context.obtainStyledAttributes(attrs, R.styleable.VideoCameraView);
        cameraid = a.getInt(R.styleable.VideoCameraView_camera_id, Camera.CameraInfo.CAMERA_FACING_FRONT);
        holder = getHolder();
        isSupportAutoFocus = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);

        DisplayMetrics dms = context.getResources().getDisplayMetrics();
        screenWidth = dms.heightPixels;
        screenHeight = dms.widthPixels;
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public int getCameraid() {
        return cameraid;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCameraPrepareListener(CameraPrepareListener cameraPrepareListener) {
        this.cameraPrepareListener = cameraPrepareListener;
    }

    /**
     * 设置帧回调
     *
     * @param previewFrameListener
     */
    public void setPreviewFrameListener(PreviewFrameListener previewFrameListener) {
        this.previewFrameListener = previewFrameListener;
    }

    /**
     * 设置拍照回调
     *
     * @param listener
     */
    public void setCameraPZListener(CameraPZListener listener) {
        cameraPZListener = listener;
    }

    public void captrue() {
        if (captrueing)
            return;
        captrueing = true;
        camera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                captrueing = false;
                camera.stopPreview();
                Bitmap bmp = ImageUtils.bitmap565(pic, camera.getParameters());
                if (cameraid == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    bmp = ImageUtils.rotaingImageView(90, bmp);
                } else {
                    bmp = ImageUtils.rotaingImageView(270, bmp);
                }

                //照片旋转
//                String brand = android.os.Build.BRAND;
//
//                if (StringUtils.equalsIgnoreCase("Redmi", brand)) {
//
//                    if (cameraid == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                        bmp = ImageUtils.rotaingImageView(90, bmp);
//                    } else {
//                        bmp = ImageUtils.rotaingImageView(-90, bmp);
//                    }
//                } else {
//                    if (cameraid == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                        bmp = ImageUtils.rotaingImageView(0, bmp);
//                    } else {
//                        bmp = ImageUtils.rotaingImageView(180, bmp);
//                    }
//                }
//                if (StringUtils.equalsIgnoreCase("HUAWEI", brand) || StringUtils.equalsIgnoreCase("vivo", brand)) {
//                    if (cameraid == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                        bmp = ImageUtils.rotaingImageView(0, bmp);
//                    } else {
//                        bmp = ImageUtils.rotaingImageView(180, bmp);
//                    }
//                } else {
//                    if (cameraid == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                        bmp = ImageUtils.rotaingImageView(90, bmp);
//                    } else {
//                        bmp = ImageUtils.rotaingImageView(-90, bmp);
//                    }
//                }

                if (cameraPZListener != null) {
                    cameraPZListener.getPZImg(bmp);
                }

                camera.startPreview();
            }
        });
    }

    //todo 第一步，创建camera对象，初始化执行一次，此时摄像头被持有
    public void openCamera() {
        try {
            camera = CameraUtils.getInstance().openCamera(cameraid);
        } catch (Exception e) {
            e.printStackTrace();
            if (camera != null) {
                camera.release();
                camera = null;
            }
        }

    }

    //todo 第二步，初始化摄像头参数  竖屏90    横屏0
    public void initCamera() {
        try {
            camera.setPreviewDisplay(holder);
            //设置最佳预览
            CameraUtils.getInstance().setPreviewSize(context);
            //设置生成照片的分辨率
            List<Camera.Size> sizes = CameraUtils.getInstance().getPictureSizeList();
            for (Camera.Size size : sizes) {
                if (size.width == 1280 && size.height == 960) {
                    CameraUtils.getInstance().setSaveSize(size);
                    break;
                }
            }
            //设置横竖屏参数
            CameraUtils.getInstance().setRotateOrientation(cameraid, context);
            if (cameraPrepareListener != null) {
                cameraPrepareListener.prepare();
            }


            //startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            releaseCamera();
        }

    }

    /**
     * 设置指定分辨率，可自行获取系统支持的分辨率来进行设置
     * @param size
     */
    public void initCamera(Camera.Size size) {
        try {
            camera.setPreviewDisplay(holder);
            //设置最佳预览
            CameraUtils.getInstance().setPreviewSize(size);
            //设置生成照片的分辨率
            List<Camera.Size> sizes = CameraUtils.getInstance().getPictureSizeList();
            for (Camera.Size s : sizes) {
                if (s.width == 1280 && s.height == 960) {
                    CameraUtils.getInstance().setSaveSize(s);
                    break;
                }
            }
            //设置横竖屏参数
            CameraUtils.getInstance().setRotateOrientation(cameraid, context);
            if (cameraPrepareListener != null) {
                cameraPrepareListener.prepare();
            }
            //startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            releaseCamera();
        }

    }


    public Camera.Size getPreviewSize () {
        return CameraUtils.getInstance().getPreSize();
    }

    /**
     * 启动预览
     */
    public void startPreview() {
        if (camera != null) {
            camera.startPreview();
            camera.setPreviewCallback(this);

            if (isSupportAutoFocus) {
                camera.autoFocus(this);
                postDelayed(this, 2000);
            }
        }
    }

    /**
     * 停止预览
     */
    public void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            if (isSupportAutoFocus) {
                camera.cancelAutoFocus();
            }
        }
    }

    /**
     * 销毁摄像头
     */
    public void releaseCamera() {
        if (camera != null) {
            CameraUtils.getInstance().releaseCamera();
            camera = null;
        }
    }

    /**
     * 切换摄像头
     *
     * @param camerid
     */
    public void changeCamera(int camerid) {
        this.cameraid = camerid;
    }

    @Override
    public void onAutoFocus(boolean b, Camera camera) {
        postDelayed(this, 500);
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        pic = bytes;
        if (previewFrameListener != null)
            previewFrameListener.getImgData(bytes, camera);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        if (camera == null) {
            openCamera();
            initCamera();
        } else {
            //startPreview();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//        releaseCamera();
//        openCamera();
//        initCamera();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        releaseCamera();
    }

    @Override
    public void run() {
        if (camera != null) {
            try {
                camera.autoFocus(this);
            } catch (Exception e) {
                e.printStackTrace();
                //camera = null;
            }
        }
    }

    public interface PreviewFrameListener {
        void getImgData(byte[] data, Camera camera);
    }

    public interface CameraPZListener {
        void getPZImg(Bitmap bmp);
    }

    public interface CameraPrepareListener {
        void prepare ();
    }
}
