package com.neu.findme.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.neu.findme.R;

/**
 * @author cxm
 * 抽屉栏关于我们界面，提供一个官网链接
 *2015-03-09 16:33:15
 */
public class AboutUsActivity extends Activity {
	@ViewInject(R.id.backButton)
	private Button backButton;
	@ViewInject(R.id.website)
	private TextView websit;
	@ViewInject(R.id.versionTextView)
	private TextView versionTextView;
	@OnClick(R.id.backButton)
	public void backBtnListener(View view){
		AboutUsActivity.this.finish();
	}
	//提供findme官网的跳转链接
	@OnClick(R.id.website)
	public void webtextListener(View view){
		Uri uri=Uri.parse("http://www.yisoft.cc");
		Intent intent=new Intent(Intent.ACTION_VIEW,uri);
		startActivity(intent);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_aboutus);
		ViewUtils.inject(this);
		String appVersion = "";
		PackageManager manager = this.getPackageManager();
		        try {
		        	PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
		        	appVersion = info.versionName; //获取manifest里的版本名
		         } catch (NameNotFoundException e) {
		        	 // TODO Auto-generated catch block
		        	 e.printStackTrace();
		         }
		versionTextView.setText(versionTextView.getText()+ appVersion);
	}

}
