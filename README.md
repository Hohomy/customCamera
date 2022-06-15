# custom_camera

A new Flutter plugin called iOS or Android camera.

## Getting Started

This project is a starting point for a Flutter
[plug-in package](https://flutter.dev/developing-packages/),
a specialized package that includes platform-specific implementation code for
Android and/or iOS.

For help getting started with Flutter, view our 
[online documentation](https://flutter.dev/docs), which offers tutorials, 
samples, guidance on mobile development, and a full API reference.

## Use example:
> 1.pubspec.yaml中导入插件

```
custom_camera:
git: https://github.com/Hohomy/customCamera
```
> 2.代码中创建相机预览视图CameraPreview
```
 Container(
                child: Stack(
                  children: <Widget>[
                    _c.isPermissionGranted ? CameraPreview(controller: _c.cameraController,) : Container(color: Colors.black87,),
                    PositionedDirectional(
                      bottom: 0,
                      start: 0,
                      end: 0,
                      child: Container(
                        height: 140,
                        color: Color(0xff383838),
                        child: Row(
                          children: <Widget>[
                            Expanded(
                              flex: 1,
                              child: Container(
                                padding: EdgeInsets.all(30),
                                child: InkWell(
                                  onTap: () {
                                    Navigator.of(context).pop();
                                  },
                                  child: Container(
                                    child: Column(
                                      mainAxisAlignment: MainAxisAlignment.center,
                                      children: <Widget>[
                                        Image.asset(
                                          "images/tusou_cancel.png",
                                        ),
                                        SizedBox(
                                          height: 3,
                                        ),
                                        Text(
                                          "取消",
                                          style: TextStyle(
                                              color: Colors.white,
                                              fontSize: 14,
                                              fontWeight: FontWeight.w500),
                                        )
                                      ],
                                    ),
                                  ),
                                ),
                              ),
                            ),
                            Expanded(
                              flex: 1,
                              child: Container(
                                child: GestureDetector(
                                  behavior: HitTestBehavior.opaque,
                                  onTap:(){
                                    _c.takePhoto(context);
                                  },
                                  child: Image.asset("images/cemara.png", width: 67, height: 67,),
                                ),
                              ),
                            ),
                            Expanded(
                              flex: 1,
                              child: Container(
                                padding: EdgeInsets.all(30),
                                child: InkWell(
                          onTap:(){
                                _c.selectImageFromGallery(context);
                          } ,
                                  child: Container(
                                    child: Column(
                                      mainAxisAlignment: MainAxisAlignment.center,
                                      children: <Widget>[
                                        Image.asset(
                                          "images/tusou_album.png",
                                        ),
                                        SizedBox(
                                          height: 3,
                                        ),
                                        Text(
                                          "相册",
                                          style: TextStyle(
                                              color: Colors.white,
                                              fontSize: 14,
                                              fontWeight: FontWeight.w500),
                                        )
                                      ],
                                    ),
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    )
                  ],
                ),
              ),
 ```
 可以自定义相机视图

> 3.拍照
```
  // 初始化相机
  CameraController cameraController; 
  
  _c.cameraController = CameraController();  
  
  // iOS判断相机权限
  void judgeCameraPermission() async {
    PermissionStatus permission = await PermissionHandler().checkPermissionStatus(PermissionGroup.camera);
    print("CameraPermission============${permission}");

    if (permission == PermissionStatus.denied) {
      showCupertinoDialog(
          context: state.context,
          builder: (context) {
        return CupertinoAlertDialog(
            title: Text("未获得授权使用相机",style: TextStyle(fontSize: 18, color: Color(0xff333333), fontWeight: FontWeight.w600), ),
            content: Text('请在iOS"设置"-"隐私"-"相机"中打开',style: TextStyle(fontSize: 14, color: Color(0xff333333)),),
            actions: <Widget>[
            CupertinoDialogAction(
            onPressed: (){
          Navigator.of(context).pop();
        },
              child: Text("取消"),
            ),
              CupertinoDialogAction(
                onPressed: () async {
                  bool isOpened = await PermissionHandler().openAppSettings();

                },
                child: Text("去设置"),
              )
            ],
        );
          }
      );
    }
  }
```
```
//点击拍照事件
  Future takePhoto(BuildContext context) async {
    try {
      final dateTime = DateTime.now();
      final path = join((await getApplicationDocumentsDirectory()).path,
          '${dateTime.millisecondsSinceEpoch}.jpg');     // 设置存储地址
      var result = await cameraController.takePhoto(path);
      state.setState(() {
        imagePath = path;
        isLoading = true;    
      });
      // loadData(File(imagePath));    
    } catch (err, stack) {
      print(err);
    }
  }
```
