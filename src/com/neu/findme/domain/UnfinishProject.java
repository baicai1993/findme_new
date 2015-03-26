package com.neu.findme.domain;

import com.google.gson.annotations.SerializedName;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * @author cxm
 *工程名实体类
 *2015-03-09 21:08:08
 */
@Table(name = "unfinishProject")  
public class UnfinishProject {
	@Id(column="projectName")@SerializedName("project_name")
	private String projectName;//工程名，也是主键
	public UnfinishProject(){}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public UnfinishProject(String name){
		this.projectName = name;
	}
	@Override
	public String toString() {
		return "UnfinishProject [projectName=" + projectName + "]";
	}
	
}
