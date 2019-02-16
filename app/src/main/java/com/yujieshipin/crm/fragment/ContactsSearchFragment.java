package com.yujieshipin.crm.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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
import com.yujieshipin.crm.activity.SchoolDetailActivity;
import com.yujieshipin.crm.activity.ShowPersonInfo;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.entity.ContactsMember;
import com.yujieshipin.crm.fragment.ContactsFragment.DialogAdapter;
import com.yujieshipin.crm.fragment.ContactsFragment.DialogAdapter.ViewHolder;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.SearchParser;
import com.yujieshipin.crm.widget.ClearEditText;

public class ContactsSearchFragment extends DialogFragment {
	ViewGroup head;
	ListView listView;
	TextView cancel;
	ClearEditText search;
	int id;
	private static final String TAG = "ContactsSearchFragment";
	private static List<ContactsMember> listData;
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
	Dialog userTypeDialog; 
	LayoutInflater inflater;
	public static ContactsSearchFragment newInstance(int id,
			List<ContactsMember> list) {
		ContactsSearchFragment contactsSearchFragment = new ContactsSearchFragment();
		Bundle localBundle = new Bundle();
		localBundle.putInt("id", id);
		listData = list;
		contactsSearchFragment.setArguments(localBundle);
		return contactsSearchFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View localView = LayoutInflater.from(getActivity()).inflate(
				R.layout.view_searchdialog, null);
		this.inflater=inflater;
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
		}, 100);

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
				holder.btn_moreMenu = (ImageButton) convertView.findViewById(R.id.ibt_moreMenu);
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
			convertView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// Toast.makeText(getActivity(), contactsMember.getName(),
					// Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(getActivity(),
							ChatMsgActivity.class);
					intent.putExtra("toid", contactsMember.getUserNumber());
					intent.putExtra("type", "消息");
					intent.putExtra("toname", contactsMember.getName());
					intent.putExtra("userImage", contactsMember.getUserImage());
					getActivity().startActivity(intent);
					dismiss();
				}
			});
			holder.photo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(getActivity(),
							ShowPersonInfo.class);
					intent.putExtra("studentId", contactsMember.getNumber());
					intent.putExtra("userImage", contactsMember.getUserImage());
					intent.putExtra("userType", contactsMember.getUserType());
					startActivity(intent);
				}
			});
			if(contactsMember.getExtraMenu()==null)
				holder.btn_moreMenu.setVisibility(View.GONE);
			else
				holder.btn_moreMenu.setVisibility(View.VISIBLE);
			holder.btn_moreMenu.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					JSONObject popMenu=contactsMember.getExtraMenu();
					if(popMenu!=null && popMenu.length()>0)
					{
						String[] popMenuStr=new String[popMenu.length()+1];
						Iterator<?> it = popMenu.keys();
						int i=0;
			            while(it.hasNext()){
			                 popMenuStr[i]= (String) it.next().toString();
			                i++;
			            }
						popMenuStr[popMenuStr.length-1]="取消";
						showUserTypeDialog(popMenuStr,contactsMember);
					}
				}
			
			});
			return convertView;
		}

	}

	class ViewHolder {
		ImageView photo;
		TextView name;
		ImageButton btn_moreMenu;
	}

	private void showUserTypeDialog(String[] data,ContactsMember achievement) {
		userTypeDialog = new Dialog(getActivity(), R.style.dialog);
		View view = inflater.inflate(
				R.layout.view_exam_login_dialog, null);
		ListView mList = (ListView) view.findViewById(R.id.list);
		DialogAdapter dialogAdapter = new DialogAdapter(data,achievement);
		mList.setAdapter(dialogAdapter);
		Window window = userTypeDialog.getWindow();
		window.setGravity(Gravity.BOTTOM);// 在底部弹出
		window.setWindowAnimations(R.style.CustomDialog);
		window.setGravity(Gravity.CENTER);
		userTypeDialog.setContentView(view);
		userTypeDialog.show();
		
	}
	public class DialogAdapter extends BaseAdapter {
		String[] arrayData;
		ContactsMember achievement;
		public DialogAdapter(String[] array,ContactsMember achievement) {
			this.arrayData = array;
			this.achievement=achievement;
		}

		@Override
		public int getCount() {
			return arrayData == null ? 0 : arrayData.length;
		}

		@Override
		public Object getItem(int position) {
			return arrayData[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.view_testing_pop, arg2,false);
				
				holder.title = (TextView) convertView.findViewById(R.id.time);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final String text = arrayData[position];
			holder.title.setText(text);
			holder.title.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					PrefUtility.put(Constants.PREF_CHECK_TEST, false);
					if ("删除".equals(text)) {
						new AlertDialog.Builder(getActivity())
					    .setIcon(android.R.drawable.ic_dialog_alert)
					    .setTitle("确认对话框")
					    .setMessage("是否确认删除?")
					    .setPositiveButton("是", new DialogInterface.OnClickListener()
					    {
					    	@Override
					    	public void onClick(DialogInterface dialog, int which) 
					    	{
					    		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
					    		JSONObject queryObj=AppUtility.parseQueryStrToJson(achievement.getExtraMenu().optString(text));
					    		JSONObject jo = new JSONObject();
					    		try {
					    			jo.put("用户较验码", checkCode);
					    			Iterator it = queryObj.keys();
									while (it.hasNext()) {
						                String key = (String) it.next();
						                String value = queryObj.getString(key); 
						                jo.put(key, value);
									}
					    			
					    		} catch (JSONException e1) {
					    			e1.printStackTrace();
					    		}
					    		CampusAPI.httpPost(jo, mHandler, 2);
					    	}
					    })
					    .setNegativeButton("否", null)
					    .show();
					} 
					else if("编辑".equals(text)) 
					{
						Intent intent =new Intent(getActivity(),SchoolDetailActivity.class);
						intent.putExtra("templateName", "调查问卷");
						JSONObject obj=achievement.getExtraMenu();
						intent.putExtra("interfaceName",obj.optString(text));
						intent.putExtra("title", "编辑客户");
						startActivityForResult(intent,101);
					}
					userTypeDialog.dismiss();
				}
			});
			return convertView;
		}
		class ViewHolder {
			TextView title;
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		case 101:
			//Thread thread = new Thread(new MyRunnable(edit_text));
			//thread.start();
		    break;
		
		default:
		    break;
		}
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
				case 2:
					String result = msg.obj.toString();
					
					if (AppUtility.isNotEmpty(result)) {
						try {
							JSONObject jo = new JSONObject(result);
							String res = jo.optString("result");
							if(res.equals("失败")){
								AppUtility.showToastMsg(getActivity(), jo.optString("errorMsg"));
							}
							else if(res.equals("成功"))
							{
								AppUtility.showToastMsg(getActivity(), jo.optString("msg"));
								String customerId=jo.optString("id");
								for(int i=0;i<listData.size();i++)
								{
									ContactsMember member=listData.get(i);
									if(member.getNumber().equals(customerId))
									{
										listData.remove(i);
										break;
									}
								}
								Thread thread = new Thread(new MyRunnable(edit_text));
								thread.start();
								
							}
						}
						catch (JSONException e) {
							e.printStackTrace();
							AppUtility.showErrorToast(getActivity(), e.getLocalizedMessage());
						}
					}
					
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
