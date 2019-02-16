package com.yujieshipin.crm.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.ChatMsgActivity;
import com.yujieshipin.crm.activity.ContactsActivity;
import com.yujieshipin.crm.activity.ContactsSelectActivity;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.ContactsMember;
import com.yujieshipin.crm.util.SearchParser;
import com.yujieshipin.crm.widget.ClearEditText;

public class ContactsSelectSearchFragment extends DialogFragment {
	ViewGroup head;
	ListView listView;
	TextView cancel;
	ClearEditText search;
	int id;
	private static final String TAG = "ContactsSearchFragment";
	private static List<ContactsMember> listData;
	private static List<ContactsMember> childSelectedList;
	DatabaseHelper database;

	SearchParser characterParser;
	Bundle bundle;
	ViewGroup search_dialog;
	List<ContactsMember> searchList;
	
	AQuery aq;
	Date date,last_date;
	long time_search;
	boolean softActive = false;
	String edit_text;
	TextView search_none;
	WindowManager wm;
	static Display display;
	LinearLayout search_layout;
	/* Acitivity要实现这个接口，这样Fragment和Activity就可以共享事件触发的资源了 */  
	 public interface MyListener   
	 {  
	     public void updateSelectedList(List<ContactsMember> selectList);   
	 }
	 private MyListener myListener;  
	
	 @Override  
	 public void onAttach(Activity activity)   
	 {  
	     super.onAttach(activity);  
	     myListener = (MyListener) activity;   
	 }  
	 
	public static ContactsSelectSearchFragment newInstance(int id,
			List<ContactsMember> list,List<ContactsMember> sublist) {
		ContactsSelectSearchFragment contactsSearchFragment = new ContactsSelectSearchFragment();
		Bundle localBundle = new Bundle();
		localBundle.putInt("id", id);
		listData = list;
		childSelectedList=sublist;
		contactsSearchFragment.setArguments(localBundle);
		return contactsSearchFragment;
	}
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View localView = LayoutInflater.from(getActivity()).inflate(
				R.layout.view_searchdialog, null);
		search_dialog = (ViewGroup) localView.findViewById(R.id.search_dialog);

		cancel = (TextView) localView.findViewById(R.id.chat_btn_cancel);
		cancel.setOnClickListener(new CancelListener());
		head = (ViewGroup) localView.findViewById(R.id.head);
		search = (ClearEditText) localView.findViewById(R.id.edit_search);
		search.setFocusable(true);
		search_none = (TextView)localView.findViewById(R.id.search_none);
//		LayoutParams params = (LayoutParams) search_none.getLayoutParams();
//		params.width = display.getWidth();
//		params.height = display.getHeight();
//		search_none.setLayoutParams(params);
		matchWindow(search_none);
		search_layout = (LinearLayout)localView.findViewById(R.id.initlayout);
		matchWindow(search_layout);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) getActivity()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(search, 0);
			}
		}, 500);

		// search.setFocusable(true);
		// search.setSelected(true);
		search.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				edit_text = search.getText().toString().trim();
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					if(cancel.getText().equals("搜索")){
						cancel.setEnabled(false);
					}
					search_layout.setVisibility(View.VISIBLE);
					Thread thread = new Thread(new MyRunnable(edit_text));
					thread.start();
				}
				return false;
			}
		});
		search.addTextChangedListener(new SearchListener());
		listView = (ListView) localView.findViewById(R.id.list);
		Log.i(TAG, "----------onCreateView is running");
		return localView;
	}

	// EditText上的文字改变时调用
	class SearchListener implements TextWatcher {
		Thread thread;
		Boolean isEnd;
		long time;
		boolean isNull = true;
		@Override
		public void afterTextChanged(Editable s) {
			if(TextUtils.isEmpty(s)){
				if(!isNull){
					cancel.setText("取消");
					isNull = true;
					listView.setAdapter(null);
					search_none.setVisibility(View.GONE);
					search_layout.setVisibility(View.GONE);
				}
			}else{
				if(isNull){
					cancel.setText("搜索");
					isNull = false;
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
			
	}

	class CancelListener implements android.view.View.OnClickListener {
		String str;
		long time;
		@Override
		public void onClick(View v) {
			
			edit_text = search.getText().toString().trim();
			if(v instanceof TextView){
				str = ((TextView) v).getText().toString();
				if(str.equals("取消")){
					dismiss();
				}else{
					InputMethodManager inputManager = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(search.getWindowToken(), 0);
					cancel.setEnabled(false);
					search_layout.setVisibility(View.VISIBLE);
					Thread thread = new Thread(new MyRunnable(edit_text));
					thread.start();
				}
			}
			
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "----------onCreateDialog is running");
		return super.onCreateDialog(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "----------onCreate is running");
		bundle = new Bundle();
		id = getArguments().getInt("id");
		setStyle(R.style.dialog, R.style.dialog);
		characterParser = new SearchParser();
		wm = getActivity().getWindowManager();
		display = wm.getDefaultDisplay();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		Log.i(TAG, "----------onDismiss is running");
		Message msg = new Message();
		msg.what = 1;
		if(id==0)
			ContactsActivity.mHandler.sendMessage(msg);
		else if(id==1)
			ContactsSelectActivity.mHandler.sendMessage(msg);
		search.setText("");
	}

	// 搜索结果（list适配器）
	class SearchAdapter extends BaseAdapter {

		List<ContactsMember> list = new ArrayList<ContactsMember>();

		public SearchAdapter(List<ContactsMember> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.list == null ? 0 : this.list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return this.list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.view_search_list, null);
				holder.photo = (ImageView) convertView.findViewById(R.id.photo);
				holder.name = (TextView) convertView.findViewById(R.id.child);
				holder.cb_select=(CheckBox)convertView.findViewById(R.id.cb_select);
				holder.cb_select.setVisibility(View.VISIBLE);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final ContactsMember contactsMember = this.list.get(position);
			
			aq = new AQuery(convertView);
			if (contactsMember.getUserImage() != null) {
				ImageOptions options = new ImageOptions();
				options.memCache=false;
			    options.round = 20;
			    aq.id(holder.photo).image(contactsMember.getUserImage(), options);
			}else{
				holder.photo.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher1));
			}
			
			holder.name.setText(contactsMember.getName());
			
			boolean flag=false;
			for(ContactsMember item:childSelectedList)
			{
				if(item.getUserNumber().equals(contactsMember.getUserNumber()))
				{
					flag=true;
					break;
				}
			}
			holder.cb_select.setChecked(flag);
			
			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// Toast.makeText(getActivity(), contactsMember.getName(),
					// Toast.LENGTH_SHORT).show();
					ViewHolder vh=(ViewHolder)v.getTag();
					if(vh.cb_select.isChecked())
					{
						vh.cb_select.setChecked(false);
					}
					else
					{
						vh.cb_select.setChecked(true);
					}
					
				}
			});
			holder.cb_select.setOnCheckedChangeListener(new OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if(isChecked)
					{
						if(!childSelectedList.contains(contactsMember))
							childSelectedList.add(contactsMember);
					}
					else
					{
						if(childSelectedList.contains(contactsMember))
							childSelectedList.remove(contactsMember);
					}
					myListener.updateSelectedList(childSelectedList);
				}
				
			});
			return convertView;
		}

	}

	class ViewHolder {
		ImageView photo;
		TextView name;
		CheckBox cb_select;
	}



	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			synchronized (msg) {
				switch (msg.what) {
				case 0:
					search_layout.setVisibility(View.GONE);
					if(edit_text.equals("")){
						listView.setAdapter(null);
					}else{
						if(searchList != null && searchList.size() > 0){
							search_none.setVisibility(View.GONE);
						}else{
							search_none.setVisibility(View.VISIBLE);
						}
						listView.setAdapter(new SearchAdapter(searchList));
					}
					cancel.setEnabled(true);
					break;

				default:
					break;
				}
			}
		}

	};
	
	public class MyRunnable implements Runnable{
		String str;
		public MyRunnable(String str){
			this.str = str;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			synchronized(this) 
			{	
				if (!str.equals("") && listData != null) {
					searchData(str);
				}
				mHandler.sendEmptyMessage(0);
			}
		}
		
	}
	/**
	 * 功能描述:根据EditText查询数据
	 *
	 * @author zhuliang  2014-1-13 下午4:05:36
	 * 
	 * @param str
	 */
	private void searchData(String str){
		searchList = new ArrayList<ContactsMember>();
		for (ContactsMember contactsMember : listData) {
			if(contactsMember != null){
				String userNumber = contactsMember.getUserNumber();
				String name = contactsMember.getName();
				String userName = contactsMember.getStudentID();
				if (characterParser.isLetter(str)) {
					if (characterParser.isFinals(str)) {
						String pinyin = SearchParser
								.getPinYin(name);
						if (pinyin.indexOf(str) > -1) {
							searchList.add(contactsMember);
						}
					} else {
						String pinyin2 = SearchParser
								.getPinYinHeadChar(name);
						if (pinyin2.indexOf(str) > -1) {
							searchList.add(contactsMember);
						}
					}
				} else if (name.indexOf(str) > -1) {
					searchList.add(contactsMember);
				}
				if (userNumber.indexOf("老师") > -1) {
					if (userName.indexOf(str) > -1) {
						searchList.add(contactsMember);
					}
				}
			}
		}
	}
	/**
	 * 功能描述:填充屏幕
	 *
	 * @author zhuliang  2014-1-14 下午2:23:58
	 * 
	 * @param v
	 */
	private static void matchWindow(View v){
		LayoutParams params = (LayoutParams) v.getLayoutParams();
		params.width = display.getWidth();
		params.height = display.getHeight();
		v.setLayoutParams(params);
	}
}
