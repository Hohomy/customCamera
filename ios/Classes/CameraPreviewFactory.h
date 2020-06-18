//
//  CameraPreviewFactory.h
//  Runner
//
//  Created by HMY on 2020/6/17.
//

#import <Foundation/Foundation.h>
#import "CameraPreview.h"

NS_ASSUME_NONNULL_BEGIN

@interface CameraPreviewFactory : NSObject<FlutterPlatformViewFactory>

@property (nonatomic, strong) NSObject<FlutterBinaryMessenger>* messager;

- (instancetype)initWithMessenger:(NSObject<FlutterBinaryMessenger>*)messager;

@end

NS_ASSUME_NONNULL_END
