package com.example.custocamera.cam;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

//import cn.jiazhengye.panda_home.application.BaseApplication;
//import cn.jiazhengye.panda_home.common.Constant;
//import cn.jiazhengye.panda_home.utils.DisplayUtil;
//import cn.jiazhengye.panda_home.utils.LoggerUtil;

public class CameraTopRectView2 extends View {

    private int panelWidth;
    private int panelHeght;

    private int viewWidth;
    private int viewHeight;

    public int rectWidth;
    public int rectHeght;

    private int rectTop;
    private int rectLeft;
    private int rectRight;
    private int rectBottom;

    private int lineLen;
    private int lineWidht;
    private static final int LINE_WIDTH = 8;
    private static final int TOP_BAR_HEIGHT = 50;
    private static final int BOTTOM_BTN_HEIGHT = 66;

//    private static final int TOP_BAR_HEIGHT = Constant.RECT_VIEW_TOP;
//    private static final int BOTTOM_BTN_HEIGHT = Constant.RECT_VIEW_BOTTOM;

    private static final int LEFT_PADDING = 10;
    private static final int RIGHT_PADDING = 10;
    private static final String TIPS = "請將方框對準目標";
    
    private float density;
	private static final int TEXT_SIZE = 16;//字体大小
	
	
    private Paint linePaint;
    private Paint wordPaint;
    private Rect rect;
    private int baseline;

    //高寬比值
    private float Highratio;
    private float Widthratio;
    
    
    private int fuckSide;
    
    
    public CameraTopRectView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        Activity activity = (Activity) context;

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        panelWidth = wm.getDefaultDisplay().getWidth();//拿到屏幕的宽
        panelHeght = wm.getDefaultDisplay().getHeight();//拿到屏幕的高
        Log.i("舊取法", panelWidth+"~"+panelHeght);
        
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        //Log.i("新取法1", width+"~"+height);
        
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;
        //Log.i("新取法2", deviceWidth+"~"+deviceHeight);
        
        
        
        //高度不需要dp转换px,不然整体相机会向上移动一小节
//        viewHeight = panelHeght - (int) DisplayUtil.dp2px(activity,TOP_BAR_HEIGHT + BOTTOM_BTN_HEIGHT);

        viewHeight = panelHeght;
        //viewHeight,界面的高,viewWidth,界面的宽
        viewWidth = panelWidth;

        /*rectWidth = panelWidth
                - UnitUtils.getInstance(activity).dip2px(
                        LEFT_PADDING + RIGHT_PADDING);*/

        rectWidth = panelWidth - (int) DisplayUtil.dp2px(activity,LEFT_PADDING + RIGHT_PADDING);
        
        
        //Log.i("误差0.5版",  DisplayUtil.dp2px(activity,LEFT_PADDING + RIGHT_PADDING)+"");
        //Log.i("精准版",  DisplayUtil.dpToPx(activity,LEFT_PADDING + RIGHT_PADDING)+"");
        
        //rectHeght = (int) (rectWidth * 54 / 85.6);//643
        rectHeght = rectWidth;
        Log.i("边长预设", rectWidth+"");
        // 相对于此view
        rectTop = (viewHeight - rectHeght) / 2;//界面的高 減去 定好的邊長高 
        rectBottom = rectTop + rectHeght;
        
        rectLeft = (viewWidth - rectWidth) / 2;
        rectRight = rectLeft + rectWidth;

        
        
        //-rectWidth 定好的正方行邊長
       /*
        float ag = (float) viewHeight/2;//中心点的y坐標
        float ah = (float) ag - (rectHeght/2);//方行頂部y坐標  --> 中心點向上移動正方行的 邊長/2
        Log.i("頂部y坐標", ah+"");
        float bg = (float) ag + (rectHeght/2);//方行底部y座標 中心點往下移正方行的 邊長/2
        Log.i("底部y座標", bg+"");
        
        float oa = (float) viewWidth/2;//中心点的x坐標
        float ow = (float) oa - (rectWidth/2);//方行頂部x坐標  --> 中心點向左移動正方行的 邊長/2
        Log.i("頂部x坐標", ow+"");
        float og = (float) ow + (rectWidth/2);//方行底部x座標 --> 中心點向右移動正方行的 邊長/2
        Log.i("底部x座標", og+"");
        
        //--寬高比值
        Log.i("肏", rectHeght+"/"+viewHeight);
        Log.i("肏", rectWidth+"/"+viewWidth);
        Highratio = (float) rectHeght / viewHeight;
        Widthratio = (float) rectWidth/viewWidth;
        Log.i("高比值", Highratio+"");
        Log.i("寬比值", Widthratio+"");
        */
        fuckSide = rectHeght;//-實際截圖邊長
        
        density = context.getResources().getDisplayMetrics().density;//将像素转换成dp
        
        lineLen = panelWidth / 8;

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.rgb(0xdd, 0x42, 0x2f));
        linePaint.setStyle(Style.STROKE);
        linePaint.setStrokeWidth(LINE_WIDTH);// 设置线宽
        linePaint.setAlpha(255);

        wordPaint = new Paint();
        wordPaint.setAntiAlias(true);
        wordPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wordPaint.setStrokeWidth(3);
        wordPaint.setTextSize(35);

        rect = new Rect(rectLeft, rectTop - 80, rectRight, rectTop - 10);
        FontMetricsInt fontMetrics = wordPaint.getFontMetricsInt();
        baseline = rect.top + (rect.bottom - rect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        wordPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        wordPaint.setColor(Color.TRANSPARENT);
        
        
        canvas.drawRect(rect, wordPaint);

        //画蒙层
        //wordPaint.setColor(0xa0000000);
        wordPaint.setColor(0xa1000000);
        
        rect = new Rect(0, viewHeight/2+rectHeght/2, viewWidth, viewHeight);
        canvas.drawRect(rect, wordPaint);

        rect = new Rect(0, 0, viewWidth, viewHeight/2-rectHeght/2);
        canvas.drawRect(rect, wordPaint);

        rect = new Rect(0, viewHeight/2-rectHeght/2, (viewWidth-rectWidth)/2, viewHeight/2+rectHeght/2);
        canvas.drawRect(rect, wordPaint);

        rect = new Rect(viewWidth-(viewWidth-rectWidth)/2, viewHeight/2-rectHeght/2, viewWidth, viewHeight/2+rectHeght/2);
        canvas.drawRect(rect, wordPaint);


        //重制rect  并画文字  吧文字置于rect中间
        rect = new Rect(rectLeft, rectTop - 80, rectRight, rectTop - 10);
        wordPaint.setColor(Color.WHITE);
        wordPaint.setTextSize(TEXT_SIZE * density); //大小
        wordPaint.setTypeface(Typeface.create("System", Typeface.BOLD));//粗体字
        
        canvas.drawText(TIPS, rect.centerX(), baseline, wordPaint);
        canvas.drawLine(rectLeft, rectTop, rectLeft + lineLen, rectTop,
                linePaint);
        canvas.drawLine(rectRight - lineLen, rectTop, rectRight, rectTop,
                linePaint);
        canvas.drawLine(rectLeft, rectTop, rectLeft, rectTop + lineLen,
                linePaint);
        canvas.drawLine(rectRight, rectTop, rectRight, rectTop + lineLen,
                linePaint);
        canvas.drawLine(rectLeft, rectBottom, rectLeft + lineLen, rectBottom,
                linePaint);
        canvas.drawLine(rectRight - lineLen, rectBottom, rectRight, rectBottom,
                linePaint);
        canvas.drawLine(rectLeft, rectBottom - lineLen, rectLeft, rectBottom,
                linePaint);
        canvas.drawLine(rectRight, rectBottom - lineLen, rectRight, rectBottom,
                linePaint);
    }

    public int getRectLeft() {
        return rectLeft;
    }

    public int getRectTop() {
        return rectTop;
    }

    public int getRectRight() {
        return rectRight;
    }

    public int getRectBottom() {
        return rectBottom;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

	public float getHighratio() {
		return Highratio;
	}


	public float getWidthratio() {
		return Widthratio;
	}

	public int getFuckSide() {
		return fuckSide;
	}


    
    
    
    
    
    
}