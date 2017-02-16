package com.yujieshipin.crm.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.entity.TestEntityItem;
import com.yujieshipin.crm.util.AppUtility;

/**
 * 
 * 功能说明: 测验数据适配器
 * 
 * <br/>
 * 创建说明: 2013-12-6 下午7:49:07 zhuliang 创建文件<br/>
 * 
 * 修改历史:<br/>
 * 
 */
public class TestAdapter extends BaseAdapter {
	private String TAG = "TestAdapter";
	List<TestEntityItem> data = new ArrayList<TestEntityItem>();
	LayoutInflater inflater;
	private String[] answerStr = { "A", "B", "C", "D", "E", "F" };
	private String[] answers;//单选选项,答案集合
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Boolean> isConsummation = new HashMap<Integer, Boolean>();
	private Context context;
	private Boolean isEnable;//是否可以点击
	private String userType , testStatus;

	public TestAdapter(Context context, List<TestEntityItem> data,String userType) {
		super();
		this.context = context;
		this.data = data;
		this.userType = userType;
		inflater = LayoutInflater.from(context);
		answers= new String[data.size()];
		initData();
	}
	private void initData(){
		for (int i = 0; i < data.size(); i++) {
			isConsummation.put(i, false);
		}
	}
	@Override
	public int getCount() {
		return data == null ? 0 : data.size();
	}

	public void setList(List<TestEntityItem> data) {
		this.data = data;
		answers= new String[data.size()];
		initData();
	}
	
	public void setTestStatus(String testStatus) {
		this.testStatus = testStatus;
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public Boolean getIsEnable() {
		return isEnable;
	}
	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}
	/**
	 * 功能描述:
	 *
	 * @author shengguo  2014-4-29 下午5:27:40
	 * 
	 * @return判断是否为全部选中
	 */
	public boolean getIsConsummation(){
		for (int i = 0; i < data.size(); i++) {
			if(!isConsummation.get(i)){
				return false;
			};
		}
		return true;
	};
	
	public String getAnswer() {
		StringBuffer strb = new StringBuffer();
		for (int i = 0; i < getCount(); i++) {
			strb.append("\"").append(answers[i]).append("\",");
		}
		if(strb.indexOf(",")!=-1){
			strb.deleteCharAt(strb.lastIndexOf(","));
		}
		return strb.toString() ;
	}
	
	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final TestEntityItem testEntity = (TestEntityItem) getItem(position);
		convertView = inflater.inflate(R.layout.view_list_testing_new, parent,
				false);

		RadioGroup group = (RadioGroup) convertView
				.findViewById(R.id.answer_group);
		TextView title = (TextView) convertView
				.findViewById(R.id.testing_title);
		TextView remark = (TextView) convertView.findViewById(R.id.tv_remark);
		title.setText(testEntity.getTopicName());
		String string = null;
		String isOk="";
		if(userType.equals("老师")){
			string = testEntity.getCFS();
		}else{
			isOk = testEntity.getStudentAnswerStatus();
			string = isOk + (isOk.equals("正确")?"":" 正确答案:"+testEntity.getAnswer());
		}
		Log.d(TAG, "------------string:"+string);
		if (testStatus!=null && testStatus.equals("已结束") && AppUtility.isNotEmpty(string)) {
			remark.setText(string);
			remark.setVisibility(View.VISIBLE);
			if(isOk.equals("正确"))
				remark.setTextColor(Color.parseColor("#008000"));
			else
				remark.setTextColor(Color.RED);
		}else{
			remark.setVisibility(View.GONE);
		}
		if (AppUtility.isNotEmpty(testEntity.getaAnswer())) {
			RadioButton button = getRadioButton("A."+testEntity.getaAnswer(), 0);
			group.addView(button);
		}
		if (AppUtility.isNotEmpty(testEntity.getbAnswer())) {
			RadioButton button = getRadioButton("B."+testEntity.getbAnswer(), 1);
			group.addView(button);
		}
		if (AppUtility.isNotEmpty(testEntity.getcAnswer())) {
			RadioButton button = getRadioButton("C."+testEntity.getcAnswer(), 2);
			group.addView(button);
		}
		if (AppUtility.isNotEmpty(testEntity.getdAnswer())) {
			RadioButton button = getRadioButton("D."+testEntity.getdAnswer(), 3);
			group.addView(button);
		}
		if (AppUtility.isNotEmpty(testEntity.geteAnswer())) {
			RadioButton button = getRadioButton("E."+testEntity.geteAnswer(), 4);
			group.addView(button);
		}
		if (AppUtility.isNotEmpty(testEntity.getfAnswer())) {
			RadioButton button = getRadioButton(testEntity.getfAnswer(), 5);
			group.addView(button);
		}
		//设置默认选中
		String answer = testEntity.getAnswer();//正确答案
		//String answer = testEntity.getStudentAnswerResult();//用户答案
		Log.d(TAG, "------------answer------:"+answer);
		if (userType.equals("老师")) {
			for (int i = 0; i < answerStr.length; i++) {
				if (answer.equals(answerStr[i])) {
					group.check(i);
					answers[position] = answerStr[i];
					isConsummation.put(position, true);
				}
			}
		}
		else
		{
			String userAnswer = testEntity.getStudentAnswerResult();
		
			for (int i = 0; i < answerStr.length; i++) {
				if (userAnswer.equals(answerStr[i])) {
					group.check(i);
					answers[position] = answerStr[i];
					isConsummation.put(position, true);
				}
			}
		}
		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				answers[position] = answerStr[checkedId];
				isConsummation.put(position, true);
				data.get(position).setStudentAnswerResult(answerStr[checkedId]);
			}
		});
		return convertView;
	}

	@SuppressWarnings("deprecation")
	private RadioButton getRadioButton(String str, int id) {
		RadioButton rb = new RadioButton(context);
		rb.setButtonDrawable(null);
		rb.setBackgroundDrawable(null);
		rb.setId(id);
		rb.setText(str);
		rb.setEnabled(isEnable);
		return rb;
	}
}
