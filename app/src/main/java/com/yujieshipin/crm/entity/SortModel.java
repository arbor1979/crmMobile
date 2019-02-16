package com.yujieshipin.crm.entity;

import android.graphics.drawable.Drawable;
/**
 * 沟通——班级群组中数据
 * @author hfthink
 *
 */
public class SortModel {
	/**
	 * 班级名称
	 */
	private String name;
	/**
	 * 班级名称首字母
	 */
	private String sortLetters;
	/**
	 * 班级头像
	 */
	private Drawable img;

	public Drawable getImg() {
		return img;
	}

	public void setImg(Drawable img) {
		this.img = img;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
