package com.gen.mube.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class Utils {
	
	private Utils () {}
	
	public static final boolean isDebug = true;
	public static final String LOG_TAG  = "Mube"; 
	
	public static void hideIME (Context context, View view) {
		InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public static String[] splitSearchWrods (String searchWord) {
		return searchWord.split("\\s|　", 0);
	}
	
	 /**
     * 指定された横幅に合わせて画像をリサイズする
     * 縦横比は固定
     * @param width
     * @param src
     * @return
     */
    public static Bitmap resizeBitmapFitWidth(float width, Bitmap src) {
    	float scale = width / (float)src.getWidth();
    	return scaledBitmap(scale, src);
    } 
    
    /**
     * 指定された高さに合わせて画像をリサイズする
     * 縦横比は固定
     * @param height
     * @param src
     * @return
     */
    public static Bitmap resizeBitmapFitHeight(float height, Bitmap src) {
    	float scale = height / (float)src.getHeight();
    	return scaledBitmap(scale, src);
    }
    
    /**
     * 指定された倍率に合わせて画像を拡大縮小する
     * 縦横比は固定
     * @param scale
     * @param src
     * @return
     */
    private static Bitmap scaledBitmap(float scale, Bitmap src) {
    	if (scale == 0) 					 return null;
    	if (src == null || src.isRecycled()) return null;
    	
    	// リサイズしたBitmapオブジェクトを新しく生成し、古いオブジェクトは解放する。
    	int dstWidth = (int)(src.getWidth() * scale);
    	int dstHeight = (int)(src.getHeight() * scale);
    	Bitmap dstBmp = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
    	
    	// 画像オブジェクトの開放
    	src.recycle();
    	src = null;
    	
    	return dstBmp;
    	
    }
	
	public static class Log {
    	private Log(){}

        public static void e(String msg){
            Utils.Log.e("", msg);
        }
        public static void e(String tag, String msg) {
            if (isDebug) android.util.Log.e(LOG_TAG + "@" + tag, msg);
        }

        public static void d(String msg) {
        	Utils.Log.d("", msg);
        }
        public static void d(String tag, String msg) {
            if (isDebug)  android.util.Log.d(LOG_TAG + "@" + tag, msg);
        }
    }

}
