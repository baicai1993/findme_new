package com.neu.findme.domain;

import com.lidroid.xutils.db.annotation.Table;


/**
 * @author cxm
 *监控记录的实体类
 *2015-03-09 21:06:11
 */
@Table(name = "other_photo")  
public class OtherRecordBean extends BaseRecordBean{
	private static final long serialVersionUID = 1L;

	public OtherRecordBean(){}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (!this.photoId.equals(((OtherRecordBean) obj).getPhotoId()))
			return false;
		return true;
	}
}
