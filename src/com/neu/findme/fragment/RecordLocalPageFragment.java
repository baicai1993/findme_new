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
import com.neu.findme.activity.PhotoRecordDetailActivity;
import com.neu.findme.adapter.RecordLocalListAdapter;
import com.neu.findme.domain.LocalRecordBean;
import com.neu.findme.server_db.LocalRecDBServer;
/**
 * @author cxm
 *本地照片记录的管理页，点击列表item进入查看详情界面
 *2015-03-11 15:36:58
 * */
public class RecordLocalPageFragment extends Fragment {
	private LocalRecDBServer dbServer;
	private RecordLocalListAdapter listViewAdapter;
	@ViewInject(R.id.listView)
	private ListView listView;
	@OnItemClick(R.id.listView)
	public void listItemListener(AdapterView<?> parent, View view,int position, long id){
		Intent intent = new Intent(getActivity(),PhotoRecordDetailActivity.class);
		intent.putExtra("recordBeanId", position);
		intent.putExtra("bean", RecordLocalListAdapter.recordBeans.get(position));
		intent.putExtra("flag", "localFlag");
		startActivity(intent);
	}
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_record_local_page, container,false);
		ViewUtils.inject(this,view);//注入view和事件
		dbServer = new LocalRecDBServer(getActivity());
		listViewAdapter = new RecordLocalListAdapter(getActivity(), 
		dbServer.queryAllOrderBy(LocalRecordBean.class, "takeTime",true));
		listView.setAdapter(listViewAdapter);
		return view;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		listViewAdapter.notifyDataSetChanged();
		super.onResume();
	}

	
}
