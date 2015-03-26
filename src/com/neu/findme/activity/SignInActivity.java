package com.neu.findme.activity;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.neu.findme.R;
import com.neu.findme.domain.SignInBean;
import com.neu.findme.domain.UserBean;
import com.neu.findme.server_db.UserDBServer;
import com.neu.findme.server_locate.AmapLocateServer;
import com.neu.findme.server_web.HttpWebServer;
import com.neu.findme.utils.ChangeDateFormatter;
import com.neu.findme.utils.DevicesUtil;
import com.neu.findme.utils.JsonParseUtils;
import com.neu.findme.utils.MyApplication;
import com.neu.findme.view.MyAlertDialog;

/**
 * @author cxm
 *签到界面，时间是服务器的标准时间，获取不到无法定位
 *定位信息获取失败无法签到或签退
 *每天只能签退和签到一次
 *2015-03-09 20:47:05
 */
public class SignInActivity extends Activity {
	@ViewInject(R.id.backButton)
	private Button backButton;
	@ViewInject(R.id.sign_in_getlocation)
	private Button sign_in_getlocation;
	@ViewInject(R.id.sign_in_button)
	private Button sign_in_Button;
	@ViewInject(R.id.sign_out_getlocation)
	private Button sign_out_getlocation;
	@ViewInject(R.id.sign_out_button)
	private Button sign_out_Button;
	@ViewInject(R.id.sign_in_time)
	private TextView nowTime;
	@ViewInject(R.id.sing_in_location)
	private TextView sign_in_location;
	@ViewInject(R.id.sign_in_remark)
	private EditText sign_in_remark;
	@ViewInject(R.id.sing_out_location)
	private TextView sign_out_location;
	@ViewInject(R.id.sign_out_remark)
	private EditText sign_out_remark;
	private HttpWebServer webServer;
	//设备IMEI
	private String imeiStr;
	//服务器时间
	private String serverTime;
	//用户信息
	private UserBean userBean;
	private UserDBServer userDBServer;
	private String userId = MyApplication.get("userId")+"";
	//定位信息
	@ResInject(id = R.string.location_timeout,type = ResType.String)
	private String timeout; 
	@ResInject(id = R.string.location_locating,type = ResType.String)
	private String locatingString;  
	@ResInject(id = R.string.location_locating,type = ResType.String)
	private String locationStr; 
	@OnClick({R.id.sign_in_button,R.id.sign_out_button})
	public void signBtnListener(View view){
		switch (view.getId()) {
		case R.id.sign_in_button:
			sign("0");
			break;
		case R.id.sign_out_button:
			sign("1");
			break;
		default:
			break;
		}
	}
	@OnClick(R.id.backButton)
	public void backBtnListner(View view){
		SignInActivity.this.finish();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_signin);
		ViewUtils.inject(this);
		sign_in_location.setText(locatingString);
		sign_out_location.setText(locatingString);
		webServer = new HttpWebServer();
		userDBServer = new UserDBServer(SignInActivity.this);
		userBean = userDBServer.queryMe();
		//获取设备imei
		imeiStr = DevicesUtil.getDeviceId(this);
		//开启定位服务
		new AmapLocateServer().startLocate(this);
		getServerTime();//获取服务器时间并显示,等服务器时间获取成功之后，显示定位结果
		
		
	}
	private void getServerTime(){
		webServer.getTime(getServerTimeRC);
		updateUIHandler.postDelayed(new Runnable() {
			//5分钟之后这个activity会自动销毁,防止用户拖延签退时间
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SignInActivity.this.finish();
			}
		}, 300000);
	}
	//"0"代表签到 "1"代表签退
	private void sign(String signType){
		final String type = signType;
		String location;
		if(type.equals("0")){
			location = sign_in_location.getText()+"";
		}else {
			location = sign_out_location.getText()+"";
		}
		//根据地理位置获取的情况进行判断
		if(location.equals(locatingString)||location.equals("")){
			Toast.makeText(SignInActivity.this, MyApplication.getApplication().
					getString(R.string.sigh_noLocation), Toast.LENGTH_SHORT).show();
		}else if(location.equals(timeout)) {
			//询问用户是否强制上传没有地理位置的签到记录
			final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
			myAlertDialog.showWoodDialog(true);
			myAlertDialog.getDialog_title().setText(MyApplication.getApplication().getString(R.string.signWithoutLocationDialog_title));
			myAlertDialog.getDialog_msg().setText(MyApplication.getApplication().getString(R.string.signWithoutLocationDialog_title_content));
			myAlertDialog.getPosButton().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					webServer.signIn(createBean(type), SignRC);
					myAlertDialog.dismiss();
				}
			});
			myAlertDialog.getNevButton().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					myAlertDialog.dismiss();
				}
			});
		}else {
			webServer.signIn(createBean(type), SignRC);
		}
	}
	private SignInBean createBean(String type){
		SignInBean signInBean = new SignInBean();
		signInBean.setLocation(locationStr);
		signInBean.setName(userBean.getName());
		signInBean.setRemark(sign_in_remark.getText()+"");
		signInBean.setType(type);
		signInBean.setUserid(userId);
		signInBean.setDeviceId(imeiStr);
		return signInBean;
	}
	private RequestCallBack<String> getServerTimeRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			Toast.makeText(SignInActivity.this, MyApplication.getApplication().getString(R.string.sigh_getTime_fail),
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			serverTime = JsonParseUtils.jsonToString(arg0.result, "timeStr");
			if(!serverTime.equals("")){
				try {
					nowTime.setText(ChangeDateFormatter.changeDateFormat2(serverTime));
					locationStr = AmapLocateServer.getLocationStr();
					if(locationStr!=null&&!(locationStr.equals(""))&&!(locationStr.equals(locatingString))){
						sign_in_location.setText(locationStr);
						sign_out_location.setText(locationStr);
						//如果一次定位不成功或出现异常，会连续请求5次地理位置，全部失败显示超时
					}else {
						new Thread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Looper.prepare();
								int i = 8;
								while(i>0){
									locationStr = AmapLocateServer.getLocationStr();
									if(locationStr!=null&&!(locationStr.equals(""))&&!(locationStr.equals(locatingString))){
										updateUIHandler.post(new Runnable() {
											public void run() {
													sign_in_location.setText(locationStr);
													sign_out_location.setText(locationStr);
											}
										});
										break;
									}
									try {
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									i--;
								}
								//五次全失败，定位超时
								if(i==0){
									updateUIHandler.post(new Runnable() {
										public void run() {
												sign_in_location.setText(timeout);
												sign_out_location.setText(timeout);
												sign_in_getlocation.setOnClickListener(getlocationBtnListener);
												sign_out_getlocation.setOnClickListener(getlocationBtnListener);
										}
									});
								}
							}
						}).start();
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					Toast.makeText(SignInActivity.this, MyApplication.getApplication().getString(R.string.sigh_getTime_fail),
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}

		}
	};
	private RequestCallBack<String> SignRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			Toast.makeText(SignInActivity.this, MyApplication.getApplication().
					getString(R.string.sighin_fail), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			String flag = "";
			try {//这个json没有按照标准格式，必须单独解析
				JSONObject jsonObject=new JSONObject(arg0.result);
				if(jsonObject!=null){
					flag= jsonObject.getString("flag");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				flag = "false";
				e.printStackTrace();
			}
			if(flag.equals("true")){
				Toast.makeText(SignInActivity.this, MyApplication.getApplication().
						getString(R.string.sighin_success), Toast.LENGTH_SHORT).show();
			}else if(flag.equals("checked")){
				Toast.makeText(SignInActivity.this, MyApplication.getApplication().
						getString(R.string.sighin_repeat), Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(SignInActivity.this, MyApplication.getApplication().
						getString(R.string.sighin_fail), Toast.LENGTH_SHORT).show();
			}
		}
	};
	//更新ui用的handler
	private static Handler updateUIHandler = new Handler() {};
	private OnClickListener getlocationBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			sign_in_location.setText(locatingString);
			sign_out_location.setText(locatingString);
			new AmapLocateServer().startLocate(SignInActivity.this);
			getServerTime();
		}
	};
 }
