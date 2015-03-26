package com.neu.findme.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.neu.findme.R;
import com.neu.findme.adapter.RecordLocalListAdapter;
import com.neu.findme.adapter.RecordNetListAdapter;
import com.neu.findme.domain.BaseRecordBean;
import com.neu.findme.domain.LocalRecordBean;
import com.neu.findme.domain.NetRecordBean;
import com.neu.findme.server_db.LocalRecDBServer;
import com.neu.findme.server_db.NetRecDBServer;
import com.neu.findme.server_db.ProjectCosttypeDBServer;
import com.neu.findme.server_web.HttpWebServer;
import com.neu.findme.utils.JsonParseUtils;
import com.neu.findme.utils.MyApplication;

/**
 * @author cxm
 *修改本地已上传记录，会更新对应的网络记录，反之亦然
 *注意：修改的数据源是从适配器的静态数组中根据列表position取出的，页面重绘时不可恢复
 *可以通过传递序列化数据来修复
 *2015-03-09 20:37:03
 */
public class PhotoRecChangeActivity extends Activity {
	@ViewInject(R.id.titleContentText)
	private EditText titleEditText;
	@ViewInject(R.id.locationContentText)
	private TextView locationEditText;
	@ViewInject(R.id.descriptionContentText)
	private EditText descriptionEditText;
	@ViewInject(R.id.affirmButton)
	private Button affirmButton;
	@ViewInject(R.id.cancleButton)
	private Button cancleButton;
	@ViewInject(R.id.backButton)
	private Button backButton;
	private int recordBeanId;
	// 修改工作组的Spinner
	@ViewInject(R.id.change_job_categary)
	private Spinner spinner;
	private ArrayList<String> projects = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	// 用于确定数据源的flag
	private String flag;
	private LocalRecordBean localRecordBean;
	private NetRecordBean netRecordBean;
	private BaseRecordBean baseRecordBean;
	// 上传相关
	private HttpWebServer webServer;
	private LocalRecDBServer localRecDBServer;
	private NetRecDBServer netRecDBServer;
	private ProjectCosttypeDBServer projectCosttypeDBServer;
	//回传相关
	private Intent intent;
	private final int RESULT_OK = 2;
	@OnClick({R.id.backButton,R.id.cancleButton})
	public void backBtnListener(View view){
		PhotoRecChangeActivity.this.finish();
	}
	@OnClick(R.id.affirmButton)
	public void affirmBtnListener(View view){
		baseRecordBean.setProject(spinner.getSelectedItem()+"");
		baseRecordBean.setDescription(descriptionEditText.getText()+"");
		baseRecordBean.setTitle(titleEditText.getText()+"");
		//如果只是本地未上传记录，只修改数据库和本地列表
		if (flag.equals("localFlag")&&!localRecordBean.getIsUploaded()) {
			try {
				localRecDBServer.updateOne(baseRecordBean.clone());
				for(LocalRecordBean bean:RecordLocalListAdapter.recordBeans){
					if(bean.getPhotoId().equals(baseRecordBean.getPhotoId())){
						bean.setProject(baseRecordBean.getProject());
						bean.setDescription(baseRecordBean.getDescription());
						bean.setTitle(baseRecordBean.getTitle());
					}
				}
				Toast.makeText(PhotoRecChangeActivity.this, MyApplication.getApplication().
						getString(R.string.change_success), Toast.LENGTH_SHORT).show();
				setResult();
				PhotoRecChangeActivity.this.finish();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(PhotoRecChangeActivity.this, MyApplication.getApplication().
						getString(R.string.change_failed), Toast.LENGTH_SHORT).show();
			}
		}else {
			webServer.updatePhotoInfo(baseRecordBean, changeRC);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photorecord_change);
		ViewUtils.inject(PhotoRecChangeActivity.this);
		webServer = new HttpWebServer();
		localRecDBServer = new LocalRecDBServer(this);
		netRecDBServer = new NetRecDBServer(this);
		projectCosttypeDBServer = new ProjectCosttypeDBServer(this);
		initViewData();
		titleEditText.requestFocus();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		webServer = new HttpWebServer();
		localRecDBServer = new LocalRecDBServer(this);
		netRecDBServer = new NetRecDBServer(this);
		projectCosttypeDBServer = new ProjectCosttypeDBServer(this);
		super.onResume();
	}
	//初始化数据
	private void initViewData(){
		projects = (ArrayList<String>) projectCosttypeDBServer.getAllProjects();
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, projects);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		flag = getIntent().getExtras().getString("flag");
		recordBeanId = getIntent().getExtras().getInt("recordBeanId");
		if (flag.equals("localFlag")) {
			localRecordBean = RecordLocalListAdapter.recordBeans.get(recordBeanId);
			baseRecordBean = localRecordBean;
		} else if (flag.equals("netFlag")) {
			netRecordBean = RecordNetListAdapter.recordBeans.get(recordBeanId);
			baseRecordBean = netRecordBean;
		}
		// 显示数据
		titleEditText.setText(baseRecordBean.getTitle());
		locationEditText.setText(baseRecordBean.getLocation());
		descriptionEditText.setText(baseRecordBean.getDescription());
		int position = projects.indexOf(baseRecordBean.getProject());
		spinner.setSelection(position);
	}
	//设置回传数据
	private void setResult(){
		intent = new Intent(PhotoRecChangeActivity.this,PhotoRecordDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("description", baseRecordBean.getDescription());
		bundle.putString("title", baseRecordBean.getTitle());
		bundle.putString("project", baseRecordBean.getProject());
		intent.putExtras(bundle);
		setResult(RESULT_OK, intent);
	}
	private RequestCallBack<String> changeRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			Toast.makeText(PhotoRecChangeActivity.this, MyApplication.getApplication().
					getString(R.string.change_failed), Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			if(JsonParseUtils.jsonToBoolean(arg0.result)){
				//更新数据库
				try {
					LocalRecordBean localRecordBean= baseRecordBean.clone();
					localRecDBServer.updateOne(localRecordBean);
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				netRecDBServer.updateOne(baseRecordBean);
				//更新本地列表数据
				for(LocalRecordBean bean:RecordLocalListAdapter.recordBeans){
					if(bean.getPhotoId().equals(baseRecordBean.getPhotoId())){
						bean.setProject(baseRecordBean.getProject());
						bean.setDescription(baseRecordBean.getDescription());
						bean.setTitle(baseRecordBean.getTitle());
					}
				}
				//更新网络列表数据
				for(NetRecordBean bean:RecordNetListAdapter.recordBeans){
					if(bean.getPhotoId().equals(baseRecordBean.getPhotoId())){
						bean.setProject(baseRecordBean.getProject());
						bean.setDescription(baseRecordBean.getDescription());
						bean.setTitle(baseRecordBean.getTitle());
					}
				}
				Toast.makeText(PhotoRecChangeActivity.this, MyApplication.getApplication().
						getString(R.string.change_success), Toast.LENGTH_SHORT).show();
				setResult();
				PhotoRecChangeActivity.this.finish();
			}else {
				Toast.makeText(PhotoRecChangeActivity.this, MyApplication.getApplication().
						getString(R.string.change_failed), Toast.LENGTH_SHORT).show();
			}
		}
	};
}
