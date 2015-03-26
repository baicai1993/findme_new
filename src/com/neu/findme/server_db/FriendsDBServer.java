package com.neu.findme.server_db;

import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.neu.findme.domain.UserBean;

import android.content.Context;

/**
 * @author cxm
 *好友数据库服务类
 *2015-03-09 21:22:56
 */
public class FriendsDBServer extends BaseDBServer {
	public FriendsDBServer(Context context){
		dbUtils = DbUtils.create(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserBean> queryAll(Class<?> type) {
		// TODO Auto-generated method stub
		return (List<UserBean>) super.queryAll(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserBean> queryTopN(Class<?> type, int end) {
		// TODO Auto-generated method stub
		return (List<UserBean>) super.queryTopN(type, end);
	}
	
}
