package com.neu.findme.server_db;


import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.neu.findme.domain.UnfinishProject;

import android.content.Context;

/**
 * @author cxm
 *未完成项目表的数据库服务
 *2015-03-11 15:16:20
 */
public class UnfinishProjectDBServer extends BaseDBServer {
	public UnfinishProjectDBServer(Context context){
		dbUtils = DbUtils.create(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UnfinishProject> queryAll(Class<?> type) {
		// TODO Auto-generated method stub
		return (List<UnfinishProject>) super.queryAll(type);
	}
	
}
