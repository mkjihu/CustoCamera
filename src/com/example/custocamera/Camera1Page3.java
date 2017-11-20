package com.example.custocamera;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

@SuppressLint("NewApi")
public class Camera1Page3 extends AppCompatActivity {
 private SurfaceView surfaceView;
 private SurfaceHolder holder;

 private CameraManager manager;
 private CaptureRequest.Builder builder;
 private CameraDevice cameraDevice;

 private HandlerThread handlerThread;
 private Handler handler;

 private ImageReader imageReader;

 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_camera1_page3);
  manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
  surfaceView = (SurfaceView) findViewById(R.id.surfaceView);

  // 取得Holder
  holder = surfaceView.getHolder();

  // 設定預覽大小
  holder.setFixedSize(1280, 960);

  // 設定事件
  holder.addCallback(surfaceCallback);
 }

 private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
   if (cameraDevice != null) {
    cameraDevice.close();
   }
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
   handlerThread = new HandlerThread("HT");
   handlerThread.start();
   handler = new Handler(handlerThread.getLooper());

   // 創建影像物件
   imageReader = ImageReader.newInstance(1280, 960, ImageFormat.RGB_565, 3);
   try {

    // 取得相機鏡頭清單,可用於判斷該相機是否具備雙鏡頭或是單鏡頭
    // manager.getCameraIdList()

	   
	   
	   
	
	   
    // 開啟相機
    // 0是後面鏡頭 = CaptureRequest.LENS_FACING_BACK
    // 1是前置鏡頭 = CaptureRequest.LENS_FACING_FRONT
	   
	   
    manager.openCamera("0", DeviceStateCallback, handler);
    
   } catch (CameraAccessException e) {
    Log.e("CameraAccessException", e.getMessage());
   }
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

  }
 };

 // 相機裝置狀態
 private CameraDevice.StateCallback DeviceStateCallback = new CameraDevice.StateCallback() {

  @Override
  public void onOpened(CameraDevice camera) {
   try {
    cameraDevice = camera;

    // 設定預覽模式
    builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

    // 將影像設定置SurfaceView
    builder.addTarget(holder.getSurface());

    // 建立影像傳輸
    cameraDevice.createCaptureSession(Arrays.asList(holder.getSurface(), imageReader.getSurface()),
      CameraCaptureCallback, handler);
   } catch (CameraAccessException e) {
    Log.e("CameraAccessException", e.getMessage());
   }
  }

  @Override
  public void onDisconnected(CameraDevice camera) {

  }

  @Override
  public void onError(CameraDevice camera, int error) {

  }
 };

 // 相機擷取中及完成的事件
 private CameraCaptureSession.CaptureCallback CameracaptureCallback = new CaptureCallback() {

  @Override
  public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
    TotalCaptureResult result) {

  }

  @Override
  public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request,
    CaptureResult partialResult) {

  }

 };

 // 相機擷取狀態
 private CameraCaptureSession.StateCallback CameraCaptureCallback = new StateCallback() {

  @Override
  public void onConfigured(CameraCaptureSession session) {
   try {
	   
	   
	   session.setRepeatingRequest(builder.build(), CameracaptureCallback, handler);
   } catch (CameraAccessException e) {
    Log.e("CameraAccessException", e.getMessage());
   }
  }

  @Override
  public void onConfigureFailed(CameraCaptureSession session) {

  }
 };

}
