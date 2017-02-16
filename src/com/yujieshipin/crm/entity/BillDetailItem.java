package com.yujieshipin.crm.entity;

import org.json.JSONObject;

public class BillDetailItem {
		

		private int id;
		private String prodid;
		private String title;
		private String detail;
		private int num;
		private String prodImage;
		private String hiddenBtn;
		private String hiddenBtnUrl;
		private String opertype;
		public BillDetailItem()

		{
			
		}
		
		public BillDetailItem(JSONObject jo) {
			
			id=jo.optInt("id");
			title=jo.optString("title");
			detail=jo.optString("detail");
			prodImage=jo.optString("prodImage");
			num=jo.optInt("num");
			hiddenBtn=jo.optString("hiddenBtn");
			hiddenBtnUrl=jo.optString("hiddenBtnUrl");
			opertype=jo.optString("opertype");
			prodid=jo.optString("prodid");
		}


		public String getProdid() {
			return prodid;
		}

		public void setProdid(String prodid) {
			this.prodid = prodid;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getOpertype() {
			return opertype;
		}

		public void setOpertype(String opertype) {
			this.opertype = opertype;
		}

		public String getHiddenBtn() {
			return hiddenBtn;
		}

		public void setHiddenBtn(String hiddenBtn) {
			this.hiddenBtn = hiddenBtn;
		}

		public String getHiddenBtnUrl() {
			return hiddenBtnUrl;
		}

		public void setHiddenBtnUrl(String hiddenBtnUrl) {
			this.hiddenBtnUrl = hiddenBtnUrl;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public int getNum() {
			return num;
		}

		public void setNum(int num) {
			this.num = num;
		}

		public String getProdImage() {
			return prodImage;
		}

		public void setProdImage(String prodImage) {
			this.prodImage = prodImage;
		}
		
	}