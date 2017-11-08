package com.example.custocamera;

import com.example.custocamera.cam.CameraSurfaceView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Camera1Page3 extends AppCompatActivity {

	
	
	private Button button;
   // private CameraSurfaceView mCameraSurfaceView;
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera1_page3);
		//mCameraSurfaceView = (CameraSurfaceView) findViewById(R.id.cameraSurfaceView);
        button = (Button) findViewById(R.id.takePic);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //mCameraSurfaceView.takePicture();
            }
        });
        
        
        
	}
	
}
