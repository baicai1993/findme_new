package com.neu.findme.domain;

import com.lidroid.xutils.db.annotation.Table;


/**
 * @author cxm
 *本地记录的实体类
 *2015-03-09 21:02:39
 */
@Table(name = "local_photo")  
public class LocalRecordBean extends BaseRecordBean{
	private static final long serialVersionUID = 1L;
	// 是否上传
	protected Boolean isUploaded;
	public LocalRecordBean(){}

	public Boolean getIsUploaded() {
		return isUploaded;
	}
	public void setIsUploaded(Boolean isUploaded) {
		this.isUploaded = isUploaded;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (!this.photoId.equals(((LocalRecordBean) obj).getPhotoId()))
			return false;
		return true;
	}
}
