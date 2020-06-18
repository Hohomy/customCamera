package com.wsy.custom_camera.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import io.flutter.plugin.common.MethodChannel;

public class CameraUtils {

    private Context mContext;
    private Camera mCamera;
    private int mCameraId;
    private Camera.Size mCameraSize;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private byte[] mBuffer;
    private byte[] mBufferRevise;
    private Handler mHandler;

    public CameraUtils(Context context) {
        this.mContext = context;
        mHandler = new Handler();
    }

    public void setSurfaceView(int width, int height, SurfaceHolder surfaceHolder) {
        try {
            mWidth = width;
            mHeight = height;

            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            mCamera = Camera.open(mCameraId);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            setPreviewSize(parameters);
            setPreviewOrientation();
            mCamera.setParameters(parameters);

            int bufferSize =  mCameraSize.width*mCameraSize.height* ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
            mBuffer = new byte[bufferSize];
            mBufferRevise = new byte[bufferSize];
            mCamera.addCallbackBuffer(mBuffer);
            mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    camera.addCallbackBuffer(data);
                }
            });

            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPreviewSize(Camera.Parameters parameters) {
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        int widthPixels = mContext.getResources().getDisplayMetrics().widthPixels;
        int heightPixels = mContext.getResources().getDisplayMetrics().heightPixels;
        mCameraSize = sizeList.get(0);
        for (int i = 1; i < sizeList.size(); i++) {
            Camera.Size nextSize = sizeList.get(i);
            float currentDif = Math.abs(mCameraSize.width *1.0f / mCameraSize.height - heightPixels *1.0f / widthPixels);
            float nextDif = Math.abs(nextSize.width * 1.0f / nextSize.height - heightPixels * 1.0f / widthPixels);
            if (nextDif < currentDif) {
                mCameraSize = nextSize;
            }
            if (nextDif==0 || currentDif==0)
                break;
        }
        parameters.setPreviewSize(mCameraSize.width, mCameraSize.height);
    }

    private void setPreviewOrientation() {
        int degrees = 0;
        mRotation = 0;
//        mRotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
//        switch (mRotation) {
//            case Surface.ROTATION_0: degrees = 0; break;
//            case Surface.ROTATION_90: degrees = 90; break;
//            case Surface.ROTATION_180: degrees = 180; break;
//            case Surface.ROTATION_270: degrees = 270; break;
//        }
        int result;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
        mRotation = result;
    }

    private void reviseData(byte[] data) {
        int index = 0;
        int ySize = mWidth * mHeight;
        int uvHeight = mHeight / 2;

        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            for (int i = 0; i < mWidth; i++) {
                for (int j = mHeight - 1; j >= 0; j--) {
                    mBufferRevise[index++] = data[mWidth * j + i];
                }
            }
            for (int i = 0; i < mWidth; i += 2) {
                for (int j = uvHeight - 1; j >= 0; j--) {
                    mBufferRevise[index++] = data[ySize + mWidth * j + i];
                    mBufferRevise[index++] = data[ySize + mWidth * j + i + 1];
                }
            }
        } else {
            for (int i = 0; i < mWidth; i++) {
                int nPos = mWidth - 1;
                for (int j = 0; j < mHeight; j++) {
                    mBufferRevise[index++] = data[nPos - i];
                    nPos += mWidth;
                }
            }
            for (int i = 0; i < mWidth; i += 2) {
                int nPos = ySize + mWidth - 1;
                for (int j = 0; j < uvHeight; j++) {
                    mBufferRevise[index++] = data[nPos - i - 1];
                    mBufferRevise[index++] = data[nPos - i];
                    nPos += mWidth;
                }
            }
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder) {
        try {
            if (surfaceHolder.getSurface() == null) {
                return;
            }
            mCamera.stopPreview();
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        mCamera.setPreviewCallbackWithBuffer(null);
        mCamera.stopPreview();
        mCamera.release();
        mHandler.removeCallbacksAndMessages(null);
    }

    public Camera.Size getCameraSize() {
        return mCameraSize;
    }

    public void takePhoto(final String path, final MethodChannel.Result result) {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data, Camera camera) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            File file;
                            if (path==null || path.isEmpty()) {
                                File externalStorageDirectory = Environment.getExternalStorageDirectory();
                                long timeMillis = System.currentTimeMillis();
                                File dir = new File(externalStorageDirectory, "网商园");
                                if (!dir.exists()) {
                                    dir.mkdirs();
                                }
                                file = new File(dir,  "wsy" + timeMillis + ".jpg");
                            } else {
                                file = new File(path);
                            }
                            if (!file.exists()) {
                                file.createNewFile();
                            }

                            Bitmap bitmapCache = BitmapFactory.decodeByteArray(data, 0, data.length);
                            Matrix matrix = new Matrix();
                            matrix.postRotate(mRotation);
                            Bitmap bitmap = Bitmap.createBitmap(bitmapCache, 0, 0, bitmapCache.getWidth(), bitmapCache.getHeight(), matrix, false);

                            FileOutputStream fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();

                            bitmap.recycle();
                            bitmapCache.recycle();

//                            FileOutputStream fos = new FileOutputStream(file);
//                            fos.write(data);
//                            fos.flush();
//                            fos.close();

//                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                            Uri contentUri = Uri.fromFile(file);
//                            mediaScanIntent.setData(contentUri);
//                            mContext.sendBroadcast(mediaScanIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                result.success(200);
                                mCamera.startPreview();
                            }
                        });
                    }
                }).start();
            }
        });
    }

}
