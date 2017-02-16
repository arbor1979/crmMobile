package com.yujieshipin.crm.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.yujieshipin.crm.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MessageChatFragment extends Fragment {
	ListView mList;
	MessageAdapter msgAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View localView = inflater.inflate(R.layout.view_chat_message,
				container, false);
		initList(localView);
		return localView;
	}

	private void initList(View view) {
		mList = (ListView) view.findViewById(R.id.msg_list);
		msgAdapter = new MessageAdapter();
		mList.setAdapter(msgAdapter);
	}

	public class MessageAdapter extends BaseAdapter {
		private ArrayList<Map<String, Object>> groupList;

		private ArrayList<Map<String, Object>> setData() {
			groupList = new ArrayList<Map<String,Object>>();
			for (int i = 0; i < 15; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("photo", R.drawable.ic_launcher);
				map.put("name", "程萌萌");
				map.put("msg_info", "我们约会吧");
				map.put("msg_num", "72");
				map.put("time", "25分钟前");
				groupList.add(map);
			}
			return groupList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return setData().size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return setData().get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater mLayoutInflater = LayoutInflater.from(getActivity());
			convertView = mLayoutInflater.inflate(R.layout.view_list_chat_msg, null);
			initChildView(position,convertView);
			return convertView;
		}
		
		private void initChildView(int position, View view){
			ViewHolder holder = new ViewHolder();
			holder.photo = (ImageView)view.findViewById(R.id.chat_img_photo);
			holder.name = (TextView)view.findViewById(R.id.chat_tv_name);
			holder.msgInfo = (TextView)view.findViewById(R.id.chat_tv_msginfo);
			holder.msg_num = (TextView)view.findViewById(R.id.chat_msg_count);
			holder.msg_time = (TextView)view.findViewById(R.id.chat_msg_time);
			holder.photo.setBackgroundResource((Integer)setData().get(position).get("photo"));
			holder.name.setText((String)setData().get(position).get("name"));
			holder.msg_num.setText((String)setData().get(position).get("msg_num"));
			if(holder.msg_num != null){
				holder.msg_num.setVisibility(1);
			}
			holder.msg_time.setText((String)setData().get(position).get("time"));
			holder.msgInfo.setText((String)setData().get(position).get("msg_info"));
		}

		private class ViewHolder {
			ImageView photo;
			TextView name, msgInfo, msg_num, msg_time;
		}

	}

}
