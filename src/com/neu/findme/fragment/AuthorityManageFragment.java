package com.neu.findme.fragment;

import java.io.File;
import java.util.List;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.neu.findme.R;
import com.neu.findme.activity.MainActivity;
import com.neu.findme.adapter.AuthorityViewPagerAdapter;
import com.neu.findme.adapter.CanSeeMeListAdapter;
import com.neu.findme.adapter.ICanSeeListAdapter;
import com.neu.findme.domain.NetRecordBean;
import com.neu.findme.domain.UserBean;
import com.neu.findme.server_db.UserDBServer;
import com.neu.findme.server_web.HttpWebServer;
import com.neu.findme.utils.FileUtil;
import com.neu.findme.utils.JsonParseUtils;
import com.neu.findme.utils.MyApplication;
import com.neu.findme.utils.MyCookie;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author cxm 权限管理fragment，负责权限信息的初始化工作，持有CanSeeMe和ICanSee两个Fragment列表的适配器对象
 *         包含一个ViewPager 2015-03-11 15:33:47
 */
public class AuthorityManageFragment extends Fragment {
	private ViewPager viewPager;
	private AuthorityViewPagerAdapter viewPagerAdapter;
	private ImageView imageView;
	private LayoutParams para;
	private View view;
	private int bmpW;// 动画图片宽度
	private int currIndex = 0;// 当前页卡编号
	private int offset = 0;// 动画图片偏移量
	// 展开抽屉的按钮
	@ViewInject(R.id.showDrawerButton)
	private Button showDrawerButton;
	// 页卡标题
	private TextView title1;
	private TextView title2;
	// 同步相关
	@ViewInject(R.id.refreshButton)
	private Button refreshButton;
	private ProgressDialog progressDialog = null;
	// 用于初始化两个权限页卡的数据
	public static ICanSeeListAdapter iCanSeeListAdapter = new ICanSeeListAdapter(
			MyApplication.getApplication(), ICanSeeListAdapter.userBeans);
	public static CanSeeMeListAdapter canSeeMeListAdapter = new CanSeeMeListAdapter(
			MyApplication.getApplication(), CanSeeMeListAdapter.userBeans);
	private UserDBServer userDBServer;
	private HttpWebServer webServer;
	private String userId = MyApplication.get("userId") + "";

	@OnClick(R.id.showDrawerButton)
	public void showBtnListener(View view) {
		MainActivity.OpenDrawer();
	}

	@OnClick(R.id.refreshButton)
	public void refreshBtnListener(View view) {
		syncData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_authority_manage, container,
				false);
		ViewUtils.inject(this, view);
		userDBServer = new UserDBServer(getActivity());
		webServer = new HttpWebServer();
		viewPager = (ViewPager) view.findViewById(R.id.viewPager);
		// 初始化滑动图片
		initImageView();
		// 初始化标题
		initTextView();
		initAuthorityData();
		viewPagerAdapter = new AuthorityViewPagerAdapter(
				getChildFragmentManager());
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		return view;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		iCanSeeListAdapter = null;
		canSeeMeListAdapter = null;
		super.onDestroy();
	}

	private void initAuthorityData() {
		if (userDBServer.queryAll(UserBean.class).size() > 0) {
			// 直接通过数据库初始化两个页卡的数据源
			CanSeeMeListAdapter.userBeans = userDBServer
					.queryAll(UserBean.class);
			ICanSeeListAdapter.userBeans = userDBServer.getICanSeeUsers();
		} else {
			progressDialog = ProgressDialog.show(
					getActivity(),
					MyApplication.getApplication().getString(
							R.string.progressDialog_load),
					MyApplication.getApplication().getString(
							R.string.progressDialog_loading), false, false);
			webServer.getAllUsers(userId, getAllUsersRC);
		}
	}

	private void initImageView() {
		// 设置背景的偏移量
		imageView = (ImageView) view.findViewById(R.id.cursor);
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
		offset = (screenW / 4);
		Matrix matrix = new Matrix();

		// 设置背景图片初始位置，动画实现
		Animation animation;
		animation = new TranslateAnimation(0, (offset - bmpW / 2), 0, 0);
		animation.setFillAfter(true);// True:图片停在动画结束位置
		animation.setDuration(300);
		imageView.startAnimation(animation);

		// 设置背景图片初始位置，图片平移
		// 不知道为什么不起作用
		matrix.postTranslate((offset - bmpW / 4), 0);
		imageView.setImageMatrix(matrix);
	}

	private void initTextView() {
		// 获得控件操作权
		title1 = (TextView) view.findViewById(R.id.title1);
		title2 = (TextView) view.findViewById(R.id.title2);
		// 添加监听器
		title1.setOnClickListener(new MyOnClickListener(0));
		title2.setOnClickListener(new MyOnClickListener(1));
	}

	// 同步权限数据
	private void syncData() {
		progressDialog = ProgressDialog.show(
				getActivity(),
				MyApplication.getApplication().getString(
						R.string.progressDialog_sync),
				MyApplication.getApplication().getString(
						R.string.progressDialog_syncing), false, true);
		webServer.getAllUsers(userId, syncPage1RC);
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
					* (currIndex * 2 + 1) - bmpW / 2),
					(one * (arg0 * 2 + 1) - bmpW / 2), 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			imageView.startAnimation(animation);
			switch (viewPager.getCurrentItem()) {
			case 0:
				title1.setTextColor(getResources().getColor(R.color.brown));
				title2.setTextColor(getResources().getColor(R.color.brown));
				break;
			case 1:
				title1.setTextColor(getResources().getColor(R.color.brown));
				title2.setTextColor(getResources().getColor(R.color.brown));
				break;
			case 2:
				title1.setTextColor(getResources().getColor(R.color.brown));
				title2.setTextColor(getResources().getColor(R.color.brown));
				break;
			default:
				break;
			}
		}
	}

	// 获取全部用户，也就是给页卡1初始化
	private RequestCallBack<String> getAllUsersRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			List<UserBean> list = (List<UserBean>) JsonParseUtils
					.jsonToEntitylist(arg0.result, UserBean.class);
			userDBServer.addList(list);
			// 此时页卡1的数据已经完善,填充数据源,建立适配器
			CanSeeMeListAdapter.userBeans.clear();
			CanSeeMeListAdapter.userBeans.addAll(userDBServer
					.queryAll(UserBean.class));
			canSeeMeListAdapter.notifyDataSetChanged();
			// 接着获取我可见的用户
			webServer.getICanSeeUsers(userId, getICanSeeUsersRC);
		}
	};
	// 获取我可见的用户
	private RequestCallBack<String> getICanSeeUsersRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			List<UserBean> list = (List<UserBean>) JsonParseUtils
					.jsonToEntitylist(arg0.result, UserBean.class);
			userDBServer.addICanSeeUsers(list);
			// 填充页卡2的数据源,建立适配器
			ICanSeeListAdapter.userBeans.clear();
			ICanSeeListAdapter.userBeans.addAll(list);
			iCanSeeListAdapter.notifyDataSetChanged();
			progressDialog.dismiss();
		}
	};
	// 同步页卡1数据
	private RequestCallBack<String> syncPage1RC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			Toast.makeText(
					getActivity(),
					MyApplication.getApplication().getString(
							R.string.syncCanseemeData_fail), Toast.LENGTH_SHORT)
					.show();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			Log.e("cxmcxm", arg0.result);
			List<UserBean> list = (List<UserBean>) JsonParseUtils
					.jsonToEntitylist(arg0.result, UserBean.class);
			// 此时页卡1的数据已经完善,更新列表数据和数据库
			if (list.size() > 0) {
				userDBServer.deleteAll(UserBean.class);
				userDBServer.addList(list);
				CanSeeMeListAdapter.userBeans.clear();
				CanSeeMeListAdapter.userBeans.addAll(userDBServer
						.queryAll(UserBean.class));
				canSeeMeListAdapter.notifyDataSetChanged();
				// 接着获取我可见的用户
				webServer.getICanSeeUsers(userId, syncPage2RC);
			} else {
				progressDialog.dismiss();
				Toast.makeText(
						getActivity(),
						MyApplication.getApplication().getString(
								R.string.syncCanseemeData_fail),
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	// 同步页卡2我可见的用户
	private RequestCallBack<String> syncPage2RC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			Toast.makeText(
					getActivity(),
					MyApplication.getApplication().getString(
							R.string.syncIcanseeData_fail), Toast.LENGTH_SHORT)
					.show();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			List<UserBean> list = (List<UserBean>) JsonParseUtils
					.jsonToEntitylist(arg0.result, UserBean.class);
			if (list.size() > 0) {
				// 更新我可见的数据，更新列表显示的数据
				userDBServer.addICanSeeUsers(list);
				ICanSeeListAdapter.userBeans.clear();
				ICanSeeListAdapter.userBeans.addAll(list);
				iCanSeeListAdapter.notifyDataSetChanged();
				// 基本信息获取成功后，获取用户头像列表
				webServer.getAllHeadImageList(syncPage3RC);
				
			} else {
				progressDialog.dismiss();
				Toast.makeText(
						getActivity(),
						MyApplication.getApplication().getString(
								R.string.syncIcanseeData_fail),
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	// 同步页卡3所有用户的头像列表
	private RequestCallBack<String> syncPage3RC = new RequestCallBack<String>() {
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			Toast.makeText(
					getActivity(),
					MyApplication.getApplication().getString(
							R.string.syncHeadImageList_fail),
					Toast.LENGTH_SHORT).show();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			List<UserBean> list = (List<UserBean>) JsonParseUtils.jsonToList(
					arg0.result, UserBean.class);
			if (list.size() > 0) {
				// 如果两个列表中有匹配数据，更新添加头像的数据，更新列表显示的数据
				for (UserBean bean : list) {
					userDBServer.updateOne(bean);
					// 判断是否有当前用户的头像，有则把头像存在MyCookie中
					if (bean.getId().equals(MyCookie.getString("userId", null))) {
						MyCookie.putString("headimage", bean.getHeadimage());
					}
				}
				List<UserBean> dblist = userDBServer.queryAll(UserBean.class);
				CanSeeMeListAdapter.userBeans.clear();
				CanSeeMeListAdapter.userBeans.addAll(dblist);
				// 如果MyCookie有headimage，下载自己的头像
				if (MyCookie.getString("headimage", null) != null) {
					UserBean myBean = new UserBean();
					myBean.setHeadimage(MyCookie.getString("headimage", null));
					String headimageSdUri = FileUtil.HEADIMAGE_PATH + myBean.getHeadimage();
					if (!(new File(headimageSdUri).exists())) {
						webServer.downloadHeadimage(downloadHeadimageRC,
								myBean.getHeadimage(), headimageSdUri);
					}
				}
				canSeeMeListAdapter.notifyDataSetChanged();
				progressDialog.dismiss();
				Toast.makeText(
						getActivity(),
						MyApplication.getApplication().getString(
								R.string.syncUserData_success),
						Toast.LENGTH_SHORT).show();
			} else {
				progressDialog.dismiss();
				Toast.makeText(
						getActivity(),
						MyApplication.getApplication().getString(
								R.string.syncHeadImageList_fail),
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	// 下载用户头像
	private RequestCallBack<File> downloadHeadimageRC = new RequestCallBack<File>() {
		@Override
		public void onFailure(HttpException arg0, String arg1) {
		}

		@Override
		public void onSuccess(ResponseInfo<File> arg0) {
			canSeeMeListAdapter.notifyDataSetChanged();
		}
	};

}
