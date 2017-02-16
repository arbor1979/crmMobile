package com.yujieshipin.crm.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="ContactsMember")
public class ContactsMember implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1983380098089464765L;
	/**
	 * 
	 */
	
	@DatabaseField
	private String id;
	@DatabaseField
	private String number;
	@DatabaseField
	private String studentID;
	@DatabaseField
	private String password;
	@DatabaseField
	private String name;
	@DatabaseField
	private String className;
	@DatabaseField
	private String seatNumber;
	@DatabaseField
	private String gender;
	@DatabaseField
	private String stuPhone;
	@DatabaseField
	private String stuEmail;
	@DatabaseField
	private String dormitory;
	@DatabaseField
	private String relativeName;
	@DatabaseField
	private String relativePhone;
	@DatabaseField
	private String address;
	@DatabaseField
	private String remark;
	@DatabaseField
	private String stuStatus;
	@DatabaseField
	private String userNumber;
	@DatabaseField
	private String userImage;
	
	@DatabaseField
	private String userType;
	@DatabaseField
	private String chargeClass;
	@DatabaseField
	private String XingMing;
	@DatabaseField
	private String virtualClass;
	@DatabaseField
	private String userGrade;
	@DatabaseField
	private String loginTime;
	@DatabaseField
	private String chargeKeCheng;
	@DatabaseField
	private String schoolName;
	private JSONObject extraMenu;
	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public ContactsMember(){
		super();
	}
	
	public ContactsMember(JSONObject jo){
		number = jo.optString("编号");
		studentID = jo.optString("学号");
		password = jo.optString("密码");
		name = jo.optString("姓名");
		className = jo.optString("班级");
		seatNumber = jo.optString("座号");
		gender = jo.optString("性别");
		stuPhone = jo.optString("学生电话");
		stuEmail = jo.optString("学生邮箱");
		dormitory = jo.optString("学生宿舍");
		relativeName = jo.optString("家长姓名");
		relativePhone = jo.optString("家长电话");
		address = jo.optString("家庭住址");
		remark = jo.optString("备注");
		stuStatus = jo.optString("学生状态");
		userNumber = jo.optString("用户唯一码");
		userImage = jo.optString("用户头像");
		if(userImage==null || userImage.length()==0)
			userImage = jo.optString("头像");
		userType=jo.optString("用户类型");
		XingMing=jo.optString("XingMing");
		userGrade=jo.optString("用户评级");
		loginTime=jo.optString("登录时间");
		
		if (userNumber.indexOf("老师") > -1) {
			studentID = jo.optString("用户名");
			virtualClass = jo.optString("虚拟班级");
			seatNumber = jo.optString("排序号");
			className = jo.optString("部门");
			stuPhone = jo.optString("手机");
			chargeClass = jo.optString("所带班级");
			chargeKeCheng= jo.optString("所带课程");
		}
		if (userNumber.indexOf("家长") > -1) {
			stuPhone = jo.optString("手机");
		}
		schoolName=jo.optString("单位名称");
		try {
			extraMenu= jo.getJSONObject("附加菜单");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public JSONObject getExtraMenu() {
		return extraMenu;
	}

	public void setExtraMenu(JSONObject extraMenu) {
		this.extraMenu = extraMenu;
	}

	public ContactsMember(net.minidev.json.JSONObject jo){
		number = String.valueOf(jo.get("编号"));
		studentID = String.valueOf(jo.get("学号"));
		password = String.valueOf(jo.get("密码"));
		name = String.valueOf(jo.get("姓名"));
		className = String.valueOf(jo.get("班级"));
		seatNumber = String.valueOf(jo.get("座号"));
		gender = String.valueOf(jo.get("性别"));
		stuPhone = String.valueOf(jo.get("学生电话"));
		stuEmail = String.valueOf(jo.get("学生邮箱")==null?"":jo.get("学生邮箱"));
		
		dormitory = String.valueOf(jo.get("学生宿舍"));
		relativeName = String.valueOf(jo.get("家长姓名")==null?"":jo.get("家长姓名"));
		relativePhone = String.valueOf(jo.get("家长电话")==null?"":jo.get("家长电话"));
		address = String.valueOf(jo.get("家庭住址")==null?"":jo.get("家庭住址"));
		remark = String.valueOf(jo.get("备注")==null?"":jo.get("备注"));
		stuStatus = String.valueOf(jo.get("学生状态"));
		userNumber = String.valueOf(jo.get("用户唯一码"));
		userImage = String.valueOf(jo.get("用户头像"));
		if(userImage==null || userImage.length()==0)
			userImage = String.valueOf(jo.get("头像")==null?"":jo.get("头像"));
		userType=String.valueOf(jo.get("用户类型"));
		XingMing=String.valueOf(jo.get("XingMing"));
		userGrade=String.valueOf(jo.get("用户评级"));
		loginTime=String.valueOf(jo.get("登录时间")==null?"":jo.get("登录时间"));
		if (userNumber.indexOf("老师") > -1) {
			studentID = String.valueOf(jo.get("用户名"));
			virtualClass = String.valueOf(jo.get("虚拟班级"));
			seatNumber = String.valueOf(jo.get("排序号"));
			className = String.valueOf(jo.get("部门"));
			stuPhone = String.valueOf(jo.get("手机"));
			chargeClass = String.valueOf(jo.get("所带班级"));
			chargeKeCheng= String.valueOf(jo.get("所带课程"));
		}
		if (userNumber.indexOf("家长") > -1) {
			stuPhone = String.valueOf(jo.get("手机"));
		}
		
		schoolName=String.valueOf(jo.get("单位名称"));
	}
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getChargeClass() {
		return chargeClass;
	}

	public void setChargeClass(String chargeClass) {
		this.chargeClass = chargeClass;
	}

	public String getXingMing() {
		return XingMing;
	}

	public void setXingMing(String xingMing) {
		XingMing = xingMing;
	}

	public String getVirtualClass() {
		return virtualClass;
	}

	public void setVirtualClass(String virtualClass) {
		this.virtualClass = virtualClass;
	}

	public String getUserGrade() {
		return userGrade;
	}

	public void setUserGrade(String userGrade) {
		this.userGrade = userGrade;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSeatNumber() {
		return seatNumber;
	}
	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getStuPhone() {
		return stuPhone;
	}
	public void setStuPhone(String stuPhone) {
		this.stuPhone = stuPhone;
	}
	public String getStuEmail() {
		return stuEmail;
	}
	public void setStuEmail(String stuEmail) {
		this.stuEmail = stuEmail;
	}
	public String getDormitory() {
		return dormitory;
	}
	public void setDormitory(String dormitory) {
		this.dormitory = dormitory;
	}
	public String getRelativeName() {
		return relativeName;
	}
	public void setRelativeName(String relativeName) {
		this.relativeName = relativeName;
	}
	public String getRelativePhone() {
		return relativePhone;
	}
	public void setRelativePhone(String relativePhone) {
		this.relativePhone = relativePhone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStuStatus() {
		return stuStatus;
	}
	public void setStuStatus(String stuStatus) {
		this.stuStatus = stuStatus;
	}
	public String getUserNumber() {
		return userNumber;
	}
	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	public String getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	public String getChargeKeCheng() {
		return chargeKeCheng;
	}

	public void setChargeKeCheng(String chargeKeCheng) {
		this.chargeKeCheng = chargeKeCheng;
	}
	
}
