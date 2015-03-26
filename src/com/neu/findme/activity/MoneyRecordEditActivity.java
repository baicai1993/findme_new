package com.neu.findme.activity;


import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemSelected;
import com.neu.findme.R;
import com.neu.findme.adapter.MoneyRecordListAdapter;
import com.neu.findme.domain.MoneyRecordBean;
import com.neu.findme.domain.UnfinishProject;
import com.neu.findme.domain.UserBean;
import com.neu.findme.server_db.MoneyRecDBServer;
import com.neu.findme.server_db.ProjectCosttypeDBServer;
import com.neu.findme.server_db.UnfinishProjectDBServer;
import com.neu.findme.server_db.UserDBServer;
import com.neu.findme.server_web.HttpWebServer;
import com.neu.findme.utils.JsonParseUtils;
import com.neu.findme.utils.MyApplication;
/**
 * @author cxm
 *财务记录上传只能选择未完成的项目，上传时小数点参与计算但总额显示均截取保留后两位，上传时有非空判断
 *上传成功后activity自动finish,会刷新列表数据
 *2015-03-09 19:53:27
 */
@SuppressLint("SimpleDateFormat") public class MoneyRecordEditActivity extends Activity  {
	@ViewInject(R.id.allMoneyText)
	private TextView allMoneyText;
	@ViewInject(R.id.projectTimeEdit)
	private TextView projectTimeText;
	@ViewInject(R.id.goodsValueEdit)
	private EditText goodsValueEdit;
	@ViewInject(R.id.goodsNumberEdit)
	private EditText goodsNumberEdit;
	@ViewInject(R.id.remarkContentEdit)
	private EditText remarkContentEdit;
	@ViewInject(R.id.recorderEditText)
	private EditText recorderEdit;
	@ViewInject(R.id.handlerText)
	private TextView handlerTextView;
	@ViewInject(R.id.uploadMoneyRecordButton)
	private Button uploadButton;
	@ViewInject(R.id.backButton)
	private Button backButton;
	private String userId;
	//服务器相关
	private HttpWebServer webServer;
	private MoneyRecordBean bean;
	// 数据库相关
	private MoneyRecDBServer recDBServer;
	private ProjectCosttypeDBServer pcDBServer;
	private UnfinishProjectDBServer unfinishProjectDBServer;
	private UserDBServer userDBServer;
	private UserBean userBean;
	//spinner相关
	@ViewInject(R.id.projectNameSpinner)
	private Spinner projectSpinner;
	@ViewInject(R.id.projectItemSpinner)
	private Spinner costtypeSpinner;
	private ArrayAdapter<String> projectAdapter;
	private ArrayAdapter<String> costtypeAdapter;
	private List<String> projects = new ArrayList<String>();
	private List<String> costtypes = new ArrayList<String>();
	@OnClick(R.id.backButton)
	public void backButtonListener(View view){
			MoneyRecordEditActivity.this.finish();
	}
	@OnClick(R.id.uploadMoneyRecordButton)
	public void uploadButtonListener(View view){
		uploadMoneyRec();
	}
	//根据用户选择的项目，从数据库查询对应的类别
	@OnItemSelected(R.id.projectNameSpinner)
	public void onItemSelected(AdapterView<?> parent, View view,
			int position, long id) {
		costtypeAdapter.clear();
		List<String> list = pcDBServer.getCosttypes(projectSpinner.getSelectedItem()+"");
		costtypeAdapter.addAll(list);
		costtypeAdapter.notifyDataSetChanged();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置没有应用名称标题栏
		setContentView(R.layout.activity_moneyrecord_edit);
		ViewUtils.inject(this);//注册
		userId = MyApplication.get("userId")+"";//获取登陆者的ID
		webServer = new HttpWebServer();
		recDBServer = new MoneyRecDBServer(this);
		userDBServer = new UserDBServer(this);
		pcDBServer = new ProjectCosttypeDBServer(this);
		unfinishProjectDBServer = new UnfinishProjectDBServer(this);
		//查询自己的信息
		userBean = userDBServer.queryMe();
		handlerTextView.setText(userBean.getName());
		projectTimeText.setText(getNowTime());
		goodsNumberEdit.addTextChangedListener(numberTextWatcher);
		goodsValueEdit.addTextChangedListener(valueTextWatcher);
		initSpinnerData();//初始化spinner的数据
	}
	// 上传财务记录
	private boolean uploadMoneyRec() {
		if(!(handlerTextView.getText()+"").equals("")&&!(goodsNumberEdit.getText()+"").
				equals("")&&!(goodsValueEdit.getText()+"").equals("")){
			bean = new MoneyRecordBean();
			bean.setDesc(remarkContentEdit.getText()+"");
			bean.setHandler(userBean.getName());
			bean.setHandler_id(userId);
			bean.setId(UUID.randomUUID()+"");//生成UUID作为主键ID
			bean.setNumber(Integer.parseInt(goodsNumberEdit.getText()+""));
			bean.setProject_name(projectSpinner.getSelectedItem()+"");
			bean.setRecorder(recorderEdit.getText()+"");
			bean.setTimeStr(projectTimeText.getText()+"");
			bean.setTotal_price(Float.parseFloat(allMoneyText.getText()+""));
			bean.setType_name(costtypeSpinner.getSelectedItem()+"");
			bean.setUnit_price(Float.parseFloat(goodsValueEdit.getText()+""));
			webServer.uploadMoneyRecord(bean, uploadMoneyRC);
		}else {
			Toast.makeText(MoneyRecordEditActivity.this, MyApplication.getApplication().
					getString(R.string.money_formUnfinished), Toast.LENGTH_SHORT).show();
		}
		return false;
	}
	// 初始化spinner数据
	private void initSpinnerData(){
		//读取未完成的项目
		List<UnfinishProject> list = unfinishProjectDBServer.queryAll(UnfinishProject.class);
		for(UnfinishProject bean:list){
			projects.add(bean.getProjectName());
		}
		projectAdapter = new ArrayAdapter<String>(this, R.layout.view_myspinner, projects){
			@Override
			public View getDropDownView(int position, View convertView,ViewGroup parent) {
				View view = LayoutInflater.from(MoneyRecordEditActivity.this).
						inflate(R.layout.view_myspinner_item, new LinearLayout(MoneyRecordEditActivity.this));
				TextView label = (TextView) view.findViewById(R.id.spinner_item_label);
				label.setText(projects.get(position));
				if (projectSpinner.getSelectedItemPosition() == position) {
					view.setBackgroundColor(getResources().getColor(
							R.color.turquoise));
				} else {
					view.setBackgroundColor(getResources().getColor(
							R.color.moneyspinner));
				}
				return view;
			}
		};
		projectAdapter.setDropDownViewResource(R.layout.view_myspinner_item);
		projectSpinner.setAdapter(projectAdapter);
		//初始化所对应的类别
		costtypes = pcDBServer.getCosttypes(projectSpinner.getSelectedItem()+"");
		costtypeAdapter = new ArrayAdapter<String>(this, R.layout.view_myspinner, costtypes){
			@Override
			public View getDropDownView(int position, View convertView,ViewGroup parent) {
				View view = LayoutInflater.from(MoneyRecordEditActivity.this).
						inflate(R.layout.view_myspinner_item, new LinearLayout(MoneyRecordEditActivity.this));
				TextView label = (TextView) view.findViewById(R.id.spinner_item_label);
				label.setText(costtypes.get(position));
				if (projectSpinner.getSelectedItemPosition() == position) {
					view.setBackgroundColor(getResources().getColor(
							R.color.turquoise));
				} else {
					view.setBackgroundColor(getResources().getColor(
							R.color.moneyspinner));
				}
				return view;
			}
		};
		costtypeAdapter.setDropDownViewResource(R.layout.view_myspinner_item);
		costtypeSpinner.setAdapter(costtypeAdapter);
	}
	//获取当前时间
	public String getNowTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String string = format.format(date);
		return string;
	}
	// 点击返回键时的操作
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			MoneyRecordEditActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	// 描述对goodsNumberEdit的监听
	private TextWatcher numberTextWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(!(goodsNumberEdit.getText()+"").equals("")&&!(goodsValueEdit.getText()+"").equals("")){
				String allMoney = (Integer.parseInt(goodsNumberEdit.getText()+"")*
						Float.parseFloat(goodsValueEdit.getText()+""))+"";
				//显示总数截取小数点两位
				allMoneyText.setText(new DecimalFormat(".00").format(Double.parseDouble(allMoney)));
			}else {
				allMoneyText.setText("0.00");
			}
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}
	};
	// 描述对goodsValueEdit的监听
	private TextWatcher valueTextWatcher = new TextWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(!(goodsValueEdit.getText()+"").equals("")&&!(goodsNumberEdit.getText()+"").equals("")){
				String allMoney = (Integer.parseInt(goodsNumberEdit.getText()+"")*
						Float.parseFloat(goodsValueEdit.getText()+""))+"";
				//显示总数截取小数点两位
				allMoneyText.setText(new DecimalFormat(".00").format(Double.parseDouble(allMoney)));
			}else {
				allMoneyText.setText("0.00");
			}
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}
	};
	//处理财务记录的上传结果
	private RequestCallBack<String> uploadMoneyRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			Toast.makeText(MoneyRecordEditActivity.this, MyApplication.getApplication().
					getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
			Log.e("cxmcxm", arg1);
		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			if(JsonParseUtils.jsonToBoolean(arg0.result)){
				recDBServer.add(bean);//写入数据库
				MoneyRecordListAdapter.recordBeans.add(0,bean);//加入数据源
				Toast.makeText(MoneyRecordEditActivity.this, MyApplication.getApplication().
						getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
				MoneyRecordEditActivity.this.finish();
			}else {
				Toast.makeText(MoneyRecordEditActivity.this, MyApplication.getApplication().
						getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
			}
		}
	};

}
