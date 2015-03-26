package com.neu.findme.activity;

import java.text.DecimalFormat;
import java.util.List;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.neu.findme.R;
import com.neu.findme.adapter.MoneyRecordListAdapter;
import com.neu.findme.domain.BudgetBalance;
import com.neu.findme.domain.MoneyRecordBean;
import com.neu.findme.server_db.MoneyRecDBServer;
import com.neu.findme.server_db.ProjectCosttypeDBServer;
import com.neu.findme.server_web.HttpWebServer;
import com.neu.findme.userInterface.PullDownFreshInterface;
import com.neu.findme.userInterface.PullUpFreshInterface;
import com.neu.findme.utils.JsonParseUtils;
import com.neu.findme.utils.MyApplication;
import com.neu.findme.view.MyAlertDialog;
import com.neu.findme.view.PullToRefreshView;

/**
 * @author cxm
 * 财务管理界面，优先从数据库加载数据
 * 上下拉刷新分别根据列表前后端数据请求服务器，每次获取前十条记录，如果本地没有记录，加载服务器第一页的记录
 * 右上角刷新按钮作为同步按钮，点击后会刷新全部数据为服务器最新数据
 * 两个spinner数据根据用户选择，相互刷新。下部显示用户选择的工程-类型组合所对应的预算和余额。
 * 2015-03-09 19:25:31
 */
public class MoneyManageActivityTab2 extends Activity implements PullDownFreshInterface,PullUpFreshInterface {
	private final String ALL = MyApplication.getApplication().getString(R.string.spinnerItem_all);
	private MoneyRecDBServer moneyDBServer;
	private ProjectCosttypeDBServer pcDBServer;
	private HttpWebServer webServer;
	@ViewInject(R.id.listView)
	private ListView listView;
	@ViewInject(R.id.addButton)
	private Button addButton;
	//上下拉刷新相关
	@ViewInject(R.id.refreshBar)
	private PullToRefreshView pullRefreshBar;
	//用户id
	private String userId; 
	private MoneyRecordListAdapter listViewAdapter;
	//预算余额相关
	@ViewInject(R.id.project_budgetTextView)
 	private TextView budgetTextView;
	@ViewInject(R.id.project_balanceTextView)
	private TextView balanceTextView;
	//spinner相关
	@ViewInject(R.id.project_spinner)
	private Spinner projectSpinner;
	@ViewInject(R.id.type_spinner)
	private Spinner costtypeSpinner;
	private ArrayAdapter<String> projectAdapter;
	private ArrayAdapter<String> costtypeAdapter;
	private List<String> projects;
	private List<String> costtypes;
	//同步数据相关
	@ViewInject(R.id.refreshButton)
	private Button refreshButton;
	private ProgressDialog progressDialog = null;
	@OnClick(R.id.addButton)
	public void addButtonListener(View view){
		startActivity(new Intent(this,MoneyRecordEditActivity.class));
	}
	@OnItemClick(R.id.listView)
	public void listItemListener(AdapterView<?> parent, View view,int position, long id){
		Intent intent = new Intent(this,MoneyRecordDetailActivity.class);
		intent.putExtra("recordBeanId", (int) id);
		//将选中的财务记录序列化传递到下一个界面
		intent.putExtra("bean", MoneyRecordListAdapter.recordBeans.get(position));
		startActivity(intent);
	}
	@OnClick(R.id.refreshButton)
	public void refreshBtnListener(View view){
		syncData();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tab2_money_manage);
		ViewUtils.inject(this);//注入view和事件
		//初始化数据库
		moneyDBServer = new MoneyRecDBServer(this);
		pcDBServer = new ProjectCosttypeDBServer(this);
		webServer = new HttpWebServer();
		pullRefreshBar.PullDownParent = this;
		pullRefreshBar.PullUpParent = this;
		initListViewData();//初始化列表数据
		initSpinnerData();//初始化spinner的数据
		webServer.getBudgetBanlance(ALL, ALL, getBudgetRC);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//更新列表数据
		if(listViewAdapter!=null){
			listViewAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}
	// 点击返回键时的操作
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			final MyAlertDialog myAlertDialog = new MyAlertDialog(this);
			myAlertDialog.showWoodDialog(true);
			myAlertDialog.getDialog_title().setText(MyApplication.getApplication().getString(R.string.quitDialog_title));
			myAlertDialog.getDialog_msg().setText(MyApplication.getApplication().getString(R.string.quitDialog_content));
			myAlertDialog.getPosButton().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					getParent().finish();
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
		return super.onKeyDown(keyCode, event);
	}
	//列表上下拉刷新调用的方法
	@Override
	public void PullUpFresh() {
		// TODO Auto-generated method stub
		//刷新之后spinner归零
		projectSpinner.setSelection(0,true);
		costtypeSpinner.setSelection(0, true);
		if(MoneyRecordListAdapter.recordBeans.size()==0){
			webServer.getMoneyRecords(1, userId, getRecordBeansRC);
		}else {
			webServer.getMoneyRecordsBefore(MoneyRecordListAdapter.recordBeans.
					get(MoneyRecordListAdapter.recordBeans.size()-1).getTimeStr(), userId, pullUpRC);
		}
	}
	@Override
	public void PullDownFresh() {
		// TODO Auto-generated method stubÍ
		//刷新之后spinner归零
		projectSpinner.setSelection(0,true);
		costtypeSpinner.setSelection(0, true);
//		if(MoneyRecordListAdapter.recordBeans.size()==0){
			webServer.getMoneyRecords(1, userId, getRecordBeansRC);
//		}else {
//			webServer.getMoneyRecordsAfter(MoneyRecordListAdapter.recordBeans.get(0).getTimeStr(), userId, pullDownRC);
//		}
	}
	private void initListViewData(){
		//从数据库中读取，如果数据库没有记录，从服务器下载
		userId = MyApplication.get("userId")+"";
		List<MoneyRecordBean> list = (List<MoneyRecordBean>) moneyDBServer.queryTopNOrderBy(MoneyRecordBean.class, 10, "rec_time", true);
		if(list.size()>0){
			listViewAdapter = new MoneyRecordListAdapter(this, list);
			listView.setAdapter(listViewAdapter);
		}else {
			progressDialog = ProgressDialog.show(MoneyManageActivityTab2.this, MyApplication.getApplication().
					getString(R.string.progressDialog_load),MyApplication.getApplication().
					getString(R.string.progressDialog_loading), false, false);
			webServer.getMoneyRecords(1, userId, getRecordBeansRC);
		}
	}
	private void initSpinnerData(){
		//从数据库中读取全部的工程名和花费类型填入两个Spinner
		projects = pcDBServer.getAllProjects();
		projects.add(0,ALL);
		projectAdapter = new ArrayAdapter<String>(this, R.layout.view_myspinner, projects){
			@Override
			public View getDropDownView(int position, View convertView,ViewGroup parent) {
				View view = LayoutInflater.from(MoneyManageActivityTab2.this).inflate(R.layout.view_myspinner_item, new LinearLayout(MoneyManageActivityTab2.this));
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
		costtypes = pcDBServer.getAllCosttypes();
		costtypes.add(0,ALL);
		costtypeAdapter = new ArrayAdapter<String>(this, R.layout.view_myspinner, costtypes){
			@Override
			public View getDropDownView(int position, View convertView,ViewGroup parent) {
				View view = LayoutInflater.from(MoneyManageActivityTab2.this).inflate(R.layout.view_myspinner_item, new LinearLayout(MoneyManageActivityTab2.this));
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
		//防止第一次进去触发OnItemSelected
		projectSpinner.setSelection(0,true);
		costtypeSpinner.setSelection(0, true);
		projectSpinner.setOnItemSelectedListener(onItemSelectedListener);
		costtypeSpinner.setOnItemSelectedListener(onItemSelectedListener);
	}
	private void changeCosttype(){
		if(projectSpinner.getSelectedItem().toString().equals(ALL)){
			costtypeAdapter.clear();
			List<String> list = pcDBServer.getAllCosttypes();
			list.add(0, ALL);
			costtypeAdapter.addAll(list);
			costtypeAdapter.notifyDataSetChanged();
		}else {
			costtypeAdapter.clear();
			List<String> list = pcDBServer.getCosttypes(projectSpinner.getSelectedItem()+"");
			list.add(0, ALL);
			costtypeAdapter.addAll(list);
			costtypeAdapter.notifyDataSetChanged();
		}
	}
	//根据被选中的spinner数据从数据库更新list
	private void changeList(String project,String costtype){
		List<MoneyRecordBean> list;
		if(project.equals(ALL)&&costtype.equals(ALL)){
			list = moneyDBServer.queryAllOrderBy(MoneyRecordBean.class, "rec_time", true);
		}else if(project.equals(ALL)){
			list = moneyDBServer.queryByCosttype(costtype);
		}else if(costtype.equals(ALL)){
			list = moneyDBServer.queryByProject(project);
		}else {
			list = moneyDBServer.queryByProjectCosttype(project, costtype);
		}
		MoneyRecordListAdapter.recordBeans = list;
		listViewAdapter.notifyDataSetChanged();
	}
	//这个方法用于将财务记录同步成最新的服务器记录
	private void syncData(){
		progressDialog = ProgressDialog.show(MoneyManageActivityTab2.this, MyApplication.getApplication().
				getString(R.string.progressDialog_sync),MyApplication.getApplication().
				getString(R.string.progressDialog_syncing), false,true);
		webServer.getMoneyRecords(1, userId, syncDataRC);
	}
	//下拉时加载记录的web请求结果处理
		private RequestCallBack<String> pullDownRC = new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				pullRefreshBar.onHeaderRefreshComplete();
				Toast.makeText(MoneyManageActivityTab2.this, MyApplication.getApplication().
						getString(R.string.refresh_fail), Toast.LENGTH_SHORT).show();
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				pullRefreshBar.onHeaderRefreshComplete();
				List<MoneyRecordBean> list = (List<MoneyRecordBean>) JsonParseUtils.jsonToEntitylist(arg0.result, MoneyRecordBean.class);
				if(list.size()>0){
					MoneyRecordListAdapter.recordBeans.addAll(0, list);
					listViewAdapter.notifyDataSetChanged();
					moneyDBServer.addListOrUpdate(list);//加入数据库
				}else {
					Toast.makeText(MoneyManageActivityTab2.this, MyApplication.getApplication().
							getString(R.string.refresh_newest), Toast.LENGTH_SHORT).show();
				}
			}
		};
		//上拉时加载记录的web请求结果处理
		private RequestCallBack<String> pullUpRC = new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				pullRefreshBar.onFooterRefreshComplete();
				Toast.makeText(MoneyManageActivityTab2.this, MyApplication.getApplication().
						getString(R.string.refresh_fail), Toast.LENGTH_SHORT).show();
			}
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				pullRefreshBar.onFooterRefreshComplete();
				List<MoneyRecordBean> list = (List<MoneyRecordBean>) JsonParseUtils.jsonToEntitylist(arg0.result, MoneyRecordBean.class);
				if(list.size()>0){
					MoneyRecordListAdapter.recordBeans.addAll(MoneyRecordListAdapter.recordBeans.size(), list);
					listViewAdapter.notifyDataSetChanged();
					moneyDBServer.addListOrUpdate(list);//加入数据库
				}else {
					Toast.makeText(MoneyManageActivityTab2.this, MyApplication.getApplication().
							getString(R.string.refresh_noMoreOld), Toast.LENGTH_SHORT).show();
				}
			}
		};
	//加载服务器第一页的记录web请求结果处理
	private RequestCallBack<String> getRecordBeansRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			pullRefreshBar.onHeaderRefreshComplete();
		}
		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			pullRefreshBar.onHeaderRefreshComplete();
			List<MoneyRecordBean> list = (List<MoneyRecordBean>) JsonParseUtils.jsonToEntitylist(arg0.result, MoneyRecordBean.class);
			if(list.size()>0){
				listViewAdapter = new MoneyRecordListAdapter(MoneyManageActivityTab2.this, list);
				listView.setAdapter(listViewAdapter);
				moneyDBServer.addListOrUpdate(list);//加入数据库
				if(progressDialog != null)
					progressDialog.dismiss();
			}
		}
	};
	//获取财务预算和余额的web请求结果处理
	private RequestCallBack<String> getBudgetRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			Log.e("cxmcxm", arg1);
			budgetTextView.setText("");
			balanceTextView.setText("");
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			List<BudgetBalance> list = (List<BudgetBalance>) JsonParseUtils.jsonToEntitylist(arg0.result, BudgetBalance.class);
			if(list.size()>0){
				if(list.get(0).getBudget().equals("0")){//防止出现.00显示
					budgetTextView.setText(MyApplication.getApplication().getString(R.string.commonVocabulary_rmb)+list.get(0).getBudget());
					}else {
					budgetTextView.setText(MyApplication.getApplication().getString(R.string.commonVocabulary_rmb)+
							new DecimalFormat(".00").format(Double.parseDouble(list.get(0).getBudget())));
					}
				if(list.get(0).getBalance().equals("0")){//防止出现.00显示
					balanceTextView.setText(MyApplication.getApplication().getString(R.string.commonVocabulary_rmb)+list.get(0).getBalance());
				}else {
					balanceTextView.setText(MyApplication.getApplication().getString(R.string.commonVocabulary_rmb)+ 
							new DecimalFormat(".00").format(Double.parseDouble(list.get(0).getBalance())));
				}
			}else {
				budgetTextView.setText("");
				balanceTextView.setText("");
			}
		}
	};
	//同步数据的web请求结果处理
	private RequestCallBack<String> syncDataRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			Toast.makeText(MoneyManageActivityTab2.this, MyApplication.getApplication().
					getString(R.string.syncData_fail), Toast.LENGTH_SHORT).show();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			List<MoneyRecordBean> list = (List<MoneyRecordBean>) JsonParseUtils.jsonToEntitylist(arg0.result, MoneyRecordBean.class);
			if(list.size()>0){
				//恢复spinner到初始
				projectSpinner.setSelection(0, true);
				costtypeSpinner.setSelection(0,true);
				//获取预算信息
				webServer.getBudgetBanlance(ALL, ALL,getBudgetRC);
				//更新列表显示的数据
				MoneyRecordListAdapter.recordBeans.clear();
				MoneyRecordListAdapter.recordBeans.addAll(list);
				listViewAdapter.notifyDataSetChanged();
				//清空老数据，更新为旧数据
				moneyDBServer.deleteAll(MoneyRecordBean.class);
				moneyDBServer.addList(list);
				progressDialog.dismiss();
				Toast.makeText(MoneyManageActivityTab2.this, MyApplication.getApplication().
						getString(R.string.syncData_success), Toast.LENGTH_SHORT).show();
			}else {
				progressDialog.dismiss();
				Toast.makeText(MoneyManageActivityTab2.this, MyApplication.getApplication().
						getString(R.string.syncData_fail), Toast.LENGTH_SHORT).show();
			}
		}
	};
	//spinner选择监听器
	private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			webServer.getBudgetBanlance(projectSpinner.getSelectedItem()+"", costtypeSpinner.getSelectedItem()+"", getBudgetRC);
			changeList(projectSpinner.getSelectedItem()+"", costtypeSpinner.getSelectedItem()+"");
			changeCosttype();
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};
}
