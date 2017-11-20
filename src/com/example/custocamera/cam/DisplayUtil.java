package com.example.custocamera.cam;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Administrator on 2017/2/16 0016.
 */
public class DisplayUtil {
	
	
	//=================误差0.5版=======================================================
    /**
     * 将px装换成dp，保证尺寸不变
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dp(Context context, float pxValue){
        float density = context.getResources().getDisplayMetrics().density;//得到设备的密度
        return (int) (pxValue/density+0.5f);
    }
    /**dp转px*/
    public static int dp2px(Context context, float dpValue){
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue*density+0.5f);
    }
    public static int px2sp(Context context, float pxValue){
        float scaleDensity = context.getResources().getDisplayMetrics().scaledDensity;//缩放密度
        return (int) (pxValue/scaleDensity+0.5f);
    }
    public static int sp2px(Context context, float spValue) {
        float scaleDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue*scaleDensity+0.5f);
    }
    
    
    //==============精准版========================
    public static  int dpToPx(Context context,int dp) {
        DisplayMetrics displayMetrics =  context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
        return px;
    }
    public static int pxToDp(Context context,int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
    
}