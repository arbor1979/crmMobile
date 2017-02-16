package com.yujieshipin.crm.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.entity.ContactsFriends;
import com.yujieshipin.crm.entity.ContactsMember;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.ExpressionUtil;
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
public class ContactsSelectFragment extends Fragment {
	
	
	private ExpandableListView expandableListView;
	public ExpandableAdapter expandableAdapter;
	public List<String> groupList;
	public List<List<ContactsMember>> childList;
	public List<List<ContactsMember>> childSelectedList;
	private LinearLayout initLayout;
	private AQuery aq;
	private static final String TAG = "ContactsSelectFragment";
	
	private PinyinComparator pinyinComparator;
	public List<ContactsMember> memberList;
	
	public Map<String,String> chatFriendMap;
	static Dialog mLoadingDialog = null;
	public List<ContactsMember> selectedlist;
	private User user;
	public ImageAdapter picadpter;
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0 :
				initLayout.setVisibility(View.GONE);
				expandableListView.setVisibility(View.VISIBLE);
				initContent();
				
				
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
		aq = new AQuery(getActivity());
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "--------------refresh is running--------------");
		View localView = inflater.inflate(R.layout.view_contacts, container, false);
		expandableListView = (ExpandableListView)localView.findViewById(R.id.contacts);
		initLayout = (LinearLayout) localView.findViewById(R.id.initlayout);
		
		expandableListView.setVisibility(View.GONE);
		initLayout.setVisibility(View.VISIBLE);
		
		
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
		return localView;
	}

	/**
	 * 功能描述:  	查询联系人数据
	 *
	 * @author zhuliang  2013-12-13 下午5:01:06
	 *
	 */
	private void query() {
		Log.d(TAG, "------------------query refresh is running----------------");
		groupList = new ArrayList<String>();
		childList = new ArrayList<List<ContactsMember>>();
		childSelectedList = new ArrayList<List<ContactsMember>>();
		memberList = new ArrayList<ContactsMember>();
		selectedlist = new ArrayList<ContactsMember>();
		String userNumber = user.getUserNumber();
		try {
		
			//searchChatContent();
			
				//List<ContactsFriends> friendsList = contactsFriendsDao.queryForAll();
				List<ContactsFriends> friendsList=((CampusApplication)getActivity().getApplicationContext()).getLinkGroupList();
				for (ContactsFriends contactsFriends : friendsList) {
					String friendsName = contactsFriends.getFriendsName();
					String friendsMember = contactsFriends.getFriendsMember();
					JSONArray jaFriends = new JSONArray(friendsMember);
					List<ContactsMember> listMember = new ArrayList<ContactsMember>();
					
					if (jaFriends != null && jaFriends.length() > 0) {
						for (int i = 0; i < jaFriends.length(); i++) {
							String str = jaFriends.optString(i);
							if (!str.equals(userNumber)) {
								
								ContactsMember contactsMember=((CampusApplication)getActivity().getApplicationContext()).getLinkManDic().get(str);
								listMember.add(contactsMember);
								memberList.add(contactsMember);
								
							}
						}
					}
					groupList.add(friendsName);
					pinyinComparator = new PinyinComparator();
					Collections.sort(listMember, pinyinComparator);
					childList.add(listMember);
					childSelectedList.add(new ArrayList<ContactsMember>());
				}
			
			

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public class PinyinComparator implements Comparator<ContactsMember> {

		public int compare(ContactsMember o1, ContactsMember o2) {
			// 这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
				String o1Name=o1.getXingMing().trim().substring(0,1)+o1.getName().trim();
				String o2Name=o2.getXingMing().trim().substring(0,1)+o2.getName().trim();
				return o1Name.compareTo(o2Name);
			
		}

	
	}
	
	private void initContent(){
		Log.d(TAG, "--------------initContent is rinning-------------");
		System.out.println(groupList.size() + "/" + childList.size());
		
		expandableAdapter = new ExpandableAdapter(groupList, childList,childSelectedList,chatFriendMap);
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
			List<List<ContactsMember>> childSelectedList = new ArrayList<List<ContactsMember>>();
			Map<String,String> map = new HashMap<String, String>();
			
			public ExpandableAdapter(List<String> group,
					List<List<ContactsMember>> child,List<List<ContactsMember>> selectedchild,Map<String,String> map) {
				this.groupList = group;
				this.childList = child;
				this.childSelectedList=selectedchild;
				this.map = map;
			}

			public void refresh(List<String> group,
					List<List<ContactsMember>> child,List<List<ContactsMember>> selectedchild,Map<String,String> map){
				this.groupList = group;
				this.childList = child;
				this.childSelectedList=selectedchild;
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
					
					holder.select_radio=(CheckBox)convertView.findViewById(R.id.radio_select_child);
					holder.select_radio.setVisibility(View.VISIBLE);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				final ContactsMember contactsMember = childList.get(groupPosition)
						.get(childPosition);
				
				if (contactsMember != null) {
					String toid = contactsMember.getUserNumber();
					String url = contactsMember.getUserImage();
					
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
					final List<ContactsMember> subList=childSelectedList.get(groupPosition);
					if(subList.contains(contactsMember))
						holder.select_radio.setChecked(true);
					else
						holder.select_radio.setChecked(false);
					holder.select_radio.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							CheckBox rb=(CheckBox)v;
							if(rb.isChecked())
							{
								if(!subList.contains(contactsMember))
									subList.add(contactsMember);
							}
							else
							{
								if(subList.contains(contactsMember))
									subList.remove(contactsMember);
							}
							updateViewBySelected();
						}
						
					});
					//Log.d(TAG,"---------------------->contactsMember.getUserImage():"+url);
					ImageOptions options = new ImageOptions();
					options.memCache=false;
					options.round = 20;
					options.fallback = R.drawable.ic_launcher;
					aq.id(holder.photo).image(url, options);
					holder.name.setText(contactsMember.getName().trim());
					holder.group.setOnClickListener(new OnClickListener() {
	
						@Override
						public void onClick(View v) {
							LinearLayout parentview=(LinearLayout)v.getParent();
							CheckBox rb=(CheckBox)parentview.findViewById(R.id.radio_select_child);
							if(rb.isChecked())
							{
								if(subList.contains(contactsMember))
									subList.remove(contactsMember);
								rb.setChecked(false);
								
							}
							else
							{
								if(!subList.contains(contactsMember))
									subList.add(contactsMember);
								rb.setChecked(true);
							}
							updateViewBySelected();
								
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
			public View getGroupView(int groupPosition, final boolean isExpanded,
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
					holder.select_radio=(CheckBox)convertView.findViewById(R.id.select_Radio);
					holder.select_radio.setOnClickListener(new OnClickListener(){
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							CheckBox rb=(CheckBox)v;
							int groupPosition=(Integer) rb.getTag();
							childSelectedList.get(groupPosition).clear();
							if(rb.isChecked())
							{
								List<ContactsMember> subList=childList.get(groupPosition);
								for(ContactsMember item:subList)
									childSelectedList.get(groupPosition).add(item);
							}
							updateViewBySelected();
								
						}
						
					});
					convertView.setTag(holder);
				}
				else{
					holder = (ViewHolder) convertView.getTag();
				}
				holder.select_radio.setTag(groupPosition);
				
				holder.showMemberBT.setVisibility(View.GONE);
				holder.groupIV.setVisibility(View.GONE);
				holder.select_radio.setVisibility(View.VISIBLE);
				holder.groupTV.setText(this.groupList.get(groupPosition));
				holder.countTV.setText(String.valueOf(this.childSelectedList.get(groupPosition)
						.size())+"/"+String.valueOf(this.childList.get(groupPosition)
						.size()) + "人");
				if(this.childSelectedList.get(groupPosition).size()==this.childList.get(groupPosition).size())
					holder.select_radio.setChecked(true);
				else
					holder.select_radio.setChecked(false);
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
			ImageView photo,groupIV;
			ImageButton callIV;
			TextView name,groupTV,countTV,lastContentTV;
			TextView showMemberBT;
			CheckBox select_radio;
		}
		
		public void updateViewBySelected()
		{
			selectedlist.clear();
			for(int i=childSelectedList.size()-1;i>=0;i--)
			{
				List<ContactsMember> sublist=childSelectedList.get(i);
				for(ContactsMember item:sublist)
				{
					selectedlist.add(item);
				}
			}
			expandableAdapter.notifyDataSetChanged();
			
			picadpter=new ImageAdapter(getActivity(),selectedlist);
			GridView grid_picture=(GridView)getActivity().findViewById(R.id.grid_picture);
			if(grid_picture!=null)
			{
				int width=AppUtility.getPixByDip(getActivity(),(32+5)*selectedlist.size());
				int height=AppUtility.getPixByDip(getActivity(),32);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(  
						width, height); 
				int margin=AppUtility.getPixByDip(getActivity(),5);
				params.setMargins(margin, margin, margin, margin);
				grid_picture.setHorizontalSpacing(margin);  
				grid_picture.setLayoutParams(params);
				grid_picture.setStretchMode(GridView.NO_STRETCH);  
				grid_picture.setNumColumns(selectedlist.size());
				grid_picture.setAdapter(picadpter);
			}
			Button selectOk=(Button)getActivity().findViewById(R.id.confirm_sel);
			if(selectOk!=null)
			{
				selectOk.setText(getResources().getText(R.string.go)+"("+selectedlist.size()+")");
				if(selectedlist.size()>0)
					selectOk.setEnabled(true);
				else
					selectOk.setEnabled(false);
			}
			
		}
		
		class ImageAdapter extends BaseAdapter{
	        Context context;
	        List<ContactsMember> list;
	       
	        public ImageAdapter(Context context,List<ContactsMember> list){
	         this.context=context;
	         this.list=list;
	        }
	  
	        public View getView(int position, View convertView, ViewGroup parent) {
	        	// TODO Auto-generated method stub  
	        	ContactsMember item=list.get(position);
	        	final ImageView imageView;
	        	int width=AppUtility.getPixByDip(getActivity(),32);
	        	if (convertView == null) {
	        		imageView = new ImageView(context);
	        		imageView.setLayoutParams(new GridView.LayoutParams(width,width));//设置图片显示长宽 图片外部外部框架
	        		imageView.setTag(item);
	        		imageView.setOnClickListener(new OnClickListener(){
	        		
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							ImageView iv=(ImageView)v;
							ContactsMember cm=(ContactsMember)iv.getTag();
							for(final List<ContactsMember> sublist:childSelectedList)
							{
								for(final ContactsMember item:sublist)
								{
									if(item.equals(cm))
									{
										Animation translateAnimation=new TranslateAnimation(0,0,0,-AppUtility.getPixByDip(getActivity(),32));
										translateAnimation.setDuration(500);    
										translateAnimation.setFillAfter (true);             //保留在终止位置  
							            translateAnimation.setFillEnabled(true); //设置动画持续时间  
										
								        
								        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
											
											@Override
											public void onAnimationStart(Animation animation) {
												// TODO Auto-generated method stub
												
											}
											
											@Override
											public void onAnimationRepeat(Animation animation) {
												// TODO Auto-generated method stub
												
											}
											
											@Override
											public void onAnimationEnd(Animation animation) {
												
												sublist.remove(item);
												updateViewBySelected();
											}
										});
								        iv.startAnimation(translateAnimation);
										return;
									}
								}
							}
						}
	        			
	        		});
	        	} else {
	        		imageView = (ImageView) convertView;
	        	}
	        	
	        	aq.id(imageView).image(item.getUserImage(),false,true);
	   
	        	return imageView;
	        }
		  public int getCount() {
		   // TODO Auto-generated method stub
		   return selectedlist.size();
		  }
		  public Object getItem(int position) {
		   // TODO Auto-generated method stub
		   return selectedlist.get(position);
		  }
		  public long getItemId(int position) {
		   // TODO Auto-generated method stub
		   return position;
		  }
	  
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
}
