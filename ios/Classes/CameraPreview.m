//
//  CameraPreview.m
//  Runner
//
//  Created by HMY on 2020/6/17.
//

#import "CameraPreview.h"
#import <AVFoundation/AVFoundation.h>

@interface CameraPreview()<FlutterPlatformView>

@property (nonatomic, strong) AVCaptureDevice *device;

//AVCaptureDeviceInput 代表输入设备，他使用AVCaptureDevice 来初始化
@property (nonatomic, strong) AVCaptureDeviceInput *input;

//输出图片
@property (nonatomic ,strong) AVCaptureStillImageOutput *imageOutput;

//session：由他把输入输出结合在一起，并开始启动捕获设备（摄像头）
@property (nonatomic, strong) AVCaptureSession *session;

//图像预览层，实时显示捕获的图像
@property (nonatomic ,strong) AVCaptureVideoPreviewLayer *previewLayer;

@property (nonatomic, strong) UIView *bgView;

@end

@implementation CameraPreview

- (instancetype)initWithWithFrame:(CGRect)frame
 viewIdentifier:(int64_t)viewId
      arguments:(id _Nullable)args
                  binaryMessenger:(NSObject<FlutterBinaryMessenger>*)messenger {
    if (self) {
        NSString *channelName = [NSString stringWithFormat:@"plugins/custom_camera_%lld", viewId];
        _channel = [FlutterMethodChannel methodChannelWithName:channelName binaryMessenger:messenger];

        __weak __typeof__(self) weakSelf = self;
        [_channel setMethodCallHandler:^(FlutterMethodCall* call, FlutterResult result) {
            if (weakSelf) {
              [weakSelf onMethodCall:call result:result];
            }
        }];
    }
    return self;
}

- (void)onMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result{
    if ([[call method] isEqualToString:@"start"]) {
        
    } else if ([[call method] isEqualToString:@"stop"]) {
        
    } else if ([[call method] isEqualToString:@"takePhoto"]) {
        NSDictionary *dic =  (NSDictionary *)call.arguments;
        if (dic[@"path"]) {
            [self takePhoto:dic[@"path"] result:result];
        }
    } else {
        result(FlutterMethodNotImplemented);
    }
}

- (UIView *)bgView {
    if (!_bgView) {
        _bgView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, UIScreen.mainScreen.bounds.size.width, UIScreen.mainScreen.bounds.size.height)];
            
        _device = [self cameraWithPosition:AVCaptureDevicePositionBack];
        _input = [[AVCaptureDeviceInput alloc] initWithDevice:_device error:nil];
        _imageOutput = [[AVCaptureStillImageOutput alloc] init];
        _session = [[AVCaptureSession alloc] init];
 
        // 输入输出设备结合
        if ([_session canAddInput:_input]) {
            [_session addInput:_input];
        }
        if ([_session canAddOutput:_imageOutput]) {
            [_session addOutput:_imageOutput];
        }
        // 预览层的生成
        _previewLayer = [[AVCaptureVideoPreviewLayer alloc] initWithSession:_session];

        CALayer * layer = _bgView.layer;
        _previewLayer.frame = layer.bounds;
        _previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill;
        [layer insertSublayer:_previewLayer atIndex:0];
        // 设备取景开始
        [_session startRunning];
        if ([_device lockForConfiguration:nil]) {
        // 自动闪光灯
        if ([_device isFlashModeSupported:AVCaptureFlashModeAuto]) {
            [_device setFlashMode:AVCaptureFlashModeAuto];
        }
        //自动白平衡,但是好像一直都进不去
        if ([_device isWhiteBalanceModeSupported:AVCaptureWhiteBalanceModeAutoWhiteBalance]) {
            [_device setWhiteBalanceMode:AVCaptureWhiteBalanceModeAutoWhiteBalance];
        }
            [_device unlockForConfiguration];
        }
        
    }
    return _bgView;
}

- (UIView *)view {
    return self.bgView;
}

- (AVCaptureDevice *)cameraWithPosition:(AVCaptureDevicePosition)position{
    NSArray *devices = [AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo];
    for ( AVCaptureDevice *device in devices )
        if ( device.position == position ){
            return device;
        }
    return nil;
}

- (void)takePhoto:(NSString *)path result:(FlutterResult)result {
    AVCaptureConnection *conntion = [self.imageOutput connectionWithMediaType:AVMediaTypeVideo];
    if (!conntion) {
        NSLog(@"拍照失败!");
        return;
    }
    [self.imageOutput captureStillImageAsynchronouslyFromConnection:conntion completionHandler:^(CMSampleBufferRef imageDataSampleBuffer, NSError *error) {
        if (imageDataSampleBuffer == nil) {
            return ;
        }
        NSData *imageData = [AVCaptureStillImageOutput jpegStillImageNSDataRepresentation:imageDataSampleBuffer];
        UIImage *image = [UIImage imageWithData:imageData];
//        [self.session stopRunning];
        bool success = [UIImageJPEGRepresentation(image, 1.0) writeToFile:path atomically:YES];
         if (!success) {
           result([FlutterError errorWithCode:@"IOError" message:@"Unable to write file" details:nil]);
           return;
         }
         result(nil);
    }];
}

@end
