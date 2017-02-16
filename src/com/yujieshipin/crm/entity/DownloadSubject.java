package com.yujieshipin.crm.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "DownloadSubject")
/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 课件下载
 * 
 *  <br/>创建说明: 2013-11-22 下午3:56:54 linrr  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */

public class DownloadSubject {


	

	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private String name;
	@DatabaseField
	private String loadCount;
	@DatabaseField
	private String lastDown;
	@DatabaseField
	private String downAddress;
	@DatabaseField
	private String fileName;
	@DatabaseField
	private int isModify=50;
	@DatabaseField
	private String filecontent;
	@DatabaseField
	private long filesize;
	@DatabaseField
	private String localfile;
	private String userName;
	@DatabaseField
	private String courseName;
	
	private int index;
	
	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getFilecontent() {
		return filecontent;
	}

	public void setFilecontent(String b) {
		this.filecontent = b;
	}

	public DownloadSubject() {

	}
	public int getIsModify() {
		return isModify;
	}
	
	public String getLocalfile() {
		return localfile;
	}

	public void setLocalfile(String localfile) {
		this.localfile = localfile;
	}

	public void setIsModify(int isModify) {
		this.isModify = isModify;
	}

	public DownloadSubject(JSONObject jo) {
		this.name = jo.optString("名称");
		this.loadCount = jo.optString("下载次数");
		this.lastDown = jo.optString("最后一次下载");
		this.downAddress = jo.optString("文件地址");
		this.fileName = jo.optString("文件名");
		this.filesize = jo.optLong("文件大小");
		this.courseName = jo.optString("课程名称");
	}
	
	public DownloadSubject(net.minidev.json.JSONObject jo) {
		this.name = String.valueOf(jo.get("名称"));
		this.loadCount = String.valueOf(jo.get("下载次数"));
		this.lastDown = String.valueOf(jo.get("最后一次下载"));
		this.downAddress = String.valueOf(jo.get("文件地址"));
		this.fileName = String.valueOf(jo.get("文件名"));
		this.filesize = Long.parseLong(jo.get("文件大小").toString());
		this.courseName = String.valueOf(jo.get("课程名称"));
	}

	// private String[] toStrArray(JSONArray ja) {
	// String[] strArray = new String[ja.length()];
	// for (int i = 0; i < ja.length(); i++) {
	// strArray[i] = ja.optString(i);
	// }
	// return strArray;
	// }
	//
	// private String getResult(JSONObject jo, String key) {
	// JSONArray ja = jo.optJSONArray(key);
	// String[] result = null;
	// if (ja != null) {
	// result = toStrArray(ja);
	// }
	// StringBuffer strbuff = new StringBuffer();
	//
	// for (int i = 0; i < result.length; i++) {
	// strbuff.append(",").append(result[i]);
	// }
	//
	// String str = strbuff.deleteCharAt(0).toString();
	// return str;
	// // String[] result = null;
	// }

	public static List<DownloadSubject> toList(JSONArray ja) {
		List<DownloadSubject> result = new ArrayList<DownloadSubject>();
		Log.d("DownloadSubject","ja.length" + ja.length()
				+ "List<DownloadSubject> toListssss");
		DownloadSubject info = null;
		if (ja.length() == 0) {
			Log.d("DownloadSubject","没有DownloadSubject数据");
			return result;
		} else {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				if(jo != null){
					info = new DownloadSubject(jo);
					result.add(info);
				}	
			}
			return result;
		}
	}
	public static List<DownloadSubject> toList(net.minidev.json.JSONArray ja) {
		List<DownloadSubject> result = new ArrayList<DownloadSubject>();
		Log.d("DownloadSubject","ja.length" + ja.size()
				+ "List<DownloadSubject> toListssss");
		DownloadSubject info = null;
		if (ja.size() == 0) {
			Log.d("DownloadSubject","没有DownloadSubject数据");
			return result;
		} else {
			for (int i = 0; i < ja.size(); i++) {
				net.minidev.json.JSONObject jo = (net.minidev.json.JSONObject)ja.get(i);
				if(jo != null){
					info = new DownloadSubject(jo);
					result.add(info);
				}	
			}
			return result;
		}
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLoadCount() {
		return loadCount;
	}

	public void setLoadCount(String loadCount) {
		this.loadCount = loadCount;
	}

	public String getLastDown() {
		return lastDown;
	}

	public void setLastDown(String lastDown) {
		this.lastDown = lastDown;
	}

	public String getDownAddress() {
		return downAddress;
	}

	public void setDownAddress(String downAddress) {
		this.downAddress = downAddress;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
