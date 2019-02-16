package com.yujieshipin.crm.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
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

public class SchoolWorkAdapter extends BaseAdapter {
	private String TAG = "SchoolWorkAdapter";
	private Context mContext;
	private LayoutInflater inflater;
	List<SchoolWorkItem> schoolWorkItems;

	public SchoolWorkAdapter(Context c, List<SchoolWorkItem> schoolWorkItems) {
		mContext = c;
		this.schoolWorkItems = schoolWorkItems;
		inflater = LayoutInflater.from(c);
	}

	@Override
	public int getCount() {
		return schoolWorkItems.size();
	}

	public void setSchoolWorkItems(List<SchoolWorkItem> schoolWorkItems){
		this.schoolWorkItems = schoolWorkItems;
	}
	@Override
	public Object getItem(int position) {
		return schoolWorkItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if (null == convertView) {
			convertView = inflater.inflate(R.layout.school_work_item, null);
			holder = new ViewHolder();

			holder.ibWrokPic = (ImageView) convertView.findViewById(R.id.ib_school_work_pic);
			holder.tvWorkName = (TextView) convertView.findViewById(R.id.tv_school_work_name);
			holder.badge = new BadgeView(mContext, holder.ibWrokPic);
			holder.badge.setBadgeMargin(0,0);
			//holder.badge.setBackgroundResource(R.drawable.badge_ifaux);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AQuery aq = new AQuery(convertView);
		final SchoolWorkItem schoolWorkItem = (SchoolWorkItem) getItem(position);
		if(schoolWorkItem.getUnread()>0)
		{
			holder.badge.setText(String.valueOf(schoolWorkItem.getUnread()));
			
			holder.badge.show();
		}
		else
			holder.badge.hide();
			

		String imgUrl = schoolWorkItem.getWorkPicPath();

		ImageLoader.getInstance().displayImage(imgUrl,holder.ibWrokPic);
					
		holder.tvWorkName.setText(schoolWorkItem.getWorkText());
		convertView.setPadding(6, 10, 6, 0);
		holder.ibWrokPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(schoolWorkItem.getTemplateName().equals("浏览器"))
				{
					Intent contractIntent = new Intent(mContext,WebSiteActivity.class);
					
					String username=PrefUtility.get(Constants.PREF_LOGIN_NAME, "");
					username=username.split("@")[0];
					String password=PrefUtility.get(Constants.PREF_LOGIN_PASS, "");
					String url=schoolWorkItem.getInterfaceName()+"&username="+username+"&password="+password;
					contractIntent.putExtra("url",url);
					contractIntent.putExtra("title", schoolWorkItem.getWorkText());
					mContext.startActivity(contractIntent);
				}
				else
				{
					Intent intent = new Intent(mContext, SchoolActivity.class);
					intent.putExtra("title", schoolWorkItem.getWorkText());
					
					intent.putExtra("interfaceName",schoolWorkItem.getInterfaceName());
					intent.putExtra("templateName",schoolWorkItem.getTemplateName());
					mContext.startActivity(intent);
				}
			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView ibWrokPic;
		TextView tvWorkName;
		BadgeView badge;
	}
}
