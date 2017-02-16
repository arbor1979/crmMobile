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
 *  功能说明: 课堂测验内容项
 * 
 *  <br/>创建说明: 2013-11-29 上午8:59:16 zhuliang  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
@DatabaseTable(tableName="Content")
public class Content implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//	final static String TESTENTITYID= "testentity_id";
	@DatabaseField(generatedId=true)
	private int id;
	
	@DatabaseField
	private int testId;
	/**
	 * 明细项
	 */
	@DatabaseField
	private String name;
	/**
	 * 学生答案
	 */
	@DatabaseField
	private String stu_answer;
	/**
	 * 正确答案
	 */
	@DatabaseField
	private String true_answer;
	/**
	 * 外键TestEntity
	 */
//	@DatabaseField(foreign=true,foreignAutoCreate=true,foreignColumnName=TESTENTITYID)
//	private TestEntity testEntity;
	List<ContentOptions> optionsList;
	public Content(){
		
	}
	
	public Content(JSONObject jo){
		this.name = jo.optString("明细项");
		this.stu_answer = jo.optString("学生答案");
		this.true_answer = jo.optString("正确答案");
		this.optionsList = ContentOptions.toList(jo.optJSONArray("选项"));
//		this.testEntity = testEntity;
	}
	
	public static List<Content> toList(JSONArray ja) {
		List<Content> result = new ArrayList<Content>();
		Content info = null;
		if (ja.length() == 0) {
			return null;
		} else {
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.optJSONObject(i);
				info = new Content(jo);
				result.add(info);
			}
			return result;
		}
	
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getTestId() {
		return testId;
	}

	public void setTestId(int testId) {
		this.testId = testId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStu_answer() {
		return stu_answer;
	}
	public void setStu_answer(String stu_answer) {
		this.stu_answer = stu_answer;
	}
	public String getTrue_answer() {
		return true_answer;
	}
	public void setTrue_answer(String true_answer) {
		this.true_answer = true_answer;
	}

	public List<ContentOptions> getOptionsList() {
		return optionsList;
	}

	public void setOptionsList(List<ContentOptions> optionsList) {
		this.optionsList = optionsList;
	}
	
	
}
