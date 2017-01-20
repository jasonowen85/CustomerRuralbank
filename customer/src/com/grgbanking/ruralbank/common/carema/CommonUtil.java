package com.grgbanking.ruralbank.common.carema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class CommonUtil {
	
	/**
	 * 获取SD卡路径
	 * @return
	 */
	public static String getSDPath() {
		String sdPath = null;
		// 判断sd卡是否存在
		boolean sdCardExit = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (sdCardExit) {
			// 获取根目录
			sdPath = Environment.getExternalStorageDirectory().toString();
		}
		return sdPath;
	}
	
	/**
	 * 返回32位UUID字符串
	 * @return
	 */
	public static String getUUID32(){
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replaceAll("-", "");
	}
	
   /**
    * 加载本地图片
    * @param url
    * @return
    */
    public static Bitmap getBitmapInLocal(String path) {
         try {
              FileInputStream fis = new FileInputStream(path);
              return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片        
           } catch (FileNotFoundException e) {
              e.printStackTrace();
              return null;
         }
    }
    
	/**
	 * 删除文件
	 * @param filePath
	 */
	public static void deleteFile(String filePath){
		if(filePath == null || "".equals(filePath)){
			return;
		}
		File file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
	}
	
	/**
	 * 图片文件进行压缩处理
	 * @param sourceFilePath
	 * @param targetFilePath
	 * @return
	 */
	public static boolean dealImage(String sourceFilePath, String targetFilePath){
		try{
			int dh = 1024;
        	int dw = 768;
        	BitmapFactory.Options factory=new BitmapFactory.Options();  
            factory.inJustDecodeBounds=true; //当为true时  允许查询图片不为 图片像素分配内存  
            InputStream is = new FileInputStream(sourceFilePath);
            Bitmap bmp = BitmapFactory.decodeStream(is,null,factory);  
            int hRatio=(int)Math.ceil(factory.outHeight/(float)dh); //图片是高度的几倍  
            int wRatio=(int)Math.ceil(factory.outWidth/(float)dw); //图片是宽度的几倍  
            System.out.println("hRatio:"+hRatio+"  wRatio:"+wRatio);  
            //缩小到  1/ratio的尺寸和 1/ratio^2的像素  
            if(hRatio>1||wRatio>1){
                if(hRatio>wRatio){
                    factory.inSampleSize=hRatio;   
                }
                else 
                    factory.inSampleSize=wRatio;  
            }
            factory.inJustDecodeBounds=false;
            is.close();
            is = new FileInputStream(sourceFilePath);
            bmp=BitmapFactory.decodeStream(is, null, factory);
            OutputStream outFile = new FileOutputStream(targetFilePath);
            bmp.compress(Bitmap.CompressFormat.JPEG, 60, outFile);
            outFile.close();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
