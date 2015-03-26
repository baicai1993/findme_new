package com.neu.findme.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.neu.findme.R;
import com.neu.findme.activity.SomeoneRecordActivity;
import com.neu.findme.adapter.ICanSeeListAdapter;

/**
 * @author cxm
 *我可见的用户，点击可以查看这个用户提交到服务器的记录
 *2015-03-11 15:35:40
 */
public class ICanSeeFragment extends Fragment {
	@ViewInject(R.id.listView)
	private ListView listView;
	@OnItemClick(R.id.listView)
	public void listItemListener(AdapterView<?> parent, View view,int position, long id){
		Intent intent = new Intent(getActivity(),SomeoneRecordActivity.class);
		intent.putExtra("someoneName", ICanSeeListAdapter.userBeans.get(position).getName());
		intent.putExtra("someoneId", ICanSeeListAdapter.userBeans.get(position).getId());
		startActivity(intent);
	}
	//点击头像，显示个人信息
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_authority_page2, container, false);
		ViewUtils.inject(this,view);
		listView.setAdapter(AuthorityManageFragment.iCanSeeListAdapter);
		return view;
	}

}
