package com.example.custocamera;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.androidquery.AQuery;
import com.example.custocamera.obj.CameraSurfaceView;
import com.example.custocamera.obj.CameraUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Camera1Page1 extends AppCompatActivity implements OnClickListener {

    private CameraSurfaceView mCameraSurfaceView;
    private Button mBtnTake;
    private Button mBtnSwitch;

    private int mOrientation;
    public AQuery aq;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera1_page1);
		aq = new AQuery(this);
        mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.view_camera);
        mOrientation = CameraUtils.calculateCameraPreviewOrientation(Camera1Page1.this);
        mBtnTake = (Button) findViewById(R.id.btn_take);
        mBtnTake.setOnClickListener(this);
        mBtnSwitch = (Button) findViewById(R.id.btn_switch);
        mBtnSwitch.setOnClickListener(this);
	}

	@Override
    protected void onResume() {
        super.onResume();
        CameraUtils.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraUtils.stopPreview();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take:
                takePicture();
                break;

            case R.id.btn_switch:
                switchCamera();
                break;
        }
        CameraUtils.requestFocus();
    }

    /**
     * 拍照
     */
    private void takePicture() {
        CameraUtils.takePicture(new ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, null, new PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
				CameraUtils.startPreview();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (bitmap !=null) {
					Log.i("淦淦", "有");
					aq.id(R.id.dvewew).image(bitmap);
				}
                
                CameraUtils.startPreview();
			}
        });
    }


    /**
     * 切换相机
     */
    private void switchCamera() {
        if (mCameraSurfaceView != null) {
            CameraUtils.switchCamera(1 - CameraUtils.getCameraID(), mCameraSurfaceView.getHolder());
            // 切换相机后需要重新计算旋转角度
            mOrientation = CameraUtils.calculateCameraPreviewOrientation(Camera1Page1.this);
        }
    }

  
}