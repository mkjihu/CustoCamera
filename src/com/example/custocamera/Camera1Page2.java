package com.example.custocamera;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.androidquery.AQuery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class Camera1Page2 extends AppCompatActivity implements SurfaceHolder.Callback, OnClickListener{

	
	private SurfaceView mSurfaceView;
	private Button mViewfinder;
	private View mCancel;
	private Button mTakePhoto;
	private boolean mWaitForTakePhoto;
	private boolean mIsSurfaceReady;
	private Camera.Size mBestPictureSize;
	private Camera.Size mBestPreviewSize;
	private Camera mCamera;
	private String mOutput;
	
	 public AQuery aq;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera1_page2);
		
		aq = new AQuery(this);
		mSurfaceView = (SurfaceView)findViewById(R.id.surface_view);
		mTakePhoto = (Button)findViewById(R.id.mTakePhoto);
		mViewfinder = (Button)findViewById(R.id.mViewfinder);
		mViewfinder.setOnClickListener(this);
	    //mCancel.setOnClickListener(this);
	    mTakePhoto.setOnClickListener(this);
	    SurfaceHolder holder = mSurfaceView.getHolder();
	    holder.addCallback(this);
	    mOutput = PhotoActionHelper.getOutputPath(getIntent());
	    
	     
	     
	}

	
	
	   private void openCamera() {
	        if (mCamera == null) {
	            try {
	                mCamera = Camera.open(1);
	            } catch (RuntimeException e) {
	                finish();
	                return;
	            }
	        }

	        final Camera.Parameters cameraParams = mCamera.getParameters();
	        cameraParams.setPictureFormat(ImageFormat.JPEG);
	        cameraParams.setRotation(90);
	        cameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
	        mCamera.setParameters(cameraParams);

	        // 短边比长边
	        final float ratio = (float) mSurfaceView.getWidth() / mSurfaceView.getHeight();

	        // 设置pictureSize
	        List<Camera.Size> pictureSizes = cameraParams.getSupportedPictureSizes();
	        if (mBestPictureSize == null) {
	            mBestPictureSize =findBestPictureSize(pictureSizes, cameraParams.getPictureSize(), ratio);
	        }
	        Log.i("設置圖片高寬", mBestPictureSize.width+"__"+mBestPictureSize.height);
	        cameraParams.setPictureSize(mBestPictureSize.width, mBestPictureSize.height);

	        // 设置previewSize
	        List<Camera.Size> previewSizes = cameraParams.getSupportedPreviewSizes();
	        if (mBestPreviewSize == null) {
	            mBestPreviewSize = findBestPreviewSize(previewSizes, cameraParams.getPreviewSize(),
	                    mBestPictureSize, ratio);
	        }
	        cameraParams.setPreviewSize(mBestPreviewSize.width, mBestPreviewSize.height);
	         //cameraParams.setPreviewSize(mBestPreviewSize.width, mBestPreviewSize.height);
	        
	        
	        
	        setSurfaceViewSize(mBestPreviewSize);
	        setCameraDisplayOrientation(this, mCamera);
	        try {
	            mCamera.setParameters(cameraParams);
	        } catch (RuntimeException e) {
	            reportBug(cameraParams, e);
	        }

	        if (mIsSurfaceReady) {
	            startPreview();
	        }
	    }

	    private void reportBug(Camera.Parameters cameraParams, RuntimeException e) {
	        final List<Camera.Size> pictureSizes = cameraParams.getSupportedPictureSizes();
	        final List<Camera.Size> previewSizes = cameraParams.getSupportedPreviewSizes();
	        final StringBuilder sb = new StringBuilder();
	        sb.append("surface[").append(mSurfaceView.getWidth()).append(",").append(mSurfaceView.getHeight()).append("]\n");
	        buildSizesLog("picture", pictureSizes, sb);
	        buildSizesLog("preview", previewSizes, sb);
	    }

	    private void buildSizesLog(String tag, List<Camera.Size> sizes, StringBuilder sb) {
	        sb.append(tag).append("{");
	        for(Camera.Size size : sizes) {
	            sb.append("[").append(size.width).append(",").append(size.height).append("],");
	        }
	        sb.deleteCharAt(sb.length() - 1);
	        sb.append("}\n");
	    }

	    private void setSurfaceViewSize(Camera.Size size) {
	        ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
	        params.height = mSurfaceView.getWidth() * size.width / size.height;
	        mSurfaceView.setLayoutParams(params);
	    }

	    /**
	     * 找到短边比长边大于于所接受的最小比例的最大尺寸
	     *
	     * @param sizes       支持的尺寸列表
	     * @param defaultSize 默认大小
	     * @param minRatio    相机图片短边比长边所接受的最小比例
	     * @return 返回计算之后的尺寸
	     */
	    private Camera.Size findBestPictureSize(List<Camera.Size> sizes, Camera.Size defaultSize, float minRatio) {
	        final int MIN_PIXELS = 320 * 480;

	        sortSizes(sizes);

	        Iterator<Camera.Size> it = sizes.iterator();
	        while (it.hasNext()) {
	            Camera.Size size = it.next();
	            //移除不满足比例的尺寸
	            if ((float) size.height / size.width <= minRatio) {
	                it.remove();
	                continue;
	            }
	            //移除太小的尺寸
	            if (size.width * size.height < MIN_PIXELS) {
	                it.remove();
	            }
	        }

	        // 返回符合条件中最大尺寸的一个
	        if (!sizes.isEmpty()) {
	            return sizes.get(0);
	        }
	        // 没得选，默认吧
	        return defaultSize;
	    }

	    /**
	     * @param sizes
	     * @param defaultSize
	     * @param pictureSize 图片的大小
	     * @param minRatio preview短边比长边所接受的最小比例
	     * @return
	     */
	    private Camera.Size findBestPreviewSize(List<Camera.Size> sizes, Camera.Size defaultSize,
	                                            Camera.Size pictureSize, float minRatio) {
	        final int pictureWidth = pictureSize.width;
	        final int pictureHeight = pictureSize.height;
	        boolean isBestSize = (pictureHeight / (float)pictureWidth) > minRatio;
	        sortSizes(sizes);

	        Iterator<Camera.Size> it = sizes.iterator();
	        while (it.hasNext()) {
	            Camera.Size size = it.next();
	            if ((float) size.height / size.width <= minRatio) {
	                it.remove();
	                continue;
	            }

	            // 找到同样的比例，直接返回
	            if (isBestSize && size.width * pictureHeight == size.height * pictureWidth) {
	                return size;
	            }
	        }

	        // 未找到同样的比例的，返回尺寸最大的
	        if (!sizes.isEmpty()) {
	            return sizes.get(0);
	        }

	        // 没得选，默认吧
	        return defaultSize;
	    }

	    private static void sortSizes(List<Camera.Size> sizes) {
	        Collections.sort(sizes, new Comparator<Camera.Size>() {
	            @Override
	            public int compare(Camera.Size a, Camera.Size b) {
	                return b.height * b.width - a.height * a.width;
	            }
	        });
	    }

	    private void startPreview() {
	        if (mCamera == null) {
	            return;
	        }
	        try {
	            mCamera.setPreviewDisplay(mSurfaceView.getHolder());
	            mCamera.setDisplayOrientation(90);
	            mCamera.startPreview();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

	    private void stopPreview() {
	        if (mCamera != null) {
	            mCamera.stopPreview();
	        }
	    }

	    private void closeCamera() {
	        if (mCamera == null) {
	            return;
	        }
	        mCamera.cancelAutoFocus();
	        stopPreview();
	        mCamera.release();
	        mCamera = null;
	    }

	    /**
	     * 请求自动对焦
	     */
	    private void requestFocus() {
	        if (mCamera == null || mWaitForTakePhoto) {
	            return;
	        }
	        mCamera.autoFocus(null);
	    }

	    /**
	     * 拍照
	     */
	    private void takePhoto() {
	        if (mCamera == null || mWaitForTakePhoto) {
	            return;
	        }
	        mWaitForTakePhoto = true;
	        mCamera.takePicture(null, null, new Camera.PictureCallback() {
	            @Override
	            public void onPictureTaken(byte[] data, Camera camera) {
	                
	            	Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
	            	Log.i("圖片高寬", bitmap.getHeight()+","+bitmap.getWidth());
	                if (bitmap !=null) {
						Log.i("淦淦", "有");
						aq.id(R.id.imageView1).image(bitmap);
					}
	            	
	                mWaitForTakePhoto = false;
	            }
	        });
	    }



	    @Override
	    protected void onResume() {
	        super.onResume();
	        mSurfaceView.post(new Runnable() {
	            @Override
	            public void run() {
	                openCamera();
	            }
	        });
	    }

	    @Override
	    protected void onPause() {
	        super.onPause();
	        closeCamera();
	    }

	    @Override
	    public void surfaceCreated(SurfaceHolder holder) {
	        mIsSurfaceReady = true;
	        startPreview();
	    }

	    @Override
	    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	    }

	    @Override
	    public void surfaceDestroyed(SurfaceHolder holder) {
	        mIsSurfaceReady = false;
	    }

	    @Override
	    public void onClick(View v) {
	        final int id = v.getId();
	        switch (id) {
	       
	            case R.id.mViewfinder:
	                requestFocus();
	                break;
	            /* case R.id.cancel:
	                cancelAndExit();
	                break;
	                */
	            case R.id.mTakePhoto:
	                takePhoto();
	                break;
	            default:// do nothing
	        }
	    }

	    @Override
	    public void onBackPressed() {
	        cancelAndExit();
	    }

	    private void cancelAndExit() {
	        setResult(Activity.RESULT_CANCELED);
	        finish();
	    }
	
	//=============================================
	
	public static class PhotoActionHelper {
	    private static final String EXTRA_OUTPUT = "output";
	    private static final String EXTRA_INPUT = "input";
	    private static final String EXTRA_OUTPUT_MAX_WIDTH = "output-max-width";

	    private final Intent mIntent;
	    private final Activity mFrom;
	    private int mRequestCode;

	    private PhotoActionHelper(Activity from, Class to) {
	        mFrom = from;
	        mIntent = new Intent(from, to);
	    }

	    public static PhotoActionHelper takePhoto(Activity from) {
	        return new PhotoActionHelper(from, Camera1Page2.class);
	    }

	    public PhotoActionHelper output(String path) {
	        mIntent.putExtra(EXTRA_OUTPUT, path);
	        return this;
	    }

	    public PhotoActionHelper maxOutputWidth(int width) {
	        mIntent.putExtra(EXTRA_OUTPUT_MAX_WIDTH, width);
	        return this;
	    }

	    public PhotoActionHelper requestCode(int code) {
	        mRequestCode = code;
	        return this;
	    }

	    public void start() {
	        mFrom.startActivityForResult(mIntent, mRequestCode);
	    }

	    public static String getOutputPath(Intent data) {
	        return data == null ? null : data.getStringExtra(EXTRA_OUTPUT);
	    }

	    public static String getInputPath(Intent data) {
	        return data == null ? null : data.getStringExtra(EXTRA_INPUT);
	    }

	    static int getMaxOutputWidth(Intent data) {
	        return data.getIntExtra(EXTRA_OUTPUT_MAX_WIDTH, 0);
	    }
	}
	
	
	//======2016 10 12 Nexus 5 反轉=========
	
	
	public void setCameraDisplayOrientation(Context context, Camera mCamera) {
	    Camera.CameraInfo info =
	            new Camera.CameraInfo();
	    Camera.getCameraInfo(0, info); // 使用第一个后置摄像头
	    int rotation = getDeviceCurrentOrientation(context);

	    int degrees = 0;
	    switch (rotation) {
	        case Surface.ROTATION_0: degrees = 0; break;
	        case Surface.ROTATION_90: degrees = 90; break;
	        case Surface.ROTATION_180: degrees = 180; break;
	        case Surface.ROTATION_270: degrees = 270; break;
	    }

	    int result;
	    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	        result = (info.orientation + degrees) % 360;
	        result = (360 - result) % 360;  // compensate the mirror
	    } else {  // back-facing
	        result = (info.orientation - degrees + 360) % 360;
	    }
	    mCamera.setDisplayOrientation(result);
	}
	
	public static int getDeviceCurrentOrientation(Context context) {
	    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    int rotation = windowManager.getDefaultDisplay().getRotation();
	    Log.d("Utils", "Current orientation = " + rotation);
	    return rotation;
	}
}
