package com.yujieshipin.crm.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.CommunicationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MemberChatFragment extends Fragment {
	ListView classList;
	ClassAdapter classAdapter;
	FragmentManager manager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View localView = inflater.inflate(R.layout.view_chat_member, container,
				false);
		manager = getFragmentManager();
		initList(localView);
		return localView;
	}

	private void initList(View view) {
		classList = (ListView) view.findViewById(R.id.chat_member_list);
		classAdapter = new ClassAdapter();
		classList.setAdapter(classAdapter);
		classList.setOnItemClickListener(new ItemClickListener());
	}

	public class ClassAdapter extends BaseAdapter {
		private ArrayList<Map<String, Object>> groupList;

		private ArrayList<Map<String, Object>> setData() {
			groupList = new ArrayList<Map<String, Object>>();
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < 20; i++) {
				map.put("photo", R.drawable.ic_launcher1);
				map.put("class", "电子信息103班");
				map.put("count", "共有成员67人");
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
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return setData().get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater mInflater = LayoutInflater.from(getActivity());
			convertView = mInflater.inflate(R.layout.view_list_chat_member,
					null);
			initView(position, convertView);
			return convertView;
		}

		public void initView(int position, View view) {
			ViewHolder holder = new ViewHolder();
			holder.photo = (ImageView) view.findViewById(R.id.chat_img_class);
			holder.tv_class = (TextView) view
					.findViewById(R.id.chat_tv_classname);
			holder.tv_count = (TextView) view
					.findViewById(R.id.chat_class_count);
			holder.photo.setBackgroundResource((Integer) setData()
					.get(position).get("photo"));
			holder.tv_class.setText((String) setData().get(position).get(
					"class"));
			holder.tv_count.setText((String) setData().get(position).get(
					"count"));
		}

		public class ViewHolder {

			ImageView photo, right;
			TextView tv_class, tv_count;
		}
	}

	public class ItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			Bundle localbundle = new Bundle();
			localbundle.putInt("number", arg2);
			MemberInfoFragment memberInfoFragment = new MemberInfoFragment();
			manager.beginTransaction().replace(R.id.member, memberInfoFragment).addToBackStack(null).commit();
			
			
		}

	}

}
