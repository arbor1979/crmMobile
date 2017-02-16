package com.yujieshipin.crm.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ContentOptions")
public class ContentOptions implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String option;
	@DatabaseField
	private String value;
	@DatabaseField
	private int contentId;
	
	public ContentOptions(){
		
	}
	
	public ContentOptions(JSONObject jo){
		this.option = jo.optString("名称");
		this.value = jo.optString("值");
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOption() {
		return option;
	}
	public void setOption(String option) {
		this.option = option;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	

	public int getContentId() {
		return contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public static List<ContentOptions> toList(JSONArray ja){
		List<ContentOptions> mList = new ArrayList<ContentOptions>();
		if(ja != null && ja.length() > 0){
			for(int i = 0; i < ja.length(); i++){
				JSONObject jo = ja.optJSONObject(i);
				ContentOptions options = new ContentOptions(jo);
				mList.add(options);
			}
		}
		return mList;
	}
}
