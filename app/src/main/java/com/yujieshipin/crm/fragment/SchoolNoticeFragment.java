package com.yujieshipin.crm.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AbstractAjaxCallback;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.activity.SchoolDetailActivity;
import com.yujieshipin.crm.activity.TabSchoolActivtiy;
import com.yujieshipin.crm.activity.WebSiteActivity;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.api.CampusException;
import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.RequestListener;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.Notice;
import com.yujieshipin.crm.entity.NoticesItem;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.widget.XListView;
import com.yujieshipin.crm.widget.XListView.IXListViewListener;

/**
 * 通知
 */
public class SchoolNoticeFragment extends Fragment implements IXListViewListener {
	private String TAG = "SchoolNoticeFragment";
	private XListView myListview;
	private Button btnLeft;
	private TextView tvTitle;
	private LinearLayout lyLeft;
	private LinearLayout loadingLayout;
	private LinearLayout contentLayout;
	private LinearLayout failedLayout;
	private LinearLayout emptyLayout;
	
	private String interfaceName,title;
	private LayoutInflater inflater;
	private NoticeAdapter adapter;
	private List<Notice> noticesList=new ArrayList<Notice>();
	
	private DatabaseHelper database;
	private Dao<Notice, Integer> noticeInfoDao;
	private String hostid=PrefUtility.get(Constants.PREF_CHECK_HOSTID,"");
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				showFetchFailedView();
				AppUtility.showErrorToast(getActivity(), msg.obj.toString());
				break;

			case 0:
			
			String result = msg.obj.toString();
			
			if (AppUtility.isNotEmpty(result)) {
				try {
					JSONObject jo = new JSONObject(result);
					String res = jo.optString("result");
					if(res.equals("失败")){
						AppUtility.showToastMsg(getActivity(), res);
					}else{
						NoticesItem noticesItem = new NoticesItem(jo);
						Log.d(TAG, "--------noticesItem.getNotices().size():"
								+ noticesItem.getNotices().size());
						List<Notice> notices = noticesItem.getNotices();
						
						for(Notice item:notices)
						{
							//item.setIfread("0");
							item.setNewsType(noticesItem.getTitle());
							item.setUserNumber(hostid);
							Notice nt=noticeInfoDao.queryBuilder().where().eq("id",item.getId()).and().eq("newsType", item.getNewsType()).and().eq("userNumber",hostid).queryForFirst();
							if(nt==null)
								noticeInfoDao.create(item);
						}
						myListview.stopRefresh();
						getNoticesList();
						myListview.setSelection(0);
						Intent intent = new Intent("refreshUnread");
						intent.putExtra("title", noticesItem.getTitle());
						getActivity().sendBroadcast(intent);
						
					}
				} catch (JSONException e) {
					showFetchFailedView();
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				showFetchFailedView();
			}
			break;
		
		}
		}
	};
	public SchoolNoticeFragment() {
		
	}
	public static final Fragment newInstance(String title, String interfaceName){
    	Fragment fragment = new SchoolNoticeFragment();
    	Bundle bundle = new Bundle();
    	bundle.putString("title", title);
    	bundle.putString("interfaceName", interfaceName);
    	fragment.setArguments(bundle);
    	return fragment;
    }


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		title=getArguments().getString("title");
		interfaceName=getArguments().getString("interfaceName");
		this.inflater = inflater;
		View view = inflater.inflate(R.layout.school_xlistview_fragment,
				container, false);
		RelativeLayout navibar=(RelativeLayout)view.findViewById(R.id.navibar);
		int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			navibar.setBackgroundColor(color);
		myListview = (XListView) view.findViewById(R.id.my_listview);
		myListview.setDivider(getResources().getDrawable(R.color.transparent));
		btnLeft = (Button) view.findViewById(R.id.btn_left);
		lyLeft = (LinearLayout) view.findViewById(R.id.layout_btn_left);
		tvTitle=(TextView) view.findViewById(R.id.tv_title);
		loadingLayout = (LinearLayout) view.findViewById(R.id.data_load);
		contentLayout = (LinearLayout) view.findViewById(R.id.content_layout);
		failedLayout = (LinearLayout) view.findViewById(R.id.empty_error);
		emptyLayout = (LinearLayout) view.findViewById(R.id.empty);
		myListview.setEmptyView(emptyLayout);
		myListview.setPullRefreshEnable(true);
		myListview.setPullLoadEnable(false);
		myListview.setXListViewListener(this);
		btnLeft.setVisibility(View.VISIBLE);
		btnLeft.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.bg_btn_left_nor, 0, 0, 0);
		adapter = new NoticeAdapter();
		tvTitle.setText(title);
		myListview.setAdapter(adapter);
		lyLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		//重新加载
		failedLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getNoticesList();
			}
		});
		Button btn_right=(Button)view.findViewById(R.id.btn_right);
		LinearLayout lyRight = (LinearLayout) view.findViewById(R.id.layout_btn_right);
		btn_right.setVisibility(View.VISIBLE);
		btn_right.setBackgroundResource(R.drawable.shuaizi);
		lyRight.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				AlertDialog.Builder showbuilder = new AlertDialog.Builder(getActivity());  
				showbuilder.setTitle("信息提示");
				showbuilder.setMessage("是否清除所有未读状态？");  
				showbuilder.setPositiveButton("确定", new clearNoticeListener());  
				showbuilder.setNegativeButton("取消", new DialogInterface.OnClickListener(){
					@Override  
			        public void onClick(DialogInterface dialog, int which) {  
			        	dialog.dismiss();
			        } 
				});
	            AlertDialog ad = showbuilder.create();  
	            ad.show();
			}
			
		});
		return view;
	}

	//监听类  
    private class clearNoticeListener implements DialogInterface.OnClickListener{  
        
		@Override  
        public void onClick(DialogInterface dialog, int which) {  
        	try {
        		
        		
				List<Notice> unreadList=noticeInfoDao.queryBuilder().where().eq("userNumber",hostid).and().eq("newsType", title).and().eq("ifread", "0").query();
				for(Notice item:unreadList)
				{
					item.setIfread("1");
					noticeInfoDao.update(item);
				}
				getNoticesList();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	Intent intent = new Intent("refreshUnread");
			intent.putExtra("title", title);
			getActivity().sendBroadcast(intent);
        	
        	
        }  
    } 
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getNoticesList();
	}
	@Override
	public void onStart()
	{
		super.onStart();
		adapter.notifyDataSetChanged();
	}
	@SuppressWarnings("deprecation")
	private void getNoticesList()
	{
		try {
			noticeInfoDao = getHelper().getNoticeInfoDao();
			
			noticesList=noticeInfoDao.queryBuilder().orderBy("autoid", false).where().eq("newsType", title).and().eq("userNumber", hostid).query();
			if(noticesList==null)
				noticesList=new ArrayList<Notice>();
			adapter.notifyDataSetChanged();
			showProgress(false);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);

		}
		return database;
	}
	/**
	 * 显示加载失败提示页
	 */
	private void showFetchFailedView() {
		loadingLayout.setVisibility(View.GONE);
		contentLayout.setVisibility(View.GONE);
		failedLayout.setVisibility(View.VISIBLE);
	}
	
	private void showProgress(boolean progress) {
		if (progress) {
			loadingLayout.setVisibility(View.VISIBLE);
			contentLayout.setVisibility(View.GONE);
			failedLayout.setVisibility(View.GONE);
		} else {
			loadingLayout.setVisibility(View.GONE);
			contentLayout.setVisibility(View.VISIBLE);
			failedLayout.setVisibility(View.GONE);
		}
	}
	
	class NoticeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return noticesList.size();
		}

		@Override
		public Object getItem(int position) {
			return noticesList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (null == convertView) {
				convertView = inflater.inflate(R.layout.school_notice_item,
						parent, false);
				holder = new ViewHolder();

				holder.title = (TextView) convertView.findViewById(R.id.tv_title);
				holder.time = (TextView) convertView.findViewById(R.id.tv_time);
				holder.image = (ImageView) convertView.findViewById(R.id.iv_image);
				holder.content = (TextView) convertView.findViewById(R.id.tv_content);
				holder.readMore = (TextView) convertView.findViewById(R.id.tv_read_more);
				holder.openDetail=(LinearLayout) convertView.findViewById(R.id.rl_detail);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final Notice notice = (Notice) getItem(position);
			if(notice.getIfread().equals("0"))
				holder.openDetail.setBackgroundColor(getResources().getColor(R.color.unread));
			else
				holder.openDetail.setBackgroundColor(Color.WHITE);
			AQuery aq = new AQuery(convertView);
			AbstractAjaxCallback.setTimeout(30000);
			String imagurl = notice.getImageUrl();
			Log.d(TAG, "----imagurl:" + imagurl);
			if (imagurl != null && !imagurl.toLowerCase().equals("null") && imagurl.length()>0) {
				aq.id(holder.image).image(imagurl,false,true,0,0);
			} else {
				aq.id(holder.image).visibility(View.GONE);
			}
			holder.title.setText(notice.getTitle());
			if(notice.getTitle().length()>4 && notice.getTitle().substring(notice.getTitle().length()-4, notice.getTitle().length()).equals("[附件]"))
			{
				Drawable d = getResources().getDrawable(R.drawable.fujian);  
			    d.setBounds(5, 0, 30, 24);
			    //创建ImageSpan
			    ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
			    //用ImageSpan替换文本
			    SpannableString ss = new SpannableString(notice.getTitle());
			    ss.setSpan(span, notice.getTitle().length()-4, notice.getTitle().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);  
			    holder.title.setText(ss);
			}
			
		    
			holder.time.setText(notice.getTime());
			String content=notice.getContent();
			if(content.length()>100)
				content=content.substring(0,100)+"...";
			holder.content.setText(Html.fromHtml(content));
			holder.readMore.setText(notice.getEndText());
			//查看详细信息
			holder.openDetail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(notice.getEndUrl().substring(0, 4).equalsIgnoreCase("http"))
					{
						Intent contractIntent = new Intent(getActivity(),WebSiteActivity.class);
						contractIntent.putExtra("url",notice.getEndUrl());
						contractIntent.putExtra("title",title+"详情");
						getActivity().startActivity(contractIntent);
					}
					else
					{
						Intent intent =new Intent(getActivity(),SchoolDetailActivity.class);
						intent.putExtra("templateName", "通知");
						intent.putExtra("interfaceName", interfaceName+notice.getEndUrl());
						intent.putExtra("title", title);
						getActivity().startActivity(intent);
					}
					if(notice.getIfread().equals("0"))
					{
						notice.setIfread("1");
						try {
							noticeInfoDao.update(notice);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						updateOANewsIfRead(notice.getId());
					}
					Intent intent = new Intent("refreshUnread");
					intent.putExtra("title", notice.getNewsType());
					getActivity().sendBroadcast(intent);
				}
			});

			return convertView;
		}

		class ViewHolder {
			TextView title;
			TextView time;
			ImageView image;
			TextView content;
			TextView readMore;
			LinearLayout openDetail;
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		getNoticesItem();
	}
	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}
	public void updateOANewsIfRead(int news_id)
	{
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("function", interfaceName.substring(0, interfaceName.length()-4));
			jo.put("ID", news_id);
			jo.put("action", "ifread");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jo, mHandler, 3);
	}
	public void getNoticesItem() 
	{
		
		Log.d(TAG, "--------" + String.valueOf(new Date().getTime()));
		long datatime = System.currentTimeMillis();
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		Log.d(TAG, "----------datatime:" + datatime);
		Log.d(TAG, "----------checkCode:" + checkCode + "++");
		int lastId=0;
		try {
			
			Notice nt=noticeInfoDao.queryBuilder().orderBy("id", false).where().eq("newsType", title).and().eq("userNumber", hostid).queryForFirst();
			if(nt!=null)
				lastId=nt.getId();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		JSONObject jo = new JSONObject();
		try {
			jo.put("用户较验码", checkCode);
			jo.put("function", interfaceName.substring(0, interfaceName.length()-4));
			jo.put("LASTID", lastId);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jo, mHandler, 0);
	}

}
