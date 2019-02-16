package com.yujieshipin.crm.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.activity.ChatMsgActivity;
import com.yujieshipin.crm.activity.ContactsActivity;
import com.yujieshipin.crm.activity.SchoolDetailActivity;
import com.yujieshipin.crm.activity.ShowPersonInfo;
import com.yujieshipin.crm.activity.StudentInfoActivity;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.ContactsFriends;
import com.yujieshipin.crm.entity.ContactsMember;
import com.yujieshipin.crm.entity.QuestionnaireList;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.entity.AchievementItem.Achievement;
import com.yujieshipin.crm.fragment.SchoolBillFragment.DialogAdapter;
import com.yujieshipin.crm.fragment.SchoolBillFragment.DialogAdapter.ViewHolder;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.ExpressionUtil;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.AppUtility.CallBackInterface;
/**
 * 
 *  #(c) ruanyun PocketCampus <br/>
 *
 *  版本说明: $id:$ <br/>
 *
 *  功能说明: 联系人详细信息
 * 
 *  <br/>创建说明: 2013-12-16 下午5:45:42 zhuliang  创建文件<br/>
 * 
 *  修改历史:<br/>
 *
 */
public class ContactsFragment extends Fragment {
	
	
	private ExpandableListView expandableListView;
	public ExpandableAdapter expandableAdapter;
	public List<String> groupList;
	public List<List<ContactsMember>> childList;
	private LinearLayout initLayout;
	private AQuery aq;
	private static final String TAG = "ContactsFragment";
	
	private PinyinComparator pinyinComparator;
	public List<ContactsMember> memberList;
	public Map<String,String> chatFriendMap;
	static Dialog mLoadingDialog = null;
	
	private User user;
	private String curPhone,curMemberId;
	private Dialog userTypeDialog;
	private LayoutInflater inflater;
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0 :
				showProgress(false);
				String userNumber = user.getUserNumber();
				
				String result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) 
				{
					try 
					{
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						if (res.equals("失败")) {
							AppUtility.showToastMsg(getActivity(), jo.optString("errorMsg"));
						} 
						else 
						{
							groupList.clear();
							childList.clear();
							memberList.clear();
							JSONArray ja=jo.optJSONArray("groupName");
							for(int i=0;i<ja.length();i++)
							{
								JSONObject groupItem=ja.getJSONObject(i);
								String groupName=groupItem.optString("name");
								groupList.add(groupName);
								JSONArray memberArray=jo.optJSONArray(groupName);
								List<ContactsMember> listMember = new ArrayList<ContactsMember>();
								for(int j=0;j<memberArray.length();j++)
								{
									JSONObject memberItem=memberArray.getJSONObject(j);
									ContactsMember contactsMember=new ContactsMember(memberItem);
									listMember.add(contactsMember);
									memberList.add(contactsMember);
								}
								childList.add(listMember);
								
							}
							initContent();
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case 1:
				result = msg.obj.toString();
				if (AppUtility.isNotEmpty(result)) 
				{
					try 
					{
						JSONObject jo = new JSONObject(result);
						String res = jo.optString("result");
						if (res.equals("失败")) {
							AppUtility.showToastMsg(getActivity(), jo.optString("errorMsg"));
						} 
						else 
						{
							String customerid=jo.optString("customerid");
							String contacttime=jo.optString("contacttime");
							boolean find=false;
							for(int i=0;i<childList.size();i++)
							{
								List<ContactsMember> subList=childList.get(i);
								for(int j=0;j<subList.size();j++)
								{
									ContactsMember item=subList.get(j);
									if(item.getNumber().equals(customerid))
									{
										item.setSeatNumber(contacttime);
										find=true;
										break;
									}
								}
								if(find)
									break;
							}
							expandableAdapter.notifyDataSetChanged();
						}
					}
					catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case 2:
				result = msg.obj.toString();
				
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
							getContracts();
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
		
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user=((CampusApplication)getActivity().getApplicationContext()).getLoginUserObj();
		Log.d(TAG, "----------------onCreate is running------------");
		

	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "--------------refresh is running--------------");
		this.inflater = inflater;
		View localView = inflater.inflate(R.layout.view_contacts, container, false);
		expandableListView = (ExpandableListView)localView.findViewById(R.id.contacts);
		initLayout = (LinearLayout) localView.findViewById(R.id.initlayout);
		
		LinearLayout contentLayout = (LinearLayout) localView.findViewById(R.id.content_layout);
		int color=PrefUtility.getInt(Constants.PREF_THEME_LISTCOLOR, 0);
		if(color!=0)
			contentLayout.setBackgroundColor(color);
		
		groupList = new ArrayList<String>();
		childList = new ArrayList<List<ContactsMember>>();
		memberList = new ArrayList<ContactsMember>();
		getContracts();
		initContent();
		/*
		Thread thread = new Thread(){

			@Override
			public void run() {
				query();
				Message msg = new Message();
				msg.what = 0;
				
				mHandler.sendMessage(msg);
			}
			
		};
		thread.start();
		*/
		return localView;
	}
	private void showProgress(boolean progress) {
		if (progress) {
			expandableListView.setVisibility(View.GONE);
			initLayout.setVisibility(View.VISIBLE);
		} else {
			expandableListView.setVisibility(View.VISIBLE);
			initLayout.setVisibility(View.GONE);
		}
	}
	public void getContracts()
	{
		showProgress(true);
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("用户较验码", checkCode);
			jsonObj.put("function", "getContracts");
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jsonObj, mHandler, 0);
			
		
	}
	
	
	public class PinyinComparator implements Comparator<ContactsMember> {

		public int compare(ContactsMember o1, ContactsMember o2) {
			// 这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
				String o1Name=o1.getXingMing().trim().substring(0,1)+o1.getName().trim();
				String o2Name=o2.getXingMing().trim().substring(0,1)+o2.getName().trim();
				return o1Name.compareTo(o2Name);
			
		}

	
	}
	private void sendCall(String phone,String memberId)
	{
		Intent phoneIntent = new Intent("android.intent.action.CALL",
		Uri.parse("tel:" + phone));
		startActivity(phoneIntent);
		updateLastestContact(memberId);
	}
	private void updateLastestContact(String memberId)
	{
		String checkCode = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("用户较验码", checkCode);
			jsonObj.put("function", "getContracts");
			jsonObj.put("action", "updateLastestContact");
			jsonObj.put("memberId", memberId);
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		CampusAPI.httpPost(jsonObj, mHandler, 1);
	}
	private void initContent(){
		Log.d(TAG, "--------------initContent is rinning-------------");
		System.out.println(groupList.size() + "/" + childList.size());
		
		expandableAdapter = new ExpandableAdapter(groupList, childList,chatFriendMap);
		expandableListView.setAdapter(expandableAdapter);
		if( groupList.size() == 1){
			expandableListView.expandGroup(0);
		}
		expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {
				for(int i = 0; i < expandableAdapter.getGroupCount(); i++){
					if(groupPosition != i && expandableListView.isGroupExpanded(i)){
						expandableListView.collapseGroup(i);
					}
				}
			}
		});
		
	}
	
	// 联系人数据适配器
		public class ExpandableAdapter extends BaseExpandableListAdapter {
			List<String> groupList = new ArrayList<String>();
			List<List<ContactsMember>> childList = new ArrayList<List<ContactsMember>>();
			Map<String,String> map = new HashMap<String, String>();
			
			public ExpandableAdapter(List<String> group,
					List<List<ContactsMember>> child,Map<String,String> map) {
				this.groupList = group;
				this.childList = child;
				this.map = map;
			}

			public void refresh(List<String> group,
					List<List<ContactsMember>> child,Map<String,String> map){
				this.groupList = group;
				this.childList = child;
				this.map = map;
				notifyDataSetChanged();
			}
			@Override
			public Object getChild(int groupPosition, int childPosition) {
				return this.childList.get(groupPosition).get(childPosition);
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				return childPosition;
			}

			@Override
			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {
				ViewHolder holder = null;
				if (convertView == null) {
					holder = new ViewHolder();
					convertView = LayoutInflater.from(getActivity())
							.inflate(R.layout.view_expandablelist_child, null);
					holder.group = (LinearLayout)convertView.findViewById(R.id.contacts_info1);
					holder.photo = (ImageView) convertView.findViewById(R.id.photo);
					holder.name = (TextView) convertView.findViewById(R.id.child);
					holder.lastContentTV = (TextView)convertView.findViewById(R.id.signature);
					holder.callIV = (ImageView) convertView.findViewById(R.id.callIV);
					holder.btn_moreMenu = (ImageButton) convertView.findViewById(R.id.btn_moreMenu);
					
					
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				final ContactsMember contactsMember = childList.get(groupPosition)
						.get(childPosition);
				aq = new AQuery(getActivity());
				if (contactsMember != null) {
					//String toid = contactsMember.getUserNumber();
					String url = contactsMember.getUserImage();
					/*
					if(toid != null && !toid.trim().equals("") && map!=null && map.containsKey(toid)){
						holder.lastContentTV.setVisibility(View.VISIBLE);
						String msgContent = map.get(toid);
						SpannableString spannableString = ExpressionUtil
								.getExpressionString(getActivity(), msgContent);
						holder.lastContentTV.setText(spannableString);
						
					}else{
						holder.lastContentTV.setVisibility(View.GONE);
						holder.lastContentTV.setText("");
					}
					*/
					holder.callIV.setVisibility(View.GONE);
				
						
					final String phone=contactsMember.getStuPhone();
					if(AppUtility.isNotEmpty(phone) && phone.length()==11)
					{
						holder.callIV.setVisibility(View.VISIBLE);
						holder.callIV.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone)));
								updateLastestContact(contactsMember.getNumber());

							}
							
						});
						
						
					}
					if(AppUtility.isNotEmpty(contactsMember.getSeatNumber()))
					{
						holder.lastContentTV.setVisibility(View.VISIBLE);
						holder.lastContentTV.setText("最后联系:"+contactsMember.getSeatNumber());
					}
					else
						holder.lastContentTV.setVisibility(View.GONE);
					//Log.d(TAG,"---------------------->contactsMember.getUserImage():"+url);
					ImageOptions options = new ImageOptions();
					options.memCache=false;
					options.fileCache=true;
					options.targetWidth=200;
					options.round = 100;
					options.fallback = R.drawable.ic_launcher1;
					aq.id(holder.photo).image(url, options);
					holder.name.setText(contactsMember.getName().trim());
					holder.group.setOnClickListener(new OnClickListener() {
	
						@Override
						public void onClick(View v) {

							Intent intent = new Intent(getActivity(),
									ShowPersonInfo.class);
							intent.putExtra("studentId", contactsMember.getNumber());
							intent.putExtra("userImage", contactsMember.getUserImage());
							intent.putExtra("userType", contactsMember.getUserType());
							startActivity(intent);
							/*
							Intent intent = new Intent(getActivity(),ChatMsgActivity.class);
							intent.putExtra("toid", contactsMember.getUserNumber());
							intent.putExtra("type", "消息");
							intent.putExtra("toname", contactsMember.getName());
							intent.putExtra("userImage", contactsMember.getUserImage());
							getActivity().startActivity(intent);
							*/
							
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
				}
				return convertView;
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				return this.childList.get(groupPosition).size();
			}

			@Override
			public Object getGroup(int groupPosition) {
				return this.groupList.get(groupPosition);
			}

			@Override
			public int getGroupCount() {
				return this.groupList.size();
			}

			@Override
			public long getGroupId(int groupPosition) {
				return groupPosition;
			}

			@Override
			public View getGroupView(final int groupPosition, final boolean isExpanded,
					View convertView, ViewGroup parent) {
				ViewHolder holder;
				if(convertView == null){
					holder = new ViewHolder();
					convertView = LayoutInflater.from(getActivity()).inflate(
							R.layout.view_expandablelist_group, null);
					holder.groupTV = (TextView) convertView
							.findViewById(R.id.group_name);
					holder.countTV = (TextView) convertView
							.findViewById(R.id.group_count);
					holder.groupIV = (ImageView) convertView.findViewById(R.id.group_image);
					holder.showMemberBT = (TextView)convertView.findViewById(R.id.show_member);
					
					convertView.setTag(holder);
				}else{
					holder = (ViewHolder) convertView.getTag();
				}
				
					holder.showMemberBT.setVisibility(View.GONE);
					holder.groupIV.setVisibility(View.GONE);
				
				holder.groupTV.setText(this.groupList.get(groupPosition));
				holder.countTV.setText(String.valueOf(this.childList.get(groupPosition)
						.size()) + "人");
				return convertView;
			}

			@Override
			public boolean hasStableIds() {
				return false;
			}

			@Override
			public boolean isChildSelectable(int groupPosition, int childPosition) {
				return false;
			}

		}
		
		class ViewHolder {
			LinearLayout group;
			ImageView photo,groupIV,callIV;
			TextView name,groupTV,countTV,lastContentTV;
			TextView showMemberBT;
			ImageButton btn_moreMenu;
		}
		
		/*
		private void searchChatContent(){
			chatFriendMap = new ConcurrentHashMap<String, ChatFriend>();
			try {
				chatFriendDao = getHelper().getChatFriendDao();
				List<ChatFriend> chatFriendList = chatFriendDao.queryForAll();
				ChatFriend chatFriend;
				String toid;
				for(int i = 0; i < chatFriendList.size(); i++){
					chatFriend = chatFriendList.get(i);
					toid = chatFriend.getToid();
					chatFriendMap.put(toid, chatFriend);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		*/
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
				getContracts();
			    break;
			
			default:
			    break;
			}
		}
		@TargetApi(23)
		@Override
		public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
		{
			AppUtility.permissionResult(requestCode,grantResults,getActivity(),callBack);
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
		
		public CallBackInterface callBack=new CallBackInterface()
		{

			@Override
			public void getLocation1() {
				// TODO Auto-generated method stub
			
			}

			@Override
			public void getPictureByCamera1() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void getPictureFromLocation1() {

			}

		};
}
