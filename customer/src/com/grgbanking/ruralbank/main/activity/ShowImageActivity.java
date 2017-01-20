package com.grgbanking.ruralbank.main.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import com.grgbanking.ruralbank.common.carema.CommonUtil;

/**
 * 查看照片详细
 * @author wangzhengfu
 *
 */
public class ShowImageActivity extends Activity {
	
	private ImageView imageView;
	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		//显示传递过来的照片地址
		String imageUrl = getIntent().getStringExtra("ImageUrl");
		ImageView iv = new ImageView(this);
		iv.setImageBitmap(CommonUtil.getBitmapInLocal(imageUrl));
		
		setContentView(iv);
	}

}
