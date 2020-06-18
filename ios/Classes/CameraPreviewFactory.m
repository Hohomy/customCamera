//
//  CameraPreviewFactory.m
//  Runner
//
//  Created by HMY on 2020/6/17.
//

#import "CameraPreviewFactory.h"

@implementation CameraPreviewFactory

- (instancetype)initWithMessenger:(NSObject<FlutterBinaryMessenger>*)messager {
    self = [super init];
    if (self) {
        self.messager = messager;
    }
    return self;
}


- (NSObject<FlutterPlatformView> *)createWithFrame:(CGRect)frame viewIdentifier:(int64_t)viewId arguments:(id)args {
    return [[CameraPreview alloc] initWithWithFrame:frame viewIdentifier:viewId arguments:args binaryMessenger:self.messager];
}

@end
