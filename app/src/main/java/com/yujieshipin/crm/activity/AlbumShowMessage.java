package com.yujieshipin.crm.activity;

import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.AlbumImageInfo;
import com.yujieshipin.crm.entity.AlbumMsgInfo;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.TimeUtility;



public class AlbumShowMessage extends FragmentActivity {

	private ListView mList;
	
	private CommentAdapter mListAdapter;
	private ArrayList<AlbumMsgInfo> msgList;
	DatabaseHelper database;
	private Dao<AlbumMsgInfo,Integer> albumMsgDao;
	private int ifRead;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_unread_msg);
		ifRead=getIntent().getIntExtra("ifRead",0);
		getMsgList(ifRead);
		if(msgList.size()==0)
			AppUtility.showToastMsg(this,"还没有消息记录");
		initTitle();
		
	}
	
	private void initTitle() {
		
		Button bn_back = (Button) findViewById(R.id.back);
		bn_back.setVisibility(View.VISIBLE);
		bn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(ifRead==0)
					UpdateUnreadList(msgList);
				finish();
			}
		});
		mList=(ListView) findViewById(R.id.listView1);
		mListAdapter=new CommentAdapter();
		mList.setAdapter(mListAdapter);
		
		
	}
	public class CommentAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return msgList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return msgList.get(position);
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
				convertView = LayoutInflater.from(AlbumShowMessage.this).inflate(R.layout.list_album_unreadmsg_item, null);
				
				vh=new ViewHolder();
				vh.iv_icon=(ImageView)convertView.findViewById(R.id.iv_icon);
				vh.tv_title=(MyTextViewEx)convertView.findViewById(R.id.tv_title);
				vh.tv_left=(TextView)convertView.findViewById(R.id.theDescription);
				vh.right_image=(ImageView)convertView.findViewById(R.id.rightImage);
				vh.praise_image=(ImageView)convertView.findViewById(R.id.praiseImage);
				convertView.setTag(vh);
			}
			else
			{
				vh = (ViewHolder) convertView.getTag();
			}
			AlbumMsgInfo msg=msgList.get(position);
			JSONObject jo = null;
			AlbumImageInfo image;
			try {
				jo = new JSONObject(msg.getImageObject());
				
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(jo!=null)
				image=new AlbumImageInfo(jo);
			else
				image=new AlbumImageInfo();
			
			ImageOptions options = new ImageOptions();
			options.memCache=false;
			options.targetWidth=100;
			options.round = 50;
			aq.id(vh.iv_icon).image(msg.getFromHeadUrl(), options);
			if(msg.getType().equals("点赞"))
			{
				vh.praise_image.setVisibility(View.VISIBLE);
				vh.tv_title.setText("");
				vh.tv_title.insertGif(msg.getFromName()+":");
			}
			else
			{
				vh.praise_image.setVisibility(View.GONE);
				vh.tv_title.setText("");
				vh.tv_title.insertGif(msg.getFromName()+":"+msg.getMsg());
			}
			vh.tv_left.setText(TimeUtility.getDayTime(msg.getTime()));
			aq.id(vh.right_image).image(image.getUrl(),false,true,120,R.drawable.empty_photo);
			vh.iv_icon.setTag(msg);
			vh.iv_icon.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(AlbumShowMessage.this,
							ShowPersonInfo.class);
					AlbumMsgInfo u=(AlbumMsgInfo)v.getTag();
					intent.putExtra("studentId", u.getFromId());
					intent.putExtra("userImage", u.getFromHeadUrl());
					startActivity(intent);
				}
				
			});
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					ImageView iv=(ImageView) v.findViewById(R.id.iv_icon);
					AlbumMsgInfo ami=(AlbumMsgInfo) iv.getTag();
					try {
						JSONObject jo = new JSONObject(ami.getImageObject());
						AlbumImageInfo aii=new AlbumImageInfo(jo);
						if(aii.getName().length()!=0)
						{
							ArrayList<AlbumImageInfo> list=new ArrayList<AlbumImageInfo>();
							list.add(aii);
							Intent intent=new Intent(AlbumShowMessage.this,AlbumShowImagePage.class);
							intent.putExtra("imageList", list);
							startActivity(intent);
						}
						else
							AppUtility.showToastMsg(AlbumShowMessage.this,"原图片已不存在");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			});

			return convertView;
		}
		public class ViewHolder {
			public MyTextViewEx tv_title;
			public ImageView iv_icon;
			public TextView tv_left;
			public ImageView right_image;
			public ImageView praise_image;
		}
		
	}

	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return database;
	}
	private void UpdateUnreadList(ArrayList<AlbumMsgInfo> list)
	{
		
		try 
		{
			albumMsgDao=getHelper().getAlbumMsgDao();
			for(int i=0;i<list.size();i++)
			{
				AlbumMsgInfo ami=list.get(i);
				AlbumMsgInfo item=albumMsgDao.queryBuilder().where().eq("id",ami.getId()).queryForFirst();
				if(item!=null)
				{
					item.setIfRead(1);
					albumMsgDao.update(item);
				}
			}
		
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void getMsgList(int ifRead)
	{
		
		try {
			String hostId=PrefUtility.get(Constants.PREF_CHECK_HOSTID, "");
			msgList=(ArrayList<AlbumMsgInfo>) getHelper().getAlbumMsgDao().queryBuilder().orderBy("id", false).where().eq("ifRead",ifRead).and().eq("toId", hostId).query();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
