package com.neu.findme.server_db;

import java.util.ArrayList;
import java.util.List;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.neu.findme.domain.CommentBean;
import com.neu.findme.domain.LocalRecordBean;
import com.neu.findme.domain.MoneyRecordBean;
import com.neu.findme.domain.NetRecordBean;
import com.neu.findme.domain.NoticeBean;
import com.neu.findme.domain.OtherRecordBean;
import com.neu.findme.domain.ProjectCosttypeBean;
import com.neu.findme.domain.UnfinishProject;
import com.neu.findme.domain.UserBean;
//基础数据库类，提供基本的增删改查功能，需要继承后才能使用
//条件数据库操作请在子类中重写或定义
/**
 * @author cxm
 *基础数据库服务类。提供基础的数据库操作
 *2015-03-09 21:22:05
 */
public class BaseDBServer {
	protected DbUtils dbUtils;
    protected  BaseDBServer(){};
	//增加一条
	public boolean add(Object object) {
		try {
			dbUtils.save(object);
			return true;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	//增加一条,如果重复则更新此条
	public boolean addOrUpdate(Object object) {
		try {
			dbUtils.saveOrUpdate(object);
			return true;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	//增加多条
	public boolean addList(List<?> list){
		try {
			dbUtils.saveAll(list);
			return true;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	//重置表数据
	public boolean resetTable(List<?> list,Class<?> type){
		dropTable(type);
		return addList(list);
	}
	//增加多条,如果重复则更新此条
	public boolean addListOrUpdate(List<?> list){
		try {
			dbUtils.saveOrUpdateAll(list);
			return true;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	//删除一条，删除的记录不存在，也返回false
	public boolean delete(Object object) {
		try {
			dbUtils.delete(object);
			return true;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	//清空表
	public boolean deleteAll(Class<?> type) {
		try {
			dbUtils.deleteAll(type);
			return true;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	//根据id匹配更改一条,如果id不存在仍然会返回true
	public boolean updateOne(Object object){
		try {
				dbUtils.update(object);
				return true;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	//根据int类型id查询一条
	public Object query(Object object,int id) {
		try {
			Object tempObject = dbUtils.findById(object.getClass(), id);
			return tempObject;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	//根据String类型id查询一条
	public Object query(Object object,String id) {
		try {
			Object tempObject = dbUtils.findById(object.getClass(), id);
			return tempObject;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	//返回表内全部数据
	public List<?> queryAll(Class<?> type) {
		try {
			List<?> list =  dbUtils.findAll(type);
			if(list==null){
				return new ArrayList<Object>();
			}
			return list;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<Object>();
		}
	}
	//查询返回前N条数据
	public  List<?> queryTopN(Class<?> type,int end) {
		// TODO Auto-generated method stub
		List<?> list = queryAll(type);
		if(list==null){
			return new ArrayList<Object>();
		}else if(list.size()>=end){
			return list.subList(0, end);
		}
			return list;
	}
	//查询返回全部数据，按拍照时间排序
	public List<?> queryAllOrderBy(Class<?> type,String columnName,boolean desc) {
		// TODO Auto-generated method stub
		try {
			List<?> list = dbUtils.findAll(Selector.from(type).orderBy(columnName, desc));
			if(list ==null){
				return new ArrayList<Object>();
			}
			return list;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<Object>();
		}
	}
	//查询返回前N条数据，按拍照时间排序
	public List<?> queryTopNOrderBy(Class<?> type, int end,String columnName,boolean desc) {
		// TODO Auto-generated method stub
		try {
			List<?> list = dbUtils.findAll(Selector.from(type).orderBy(columnName, desc));
			if(list==null){
				return new ArrayList<Object>();
			}else if(list.size()>=end){
				return list.subList(0, end);
			}
			return list;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<Object>();
		}
	}
	//删除表
	public void dropTable(Class<?> type){
		try {
			dbUtils.dropTable(type);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//更新表的一部分
	public boolean updateColumn(List<?> list,String... columnName ){
		try {
			dbUtils.updateAll(list, columnName);
			return true;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	//清空全部数据表
	public void clearAllTable(){
		try {
			dbUtils.deleteAll(CommentBean.class);
			dbUtils.deleteAll(LocalRecordBean.class);
			dbUtils.deleteAll(NetRecordBean.class);
			dbUtils.deleteAll(OtherRecordBean.class);
			dbUtils.deleteAll(MoneyRecordBean.class);
			dbUtils.deleteAll(NoticeBean.class);
			dbUtils.deleteAll(ProjectCosttypeBean.class);
			dbUtils.deleteAll(UnfinishProject.class);
			dbUtils.deleteAll(UserBean.class);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//删除数据库
	public void dropDB(){
		try {
			dbUtils.dropDb();
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
