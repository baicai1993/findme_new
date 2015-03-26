package com.neu.findme.server_db;

import java.util.ArrayList;
import java.util.List;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;
import android.content.Context;

/**
 * @author cxm
 *工程名-类型名表的数据库服务
 *2015-03-11 15:16:02
 */
public class ProjectCosttypeDBServer extends BaseDBServer {
	public ProjectCosttypeDBServer(Context context){
		dbUtils = DbUtils.create(context);
	}

	//返回无重复的工程名
	public List<String> getAllProjects(){
		SqlInfo sqlInfo = new SqlInfo("select distinct projectName from project_costtype");
		List<String> projects = new ArrayList<String>();
		try {
			List<DbModel> list = dbUtils.findDbModelAll(sqlInfo);
			for(DbModel dbModel:list){
				projects.add(dbModel.getString("projectName"));
			}
			return projects;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return projects;
		}
	}
	//返回无重复的类型名
	public List<String> getAllCosttypes(){
		SqlInfo sqlInfo = new SqlInfo("select distinct costtypeName from project_costtype");
		List<String> costtypes = new ArrayList<String>();
		try {
			List<DbModel> list = dbUtils.findDbModelAll(sqlInfo);
			for(DbModel dbModel:list){
				costtypes.add(dbModel.getString("costtypeName"));
			}
			return costtypes;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return costtypes;
		}
	}
	//根据工程名查询类型名
	public List<String> getCosttypes(String projectName){
		SqlInfo sqlInfo = new SqlInfo("select costtypeName from project_costtype where projectName='"+projectName+"'");
		List<String> costtypes = new ArrayList<String>();
		List<DbModel> list;
		try {
			list = dbUtils.findDbModelAll(sqlInfo);
			for(DbModel dbModel:list){
				costtypes.add(dbModel.getString("costtypeName"));
			}
			return costtypes;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return costtypes;
		}

	}
	//根据类型名查询工程名
	public List<String> getProjects(String costtypeName){
		SqlInfo sqlInfo = new SqlInfo("select projectName from project_costtype where costtypeName='"+costtypeName+"'");
		List<String> projects = new ArrayList<String>();
		List<DbModel> list;
		try {
			list = dbUtils.findDbModelAll(sqlInfo);
			for(DbModel dbModel:list){
				projects.add(dbModel.getString("projectName"));
			}
			return projects;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return projects;
		}

	}
}
