package com.yujieshipin.crm.entity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
/**
 * 发动态中本地图片信息
 * @author shengguo
 */
public class ImageInfo {

	public int id;
	public Bitmap icon;//路径下的一张图片
	public String displayName;//
	public String path;//文件路径
	public int picturecount;//图片数量
	public List<String> tag=new ArrayList<String>();//路径下图片路径集合
}
