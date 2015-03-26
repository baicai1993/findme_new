package com.neu.findme.server_db;

import java.util.ArrayList;
import java.util.List;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.neu.findme.domain.UserBean;

import android.content.Context;

/**
 * @author cxm
 *用户表的数据库服务，查询全部默认是不包括自己的
 *有单独查询用户自己的方法
 *2015-03-11 15:16:58
 */
public class UserDBServer extends BaseDBServer {
	public UserDBServer(Context context){
		dbUtils = DbUtils.create(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserBean> queryAll(Class<?> type) {//不包括自己
		// TODO Auto-generated method stub
		List<UserBean> list = (List<UserBean>) super.queryAll(type);
		List<UserBean> tempList = new ArrayList<UserBean>();
		for(UserBean bean:list){
			if(bean.getAuthorityFlag()==5){
				tempList.add(bean);
			}
		}
		list.removeAll(tempList);
		return list;
	}
	//返回自己的信息
	public UserBean queryMe(){
		try {
			return dbUtils.findFirst(Selector.from(UserBean.class).where("authorityFlag","=","5"));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new UserBean();
		}
	}

	@Override
	public boolean resetTable(List<?> list, Class<?> type) {
		// TODO Auto-generated method stub
		//不删除自己
		SqlInfo sqlInfo = new SqlInfo("delete from user where authorityFlag < 5");
		try {
			dbUtils.execNonQuery(sqlInfo);
			addList(list);
			return true;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	//增加对我可见的用户
	public boolean addICanSeeUsers(List<UserBean> list){
		for(UserBean bean:list){
			bean.setIcanSee(true);
		}
		try {
			dbUtils.updateAll(list, "icanSee");
			return true;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	//获得我可见的用户列表
	public List<UserBean> getICanSeeUsers(){
		try {
			return dbUtils.findAll(Selector.from(UserBean.class).where("icanSee", "=", "1"));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<UserBean>();
		}
	}
	
	
	//修改一个用户的信息
	@Override
	public boolean updateOne(Object object) {
		try {
			dbUtils.update(object, "headimage");
			return true;
		} catch (DbException e) {
			e.printStackTrace();
			return false;
		}
	}

	//删除的时候不删除自己
	@Override
	public boolean deleteAll(Class<?> type) {
		// TODO Auto-generated method stub
		 try {
			dbUtils.delete(UserBean.class, WhereBuilder.b("authorityFlag", "!=", "5"));
			return true;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
}
