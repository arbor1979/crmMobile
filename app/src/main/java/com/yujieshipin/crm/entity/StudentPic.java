package com.yujieshipin.crm.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "StudentPic")
public class StudentPic implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(id = true)
	private String id;
	@DatabaseField
	private String className; //班级
	@DatabaseField
	private String picUrl; //头像地址
	@DatabaseField
	private String remark; //说明

	public StudentPic() {

	}

	public StudentPic(JSONObject jo) {
		this.className  = jo.optString("班级");
		this.picUrl = jo.optString("头像");
		this.remark = jo.optString("说明");
	}
	
	public static List<StudentPic> toList(JSONArray ja) {
		List<StudentPic> result = new ArrayList<StudentPic>();
		StudentPic info = null;
		
		if (ja != null && ja.length() > 0) {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				info = new StudentPic(jo);
				result.add(info);
			}
			return result;
		}else{
			System.out.println("没有StudentPic数据");
			return null;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	
	
}
