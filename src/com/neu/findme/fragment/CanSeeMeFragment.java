package com.neu.findme.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.neu.findme.R;
import com.neu.findme.adapter.CanSeeMeListAdapter;
import com.neu.findme.domain.UserBean;
import com.neu.findme.server_db.UserDBServer;
import com.neu.findme.server_web.HttpWebServer;
import com.neu.findme.utils.JsonParseUtils;
import com.neu.findme.utils.MyApplication;


/**
 * @author cxm
 *第一个页卡 可以修改用户对是否课可见，在离开页面时由onpause提交更改给服务器
 *2015-03-11 15:35:15
 */
public class CanSeeMeFragment extends Fragment {
	@ViewInject(R.id.listView)
	private ListView listView;
	private CanSeeMeListAdapter listViewAdapter;
	private UserDBServer userDBServer;
	private HttpWebServer webServer;
	private String userId = MyApplication.get("userId")+"";
	private List<UserBean> addTempList;
	private List<UserBean> deleteTempList;
	private List<UserBean> listAfterChange;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_authority_page1, container, false);
		ViewUtils.inject(this,view);
		userDBServer = new UserDBServer(getActivity());
		webServer = new HttpWebServer();
		listView.setAdapter(AuthorityManageFragment.canSeeMeListAdapter);
		return view;
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		if(listViewAdapter!=null){
			listViewAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		//在此生命周期执行修改权限的动作
		changeAuthority();
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	private void changeAuthority(){
		List<UserBean> listFromDB = userDBServer.queryAll(UserBean.class);
		listAfterChange = CanSeeMeListAdapter.userBeans;
		addTempList = new ArrayList<UserBean>();
		deleteTempList = new ArrayList<UserBean>();
		for(int i = 0;i<listFromDB.size();i++){
			UserBean bean = listAfterChange.get(i);
			if(!(listFromDB.get(i).isCanSeeMe()==bean.isCanSeeMe())){//如果权限被更新了
				if(bean.isCanSeeMe()){//分别加入增加或减少的临时数组
					addTempList.add(bean);
				}else {
					deleteTempList.add(bean);
				}
			}
		}
		webServer.addUserCanSeeMe(userId, addTempList, addAuthorityRC);
	}
	private RequestCallBack<String> addAuthorityRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			if(JsonParseUtils.jsonToBoolean(arg0.result)){
				webServer.deleteUserCanSeeMe(userId, deleteTempList, deleteAuthorityRC);

			}
		}
	};
	private RequestCallBack<String> deleteAuthorityRC = new RequestCallBack<String>() {

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub
			//提示更改失败

		}

		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			// TODO Auto-generated method stub
			if(JsonParseUtils.jsonToBoolean(arg0.result)){
				//提示更改成功
				userDBServer.updateColumn(listAfterChange, "canSeeMe");

			}
		}
	};
}
