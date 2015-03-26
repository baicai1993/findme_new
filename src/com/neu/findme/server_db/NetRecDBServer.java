package com.neu.findme.server_db;

import java.util.List;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.neu.findme.domain.NetRecordBean;

/**
 * @author cxm
 *网络记录的数据库服务
 *2015-03-11 15:15:01
 */
public class NetRecDBServer extends BaseDBServer {
	public NetRecDBServer(Context context){
		dbUtils = DbUtils.create(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NetRecordBean> queryAllOrderBy(Class<?> type, String columnName,boolean desc) {
		// TODO Auto-generated method stub
		return (List<NetRecordBean>) super.queryAllOrderBy(type, columnName,desc);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<NetRecordBean> queryTopNOrderBy(Class<?> type, int end, String columnName,boolean desc) {
		// TODO Auto-generated method stub
		return (List<NetRecordBean>) super.queryTopNOrderBy(type, end, columnName,desc);
	}


	
}
