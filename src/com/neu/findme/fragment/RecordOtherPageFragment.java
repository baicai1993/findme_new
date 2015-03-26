package com.neu.findme.fragment;


import java.io.File;
import java.util.Calendar;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.neu.findme.R;
import com.neu.findme.activity.PhotoRecordDetailActivity;
import com.neu.findme.adapter.RecordNetListAdapter;
import com.neu.findme.adapter.RecordOtherListAdapter;
import com.neu.findme.domain.OtherRecordBean;
import com.neu.findme.server_db.OtherRecDBServer;
import com.neu.findme.server_web.HttpWebServer;
import com.neu.findme.userInterface.PullDownFreshInterface;
import com.neu.findme.userInterface.PullUpFreshInterface;
import com.neu.findme.utils.FileUtil;
import com.neu.findme.utils.JsonParseUtils;
import com.neu.findme.utils.MyApplication;
import com.neu.findme.view.PullToRefreshView;

/**
 * @author cxm
 *监控照片记录管理界面，可以查看所有对自己可见的用户记录列表
 *下拉可以加载最新上传的记录，上拉加载历史记录
 *2015-03-11 15:39:53
 */
public class RecordOtherPageFragment extends Fragment implements PullDownFreshInterface,PullUpFreshInterface{
	private OtherRecDBServer dbServer;
	private HttpWebServer webServer;
	@ViewInject(R.id.listView)
	private ListView listView;
	//上下拉刷新相关
	@ViewInject(R.id.pullRefreshBar)
	private PullToRefreshView pullRefreshBar;
	public static RecordOtherListAdapter listViewadapter;
	//时间搜索相关
	@ViewInject(R.id.timeSearchEditText)
	private TextView timeSearchEditText;
	@ViewInject(R.id.timeSearchLinearLayout)
	private LinearLayout timeSearchLinearLayout;
	@ViewInject(R.id.timeSearchProgressBar)
	private ProgressBar timeSearchProgressBar;
	//用户id
	private String userId;
	@OnItemClick(R.id.listView)
	public void listItemListener(AdapterView<?> parent, View view,int position, long id){
		Intent intent = new Intent(getActivity(),PhotoRecordDetailActivity.class);
		intent.putExtra("recordBeanId", position);
		intent.putExtra("bean", RecordOtherListAdapter.recordBeans.get(position));
		intent.putExtra("flag", "otherFlag");
		startActivity(intent);
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_record_other_page, container,false);
		ViewUtils.inject(this,view);//注入view和事件
		dbServer = new OtherRecDBServer(getActivity());
		webServer = new HttpWebServer();
		pullRefreshBar.PullDownParent = this;
		pullRefreshBar.PullUpParent = this;
		initListViewData();//初始化列表数据
		listView.setAdapter(listViewadapter);
		timeSearchEditText.setOnTouchListener((OnTouchListener) new TimeSearchListener());
		return view;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		listViewadapter = null;
		super.onDestroy();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if(listViewadapter!=null){
			listViewadapter.notifyDataSetChanged();
		}
		super.onResume();
	}
	private void initListViewData(){
		userId = MyApplication.get("userId")+"";
		//从数据库中读取，如果数据库没有记录，从服务器下载
		List<OtherRecordBean> list = (List<OtherRecordBean>) dbServer.queryTopNOrderBy(OtherRecordBean.class, 10, "upLoadedTime", true);
		if(list.size()>0){
			listViewadapter = new RecordOtherListAdapter(getActivity(), list);
		}else {
			webServer.getOtherPhotoBeans(1, userId, getRecordBeansRC);
		}
	}
	private void downloadPhoto(OtherRecordBean bean){
		bean.setSdUri(FileUtil.OTHER_PATH+userId+File.separator+bean.getPhotoId());
		if(!(new File(bean.getSdUri().toString().substring(bean.getSdUri().toString().indexOf(":")+1)).exists())){
			webServer.downloadPhoto(downloadPhotoRC, bean.getPhotoId(), bean.getSdUri());
		}
	}
	//按照时间搜索并显示记录
	private void TimeSearch(final String timeStr) {
		timeSearchProgressBar.setVisibility(View.VISIBLE);
		timeSearchProgressBar.setProgress(0);
//		progressDialog = ProgressDialog.show(getActivity(),
//				MyApplication.getApplication().getString(R.string.progressDialog_load),
//				MyApplication.getApplication().getString(R.string.progressDialog_loading), false,false);
		webServer.getOtherPhotoBeansBefore(timeStr, userId, timeSearchRC);
	}
	//上下拉刷新时调用的方法
	@Override
	public void PullUpFresh() {
		// TODO Auto-generated method stub
		if(RecordOtherListAdapter.recordBeans.size()==0){
			webServer.getOtherPhotoBeans(1, userId, getRecordBeansRC);
		}else {
			webServer.getOtherPhotoBeansBefore(RecordOtherListAdapter.recordBeans.
					get(RecordOtherListAdapter.recordBeans.size()-1).getUpLoadedTime(), userId, pullUpRC);
		}
	}
	@Override
	public void PullDownFresh() {
		// TODO Auto-generated method stub
		if(RecordOtherListAdapter.recordBeans.size()==0){
			webServer.getOtherPhotoBeans(1, userId, getRecordBeansRC);
		}else {
			webServer.getOtherPhotoBeansAfter(RecordOtherListAdapter.recordBeans.get(0).getUpLoadedTime(), userId, pullDownRC);
		}
	}
	//加载服务器第一页的记录
	private RequestCallBack<String> getRecordBeansRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			pullRefreshBar.onHeaderRefreshComplete();
		}
		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			pullRefreshBar.onHeaderRefreshComplete();
			List<OtherRecordBean> list = (List<OtherRecordBean>) JsonParseUtils.jsonToEntitylist(arg0.result, OtherRecordBean.class);
			if(list.size()>0){
				//更新列表内数据的sdUri
				for(OtherRecordBean bean:list){
					downloadPhoto(bean);
				}
				listViewadapter = new RecordOtherListAdapter(getActivity(), list);
				listView.setAdapter(listViewadapter);
				dbServer.addListOrUpdate(list);//加入数据库
			}
		}
	};
	//下拉时加载记录
	private RequestCallBack<String> pullDownRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			pullRefreshBar.onHeaderRefreshComplete();
			Toast.makeText(getActivity(), MyApplication.getApplication().
					getString(R.string.refresh_fail), Toast.LENGTH_SHORT).show();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			pullRefreshBar.onHeaderRefreshComplete();
			List<OtherRecordBean> list = (List<OtherRecordBean>) JsonParseUtils.jsonToEntitylist(arg0.result, OtherRecordBean.class);
			//更新每条记录的sdUri
			if(list.size()>0){
				RecordOtherListAdapter.recordBeans.addAll(0, list);
				listViewadapter.notifyDataSetChanged();
			for(OtherRecordBean bean:RecordOtherListAdapter.recordBeans){
				downloadPhoto(bean);
			}
				dbServer.addListOrUpdate(list);//加入数据库
			}else {
				Toast.makeText(getActivity(), MyApplication.getApplication().
						getString(R.string.refresh_newest), Toast.LENGTH_SHORT).show();
			}
		}
	};
	//上拉时加载记录
	private RequestCallBack<String> pullUpRC = new RequestCallBack<String>() {
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			pullRefreshBar.onFooterRefreshComplete();
			Toast.makeText(getActivity(), MyApplication.getApplication().
					getString(R.string.refresh_fail), Toast.LENGTH_SHORT).show();
		}
		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			pullRefreshBar.onFooterRefreshComplete();
			List<OtherRecordBean> list = (List<OtherRecordBean>) JsonParseUtils.jsonToEntitylist(arg0.result, OtherRecordBean.class);
			if(list.size()>0){
				RecordOtherListAdapter.recordBeans.addAll(RecordOtherListAdapter.recordBeans.size(), list);
				listViewadapter.notifyDataSetChanged();
			for(OtherRecordBean bean:RecordOtherListAdapter.recordBeans){
				downloadPhoto(bean);
			}
				dbServer.addListOrUpdate(list);//加入数据库
			}else {
				Toast.makeText(getActivity(), MyApplication.getApplication().
						getString(R.string.refresh_noMoreOld), Toast.LENGTH_SHORT).show();
			}
		}
	};
	//时间搜索时加载记录
	private RequestCallBack<String> timeSearchRC = new RequestCallBack<String>() {
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			timeSearchProgressBar.setVisibility(View.INVISIBLE);
			Toast.makeText(getActivity(), MyApplication.getApplication().
					getString(R.string.timesearch_fail), Toast.LENGTH_SHORT).show();
		}
		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			timeSearchProgressBar.setVisibility(View.INVISIBLE);
			List<OtherRecordBean> list = (List<OtherRecordBean>) JsonParseUtils.jsonToEntitylist(arg0.result, OtherRecordBean.class);
			if(list.size()>0){
				RecordOtherListAdapter.recordBeans.clear();
				RecordOtherListAdapter.recordBeans.addAll(list);
				listViewadapter.notifyDataSetChanged();
			for(OtherRecordBean bean:RecordOtherListAdapter.recordBeans){
				downloadPhoto(bean);
			}
				dbServer.addListOrUpdate(list);//加入数据库
			}else {
				Toast.makeText(getActivity(), MyApplication.getApplication().
						getString(R.string.timesearch_fail), Toast.LENGTH_SHORT).show();
			}
		}
	};
	//下载图片
	private RequestCallBack<File> downloadPhotoRC = new RequestCallBack<File>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			Log.e("cxmcxm", arg1);
		}

		@Override
		public void onSuccess(ResponseInfo<File> arg0) {
			// TODO Auto-generated method stub
			listViewadapter.notifyDataSetChanged();
		}
	};
	@SuppressLint("ClickableViewAccessibility") private class TimeSearchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				View view = View.inflate(getActivity(),
						R.layout.view_datetime_picker, null);
				final DatePicker datePicker = (DatePicker) view
						.findViewById(R.id.datePicker1);
				datePicker.setCalendarViewShown(false);
				final TimePicker timePicker = (android.widget.TimePicker) view
						.findViewById(R.id.timePicker1);
				builder.setView(view);
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				datePicker.init(cal.get(Calendar.YEAR),
						cal.get(Calendar.MONTH),
						cal.get(Calendar.DAY_OF_MONTH), null);
				timePicker.setIs24HourView(true);
				timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
				timePicker.setCurrentMinute(Calendar.MINUTE);
				if (v.getId() == R.id.timeSearchEditText) {
					final int inType = timeSearchEditText.getInputType();
					timeSearchEditText.setInputType(InputType.TYPE_NULL);
					timeSearchEditText.onTouchEvent(event);
					timeSearchEditText.setInputType(inType);
					// timeSearchEditText.setSelection(timeSearchEditText
					// .getText().length());
					builder.setTitle(MyApplication.getApplication().getString(
							R.string.timePickerDialog_title));
					builder.setPositiveButton(MyApplication.getApplication()
							.getString(R.string.timePickerDialog_content),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									StringBuffer sb = new StringBuffer();
									StringBuffer sb2 = new StringBuffer();
									sb.append(String.format("%d-%02d-%02d",
											datePicker.getYear(),
											datePicker.getMonth() + 1,
											datePicker.getDayOfMonth()));
									sb2.append(String.format("%d%02d%02d",
											datePicker.getYear(),
											datePicker.getMonth() + 1,
											datePicker.getDayOfMonth()));
									sb.append(" ");
									sb.append(timePicker.getCurrentHour())
											.append(":")
											.append(timePicker
													.getCurrentMinute());
									sb2.append(timePicker.getCurrentHour())
											.append(timePicker
													.getCurrentMinute());
									Calendar c = Calendar.getInstance();// 获得系统当前日期
									int second = c.get(Calendar.SECOND);
									sb.append(":" + String.valueOf(second));
									sb2.append(String.valueOf(second));
									timeSearchEditText.setText(sb);
									if (sb != null || !"".equals(sb)) {
										// 启动上拉刷新的步骤
										TimeSearch(sb2.toString());
									}
									dialog.cancel();
								}
							});
				}
				Dialog dialog = builder.create();
				dialog.show();
			}
			return true;
		}
	}
}
