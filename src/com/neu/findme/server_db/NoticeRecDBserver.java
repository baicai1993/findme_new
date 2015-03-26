package com.neu.findme.server_db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.neu.findme.domain.NoticeBean;

/**
 * @author cxm
 *通知中心记录的数据库服务
 *2015-03-11 15:15:25
 */
public class NoticeRecDBserver extends BaseDBServer {
	public NoticeRecDBserver(Context context){
		dbUtils = DbUtils.create(context);
	}

	//查询未读的全部数据
	public List<NoticeBean> queryAllNotRead() {
		// TODO Auto-generated method stub
		try {
			List<NoticeBean> list = dbUtils.findAll(Selector.from(NoticeBean.class).where("isRead", "=", "0"));
			if(list==null){
				return new ArrayList<NoticeBean>();
			}
			return list;
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<NoticeBean>();
		}
	}
	
}
