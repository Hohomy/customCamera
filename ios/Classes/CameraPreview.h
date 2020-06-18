//
//  CameraPreview.h
//  Runner
//
//  Created by HMY on 2020/6/17.
//

#import <Foundation/Foundation.h>
#import <Flutter/Flutter.h>

NS_ASSUME_NONNULL_BEGIN

@interface CameraPreview : NSObject

@property (nonatomic, strong) FlutterMethodChannel *channel;

- (instancetype)initWithWithFrame:(CGRect)frame
                  viewIdentifier:(int64_t)viewId
                       arguments:(id _Nullable)args
                 binaryMessenger:(NSObject<FlutterBinaryMessenger>*)messenger;


@end

NS_ASSUME_NONNULL_END
