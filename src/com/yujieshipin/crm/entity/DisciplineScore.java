package com.yujieshipin.crm.entity;

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
 *  功能说明: 评分（扣分，加分）
 *  "考勤名称":"出勤,迟到,缺勤,请假",
 "考勤分值":"1,-1,-3,0,0",
 "加分名称":"完成作业良好,课堂积极发言,撰写课堂笔记",
 "加分分值":"1,1,1",
 "减分名称":"玩手机、小声说话、随意走动、扰乱课堂",
 "减分分值":"-1,-1,-1,-2",
 *  
 *  <br/>创建说明: 2013-11-22 下午4:19:23 linrr  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
@DatabaseTable(tableName="DisciplineScore")
public class DisciplineScore {
	@DatabaseField
	private String disciplineName;//考勤名称
		@DatabaseField
	private String disciplineScore;//考勤分值
		@DatabaseField
	private String addScoreName;//加分名称
		@DatabaseField
		private String addScore;//加分分值
		@DatabaseField
	private String minusName;//减分名称
		@DatabaseField
		private String minusScore;//减分分值
		private String disciplineName1[];
		private String disciplineScore1[];
		private String addScoreName1[];//加分名称
		private String addScore1[];//加分名称
		private String minusName1[];//加分名称
		private String minusScore1[];	//加分名称
		public DisciplineScore(){}
		public DisciplineScore(JSONObject jo) {
			disciplineName1 = getResult(jo,"考勤名称");
			System.out.println(disciplineName1.toString()+">>>>>>>>>>>>>>>>>>>");
			disciplineScore1 =getResult(jo,"考勤分值");
			//for(int i=0;i<disciplineName1.length;i++){
				if(disciplineName1[0].equals("出勤")){
					disciplineScore1[0]=1+"";
				}else if(disciplineName1[1].equals("迟到")){
					disciplineScore1[1]=-1+"";
				}
				else if(disciplineName1[2].equals("缺勤")){
					disciplineScore1[2]=-3+"";
				}
				else if(disciplineName1[3].equals("请假")){
					disciplineScore1[3]=0+"";
				}
			//}
			addScoreName1 = getResult(jo,"加分名称");
			addScore1 =getResult(jo, "加分分值");
			//for(int i=0;i<addScoreName1.length;i++){
				if(addScoreName1[0].equals("完成作业良好")){
					addScore1[0]=1+"";
				}else if(addScoreName1[1].equals("课堂积极发言")){
					addScore1[1]=1+"";
				}
				else if(addScoreName1[2].equals("撰写课堂笔记")){
					addScore1[2]=1+"";
				}
			//}
			minusName1 = getResult(jo,"减分名称");
			minusScore1 =getResult(jo,"減分分值");
			//for(int i=0;i<minusName1.length;i++){
				if(minusName1[0].equals("玩手机")){
					minusScore1[0]=-1+"";
				}else if(minusName1[1].equals("小声说话")){
					minusScore1[1]=-1+"";
				}
				else if(minusName1[2].equals("随意走动")){
					minusScore1[2]=-1+"";
				}
				else if(minusName1[2].equals("扰乱课堂")){
					minusScore1[2]=-2+"";
				}
			//}
			disciplineName = parse(disciplineName1);
			disciplineScore=parse(disciplineScore1);
			addScoreName = parse(addScoreName1);
			addScore=parse(addScore1);
			minusName = parse(minusName1);
			minusScore=parse(minusScore1);
		}
		private String[] getResult(JSONObject jo, String key) {
			JSONArray ja = jo.optJSONArray(key);
			String[] result = null;
			if (ja != null) {
				result = toStrArray(ja);
			}
			System.out.println(result.toString());
			return result;
//			
		}
/**
 * 功能描述:
 *将字符串数组转换成字符串
 * @author linrr  2013-11-22 下午4:41:57
 * 
 * @param result
 * @return
 */
		public String parse(String[] result){
			StringBuffer strbuff = new StringBuffer();

		for (int i = 0; i < result.length; i++) {
			strbuff.append(",").append(result[i]);
		}

		String str = strbuff.deleteCharAt(0).toString();
		return str;
		}
		private String[] toStrArray(JSONArray ja) {
			String[] strArray = new String[ja.length()];
			for (int i = 0; i < ja.length(); i++) {
				strArray[i] = ja.optString(i);
			}
			return strArray;
		}
		public String getDisciplineName() {
			return disciplineName;
		}
		public void setDisciplineName(String disciplineName) {
			this.disciplineName = disciplineName;
		}
		public String getDisciplineScore() {
			return disciplineScore;
		}
		public void setDisciplineScore(String disciplineScore) {
			this.disciplineScore = disciplineScore;
		}
		public String getAddScoreName() {
			return addScoreName;
		}
		public void setAddScoreName(String addScoreName) {
			this.addScoreName = addScoreName;
		}
		public String getAddScore() {
			return addScore;
		}
		public void setAddScore(String addScore) {
			this.addScore = addScore;
		}
		public String getMinusName() {
			return minusName;
		}
		public void setMinusName(String minusName) {
			this.minusName = minusName;
		}
		public String getMinusScore() {
			return minusScore;
		}
		public void setMinusScore(String minusScore) {
			this.minusScore = minusScore;
		}
}
