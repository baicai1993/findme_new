package com.neu.findme.server_db;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.neu.findme.domain.OtherRecordBean;

import android.content.Context;

/**
 * @author cxm
 *监控数据的数据库服务
 *2015-03-11 15:15:39
 */
public class OtherRecDBServer extends BaseDBServer{
	public OtherRecDBServer(Context context){
		dbUtils = DbUtils.create(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OtherRecordBean> queryAllOrderBy(Class<?> type, String columnName,boolean desc) {
		// TODO Auto-generated method stub
		return (List<OtherRecordBean>) super.queryAllOrderBy(type, columnName,desc);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<OtherRecordBean> queryTopNOrderBy(Class<?> type, int end, String columnName,boolean desc) {
		// TODO Auto-generated method stub
		return (List<OtherRecordBean>) super.queryTopNOrderBy(type, end, columnName,desc);
	}
	//查找某人的记录，有序
	public List<OtherRecordBean> queryOnesInOrder(Class<?> type, String columnName,String userId,boolean desc) {
		// TODO Auto-generated method stub
		try {
			return dbUtils.findAll(Selector.from(OtherRecordBean.class).where("userId", "=", userId));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<OtherRecordBean>();
		}
	}
	//查找某人前N条记录，有序
	public List<OtherRecordBean> queryOnesTopN(Class<?> type, String columnName,String userId,boolean desc,int end) {
		// TODO Auto-generated method stub
		try {
			List<OtherRecordBean> list = dbUtils.findAll(Selector.from(OtherRecordBean.class).where("userId", "=", userId));
			if(list==null){
				return new ArrayList<OtherRecordBean>();
			}else if(list.size()>=end){
				return list.subList(0, end);
			}
			return list;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<OtherRecordBean>();
		}
	}
}
