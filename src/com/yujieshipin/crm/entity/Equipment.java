package com.yujieshipin.crm.entity;

import java.io.Serializable;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 用户登录设备信息
 * 
 *  <br/>创建说明: 2014-4-22 下午4:40:28 shengguo  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
@DatabaseTable(tableName = "equipment")
public class Equipment implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 790471034666598343L;
	/** 
     * foreign = true:说明这是一个外部引用关系 
     * foreignAutoRefresh = true：当对象被查询时，外部属性自动刷新（暂时我也没看懂其作用） 
     *  
     */ 
	@DatabaseField (foreign = true, foreignAutoRefresh = true,columnName = "User")
	private User user;
	@DatabaseField
	private String id;
	@DatabaseField
	private String iosDeviceToken;
	@DatabaseField
	private String name;
	@DatabaseField
	private String localModal;
	@DatabaseField
	private String systemName;
	@DatabaseField
	private String systemVersion;
	
	public Equipment() {

	}
	
	public Equipment(JSONObject jo) {
		id = jo.optString("设备唯一码");
		iosDeviceToken = jo.optString("IosDeviceToken");
		name = jo.optString("设备名");
		localModal = jo.optString("本地模式");
		systemName = jo.optString("系统名");
		systemVersion = jo.optString("系统版本");
	}
	
	public Equipment(net.minidev.json.JSONObject jo) {
		id = String.valueOf(jo.get("设备唯一码"));
		iosDeviceToken = String.valueOf(jo.get("IosDeviceToken"));
		name = String.valueOf(jo.get("设备名"));
		localModal = String.valueOf(jo.get("本地模式"));
		systemName = String.valueOf(jo.get("系统名"));
		systemVersion = String.valueOf(jo.get("系统版本"));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getIosDeviceToken() {
		return iosDeviceToken;
	}

	public void setIosDeviceToken(String iosDeviceToken) {
		this.iosDeviceToken = iosDeviceToken;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocalModal() {
		return localModal;
	}

	public void setLocalModal(String localModal) {
		this.localModal = localModal;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}
}