#import "CustomCameraPlugin.h"
#import "CameraPreviewFactory.h"

@implementation CustomCameraPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"custom_camera"
            binaryMessenger:[registrar messenger]];
  CustomCameraPlugin* instance = [[CustomCameraPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
    
  CameraPreviewFactory *factory = [[CameraPreviewFactory alloc] initWithMessenger:[registrar messenger]];
  [registrar registerViewFactory:factory withId:@"plugins/custom_camera"];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else {
    result(FlutterMethodNotImplemented);
  }
}

@end
