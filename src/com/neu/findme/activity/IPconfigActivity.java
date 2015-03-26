package com.neu.findme.activity;


import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.neu.findme.R;
import com.neu.findme.utils.MyApplication;
import com.neu.findme.utils.MyCookie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * @author cxm
 *ip设置页面，第一次进入app的初始页面，默认是会记住用户的设置，并不在下一次显示
 *ip设置可以在登录界面返回修改，也可以在设置界面进行修改
 *2015-03-09 16:39:59
 */
public class IPconfigActivity extends Activity {
	@ViewInject(R.id.ipButton)
	private Button ipButton;
	@ViewInject(R.id.resetIpButton)
	private Button resetIpButton;
	@ViewInject(R.id.remeberIpConfig)
	private CheckBox remeberCheckBox;
	@ViewInject(R.id.ipEditText)
	private EditText ipEditText;
	@OnClick(R.id.ipButton)
	public void ipButtonListener(View view){//对输入的字符串进行处理，截取出ip，port，company三段
		String all = ipEditText.getText()+"";
		String ip = all.substring(0, all.indexOf(":"));//服务器ip地址
		String port = all.substring(all.indexOf(":") + 1, all.indexOf("/"));//端口号
		String company = all.substring(all.indexOf("/") + 1, all.length());//为不同公司用户分配的项目名
		if(remeberCheckBox.isChecked()){//持久化存储
			MyCookie.putString("ipconfig", all);
			MyCookie.putString("ip", ip);
			MyCookie.putString("port", port);
			MyCookie.putString("company", company);
		}
		//全局化存储
			MyApplication.put("ipconfig",all );
			MyApplication.put("ip", ip );
			MyApplication.put("port", port);
			MyApplication.put("company", company);
		startActivity(new Intent(this, LoginActivity.class));
		IPconfigActivity.this.finish();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ipconfig);
		ViewUtils.inject(this);//注入事件
		//读取配置文件 如果有ip并且不是从登录界面跳转过来， 直接进入下一个页面
		if(!MyCookie.getString("ipconfig", "").equals("")){
			if(getIntent().getBooleanExtra("flagFromLoginActivity", false)){//flag来判断intent是不是来自登录界面
				ipEditText.setText(MyCookie.
						getString("ipconfig", MyApplication.getApplication().getString(R.string.server_address_content)));
			}else {
				startActivity(new Intent(this, LoginActivity.class));
				IPconfigActivity.this.finish();
			}
		}
	}

}
