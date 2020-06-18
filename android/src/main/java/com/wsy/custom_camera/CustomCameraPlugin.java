package com.wsy.custom_camera;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.wsy.custom_camera.camera.CameraView;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

/** CustomCameraPlugin */
public class CustomCameraPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  @Override
  public void onAttachedToEngine(@NonNull final FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "custom_camera");
    channel.setMethodCallHandler(this);

    PlatformViewFactory platformViewFactory = new PlatformViewFactory(StandardMessageCodec.INSTANCE){
      @Override
      public PlatformView create(Context context, int i, Object o) {
        Log.d("Tag", "cameraaa ========= " + o);
        MethodChannel methodChannel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "plugins/custom_camera_" + i);
        CameraView cameraView = new CameraView(context);
        cameraView.setMethodChannel(methodChannel);
        return cameraView;
      }
    };
    flutterPluginBinding.getPlatformViewRegistry().registerViewFactory("plugins/custom_camera", platformViewFactory);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  public static void registerWith(final Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "custom_camera");
    channel.setMethodCallHandler(new CustomCameraPlugin());

    PlatformViewFactory platformViewFactory = new PlatformViewFactory(StandardMessageCodec.INSTANCE){
      @Override
      public PlatformView create(Context context, int i, Object o) {
        Log.d("Tag", "cameraaa ========= " + o);
        MethodChannel methodChannel = new MethodChannel(registrar.messenger(), "plugins/custom_camera_" + i);
        CameraView cameraView = new CameraView(context);
        cameraView.setMethodChannel(methodChannel);
        return cameraView;
      }
    };
    registrar.platformViewRegistry().registerViewFactory("plugins/custom_camera", platformViewFactory);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
