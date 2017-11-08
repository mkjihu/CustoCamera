package com.example.custocamera;
import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.hardware.camera2.*;
import android.hardware.camera2.params.*;
import android.media.*;
import android.os.*;
import android.support.annotation.*;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.*;
import android.util.Size;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import com.androidquery.AQuery;
import com.example.custocamera.cam.CameraTopRectView2;
import java.nio.ByteBuffer;
import java.util.*;


/**
 *
 * https://github.com/googlesamples/android-Camera2Basic/blob/master/Application/src/main/java/
 *
 */
public class Camera2Page3 extends AppCompatActivity {

	
    private TextureView textureView;
    //private SurfaceHolder surfaceHolder;
    private int currentCameraId = CameraCharacteristics.LENS_FACING_FRONT;//摄像头id（通常0代表后置摄像头，1代表前置摄像头）
    
    private int height=0,width=0;
    
	private CameraManager cameraManager;
	private HandlerThread handlerThread;
	private Handler handler;
	private Size previewSize;
	private CameraDevice cameraDevice;
	private ImageReader imageReader;
	private CameraCaptureSession cameraCaptureSession;
	private CaptureRequest.Builder previewBuilder;
	private CaptureRequest.Builder captureBuilder;
	 
	/**相機傳感器的方向*/
	private int mSensorOrientation;
	///===
	public ImageView cocosw,cllaimg;
	//-拍照完成
	public FrameLayout lair;
	public ImageView opimag;
	public AQuery aq;
	
	public CameraTopRectView2 rectOnCamera;
	
	
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static
    {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2_page3);
        aq = new AQuery(this);
        initView();
    }


	private void initView() {
		textureView = (TextureView) findViewById(R.id.textureView);
		cocosw = (ImageView)findViewById(R.id.cocosw);
		cllaimg = (ImageView)findViewById(R.id.cllaimg);
		opimag = (ImageView)findViewById(R.id.opimag);
		lair = (FrameLayout)findViewById(R.id.lair);
		rectOnCamera = (CameraTopRectView2)findViewById(R.id.rectOnCamera);
		
	    //Camera2全程异步
	    handlerThread = new HandlerThread("Camera2");
	    handlerThread.start();
	    handler = new Handler(handlerThread.getLooper());
	    
	    cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
	    
	    textureView.setSurfaceTextureListener(surfaceTextureListener);
	
        
        cocosw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switchCamera();
			}
		});
        cllaimg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				takePhoto();
			}
		});
	   

	}
	 /**TextureView的监听*/
    private TextureView.SurfaceTextureListener surfaceTextureListener= new TextureView.SurfaceTextureListener() {

        //可用
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        	Camera2Page3.this.width=width;
        	Camera2Page3.this.height=height;
        	Log.i("淦", ""+height);
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}
        //释放
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            closeCamera();
            return true;
        }
        //更新
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
    };
    
    
    /**
     * 打开相机
     */
    private void openCamera() {
    	
    	//设置摄像头特性   设置相机输出
    	 try {
             if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                 //提示用户开户权限
            	 Log.i("幹你娘", "沒有權限開啥小相機");
             }else {
                 // 获取指定摄像头的特性   ----是他妈要开前还是开后啦淦
                 CameraCharacteristics cameraCharacteristics= cameraManager.getCameraCharacteristics(String.valueOf(currentCameraId));
                 //获取摄像头支持的配置属性
                 StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                 
                 // 获取手机目前的旋转方向(横屏还是竖屏
                 mSensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                 
                 // 获取摄像头支持的最大尺寸 获取最佳的预览尺寸
                 Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                 imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),ImageFormat.JPEG, 2);
                 previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height, largest);
                 
                 //监听ImageReader时间，有图像数据可用时回调，参数就是帧数据         註冊偵聽器，以便在ImageReader中有新圖像可用時進行調用。
                 imageReader.setOnImageAvailableListener(imageAvailableListener,handler);//-诞生用来处理main线程的Handler对象
                 
                 //第一个参数指定哪个摄像头，第二个参数打开摄像头的状态回调，第三个参数是运行在哪个线程(null是当前线程)
                 cameraManager.openCamera(String.valueOf(currentCameraId), stateCallback, handler);
                 //cameraManager.openCamera(String.valueOf(currentCameraId), stateCallback, null);
             }

         } catch (CameraAccessException e){
        	 Log.i("幹你娘", "错啥小 幹");
         }
    } 

    //打开相机回调
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            // 相机开启，打开预览
            cameraDevice = camera;
            startPreview();
        }
        
		@Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            //相机关闭
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            //相机报错
            camera.close();
            cameraDevice = null;
            finish();
        }
    };  
    
    //======开始预览===========================================================================================================================================================
    private void startPreview() {
        try {
        	
        	// 预览请求构建
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //设置自动对焦模式
            previewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            
            SurfaceTexture texture = textureView.getSurfaceTexture();
            // 设置宽度和高度
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            // 用来开始预览的输出surface
            Surface surface = new Surface(texture);
            previewBuilder.addTarget(surface);//设置Surface作为预览数据的显示界面
            
            //创建相机捕获会话，
            //第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession状态回调接口，第三个参数是在哪个线程(null是当前线程)
            cameraDevice.createCaptureSession(Arrays.asList(surface, imageReader.getSurface()), sessionStateCallback, handler);
        } catch (CameraAccessException e) {
        	Log.i("阿幹林娘錯誤", "操你 錯啥小1"+e.getMessage());
        	e.printStackTrace();
        }

    }

    //创建  预览  session回调
    private CameraCaptureSession.StateCallback sessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                cameraCaptureSession = session;
                //自动对焦--設置第二次
                previewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
                cameraCaptureSession.setRepeatingRequest(previewBuilder.build(), null, handler);
                
            } catch (CameraAccessException e) {
               Log.i("阿幹林娘錯誤", "操你 錯啥小");
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
        	Log.i("幹林老師","操雞掰");
            session.close();
            cameraCaptureSession = null;
            cameraDevice.close();
            cameraDevice = null;
        }
    };  
    
 	//=================================================================================================================================================================
    
    //==============切换摄像头===================================================================================================================================================
    private void switchCamera() {
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                //Size maxSize = getMaxSize(map.getOutputSizes(SurfaceHolder.class));
                Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                Size maxSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height, largest);
                if (currentCameraId == CameraCharacteristics.LENS_FACING_BACK && characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    //前置转后置
                    previewSize = maxSize;
                    currentCameraId = CameraCharacteristics.LENS_FACING_FRONT;
                    cameraDevice.close();
                    openCamera();
                    break;
                } else if (currentCameraId == CameraCharacteristics.LENS_FACING_FRONT && characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    //后置转前置
                    previewSize = maxSize;
                    currentCameraId = CameraCharacteristics.LENS_FACING_BACK;
                    cameraDevice.close();
                    openCamera();
                    break;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    //=================================================================================================================================================================
  
    //=============拍照====================================================================================================================================================

    private void takePhoto() {
    	
    	//需要先判断是否为前鏡頭
    	//問題在於許多前置攝像頭具有固定的焦距。因為前鏡頭沒有對焦跟閃光燈
    	
        try {
        	if (cameraDevice == null)
            {
                return;
            }
            // 创建拍照请求
            captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 设置自动对焦模式
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 打开闪光灯 閃光燈在必要時自動啟用。
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            
            // 将imageReader的surface设为目标
            captureBuilder.addTarget(imageReader.getSurface());
            
            // 获取设备方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();// 根据设备方向计算设置照片的方向
            //captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));
            
            //captureBuilder.set(CaptureRequest.JPEG_ORIENTATION,270);//旋转
            
            
            // 停止连续取景
            cameraCaptureSession.stopRepeating();
            
            //设置拍照监听 //拍照
            cameraCaptureSession.capture(captureBuilder.build(), captureCallback, handler);
            
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    
    public int getOrientation(int rotation) {
    	//大多數設備的傳感器方向為90°，某些設備的傳感器方向為270°（例如Nexus 5X）
    	//我們必須考慮到這一點，並正確地旋轉JPEG。
    	//對於方向為90的設備，我們只需返回ORIENTATIONS的映射。
    	//對於方向為270的設備，我們需要將JPEG旋轉180度。
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    
    
    //拍完照回调
    private CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }
        // 拍照成功
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            try { 
            	// 重设自动对焦模式
                captureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                //captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);// 设置自动曝光模式
                //重新进行预览
            	session.setRepeatingRequest(previewBuilder.build(), null, handler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            cameraCaptureSession.close();
            cameraCaptureSession = null;
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    
    /**监听拍照的图片*/
    private ImageReader.OnImageAvailableListener imageAvailableListener= new ImageReader.OnImageAvailableListener()
    {
        // 当照片数据可用时激发该方法
        @Override
        public void onImageAvailable(ImageReader reader) {
        	Log.i("淦你娘", "操機掰");
        	//http://blog.csdn.net/lhbtm/article/details/55505668
        	//https://github.com/xingda920813/HelloCamera2
        	Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;//1是載入原圖   根据inSampleSize载入一个缩略图。 比如inSampleSize=4，载入的缩略图是原图大小的1/4。
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        	Log.i("高", bitmap.getHeight()+"");
        	Log.i("寬", bitmap.getWidth()+"");
        	
        	//rectOnCamera.draw(new Canvas());
        	Bitmap bm = Bitmap.createBitmap(bitmap, rectOnCamera.getRectLeft(),
        			rectOnCamera.getRectTop(),
        			rectOnCamera.getRectRight() - rectOnCamera.getRectLeft(),
        			rectOnCamera.getRectBottom() - rectOnCamera.getRectTop());// 截取
        	Log.i("高1", bm.getHeight()+"");
        	Log.i("寬2", bm.getWidth()+"");
        	
        	//handler.post(new ImageSaver(bitmap));
        	runOnUiThread(new ImageSaver(bm));
        	
        	image.close();
        }
    };
    public class ImageSaver implements Runnable {
    	public Bitmap bitmap;
    	public ImageSaver(Bitmap bitmap) {
    		this.bitmap = bitmap;
		}
		@Override
		public void run() {
			lair.setVisibility(View.VISIBLE);
			opimag.setImageBitmap(bitmap);
		}
    
    }
    //=================================================================================================================================================================
    
    
    
    
    
    
    /**
     * 关闭相机
     */
    private void closeCamera() {
        //关闭捕捉会话
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
        //关闭相机
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        //关闭拍照处理器
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //=================================================================================================================================================================
    /**获取最大预览尺寸*/
    private Size getMaxSize(Size[] outputSizes) {
        Size sizeMax = null;
        if (outputSizes != null) {
            sizeMax = outputSizes[0];
            for (Size size : outputSizes) {
                if (size.getWidth() * size.getHeight() > sizeMax.getWidth() * sizeMax.getHeight()) {
                    sizeMax = size;
                }
            }
        }
        return sizeMax;
    }
    
    private Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio)
    {
        // 收集摄像头支持的大过预览Surface的分辨率
        List<Size> bigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices)
        {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height)
            {
                bigEnough.add(option);
            }
        }
        // 如果找到多个预览尺寸，获取其中面积最小的
        if (bigEnough.size() > 0)
        {
            return Collections.min(bigEnough, new CompareSizesByArea());
        }
        else
        {
           //没有合适的预览尺寸
            return choices[0];
        }
    } 
    
    
    public class CompareSizesByArea implements Comparator<Size>
    {
        @Override
        public int compare(Size lhs, Size rhs)
        {
            // 强转为long保证不会发生溢出
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }  
     
}