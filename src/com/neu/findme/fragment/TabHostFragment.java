package com.neu.findme.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;

import com.neu.findme.R;
import com.neu.findme.activity.AuthorityManageActivityTab3;
import com.neu.findme.activity.MoneyManageActivityTab2;
import com.neu.findme.activity.RecordManageActivityTab1;
/**
 * @author cxm
 *下部TabHost的布局管理界面
 *2015-03-11 15:40:25
 */
@SuppressWarnings("deprecation")
public class TabHostFragment extends BaseFragment {
	public List<ImageView> imageList = new ArrayList<ImageView>();
	LocalActivityManager localActivityManager;
	TabHost tabHost;
	TabWidget tabWidget;
	Field mBottomLeftStrip;
	Field mBottomRightStrip;
	public TabHostFragment() {
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mMainView = inflater
				.inflate(R.layout.fragment_tabhost, container, false);

		findTabView();
		localActivityManager = new LocalActivityManager(getActivity(), true);
		localActivityManager.dispatchCreate(savedInstanceState);
		tabHost.setup(localActivityManager);
		Resources localResources = getResources();

		Intent localIntent1 = new Intent();
		localIntent1.setClass(getActivity(), RecordManageActivityTab1.class);
//		tabHost.setPadding(0, 0, 0, -5);
		tabHost.addTab(tabHost
				.newTabSpec("记录管理")
				.setIndicator("",localResources.getDrawable(R.drawable.ic_record_n))
				.setContent(localIntent1));

		Intent localIntent2 = new Intent();
		localIntent2.setClass(getActivity(), MoneyManageActivityTab2.class);

		TabHost.TabSpec localTabSpec2 = tabHost.newTabSpec("财务管理");
		localTabSpec2.setIndicator("",localResources.getDrawable(R.drawable.ic_money_n));
		localTabSpec2.setContent(localIntent2);
		tabHost.addTab(localTabSpec2);

		Intent localIntent3 = new Intent();
		localIntent3.setClass(getActivity(), AuthorityManageActivityTab3.class);
		tabHost.addTab(tabHost.newTabSpec("权限管理")
				.setIndicator("",localResources.getDrawable(R.drawable.ic_authority_n))
			    .setContent(localIntent3));
		initTab();//初始化工作 去掉下划线
		return mMainView;
	}

	@Override
	public void onResume() {
		super.onResume();
		localActivityManager.dispatchResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		localActivityManager.dispatchPause(getActivity().isFinishing());
	}
	//将tabhost的子Activity：RecordManageActivityTab1拍照请求返回的结果转发给子Activity
	public void initTab(){
		//设置监听
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub
				for (int i = 0; i < tabWidget.getChildCount(); i++) {
					View vvv = tabWidget.getChildAt(i);
					if (tabHost.getCurrentTab() == i) {
						vvv.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.bg_tabhost1));
					} else {
						vvv.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.ic_nothing));
					}
				}
			}
		});

		//进来默认
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			View vvv = tabWidget.getChildAt(i);
			if (tabHost.getCurrentTab() == i) {
				vvv.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.ic_nothing));
			} else {
				vvv.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.ic_nothing));
			}
		}

		//去下划线
		if (Float.valueOf(Build.VERSION.RELEASE.substring(0, 3)) <= 2.1) {
			try {
				mBottomLeftStrip = tabWidget.getClass().getDeclaredField(
						"mBottomLeftStrip");
				mBottomRightStrip = tabWidget.getClass().getDeclaredField(
						"mBottomRightStrip");
				if (!mBottomLeftStrip.isAccessible()) {
					mBottomLeftStrip.setAccessible(true);
				}
				if (!mBottomRightStrip.isAccessible()) {
					mBottomRightStrip.setAccessible(true);
				}
				mBottomLeftStrip.set(tabWidget,
						getResources().getDrawable(R.drawable.ic_nothing));
				mBottomRightStrip.set(tabWidget,
						getResources().getDrawable(R.drawable.ic_nothing));

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			// 如果是2.2,2.3版本开发,可以使用一下方法tabWidget.setStripEnabled(false)
			// tabWidget.setStripEnabled(false);

			// 但是很可能你开发的android应用是2.1版本，
			// tabWidget.setStripEnabled(false)编译器是无法识别而报错的,这时仍然可以使用上面的
			// 反射实现，但是代码的改改

			try {
				// 2.2,2.3接口是mLeftStrip，mRightStrip两个变量，当然代码与上面部分重复了
				mBottomLeftStrip = tabWidget.getClass().getDeclaredField(
						"mLeftStrip");
				mBottomRightStrip = tabWidget.getClass().getDeclaredField(
						"mRightStrip");
				if (!mBottomLeftStrip.isAccessible()) {
					mBottomLeftStrip.setAccessible(true);
				}
				if (!mBottomRightStrip.isAccessible()) {
					mBottomRightStrip.setAccessible(true);
				}
				mBottomLeftStrip.set(tabWidget,
						getResources().getDrawable(R.drawable.ic_nothing));
				mBottomRightStrip.set(tabWidget,
						getResources().getDrawable(R.drawable.ic_nothing));

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 找到Tabhost布局
	 */
	public void findTabView() {
		tabHost = (TabHost) mMainView.findViewById(android.R.id.tabhost);
		tabWidget = (TabWidget) mMainView.findViewById(android.R.id.tabs);
	}

}
