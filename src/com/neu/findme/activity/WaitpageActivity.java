package com.neu.findme.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.neu.findme.R;

/**
 * @author cxm
 *欢迎页面，只在启动app时显示2秒钟
 *2015-03-09 20:49:02
 */
public class WaitpageActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_waitpage);
        new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
	    		Intent i = new Intent(WaitpageActivity.this,IPconfigActivity.class);
	    		startActivity(i);
	    		WaitpageActivity.this.finish();
			}
        }, 2000); //启动等待2秒钟
	}
}
