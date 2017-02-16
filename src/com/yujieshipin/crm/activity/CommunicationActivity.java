package com.yujieshipin.crm.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yujieshipin.crm.R;
//import com.yujieshipin.crm.fragment.MemberInfoFragment;
import com.yujieshipin.crm.base.ExitApplication;
import com.yujieshipin.crm.fragment.GroupChatFragment;
import com.yujieshipin.crm.fragment.MemberChatFragment;
import com.yujieshipin.crm.fragment.MessageChatFragment;
import com.yujieshipin.crm.fragment.NoticeChatFragment;

public class CommunicationActivity extends FragmentActivity {
	public ViewPager mViewPager;
	ChatPagerAdapter mChatPagerAdapter;
	Button  bn_search;
	static Button bn_menu;
	TextView message, notice, member, group, cancel;
	LinearLayout title_head, search_head, search_choose;
	ImageView search_img;
	EditText edit_search;
	RadioButton search_stu, search_class;
	RadioGroup radiogroup;
	final boolean tag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ExitApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_communication);
		mViewPager = (ViewPager) findViewById(R.id.chat_pager);
		mChatPagerAdapter = new ChatPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mChatPagerAdapter);
		setTitle();
		mViewPager.setOnPageChangeListener(new MyListener());
	}

	private void setTitle() {
//		title_head = (LinearLayout) findViewById(R.id.title_head);
//		search_head = (LinearLayout) findViewById(R.id.search_head);
		search_choose = (LinearLayout) findViewById(R.id.search_choose);
		message = (TextView) findViewById(R.id.title_message);
		notice = (TextView) findViewById(R.id.title_notice);
		member = (TextView) findViewById(R.id.title_member);
		group = (TextView) findViewById(R.id.title_group);
		bn_search = (Button) findViewById(R.id.chat_btn_search);
		bn_menu = (Button) findViewById(R.id.chat_btn_menu);
		
		if (!tag) {
			message.setBackgroundResource(R.drawable.chat_msg_bg_sel);
			message.setTextColor(Color.parseColor("#27ae62"));

		}
		message.setOnClickListener(new TitleListener());
		notice.setOnClickListener(new TitleListener());
		member.setOnClickListener(new TitleListener());
		group.setOnClickListener(new TitleListener());
	
	}
	
	
	public class ChatPagerAdapter extends FragmentPagerAdapter {

		public ChatPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@SuppressLint("NewApi")
		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			switch (arg0) {
			case 0:
				MessageChatFragment mMessageChatFragment = new MessageChatFragment();
				return mMessageChatFragment;
			case 1:
				NoticeChatFragment mNoticeChatFragment = new NoticeChatFragment();
				return mNoticeChatFragment;
			case 2:
				MemberChatFragment mMemberChatFragment = new MemberChatFragment();
				return mMemberChatFragment;
			case 3:
				GroupChatFragment mGroupChatFragment = new GroupChatFragment();
				return mGroupChatFragment;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 4;
		}

	}

	public class MyListener implements OnPageChangeListener {

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
			// TODO Auto-generated method stub
			switch (arg0) {
			case 0:
				message.setBackgroundResource(R.drawable.chat_msg_bg_sel);
				notice.setBackgroundResource(R.drawable.chat_middle_bg_nor);
				member.setBackgroundResource(R.drawable.chat_middle_bg_nor);
				group.setBackgroundResource(R.drawable.chat_group_bg_nor);
				message.setTextColor(Color.parseColor("#27ae62"));
				notice.setTextColor(Color.WHITE);
				member.setTextColor(Color.WHITE);
				group.setTextColor(Color.WHITE);
				break;
			case 1:
				message.setBackgroundResource(R.drawable.chat_msg_bg_nor);
				notice.setBackgroundResource(R.drawable.chat_middle_bg_sel);
				member.setBackgroundResource(R.drawable.chat_middle_bg_nor);
				group.setBackgroundResource(R.drawable.chat_group_bg_nor);
				message.setTextColor(Color.WHITE);
				notice.setTextColor(Color.parseColor("#27ae62"));
				member.setTextColor(Color.WHITE);
				group.setTextColor(Color.WHITE);
				break;
			case 2:
				message.setBackgroundResource(R.drawable.chat_msg_bg_nor);
				notice.setBackgroundResource(R.drawable.chat_middle_bg_nor);
				member.setBackgroundResource(R.drawable.chat_middle_bg_sel);
				group.setBackgroundResource(R.drawable.chat_group_bg_nor);
				message.setTextColor(Color.WHITE);
				notice.setTextColor(Color.WHITE);
				member.setTextColor(Color.parseColor("#27ae62"));
				group.setTextColor(Color.WHITE);
				break;
			case 3:
				message.setBackgroundResource(R.drawable.chat_msg_bg_nor);
				notice.setBackgroundResource(R.drawable.chat_middle_bg_nor);
				member.setBackgroundResource(R.drawable.chat_middle_bg_nor);
				group.setBackgroundResource(R.drawable.chat_group_bg_sel);
				message.setTextColor(Color.WHITE);
				notice.setTextColor(Color.WHITE);
				member.setTextColor(Color.WHITE);
				group.setTextColor(Color.parseColor("#27ae62"));
				
				
			
				break;
				
			}
		}
	}

	public class TitleListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.equals(message)) {
				mViewPager.setCurrentItem(0);
			} else {
				if (v.equals(notice)) {
					mViewPager.setCurrentItem(1);
				} else {
					if (v.equals(member)) {
						mViewPager.setCurrentItem(2);
					} else {
						mViewPager.setCurrentItem(3);
					}
				}
			}
		}
	}
}
