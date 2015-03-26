package com.neu.findme.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.neu.findme.fragment.CanSeeMeFragment;
import com.neu.findme.fragment.ICanSeeFragment;

/**
 * @author cxm
 *权限界面的ViewPager适配器
 *2015-03-09 20:51:45
 */
public class AuthorityViewPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	public AuthorityViewPagerAdapter(FragmentManager fm) {
		super(fm);
		//将想要适配进去的fragment添加到数组中
		fragments.add(new CanSeeMeFragment());
		fragments.add(new ICanSeeFragment());
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
