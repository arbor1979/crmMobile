package com.yujieshipin.crm.entity;

import org.json.JSONObject;

public class BillDetailItem {
		

		private int id;
		private String prodid;
		private String title;
		private String detail;
		private int num;
		private int zhekou;
		private boolean isShowzhekou;
		private boolean isEditzhekou;
        private double jine;
        private boolean isShowJine;
        private boolean isEditJine;
        private boolean isEditNum;
        private int progress;
        private String progressText;
        private String rightText;

	public boolean isEditNum() {
		return isEditNum;
	}

	public JSONObject getColorNum() {
		return colorNum;
	}

	private JSONObject colorNum;
	public int getZhekou() {
		return zhekou;
	}

	public void setZhekou(int zhekou) {
		this.zhekou = zhekou;
	}

	private String prodImage;
		private String hiddenBtn;
		private String hiddenBtnUrl;
		private String opertype;

	public String getPicDesc() {
		return picDesc;
	}

	public void setPicDesc(String picDesc) {
		this.picDesc = picDesc;
	}

	private String picDesc;
	private boolean showmemo;
	private String beizhu;
		public BillDetailItem()

		{
			
		}

	public boolean isShowzhekou() {
		return isShowzhekou;
	}

	public boolean isEditzhekou() {
		return isEditzhekou;
	}

    public double getJine() {
        return jine;
    }

    public boolean isShowJine() {
        return isShowJine;
    }

    public boolean isEditJine() {
        return isEditJine;
    }

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getProgressText() {
		return progressText;
	}

	public void setProgressText(String progressText) {
		this.progressText = progressText;
	}

	public boolean isShowmemo() {
		return showmemo;
	}

	public String getBeizhu() {
		return beizhu;
	}

	public BillDetailItem(JSONObject jo) {
			
			id=jo.optInt("id");
			title=jo.optString("title");
			detail=jo.optString("detail");
			prodImage=jo.optString("prodImage");
			num=jo.optInt("num");
			zhekou=jo.optInt("zhekou");
			hiddenBtn=jo.optString("hiddenBtn");
			hiddenBtnUrl=jo.optString("hiddenBtnUrl");
			opertype=jo.optString("opertype");
			prodid=jo.optString("prodid");
			isShowzhekou=jo.optBoolean("showzhekou");
            isEditzhekou=jo.optBoolean("editzhekou");
            jine=jo.optDouble("jine");
            isShowJine=jo.optBoolean("showjine");
            isEditJine=jo.optBoolean("editjine");
			isEditNum=jo.optBoolean("editnum");
			colorNum=jo.optJSONObject("colornum");
			if(jo.optString("进度条").length()>0)
				progress=jo.optInt("进度条");
			else
				progress=-1;
			progressText=jo.optString("进度条文本");
			rightText=jo.optString("rightText");
			showmemo=jo.optBoolean("showmemo");
			beizhu=jo.optString("beizhu");
			picDesc=jo.optString("picDesc");
		}

	public String getRightText() {
		return rightText;
	}

	public void setRightText(String rightText) {
		this.rightText = rightText;
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