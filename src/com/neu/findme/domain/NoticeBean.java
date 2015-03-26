package com.neu.findme.domain;

import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

/**
 * @author cxm
 *通知中心的记录实体类
 *2015-03-09 21:05:57
 */
@Table(name = "notice_rec")  
public class NoticeBean extends BaseRecordBean {

	private static final long serialVersionUID = 1L;
	private Boolean isRead;//是否已读
	@Transient
	private boolean loading;//是否正在加载照片
	
	public boolean isLoading() {
		return loading;
	}
	public void setIsloading(boolean isloading) {
		this.loading = isloading;
	}
	public Boolean getIsRead() {
		return isRead;
	}
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
	public NoticeBean() {
	}
	@Override
	public String toString() {

		return "NoticeBean [isRead=" + isRead + "]"+ super.toString();
	}

}
