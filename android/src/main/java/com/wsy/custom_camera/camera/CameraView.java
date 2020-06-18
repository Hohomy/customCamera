package com.wsy.custom_camera.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, PlatformView, MethodChannel.MethodCallHandler {

    private CameraUtils mCameraUtils;
    private MethodChannel mMethodChannel;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        getHolder().addCallback(this);
        mCameraUtils = new CameraUtils(getContext());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCameraUtils.setSurfaceView(getWidth(), getHeight(), holder);
        resize();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCameraUtils.surfaceChanged(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCameraUtils.destroy();
    }

    private void resize() {
        Camera.Size cameraSize = mCameraUtils.getCameraSize();
        float ratio = 1;
        int widthOrigin = getWidth();
        if (widthOrigin > cameraSize.height) {
            ratio = widthOrigin * 1.0f / cameraSize.height;
        }
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = (int) (cameraSize.height * ratio);
        layoutParams.height = (int) (cameraSize.width * ratio);
        setLayoutParams(layoutParams);
    }

    public void takePhoto(String path, MethodChannel.Result result) {
        mCameraUtils.takePhoto(path, result);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void dispose() {

    }

    public void setMethodChannel(MethodChannel methodChannel) {
        mMethodChannel = methodChannel;
        mMethodChannel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
        String method = methodCall.method;
        HashMap<String, String> arguments = (HashMap<String, String>) methodCall.arguments;
        if (method.equals("takePhoto")) {
            takePhoto(arguments.get("path"), result);
        }
    }
}
