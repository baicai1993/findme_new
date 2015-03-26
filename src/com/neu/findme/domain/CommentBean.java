package com.neu.findme.domain;

import com.google.gson.annotations.SerializedName;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

/**
 * @author cxm
 *评论的实体类
 *2015-03-09 21:01:47
 */
@Table(name = "comment")  // 加上注解， 混淆后表名不受影响
public class CommentBean {
	@Id(column="id")
	private String id;//评论记录的id主键 是uuid
	@SerializedName("photoid")
	private String photoId;//对应的照片id
	@Column(column = "upload_time")@SerializedName("timeStr")
	private String time;//评论的时间
	@SerializedName("userid")
	private String userId;//用户id
	@SerializedName("name")
	private String userName;//用户名
	private String content;//评论的内容
	@Transient
	private String targetName;//评论面对的人，未实现
	
	public CommentBean() {}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPhotoId() {
		return photoId;
	}
	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTargetName() {
		return targetName;
	}
	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	@Override
	public String toString() {
		return "CommentBean [id=" + id + ", photoId=" + photoId + ", time="
				+ time + ", userId=" + userId + ", userName=" + userName
				+ ", content=" + content + ", targetName=" + targetName + "]";
	}



	
}
