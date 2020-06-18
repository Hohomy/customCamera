import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

typedef void CameraPreviewWidgetCreatedCallback(CameraController controller);

class CameraController {

   MethodChannel _channel;
  void setChannel(int id) {
    _channel = MethodChannel('plugins/custom_camera_$id');
  }

  Future<void> start() async {
    return _channel.invokeMethod('start');
  }

  Future<void> stop() async {
    return _channel.invokeMethod('stop');
  }

  Future<void> takePhoto() async {
    return _channel.invokeMethod('takePhoto');
  }

}

class CameraPreview extends StatefulWidget {
  const CameraPreview({
    Key key,
    this.onCameraPreviewWidgetCreated,
    this.previewSizeWidth,
    this.previewSizeHeight,
    this.controller
  }
  ):super(key:key);

  final CameraPreviewWidgetCreatedCallback onCameraPreviewWidgetCreated;
  final double previewSizeWidth;
  final double previewSizeHeight;
  final CameraController controller;

  @override
  State<StatefulWidget> createState() {
    return _CameraPreviewState();
  }
}

class _CameraPreviewState extends State<CameraPreview> {
  @override
  Widget build(BuildContext context) {
    if(defaultTargetPlatform == TargetPlatform.iOS){
      return UiKitView(
        viewType: "plugins/custom_camera",
        onPlatformViewCreated:_onPlatformViewCreated,
        creationParams: <String,dynamic>{
          "previewSizeWidth":widget.previewSizeWidth,
          "previewSizeHeight":widget.previewSizeHeight,

        },
        creationParamsCodec: new StandardMessageCodec(),

      );
    }
    return Text('activity_indicator插件尚不支持$defaultTargetPlatform ');
  }

  void _onPlatformViewCreated(int id){
    if(widget.onCameraPreviewWidgetCreated == null){
      return;
    }
  widget.controller.setChannel(id);
  }
}

