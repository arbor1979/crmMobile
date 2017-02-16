package com.yujieshipin.crm.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 字典表
 * 
 *  <br/>创建说明: 2013-11-30 下午1:58:54 yanzy  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
@DatabaseTable(tableName = "Dictionary")
public class Dictionary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@DatabaseField(id = true)
	private String id;
	@DatabaseField
	private String parentCode; 
	@DatabaseField
	private String parentName;
	@DatabaseField
	private String itemCode;
	@DatabaseField
	private String itemValue;
	
	

	public Dictionary() {

	}

	public Dictionary(JSONObject jo) {
		
	}
	
	public static List<Dictionary> toList(JSONArray ja) {
		List<Dictionary> result = new ArrayList<Dictionary>();
		Dictionary info = null;
		
		if (ja != null && ja.length() > 0) {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				info = new Dictionary(jo);
				result.add(info);
			}
			return result;
		}else{
			System.out.println("没有StudentAttence数据");
			return null;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemValue() {
		return itemValue;
	}

	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}

	
	
	
}
