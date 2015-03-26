package com.neu.findme.fragment;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Matrix;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.neu.findme.R;
import com.neu.findme.activity.MainActivity;
import com.neu.findme.adapter.RecordNetListAdapter;
import com.neu.findme.adapter.RecordOtherListAdapter;
import com.neu.findme.adapter.RecordViewPagerAdapter;
import com.neu.findme.domain.NetRecordBean;
import com.neu.findme.domain.OtherRecordBean;
import com.neu.findme.server_db.NetRecDBServer;
import com.neu.findme.server_db.OtherRecDBServer;
import com.neu.findme.server_web.HttpWebServer;
import com.neu.findme.utils.FileUtil;
import com.neu.findme.utils.JsonParseUtils;
import com.neu.findme.utils.MyApplication;
import com.neu.findme.utils.MyCookie;
/**
 * @author cxm
 *记录管理fragment 包含本地、网络、监控三个页卡
 *提供拍照功能
 *提供地理定位功能
 *2015-03-11 15:38:06
 */
@SuppressLint("SimpleDateFormat") public class RecordManageFragment extends Fragment implements AMapLocationListener{
	@ViewInject(R.id.viewPager)
	private ViewPager viewPager;
	@ViewInject(R.id.cursor)
	private ImageView imageView;
	private RecordViewPagerAdapter viewPagerAdapter;
	private View view;
	//标题栏相关
	@ViewInject(R.id.showDrawerButton)
	private Button showDrawerButton;
	@ViewInject(R.id.refreshButton)
	private Button refreshButton;
	//拍照相关
	@ViewInject(R.id.camera)
	private Button cameraButton;
	private static String photoName;
	//定位相关
	@ViewInject(R.id.localButton)
	private Button locateButton;
	@ViewInject(R.id.hidebottomButton)
	private Button hittenButton;// 隐藏下部地址栏按钮
	@ViewInject(R.id.showBottomButton)
	private Button showBottomButton;// 显示下部地址栏按钮
	@ViewInject(R.id.local_buttom_Layout)
	private RelativeLayout localBottomLayout;// 下部地址栏
	@ViewInject(R.id.localText)
	private TextView localView;//显示地理位置的文本框
	@ViewInject(R.id.viewPagerLinearLayout)
	private LinearLayout viewPagerLinearLayout;
	@ResInject(id = R.string.location_timeout, type = ResType.String)
	private static String locationStr;//位置信息
	private static float latitude;// 经度
	private static float longitude;// 纬度
	private LocationManagerProxy aMapLocManager = null;//定位委托对象
	//动画相关
	private LayoutParams para;
	private int bmpW;// 动画图片宽度
	private int currIndex = 0;// 当前页卡编号
	private int offset = 0;// 动画图片偏移量
	//页卡标题
	@ViewInject(R.id.title1)
	private TextView title1;
	@ViewInject(R.id.title2)
	private TextView title2;
	@ViewInject(R.id.title3)
	private TextView title3;
	//同步数据相关
	private ProgressDialog progressDialog = null;
	private HttpWebServer webServer;
	private NetRecDBServer netRecDBServer;
	private OtherRecDBServer otherRecDBServer;
	private String userId;
	//按钮监听方法
	@OnClick(R.id.localButton)
	public void localButtonListenner(View view){
		localView.setText(MyApplication.getApplication().getString(R.string.location_locating));
		getNewLocation();
	}
	@OnClick(R.id.hidebottomButton)
	public void hidebottomButtonListenner(View view){
		localBottomLayout.setVisibility(View.GONE);
		showBottomButton.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(5, 5, 5, 5);
		viewPagerLinearLayout.setLayoutParams(lp);
	}
	@OnClick(R.id.showBottomButton)
	public void showBottomButtonListenner(View view){
		showBottomButton.setVisibility(View.INVISIBLE);
		localBottomLayout.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		lp.setMargins(5, 5, 5, 80);
		viewPagerLinearLayout.setLayoutParams(lp);
		localView.requestFocus();// 给跑马灯焦点
	}
	@OnClick(R.id.camera)
	public void cameraButtonListenner(View view){
		if(!localView.getText().equals("")){
			takePhoto();
		}else {
			Toast.makeText(getActivity(), MyApplication.getApplication().getString(R.string.location_exception), Toast.LENGTH_SHORT).show();
		}
	}
	@OnClick(R.id.showDrawerButton)
	public void drawerBtnListener(View view){
		MainActivity.OpenDrawer();
	}
	@OnClick(R.id.refreshButton)
	public void refreshBtnListener(View view){
		syncData();//数据同步
	}
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	    view = inflater.inflate(R.layout.fragment_record_manage, container,false);
		ViewUtils.inject(this,view);//注入view和事件
		webServer = new HttpWebServer();
		netRecDBServer = new NetRecDBServer(getActivity());
		otherRecDBServer = new OtherRecDBServer(getActivity());
		userId = MyApplication.get("userId")+"";
		InitImageView();// 初始化滑动图片
		InitTextView();// 初始化标题
		getNewLocation();//启动定位
		userId = MyApplication.get("userId")+"";
		//适配页卡
		viewPagerAdapter = new RecordViewPagerAdapter(getChildFragmentManager());
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setOffscreenPageLimit(3);//缓存3页
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if(viewPagerAdapter!=null){
			viewPagerAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}
	private void InitImageView() {
		// 设置布局
		para = imageView.getLayoutParams();
		// 获得滑动图片的宽度
		bmpW = para.width;
		// 获得分辨率信息
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 获得屏幕宽度
		int screenW = dm.widthPixels;
		// 初始化图片偏移量
		offset = (screenW / 8);
		Matrix matrix = new Matrix();
		// 设置背景图片初始位置，动画实现
		Animation animation;
		animation = new TranslateAnimation(0, (offset*3 - bmpW / 2), 0, 0);
		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(300);
		imageView.startAnimation(animation);
		// 设置背景图片初始位置，图片平移
		// 不知道为什么不起作用
		matrix.postTranslate((offset - bmpW / 4), 0);
		imageView.setImageMatrix(matrix);
	}
	private void InitTextView() {
		// 获得控件操作权
		title1 = (TextView) view.findViewById(R.id.title1);
		title2 = (TextView) view.findViewById(R.id.title2);
		title3 = (TextView) view.findViewById(R.id.title3);
		// 添加监听器
		title1.setOnClickListener(new MyOnClickListener(0));
		title2.setOnClickListener(new MyOnClickListener(1));
		title3.setOnClickListener(new MyOnClickListener(2));
	}
	public static float getLatitude() {
		return latitude;
	}
	public static float getLongitude() {
		return longitude;
	}
	// 高德地理位置解析
	@SuppressWarnings("deprecation")
	private void getNewLocation() {
		locationStr = MyApplication.getApplication().getString(R.string.location_timeout);
		updateUIHandler.postDelayed(updateUIRunnable, 30000);// 30秒后定位超时
		if(aMapLocManager!=null){
			//销毁原来的定位者，防止多个manager一起定位
			aMapLocManager.destory();
		}
		aMapLocManager = LocationManagerProxy.getInstance(getActivity());
		aMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);
	}
	// 给照片唯一命名
	private String createPhotoName() {
		String fileName = "";
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("_yyyyMMddHHmmss");
		fileName = dateFormat.format(date) + ".jpg";
		fileName = userId+ fileName;
		return fileName;
	}
	// 调用照相机
	public void takePhoto() {
		photoName = createPhotoName();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 根据文件地址创建文件目录
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File folderFile = new File(FileUtil.RECORD_PATH+userId);
			if (!folderFile.exists()){
				folderFile.mkdirs();
			}
			//如果时间重复，重新创建一个文件
			File file = new File(folderFile, photoName);
			if (file.exists()) {
				photoName = createPhotoName();
				file = new File(folderFile, photoName);
			}
			// 把文件地址转换成Uri格式
			Uri uri = Uri.fromFile(file);
			// 设置系统相机拍摄照片完成后图片文件的存放地址
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			//"photoName"和"locationStr"需要持久化以便MainActivity使用
			MyCookie.putString("photoName", photoName);
			MyCookie.putString("locationStr", locationStr);
//			MyApplication.put("photoName", photoName);
//			MyApplication.put("locationStr", locationStr);
			//onActivityResult方法实现在MainActivity内，用TabHost控件引起的不便
			getActivity().getParent().startActivityForResult(intent, 1);
		} else {
			Toast.makeText(getActivity(),MyApplication.getApplication().getString(R.string.takePhoto_notFoundStorage), Toast.LENGTH_SHORT).show();
		}
	}
	//这个方法用于将网络和监控的记录同步成最新的服务器记录
	private void syncData(){
		progressDialog = ProgressDialog.show(getActivity(), MyApplication.getApplication().
				getString(R.string.progressDialog_sync),MyApplication.getApplication().
				getString(R.string.progressDialog_syncing), false, true);
		webServer.getNetPhotoRecords(1, userId, syncNetDataRC);
	}

	//高德定位相关回调方法
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLocationChanged(AMapLocation location) {
		// TODO Auto-generated method stub
		if (location != null) {
			String desc = MyApplication.getApplication().getString(R.string.location_locating);
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				desc = locBundle.getString("desc");
			}
			locationStr = desc;
			//更新地理位置显示
			if(!desc.equals("")&&!desc.equals(MyApplication.getApplication().getString(R.string.location_locating))){
				updateUIHandler.post(updateUIRunnable);
			}
			//设置经纬度信息
			RecordManageFragment.latitude = (float) location.getLatitude();
			RecordManageFragment.longitude = (float) location.getLongitude();
		}
	}
	private class MyOnClickListener implements OnClickListener {
		// 当我们点击标题的时候我们就会触发这个方法
		private int index = 0;
		// 传进来的参数初始化
		public MyOnClickListener(int i) {
			index = i;
		}
		@Override
		public void onClick(View v) {
			// 当我们点击到这个控件的时候调用
			viewPager.setCurrentItem(index);
		}
	}
	// 更改页卡
	public class MyOnPageChangeListener implements OnPageChangeListener {
					// 监听
					int one = offset;// 页卡1 -> 页卡2 偏移量
					@Override
					public void onPageScrollStateChanged(int arg0) {
					}
					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
					}
					@Override
					public void onPageSelected(int arg0) {
						// 刷新adapter
						if (viewPagerAdapter != null) {
							viewPagerAdapter.notifyDataSetChanged();
						}
						// 动画从B点(x+fromXDelta, y+fromYDelta)点移动到C 点(x+toXDelta,y+toYDelta)点
						Animation animation = new TranslateAnimation((one
								* (currIndex * 2 + 3) - bmpW / 2),
								(one * (arg0 * 2 + 3) - bmpW / 2), 0, 0);
						currIndex = arg0;
						animation.setFillAfter(true);// True:图片停在动画结束位置
						animation.setDuration(300);
						imageView.startAnimation(animation);
						switch (viewPager.getCurrentItem()) {
						case 0:
							title1.setTextColor(getResources().getColor(R.color.brown));
							title2.setTextColor(getResources().getColor(R.color.brown));
							title3.setTextColor(getResources().getColor(R.color.brown));
							break;
						case 1:
							title1.setTextColor(getResources().getColor(R.color.brown));
							title2.setTextColor(getResources().getColor(R.color.brown));
							title3.setTextColor(getResources().getColor(R.color.brown));
							break;
						case 2:
							title1.setTextColor(getResources().getColor(R.color.brown));
							title2.setTextColor(getResources().getColor(R.color.brown));
							title3.setTextColor(getResources().getColor(R.color.brown));
							break;
						default:
							break;
						}
					}
				}

	private RequestCallBack<String> syncNetDataRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			Toast.makeText(getActivity(), MyApplication.getApplication().
					getString(R.string.syncNetData_fail), Toast.LENGTH_SHORT).show();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			List<NetRecordBean> list = (List<NetRecordBean>) JsonParseUtils.jsonToEntitylist(arg0.result, NetRecordBean.class);
			if(list.size()>0){
				//更新列表内显示的数据
				RecordNetListAdapter.recordBeans.clear();
				RecordNetListAdapter.recordBeans.addAll(list);
				RecordNetPageFragment.listViewAdapter.notifyDataSetChanged();
				//更新列表内数据的sdUri,下载未下载的图片
				for(NetRecordBean bean:list){
					bean.setSdUri(FileUtil.NET_PATH+userId+File.separator+bean.getPhotoId());
					if(!(new File(bean.getSdUri().toString().substring(bean.getSdUri().toString().indexOf(":")+1)).exists())){
						webServer.downloadPhoto(downloadPhotoRC, bean.getPhotoId(), bean.getSdUri());
					}
				}
				netRecDBServer.addListOrUpdate(list);//更新数据库
				//网络同步成功,开始同步监控数据
				webServer.getOtherPhotoBeans(1, userId, syncOtherDataRC);
			}else {
				Toast.makeText(getActivity(), MyApplication.getApplication().
						getString(R.string.syncNetData_fail), Toast.LENGTH_SHORT).show();
			}
		}
	};
	private RequestCallBack<String> syncOtherDataRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			List<OtherRecordBean> list = (List<OtherRecordBean>) JsonParseUtils.jsonToEntitylist(arg0.result, OtherRecordBean.class);
			if(list.size()>0){
				//更新列表内显示的数据
				RecordOtherListAdapter.recordBeans.clear();
				RecordOtherListAdapter.recordBeans.addAll(list);
				RecordOtherPageFragment.listViewadapter.notifyDataSetChanged();
				//更新列表内数据的sdUri,并下载未下载的图片
				for(OtherRecordBean bean:list){
					bean.setSdUri(FileUtil.OTHER_PATH+userId+File.separator+bean.getPhotoId());
					if(!(new File(bean.getSdUri().toString().substring(bean.getSdUri().toString().indexOf(":")+1)).exists())){
						webServer.downloadPhoto(downloadPhotoRC, bean.getPhotoId(), bean.getSdUri());
					}
				}
				otherRecDBServer.addListOrUpdate(list);//更新数据库
				progressDialog.dismiss();
				Toast.makeText(getActivity(), MyApplication.getApplication().
						getString(R.string.syncData_success), Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(getActivity(), MyApplication.getApplication().
						getString(R.string.syncOtherData_fail), Toast.LENGTH_SHORT).show();
			}
		}
	};
	private RequestCallBack<File> downloadPhotoRC = new RequestCallBack<File>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			Log.e("cxmcxm", arg1);
		}

		@Override
		public void onSuccess(ResponseInfo<File> arg0) {
			// TODO Auto-generated method stub
			viewPagerAdapter.notifyDataSetChanged();
		}
	};
	Runnable updateUIRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 如果定位到新的地理位置，下部显示地理信息
			if (locationStr!=null&&!locationStr.equals("")&&!localView.getText().equals(locationStr)) {
				localView.setText(locationStr);
				localView.requestFocus();// 给跑马灯焦点
			}
		}
	};
	private static Handler updateUIHandler = new Handler(){};
	
}
