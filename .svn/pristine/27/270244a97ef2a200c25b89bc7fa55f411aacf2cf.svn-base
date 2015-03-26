package com.neu.findme.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.neu.findme.R;
import com.neu.findme.utils.DevicesUtil;

public class SettingActivity extends Activity {

	@ViewInject(R.id.setting_net_linear)
	private RelativeLayout netSettingLayout;
	@ViewInject(R.id.setting_version_linear)
	private RelativeLayout versionSettingLayout;
	@ViewInject(R.id.backButton)
	private Button backButton;

	@OnClick(R.id.backButton)
	public void backBtnListener(View view) {
		SettingActivity.this.finish();
	}

	@OnClick(R.id.setting_net_linear)
	public void netSettingListener(View view) {
		Intent intent = new Intent(SettingActivity.this,
				NetSettingActivity.class);
		startActivity(intent);
	}

	@OnClick(R.id.setting_version_linear)
	public void versionSettingListener(View view) {
		DevicesUtil du = new DevicesUtil();
		du.getNewApk(SettingActivity.this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);
		ViewUtils.inject(this);
	}

}
