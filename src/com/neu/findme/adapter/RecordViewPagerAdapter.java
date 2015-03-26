package com.neu.findme.adapter;

import java.util.ArrayList;

import com.neu.findme.fragment.RecordLocalPageFragment;
import com.neu.findme.fragment.RecordNetPageFragment;
import com.neu.findme.fragment.RecordOtherPageFragment;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author cxm
 *财务管理PageView适配器
 *2015-03-09 20:57:37
 */
public class RecordViewPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	public RecordViewPagerAdapter(FragmentManager fm) {
		super(fm);
		//添加要显示的fragment进入适配器数组
		fragments.add(new RecordLocalPageFragment());
		fragments.add(new RecordNetPageFragment());
		fragments.add(new RecordOtherPageFragment());
		// TODO Auto-generated constructor stub
	}
	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.size();
	}

}
