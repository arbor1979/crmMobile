package com.yujieshipin.crm.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.SchoolActivity;
import com.yujieshipin.crm.activity.WebSiteActivity;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.SchoolWorkItem;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.BadgeView;
import com.yujieshipin.crm.util.PrefUtility;

import org.json.JSONException;
import org.json.JSONObject;

public class SchoolWorkAdapter extends SectionedRecyclerViewAdapter<HeaderHolder, DescHolder, RecyclerView.ViewHolder> {
	private String TAG = "SchoolWorkAdapter";

	private List<SchoolWorkItem> schoolWorkItems;
	private List<String> groupList=new ArrayList<String>();
	private Context mContext;
	private LayoutInflater mInflater;
	private Map<String,List<SchoolWorkItem>> allItemMap = new HashMap<String,List<SchoolWorkItem>>();
	private SparseBooleanArray mBooleanMap;

	public SchoolWorkAdapter(Context context, List<SchoolWorkItem> schoolWorkItems) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mBooleanMap = new SparseBooleanArray();
		setSchoolWorkItems(schoolWorkItems);
	}

	public void setSchoolWorkItems(List<SchoolWorkItem> schoolWorkItems){
		this.schoolWorkItems = schoolWorkItems;
		for (SchoolWorkItem item :schoolWorkItems)
		{
			String groupname=item.getGroupName();
			if(!groupList.contains(groupname)) {
				groupList.add(groupname);
			}
		}
		for (int i=0;i<groupList.size();i++)
		{
			String groupname=groupList.get(i);
			List<SchoolWorkItem> itemList=new ArrayList<SchoolWorkItem>();
			for (SchoolWorkItem item :schoolWorkItems)
			{
				if(item.getGroupName().equals(groupname))
					itemList.add(item);
			}
			allItemMap.put(groupname,itemList);
		}
		notifyDataSetChanged();
	}
	@Override
	protected int getSectionCount() {
		return groupList.size();
	}

	@Override
	protected int getItemCountForSection(int section) {
		String groupname = groupList.get(section);
		List<SchoolWorkItem> itemList=allItemMap.get(groupname);
		return itemList.size();
	}

	//是否有footer布局
	@Override
	protected boolean hasFooterInSection(int section) {
		return false;
	}

	@Override
	protected HeaderHolder onCreateSectionHeaderViewHolder(ViewGroup parent, int viewType) {
		return new HeaderHolder(mInflater.inflate(R.layout.hotel_title_item, parent, false));
	}


	@Override
	protected RecyclerView.ViewHolder onCreateSectionFooterViewHolder(ViewGroup parent, int viewType) {
		return null;
	}

	@Override
	protected DescHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
		return new DescHolder(mInflater.inflate(R.layout.school_work_item, parent, false));
	}


	@Override
	protected void onBindSectionHeaderViewHolder(final HeaderHolder holder, final int section) {
		holder.openView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isOpen = mBooleanMap.get(section);
				String text = isOpen ? "展开" : "关闭";
				mBooleanMap.put(section, !isOpen);
				//holder.openView.setText(text);
				//notifyDataSetChanged();
			}
		});
		String groupname = groupList.get(section);
		holder.titleView.setText(groupname);
		//holder.openView.setText(mBooleanMap.get(section) ? "关闭" : "展开");

	}


	@Override
	protected void onBindSectionFooterViewHolder(RecyclerView.ViewHolder holder, int section) {

	}

	@Override
	protected void onBindItemViewHolder(DescHolder holder, int section, int position) {
		String groupname = groupList.get(section);
		List<SchoolWorkItem> itemList=allItemMap.get(groupname);
		final SchoolWorkItem item=itemList.get(position);
		holder.descView.setText(item.getWorkText());
		AQuery aq = new AQuery(mContext);
		Bitmap bitmap = aq.getCachedImage(item.getTransIcon());
		if (bitmap != null) {
			aq.id(holder.itemIcon).image(bitmap);
		}
		else
			aq.id(holder.itemIcon).image(item.getTransIcon(),false,true);
		if(holder.badge==null) {
			holder.badge = new BadgeView(mContext, holder.itemIcon);
			holder.badge.setBadgeMargin(0, 0);
		}
		if(item.getUnread()>0)
		{
			holder.badge.setText(String.valueOf(item.getUnread()));
			holder.badge.show();
		}
		else
			holder.badge.hide();
		holder.itemIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(item.getTemplateName().equals("浏览器"))
				{
					Intent contractIntent = new Intent(mContext,WebSiteActivity.class);
					String username=PrefUtility.get(Constants.PREF_LOGIN_NAME, "");
					username=username.split("@")[0];
					String password=PrefUtility.get(Constants.PREF_LOGIN_PASS, "");
					String url=item.getInterfaceName()+"&username="+username+"&password="+password;
					contractIntent.putExtra("url",url);
					contractIntent.putExtra("title", item.getWorkText());
					mContext.startActivity(contractIntent);
				}
				else
				{
					Intent intent = new Intent(mContext, SchoolActivity.class);
					intent.putExtra("title", item.getWorkText());
					intent.putExtra("interfaceName",item.getInterfaceName());
					intent.putExtra("templateName",item.getTemplateName());
					mContext.startActivity(intent);
				}
			}
		});
	}

}
