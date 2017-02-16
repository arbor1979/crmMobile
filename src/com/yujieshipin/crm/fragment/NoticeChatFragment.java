package com.yujieshipin.crm.fragment;

import com.yujieshipin.crm.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NoticeChatFragment extends Fragment {
	Button bn_back,bn_go;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View localView = inflater.inflate(R.layout.view_chat_notice, container, false);
		initTitle();
		return localView;
	}

	private void initTitle(){
		LayoutInflater m = LayoutInflater.from(getActivity());
		View view = m.inflate(R.layout.activity_communication, null);
		bn_back = (Button)view.findViewById(R.id.chat_btn_menu);
		bn_back.setBackgroundResource(R.drawable.stuinfo_bn_back);
		bn_go = (Button)view.findViewById(R.id.chat_btn_search);
		bn_go.setVisibility(View.INVISIBLE);
	}
}
