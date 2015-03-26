package com.neu.findme.domain;

import com.lidroid.xutils.db.annotation.Table;


/**
 * @author cxm
 *网路记录的实体类
 *2015-03-09 21:05:05
 */
@Table(name = "net_photo")  
public class NetRecordBean extends BaseRecordBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NetRecordBean(){}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (!this.photoId.equals(((NetRecordBean) obj).getPhotoId()))
			return false;
		return true;
	}
}
