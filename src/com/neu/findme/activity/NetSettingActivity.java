package com.neu.findme.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.neu.findme.R;
import com.neu.findme.server_db.UserDBServer;
import com.neu.findme.utils.MyApplication;
import com.neu.findme.utils.MyCookie;
import com.neu.findme.view.MyAlertDialog;

/**
 * @author cxm
 *设置新的服务器地址，清空原有的所有用户本地数据，清空任务栈，重启到登录界面
 *2015-03-09 20:44:30
 */
public class NetSettingActivity extends Activity {
	@ViewInject(R.id.ip_EditText)
	private EditText serverAddEditText;
	@ViewInject(R.id.confirmButton)
	private Button confirmButton;
	@ViewInject(R.id.resetButton)
	private Button resetButton;
	@ViewInject(R.id.backButton)
	private Button backButton;
	@OnClick(R.id.backButton)
	public void backBtnListener(View view){
		NetSettingActivity.this.finish();
	}
	@OnClick(R.id.resetButton)
	public void resetBtnListener(View view){
		serverAddEditText.setText(MyCookie.getString("ipconfig", ""));
	}
	@OnClick(R.id.confirmButton)
	public void confirmBtnListener(View view){
		final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
		myAlertDialog.showWoodDialog(true);
		myAlertDialog.getDialog_title().setText(MyApplication.getApplication().getString(R.string.settingDialog_title));
		myAlertDialog.getDialog_msg().setText(MyApplication.getApplication().getString(R.string.netSettingDialog_content));
		myAlertDialog.getPosButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String all = serverAddEditText.getText()+"";
				String ip = all.substring(0, all.indexOf(":"));
				String port = all.substring(all.indexOf(":") + 1, all.indexOf("/"));
				String company = all.substring(all.indexOf("/") + 1, all.length());
				new UserDBServer(NetSettingActivity.this).clearAllTable();//清空数据库
				MyCookie.removeCookie();//清空配置文件
				//把新填入的URL写入新cookie
				MyCookie.putString("ipconfig", all);
				MyCookie.putString("ip", ip);
				MyCookie.putString("port", port);
				MyCookie.putString("company", company);
				//全局化存储
				MyApplication.put("ipconfig",all );
				MyApplication.put("ip", ip );
				MyApplication.put("port", port);
				MyApplication.put("company", company);
				//清理任务栈，并开启新任务栈
				myAlertDialog.dismiss();
				Intent logoutIntent = new Intent(NetSettingActivity.this, IPconfigActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(logoutIntent);
//				File file = new File(FileUtil.BASE_PATH);//清除文件
//				if (file.exists()) {
//					FileUtil.deleteFiles(file);
//				}
			}
		});
		myAlertDialog.getNevButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				myAlertDialog.dismiss();
			}
		});
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_setting);
		ViewUtils.inject(this);
		serverAddEditText.setText(MyCookie.
				getString("ipconfig", MyApplication.getApplication().getString(R.string.IP_address)));
	}

}
