package com.neu.findme.domain;

import java.util.Date;

/**
 * @author cxm
 *签到/签退实体类
 *2015-03-09 21:07:43
 */
public class SignInBean {
	private String userid;//用户id
	private String name;//用户名
	private String location;//地理位置
	// @DateTimeFormat(pattern="yyyyMMddHHmmss")
	private Date time;//上传时间
	private String type;//签到或者是签退
	private String remark;//备注
	private String deviceId;//设备IMEI

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "SignInBean [userid=" + userid + ", name=" + name
				+ ", location=" + location + ", time=" + time + ", type="
				+ type + ", remark=" + remark + "]";
	}

}
