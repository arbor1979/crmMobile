package com.yujieshipin.crm.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.example.androidgifdemo.MyTextViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.entity.AlbumMsgInfo;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.TimeUtility;



public class AlbumShowImageDetail extends FragmentActivity {

	private ListView mList;
	private TextView mText;
	private CommentAdapter mListAdapter;
	private String type;
	private ArrayList<AlbumMsgInfo> userList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_praise_detail);
		userList=(ArrayList<AlbumMsgInfo>)getIntent().getSerializableExtra("userList");
		type=(String)getIntent().getStringExtra("type");
		initTitle();
		
	}
	
	private void initTitle() {
		
		Button bn_back = (Button) findViewById(R.id.back);
		bn_back.setVisibility(View.VISIBLE);
		bn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mList=(ListView) findViewById(R.id.listView1);
		mText=(TextView)findViewById(R.id.viewMore);
		mText.setText(userList.size()+"人"+type);
		
		mListAdapter=new CommentAdapter();
		mList.setAdapter(mListAdapter);
		
		
	}
	public class CommentAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return userList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return userList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh;
			AQuery aq = new AQuery(convertView);
			if (convertView == null) 
			{		
				convertView = LayoutInflater.from(AlbumShowImageDetail.this).inflate(R.layout.fragment_album_comment, null);
				
				vh=new ViewHolder();
				vh.iv_icon=(ImageView)convertView.findViewById(R.id.iv_icon);
				vh.tv_title=(MyTextViewEx)convertView.findViewById(R.id.tv_title);
				vh.tv_left=(TextView)convertView.findViewById(R.id.thieDescription);
				convertView.setTag(vh);
			}
			else
			{
				vh = (ViewHolder) convertView.getTag();
			}
            /*
			ImageOptions options = new ImageOptions();
			options.memCache=false;
			options.targetWidth=100;
			options.round = 50;
			aq.id(vh.iv_icon).image(userList.get(position).getFromHeadUrl(), options);
			*/

            ImageLoader.getInstance().displayImage(userList.get(position).getFromHeadUrl(),vh.iv_icon,AppUtility.headOptions);
			if(type.equals("喜欢"))
				vh.tv_title.setText(userList.get(position).getFromName());
			else
			{
				vh.tv_title.setText("");
				vh.tv_title.insertGif(userList.get(position).getFromName()+":"+userList.get(position).getMsg());
			}
			
			String timeStr=TimeUtility.getDayTime(userList.get(position).getTime());
			
			vh.tv_left.setText(timeStr);
			vh.iv_icon.setTag(userList.get(position));
			vh.iv_icon.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(AlbumShowImageDetail.this,
							ShowPersonInfo.class);
					AlbumMsgInfo u=(AlbumMsgInfo)v.getTag();
					intent.putExtra("studentId", u.getFromId());
					intent.putExtra("userImage", u.getFromHeadUrl());
					startActivity(intent);
				}
				
			});
			return convertView;
		}
		public class ViewHolder {
			public MyTextViewEx tv_title;
			public ImageView iv_icon;
			public TextView tv_left;
			
		}
		
	}

}
