package com.yujieshipin.crm.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "NoticeClass")
public class NoticeClass {
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String notice;
	@DatabaseField
	private int isModify;
	@DatabaseField
	private String ClassName;
	public String getClassName() {
		return ClassName;
	}

	public void setClassName(String className) {
		ClassName = className;
	}

	public int getIsModify() {
		return isModify;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}

	public void setIsModify(int isModify) {
		this.isModify = isModify;
	}

	public NoticeClass(){
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
