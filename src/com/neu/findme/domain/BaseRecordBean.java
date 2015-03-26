package com.neu.findme.domain;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Transient;

/**
 * @author cxm
 *网络，本地，监控等照片记录的父类，这样设计其实有些缺乏灵活性
 *2015-03-09 20:59:32
 */
public class BaseRecordBean implements Cloneable,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id(column="photoId")@SerializedName("imagename")
	protected String photoId;	// 照片id
	@SerializedName("takeaddress")
	protected String location;	// 位置
	// 照片描述
	@SerializedName("description")
	protected String description;
	// 用户id
	@SerializedName("userid")
	protected Long userId;
	// 用户姓名
	@SerializedName("username")
	protected String userName;
//	// 照片名字，和ID重复，影响反射，需要解决
//	@SerializedName("imagename")
//	protected String imageName;
	// 上传时间
	@SerializedName("uploadTimeStr")
	protected String upLoadedTime;
	// 拍照时间
	@SerializedName("takeTimeStr")
	protected String takeTime;
	// 经度
	protected float latitude;
	// 纬度
	protected float longitude;
	// 照片宽度
	protected int width;
	// 照片高度
	protected int height;
	// 工作项目组
	@SerializedName("label")
	protected String project;
	// sdCard路径
	protected String sdUri;
	// 照片标题
	protected String title;
	// 热度
	@Transient
	protected String popularity;
	// 是否公开
	@Transient@SerializedName("ispublic")
	protected int isPubilc;
	protected BaseRecordBean(){}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSdUri() {
		return sdUri;
	}

	public void setSdUri(String sdUri) {
		this.sdUri = sdUri;
	}

	public int getHeight() {
		return height;
	}


	public int getIsPubilc() {
		return isPubilc;
	}


	public float getLatitude() {
		return latitude;
	}

	public String getLocation() {
		return location;
	}

	public float getLongitude() {
		return longitude;
	}

	public String getPhotoId() {
		return photoId;
	}

	public String getPopularity() {
		return popularity;
	}

	public String getTakeTime() {
		return takeTime;
	}

	public String getUpLoadedTime() {
		return upLoadedTime;
	}

	public Long getUserId() {
		return userId;
	}

	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		this.height = height;
	}


	public void setIsPubilc(int isPubilc) {
		this.isPubilc = isPubilc;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public void setPopularity(String popularity) {
		this.popularity = popularity;
	}


	public void setTakeTime(String takeTime) {
		this.takeTime = takeTime;
	}

	public void setUpLoadedTime(String upLoadedTime) {
		this.upLoadedTime = upLoadedTime;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
	@Override
	public String toString() {
		return "BaseRecordBean [photoId=" + photoId + ", location=" + location
				+ ", description=" + description + ", userId=" + userId
				+ ", userName=" + userName+ ", upLoadedTime=" + upLoadedTime + ", takeTime=" + takeTime
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", width=" + width + ", height=" + height + ", project="
				+ project + ", sdUri=" + sdUri + ", title=" + title + ", popularity=" + popularity
				+ ", isPubilc=" + isPubilc + "]";
	}
	//弥补设计错误：本地网络和监控这三个bean应该统一为recordBean更好，这样克隆是便于网络、监控记录转为本地记录
	@Override
	public LocalRecordBean clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		LocalRecordBean localRecordBean = new LocalRecordBean();
		localRecordBean.setDescription(this.description);
		localRecordBean.setHeight(this.height);
		localRecordBean.setIsPubilc(this.isPubilc);
		localRecordBean.setIsUploaded(true);
		localRecordBean.setLatitude(this.latitude);
		localRecordBean.setLocation(this.location);
		localRecordBean.setLongitude(this.longitude);
		localRecordBean.setPhotoId(this.photoId);
		localRecordBean.setPopularity(this.popularity);
		localRecordBean.setProject(this.project);
		localRecordBean.setSdUri(this.sdUri);
		localRecordBean.setTakeTime(this.takeTime);
		localRecordBean.setTitle(this.title);
		localRecordBean.setUpLoadedTime(this.upLoadedTime);
		localRecordBean.setUserId(this.userId);
		localRecordBean.setUserName(this.userName);
		localRecordBean.setWidth(this.width);
		return localRecordBean;
	}

}
