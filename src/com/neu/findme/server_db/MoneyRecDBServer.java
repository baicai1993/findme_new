package com.neu.findme.server_db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.neu.findme.domain.MoneyRecordBean;

/**
 * @author cxm
 *财务记录的数据库服务
 *包括按工程名-类名互相筛选记录
 *2015-03-11 15:14:36
 */
public class MoneyRecDBServer extends BaseDBServer {
	public MoneyRecDBServer(Context context){
		dbUtils = DbUtils.create(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MoneyRecordBean> queryAllOrderBy(Class<?> type, String columnName,boolean desc) {
		// TODO Auto-generated method stub
		return (List<MoneyRecordBean>) super.queryAllOrderBy(type, columnName,desc);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MoneyRecordBean> queryTopNOrderBy(Class<?> type, int end, String columnName,boolean desc) {
		// TODO Auto-generated method stub
		return (List<MoneyRecordBean>) super.queryTopNOrderBy(type, end, columnName,desc);
	}
	//按照工程名和类型名返回list
	public List<MoneyRecordBean> queryByProjectCosttype(String project,String costtype){
		try {
			List<MoneyRecordBean> list = dbUtils.findAll(Selector.from(MoneyRecordBean.class).where("project_name", "=", project).
					and("type_name", "=", costtype).orderBy("rec_time", true));
			if(list!=null){
				return list;
			}
			return new ArrayList<MoneyRecordBean>();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<MoneyRecordBean>();
		}
	}
	//按照工程名返回list
	public List<MoneyRecordBean> queryByProject(String project){
		try {
			List<MoneyRecordBean> list = dbUtils.findAll(Selector.from(MoneyRecordBean.class).where("project_name", "=", project).orderBy("rec_time", true));
			if(list!=null){
				return list;
			}
			return new ArrayList<MoneyRecordBean>();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<MoneyRecordBean>();
		}
	}
	//按照类型名返回list
	public List<MoneyRecordBean> queryByCosttype(String costtype){
		try {
			List<MoneyRecordBean> list = dbUtils.findAll(Selector.from(MoneyRecordBean.class).where("type_name", "=", costtype).orderBy("rec_time", true));
			if(list!=null){
				return list;
			}
			return new ArrayList<MoneyRecordBean>();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<MoneyRecordBean>();
		}
	}
}
