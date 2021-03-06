import 'dart:async';

import 'package:flutter/services.dart';

class CustomCamera {
  static const MethodChannel _channel =
      const MethodChannel('custom_camera');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
