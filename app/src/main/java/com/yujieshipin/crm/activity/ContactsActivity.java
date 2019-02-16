package com.yujieshipin.crm.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.yujieshipin.crm.CampusApplication;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.db.DatabaseHelper;
import com.yujieshipin.crm.db.InitData;
import com.yujieshipin.crm.entity.User;
import com.yujieshipin.crm.fragment.ContactsFragment;
import com.yujieshipin.crm.fragment.ContactsSearchFragment;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.DialogUtility;
import com.yujieshipin.crm.util.PrefUtility;
import com.yujieshipin.crm.util.SerializableMap;
import com.yujieshipin.crm.util.AppUtility.CallBackInterface;

/**
 * 
 * #(c) ruanyun PocketCampus <br/>
 * 
 * 版本说明: $id:$ <br/>
 * 
 * 功能说明: 联系人界面
 * 
 * <br/>
 * 创建说明: 2013-12-9 上午10:04:26 zhuliang 创建文件<br/>
 * 
 * 修改历史: 正在修改。。。。expandablelistview<br/>
 * 
 */
@SuppressLint({ "NewApi", "HandlerLeak" })
public class ContactsActivity extends FragmentActivity {
	static Button menu;
	static LinearLayout layout_menu;
	public static LinearLayout layout_refresh;
	private TextView title,cancel,friends, group;
	private ViewGroup search_head;
	private DatabaseHelper database;
	public static EditText search;
	private LinearLayout contacts;
	private LinearLayout titleGroup;
	private static int currentId = 0;
	public static int STATUS = 0;
	private static final String TAG = "ContactsActivity";
	public static MyHandler mHandler;
	static ContactsSearchFragment contactsSearchFragment;
	private ViewPager viewPager;
	private ContactsPageAdapter adapter;
	private List<ContactsFragment> contactsFragmentList;
	private DisplayMetrics dm;
	public static Dialog mLoadingDialog;
	
	RelativeLayout contactlayout;
	LinearLayout initlayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "----------------onCreate-----------------------");
		dm = getResources().getDisplayMetrics();
		setContentView(R.layout.activity_contacts);
		contacts = (LinearLayout) findViewById(R.id.content);
		search = (EditText) findViewById(R.id.edit_search);
		mLoadingDialog = DialogUtility.createLoadingDialog(ContactsActivity.this, "正在获取数据...");
		mHandler = new MyHandler();
		initViews();
		initContent();
		
		initSearch();
		registerBoradcastReceiver();
		
		/*
		if(AppUtility.isInitContactData())
		{
			Message msg = new Message();
			msg.what = 3;
			mHandler.sendMessageDelayed(msg,1000);
		}
		else
		{
			startRefreshContacts(mLoadingDialog);
		}
		*/
	}
	
	private void startRefreshContacts(Dialog dg)
	{
		PrefUtility.put(Constants.PREF_INIT_CONTACT_FLAG, false);
		InitData initData = new InitData(ContactsActivity.this,
				getHelper(), dg, "refreshContact", PrefUtility.get(Constants.PREF_CHECK_CODE, ""));
		initData.initContactInfo();
	}
	
	@Override
	protected void onDestroy() {
		mBroadcastReceiver.clearAbortBroadcast();
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}
	
	/**
	 * 功能描述: 搜索框处理
	 *
	 * @author zhuliang  2013-12-13 下午5:03:12
	 *
	 */
	@SuppressLint("NewApi")
	private void initSearch() {
		search.setFocusable(false);
		search.setFocusableInTouchMode(false);
		search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//界面上移动画
				AnimationSet animationSet = new AnimationSet(true);
				TranslateAnimation translateAnimation = new TranslateAnimation(
						0, 0, contacts.getY(), contacts.getY() - 44 * dm.densityDpi/160);
				animationSet.addAnimation(translateAnimation);
				animationSet.setDuration(300);
				animationSet.setFillAfter(true);
				animationSet.setFillBefore(false);
				contacts.startAnimation(animationSet);
				
				if(viewPager.getCurrentItem() == 0){
					ContactsFragment mContactsFragment = contactsFragmentList.get(0);
					contactsSearchFragment = ContactsSearchFragment.newInstance(0, mContactsFragment.memberList);
				}
				if(viewPager.getCurrentItem() == 1){
					ContactsFragment mContactsFragment = contactsFragmentList.get(1);
					contactsSearchFragment = ContactsSearchFragment.newInstance(1, mContactsFragment.memberList);
				}
				Message msg = new Message();
				msg.what = 0;
				mHandler.sendMessageDelayed(msg, 300);
			}
		});
	}
	
	// 初始化Views
	private void initViews() {
		contactlayout = (RelativeLayout) findViewById(R.id.contactlayout);
		initlayout = (LinearLayout) findViewById(R.id.initlayout);
		menu = (Button) findViewById(R.id.btn_back);
		layout_menu = (LinearLayout)findViewById(R.id.layout_back);
		RelativeLayout headerlayout=(RelativeLayout)findViewById(R.id.navibar);
        int color=PrefUtility.getInt(Constants.PREF_THEME_NAVBARCOLOR, 0);
		if(color!=0)
			headerlayout.setBackgroundColor(color);
		
		layout_refresh = (LinearLayout)findViewById(R.id.layout_goto);
		/*
		layout_refresh.setVisibility(View.VISIBLE);
		refresh = (Button)findViewById(R.id.btn_goto);
		refresh.setBackgroundResource(R.drawable.bg_title_homepage_go);
		layout_refresh.setOnClickListener(new TabListener());
		*/
		title = (TextView) findViewById(R.id.tv_title);
		title.setVisibility(View.VISIBLE);
		title.setText("客户");
		search_head = (ViewGroup) findViewById(R.id.search_head);
		
		//search_head.getBackground().setAlpha(50);
		cancel = (TextView) findViewById(R.id.chat_btn_cancel);
		cancel.setVisibility(View.GONE);
		menu.setBackgroundResource(R.drawable.bg_title_homepage_back);
		titleGroup = (LinearLayout)findViewById(R.id.title_group);
		titleGroup.setVisibility(View.GONE);
		friends = (TextView)findViewById(R.id.friends);
		group = (TextView)findViewById(R.id.group);
		viewPager = (ViewPager)findViewById(R.id.contacts_pager);
		viewPager.setOnPageChangeListener(new PagerChangeListener());
		friends.setOnClickListener(new TabListener());
		group.setOnClickListener(new TabListener());
		layout_menu.setOnClickListener(TabHostActivity.menuListener);
		User user=((CampusApplication)getApplicationContext()).getLoginUserObj();
		int userType=0;
		try
		{
			userType=Integer.parseInt(user.getUserType());
		}
		catch(NumberFormatException e)
		{
		
		}
		if(userType>0)
		{
			LinearLayout layout_goto=(LinearLayout)findViewById(R.id.layout_goto);
			Button btn_goto=(Button)findViewById(R.id.btn_goto);
			btn_goto.setText("新增");
			layout_goto.setVisibility(View.VISIBLE);
			layout_goto.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					
					Intent intent =new Intent(ContactsActivity.this,SchoolDetailActivity.class);
					intent.putExtra("templateName", "调查问卷");
					intent.putExtra("interfaceName", "?function=getContracts&action=add");
					intent.putExtra("title", "新增客户");
					startActivityForResult(intent,101);
				}
				
			});
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
		case 101:
			ContactsFragment friendContacts=contactsFragmentList.get(0);
			friendContacts.getContracts();
		    break;
		default:
		    break;
		}
	}
	//内容
	private void initContent() {
		Log.d(TAG, "----------refresh is running----------");
		
		contactsFragmentList = new ArrayList<ContactsFragment>();
		ContactsFragment friendContacts = new ContactsFragment();
		
		contactsFragmentList.add(friendContacts);
		/*
		ContactsFragment groupContacts = new ContactsFragment();
		Bundle localBundle1 = new Bundle();
		localBundle1.putString("title", "群组");
		groupContacts.setArguments(localBundle1);
		contactsFragmentList.add(groupContacts);
		*/
		adapter = new ContactsPageAdapter(getSupportFragmentManager(), contactsFragmentList);
		viewPager.setAdapter(adapter);
	}
	
	class PagerChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int arg0) {
			currentId = arg0;
			switch(arg0){
			case 0 :
				friends.setBackgroundResource(R.drawable.chat_msg_bg_sel);
				group.setBackgroundResource(R.drawable.chat_group_bg_nor);
				friends.setTextColor(Color.parseColor("#27ae62"));
				group.setTextColor(Color.WHITE);
				break;
			case 1 :
				friends.setBackgroundResource(R.drawable.chat_msg_bg_nor);
				group.setBackgroundResource(R.drawable.chat_group_bg_sel);
				friends.setTextColor(Color.WHITE);
				group.setTextColor(Color.parseColor("#27ae62"));
				break;
				default :
					break;
			}
		}
	}
	class TabListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.friends :
				viewPager.setCurrentItem(0);
				break;
			case R.id.group :
				viewPager.setCurrentItem(1);
				break;
			case R.id.layout_goto:
				mLoadingDialog.show();
				mHandler.sendEmptyMessage(2);
				
				break;
				default :
					break;
			}
		}
		
	}
	class ContactsPageAdapter extends FragmentPagerAdapter{
		List<ContactsFragment> fragmentList ;
		public ContactsPageAdapter(FragmentManager fm,List<ContactsFragment> fragmentList) {
			super(fm);
			this.fragmentList = fragmentList;
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position);
		}

		@Override
		public int getCount() {
			return fragmentList == null ? 0 : this.fragmentList.size();
		}

		
	}
	//	消息处理
	public class MyHandler extends Handler {
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				//弹出搜索窗口
				Log.d(TAG, "-------isAdded----------" + contactsSearchFragment.isAdded());
				if (!contactsSearchFragment.isAdded()) {
					contactsSearchFragment.show(getSupportFragmentManager(),
							"search");
					getSupportFragmentManager().executePendingTransactions();
					Dialog dialog = contactsSearchFragment.getDialog();
					WindowManager wm = getWindowManager();
					Display display = wm.getDefaultDisplay();
					LayoutParams lp = dialog.getWindow().getAttributes();
					dialog.getWindow().setWindowAnimations(R.style.dialogWindowAnim);
					lp.width = display.getWidth();
					Log.d(TAG, "----------height----------" + lp.height);
					dialog.getWindow().setGravity(Gravity.TOP);
					dialog.getWindow().setAttributes(lp);
					
					//点击search时，不弹出输入键盘
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
					
					search.setInputType(0);
				}
				break;
			case 1:
				//界面下移
				Log.d(TAG, "--->  执行界面隐藏方法...");
				
				AnimationSet animationSet1 = new AnimationSet(true);
				TranslateAnimation translateAnimation1 = new TranslateAnimation(
						0, 0, contacts.getY() + 44 * dm.densityDpi/160, contacts.getY());
				animationSet1.addAnimation(translateAnimation1);
				animationSet1.setFillAfter(true);
				animationSet1.setFillBefore(false);
				contacts.startAnimation(animationSet1);
				
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(search.getWindowToken(), 0); //强制隐藏键盘  
				
				break;
			case 2 :
				adapter = new ContactsPageAdapter(getSupportFragmentManager(), contactsFragmentList);
				viewPager.setAdapter(adapter);
				Log.d(TAG, "-----------------size:" + contactsFragmentList.size());
				viewPager.setCurrentItem(currentId);
				break;
			case 3:
				startRefreshContacts(null);
        		break;
			default:
				break;
			}
		}

	}

	
	private DatabaseHelper getHelper() {
		if (database == null) {
			database = OpenHelperManager.getHelper(this, DatabaseHelper.class);

		}
		return database;
	}

	class ViewHolder {
		ImageView photo;
		TextView name;
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){ 
        @Override 
        public void onReceive(Context context, Intent intent) { 
            String action = intent.getAction(); 
         
            if(action.equals("refreshContact")){ 
            	Log.d(TAG, "----------->BroadcastReceiver：refreshContact");
            	/*
            	search.setEnabled(true);
        		initlayout.setVisibility(View.INVISIBLE);
    			contactlayout.setVisibility(View.VISIBLE);
    			layout_refresh.setEnabled(true);
    			*/
    			initContent();
    			
    			//获取最后一次聊天记录
    			InitData initData = new InitData(ContactsActivity.this,
    					getHelper(), null, "getLastMsg",  PrefUtility.get(Constants.PREF_CHECK_CODE, ""));
    			initData.initContactLastMsg();
				
            }
            if(action.equals("getLastMsg")){ 
            	Log.d(TAG, "----------->BroadcastReceiver：getLastMsg");
            	Bundle bdl=intent.getExtras();
            	SerializableMap myMap=(SerializableMap) bdl.getSerializable("result");
            	Map<String,String> lastMsgMap=myMap.getMap();
            	ContactsFragment mContactsFragment = contactsFragmentList.get(0);
            	mContactsFragment.chatFriendMap=lastMsgMap;
            	if(mContactsFragment.expandableAdapter!=null)
            		mContactsFragment.expandableAdapter.refresh(mContactsFragment.groupList, mContactsFragment.childList, lastMsgMap);
            	
            }
            
        } 
    }; 
    
    public void registerBoradcastReceiver(){ 
        IntentFilter myIntentFilter = new IntentFilter(); 
        myIntentFilter.addAction("refreshContact"); 
        myIntentFilter.addAction("getLastMsg"); 
        
        //注册广播       
        registerReceiver(mBroadcastReceiver, myIntentFilter); 
        
        
    }
    
}
