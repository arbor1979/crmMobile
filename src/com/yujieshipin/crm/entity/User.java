package com.yujieshipin.crm.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.yujieshipin.crm.util.AppUtility;

@DatabaseTable(tableName = "User")
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9203715820163398998L;
	/**
	 * 
	 */
	
	@DatabaseField(id = true)
	private String id;
	@DatabaseField
	private String username;
	@DatabaseField
	private String name;
	@DatabaseField
	private String deptId;
	@DatabaseField
	private String department;
	@DatabaseField
	private String gender;
	@DatabaseField
	private String birthday;
	@DatabaseField
	private String phone;
	@DatabaseField
	private String email;
	
	@DatabaseField
	private String companyName;
	
	@DatabaseField
	private String loginTime;
	@DatabaseField
	private int isModify;
	
	@DatabaseField
	private String checkCode;
	@DatabaseField
	private String userImage;
	
	@DatabaseField
	private String userNumber;
	
	@DatabaseField
	private String userType;
	@DatabaseField
	private String recentlyUsedEquipment;
	@DatabaseField
	private String iosDeviceToken;
	
	@DatabaseField
	private String userRating;
	@DatabaseField
	private String mainRole;
	
	@DatabaseField
	private String sortNumber;
	@DatabaseField
	private String banLogin;
	@DatabaseField
	private String password;
	
	@DatabaseField
	private String address;
	@DatabaseField
	private String remark;
	
	@DatabaseField
	private String company;
	@DatabaseField
	private String albumAdmin;
	@DatabaseField
	private String membercard;
	@DatabaseField
	private String belongTo;
	@DatabaseField
	private String yuchuzhi;
	@DatabaseField
	private String viewPriv;
	@DatabaseField
	private String integral;
	@DatabaseField
	private String tuihuorate;
	@DatabaseField
	private String minzhekou;
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMembercard() {
		return membercard;
	}

	public void setMembercard(String membercard) {
		this.membercard = membercard;
	}

	public String getBelongTo() {
		return belongTo;
	}

	public void setBelongTo(String belongTo) {
		this.belongTo = belongTo;
	}

	public String getYuchuzhi() {
		return yuchuzhi;
	}

	public void setYuchuzhi(String yuchuzhi) {
		this.yuchuzhi = yuchuzhi;
	}

	public String getViewPriv() {
		return viewPriv;
	}

	public void setViewPriv(String viewPriv) {
		this.viewPriv = viewPriv;
	}

	public String getIntegral() {
		return integral;
	}

	public void setIntegral(String integral) {
		this.integral = integral;
	}

	public String getTuihuorate() {
		return tuihuorate;
	}

	public void setTuihuorate(String tuihuorate) {
		this.tuihuorate = tuihuorate;
	}

	public String getMinzhekou() {
		return minzhekou;
	}

	public void setMinzhekou(String minzhekou) {
		this.minzhekou = minzhekou;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	@DatabaseField
	private String createtime;
	public String getAlbumAdmin() {
		return albumAdmin;
	}

	public void setAlbumAdmin(String albumAdmin) {
		this.albumAdmin = albumAdmin;
	}

	private String latestAddress;
	
	public String getLatestAddress() {
		return latestAddress;
	}

	public void setLatestAddress(String latestAddress) {
		this.latestAddress = latestAddress;
	}

	

	public User() {
		userType="";
		userNumber="";
		username="";
		name="";
		userImage="";
		latestAddress="";
	}


	public User(JSONObject jo) {
		userType = jo.optString("用户类型");
		id = jo.optString("编号");
		username = jo.optString("用户名");
		name = jo.optString("姓名");
		password = jo.optString("密码");
		deptId = jo.optString("部门ID");
		department = jo.optString("部门");
		gender = jo.optString("性别");
		birthday = jo.optString("出生日期");
		phone = jo.optString("手机");
		email = jo.optString("电邮");
		loginTime = jo.optString("登录时间");
		isModify = jo.optInt("limitEditDel");
		checkCode = jo.optString("用户较验码");
		userImage = jo.optString("用户头像");
		userNumber = jo.optString("用户唯一码");
		banLogin = jo.optString("是否启用");
		sortNumber = jo.optString("排序号");
		mainRole = jo.optString("角色名称");
		userRating = jo.optString("TuiHuoRate");
		iosDeviceToken = jo.optString("IosDeviceToken");
		

		companyName = jo.optString("单位名称");
		company = jo.optString("单位");
		address = jo.optString("地址");
		remark = jo.optString("备注");
		albumAdmin= jo.optString("相册管理员");
		latestAddress="";
		
		membercard=jo.optString("会员卡");
		belongTo=jo.optString("隶属人");
		yuchuzhi=jo.optString("预储值");
		viewPriv=jo.optString("查看权限");
		integral=jo.optString("积分");
		tuihuorate=jo.optString("退货率");
		minzhekou=jo.optString("最低折扣");
		createtime=jo.optString("创建时间");
	}

	
	/**
	 * 编号
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 用户名
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 姓名
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	/**
	 * 部门
	 * 
	 * @return
	 */
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	/**
	 * 性别
	 * 
	 * @return
	 */
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * 出生日期
	 * 
	 * @return
	 */
	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	/**
	 * 手机
	 * 
	 * @return
	 */
	public String getPhone() {
		if(AppUtility.isNotEmpty(phone))
			return phone;
		else
			return "";
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * 电邮
	 * 
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	

	/**
	 * 单位名称
	 * 
	 * @return
	 */
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	/**
	 * 登录时间
	 * 
	 * @return
	 */
	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	/**
	 * 是否修改
	 * 
	 * @return
	 */
	public int getIsModify() {
		return isModify;
	}

	public void setIsModify(int isModify) {
		this.isModify = isModify;
	}

	
	/**
	 * 用户校验码
	 * 
	 * @return
	 */
	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	/**
	 * 用户头像
	 * 
	 * @return
	 */
	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	

	/**
	 * 用户唯一码
	 * 
	 * @return
	 */
	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	

	/**
	 * 用户类型
	 * 
	 * @return
	 */
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}



	/**
	 * 用户最经使用设备
	 * 
	 * @return
	 */
	public String getRecentlyUsedEquipment() {
		return recentlyUsedEquipment;
	}

	public void setRecentlyUsedEquipment(String recentlyUsedEquipment) {
		this.recentlyUsedEquipment = recentlyUsedEquipment;
	}

	/**
	 * 功能描述:
	 * 
	 * @return
	 */
	public String getIosDeviceToken() {
		return iosDeviceToken;
	}

	public void setIosDeviceToken(String iosDeviceToken) {
		this.iosDeviceToken = iosDeviceToken;
	}

	
	/**
	 * 用户评级
	 * 
	 * @return
	 */
	public String getUserRating() {
		return userRating;
	}

	public void setUserRating(String userRating) {
		this.userRating = userRating;
	}

	/**
	 * 主要角色
	 * 
	 * @return
	 */
	public String getMainRole() {
		return mainRole;
	}

	public void setMainRole(String mainRole) {
		this.mainRole = mainRole;
	}

	
	/**
	 * 排序号
	 * 
	 * @return
	 */
	public String getSortNumber() {
		return sortNumber;
	}

	public void setSortNumber(String sortNumber) {
		this.sortNumber = sortNumber;
	}

	/**
	 * 禁止登录
	 * 
	 * @return
	 */
	public String getBanLogin() {
		return banLogin;
	}

	public void setBanLogin(String banLogin) {
		this.banLogin = banLogin;
	}

	/**
	 * 密码
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	

	/**
	 * 备注
	 * 
	 * @return
	 */
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	
	/**
	 * 单位
	 * 
	 * @return
	 */
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
}
