package com.yujieshipin.crm.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
public class Question implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -5740781476744525865L;
	private String title;
	private String status;
	private String usersAnswer;
	private String remark;
	private int lines;
	private boolean ifRead;
	private String backgroundcolor;
	private String callback;
	private boolean ifHide;
	public int getLines() {
		return lines;
	}

	public void setLines(int lines) {
		this.lines = lines;
	}

	private List<JSONObject> options;
	private List<ImageItem> images;
	private String isRequired;
	private String validate;
	private String remardColor;
	private String colorName;
	private String colorImage;
	private String imageFolder;
	public String getValidate() {
		return validate;
	}

	public String getRemardColor() {
		return remardColor;
	}

	public void setRemardColor(String remardColor) {
		this.remardColor = remardColor;
	}

	public String getColorName() {
		return colorName;
	}

	public String getColorImage() {
		return colorImage;
	}

	public String getImageFolder() {
		return imageFolder;
	}

	public Question(JSONObject jo) {
		title = jo.optString("题目");
		status = jo.optString("类型");
		remark = jo.optString("备注");
		remardColor=jo.optString("备注颜色");
		Log.d("-----", jo.toString());
			/*
			JSONArray ja = jo.optJSONArray("选项");
			if(ja!=null){
				options = new String[ja.length()];
				for (int i = 0; i < ja.length(); i++) {
					options[i] = ja.optString(i);
				}
			}
			*/
		options=new ArrayList<JSONObject>();
		try {
			JSONArray josArr=jo.optJSONArray("选项");
			if(josArr!=null) {
				for (int i = 0; i < josArr.length(); i++) {
					JSONObject obj = (JSONObject) josArr.get(i);
					if (obj != null)
						options.add(obj);

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isRequired = jo.optString("是否必填");
		lines=jo.optInt("行数");
		if(status.equals("图片")){
			JSONArray jaimages = jo.optJSONArray("用户答案");
			if(jaimages!=null){
				setImages(ImageItem.toList(jaimages));
			}else{
				setImages(new ArrayList<ImageItem>());
			}
		}else{
			usersAnswer = jo.optString("用户答案");
		}
		ifRead=jo.optBoolean("只读");
		validate=jo.optString("校验");
		backgroundcolor=jo.optString("背景色");
		callback=jo.optString("回调");
		ifHide=jo.optBoolean("隐藏");
		colorImage=jo.optString("颜色图片");
		colorName=jo.optString("颜色名称");
		imageFolder=jo.optString("图片目录");
	}

	public boolean isIfHide() {
		return ifHide;
	}

	public void setIfHide(boolean ifHide) {
		this.ifHide = ifHide;
	}

	public String getBackgroundcolor() {
		return backgroundcolor;
	}

	public void setBackgroundcolor(String backgroundcolor) {
		this.backgroundcolor = backgroundcolor;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public void setValidate(String validate) {
		this.validate = validate;
	}

	public boolean isIfRead() {
		return ifRead;
	}

	public void setIfRead(boolean ifRead) {
		this.ifRead = ifRead;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUsersAnswer() {
		return usersAnswer;
	}

	public void setUsersAnswer(String usersAnswer) {
		this.usersAnswer = usersAnswer;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}



	public List<JSONObject> getOptions() {
		return options;
	}

	public void setOptions(List<JSONObject> options) {
		this.options = options;
	}

	public String getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(String isRequired) {
		this.isRequired = isRequired;
	}

	public List<ImageItem> getImages() {
		return images;
	}

	public void setImages(List<ImageItem> images) {
		this.images = images;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	public void setColorImage(String colorImage) {
		this.colorImage = colorImage;
	}
}
