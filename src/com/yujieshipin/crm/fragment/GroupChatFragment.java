package com.yujieshipin.crm.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.entity.SortModel;
import com.yujieshipin.crm.util.CharacterParser;
import com.yujieshipin.crm.widget.SideBar;
import com.yujieshipin.crm.widget.SideBar.OnTouchingLetterChangedListener;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class GroupChatFragment extends Fragment {

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		/*
		 *隐藏图层处理
		 */
		bn_search = (Button) getActivity().findViewById(R.id.chat_btn_search);
//		title_head = (LinearLayout) getActivity().findViewById(R.id.title_head);
//		search_head = (LinearLayout)getActivity(). findViewById(R.id.search_head);
		search_choose = (LinearLayout) getActivity().findViewById(R.id.search_choose);
		bn_search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				title_head.setVisibility(View.INVISIBLE);
//				search_head.setVisibility(View.VISIBLE);
				search_choose.setVisibility(View.VISIBLE);
//				cancel = (TextView) search_head
//						.findViewById(R.id.chat_btn_cancel);
//				search_img = (ImageView) search_head
//						.findViewById(R.id.search_img);
//				edit_search = (EditText) search_head
//						.findViewById(R.id.edit_search);
				search_stu = (RadioButton) search_choose
						.findViewById(R.id.search_stu);
				search_class = (RadioButton) search_choose
						.findViewById(R.id.search_class);
				radiogroup = (RadioGroup) search_choose
						.findViewById(R.id.group_discipline);
				if (search_stu.isChecked()) {
					search_stu.setTextColor(Color.WHITE);
				} else {
					search_stu.setTextColor(Color.parseColor("#aacdb8"));
				}
				if (search_class.isChecked()) {
					search_class.setTextColor(Color.WHITE);
				} else {
					search_class.setTextColor(Color.parseColor("#aacdb8"));
				}
				radiogroup
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(RadioGroup group,
									int checkedId) {
								// TODO Auto-generated method stub
								if (search_stu.isChecked()) {
									search_stu.setTextColor(Color.WHITE);
								} else {
									search_stu.setTextColor(Color
											.parseColor("#aacdb8"));
								}
								if (search_class.isChecked()) {
									search_class.setTextColor(Color.WHITE);
								} else {
									search_class.setTextColor(Color
											.parseColor("#aacdb8"));
								}
							}
						});
				cancel.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						title_head.setVisibility(View.VISIBLE);
						search_head.setVisibility(View.INVISIBLE);
						search_choose.setVisibility(View.INVISIBLE);
					}
				});
			}
		});
	}

	private ListView sortListView;
	private SideBar sideBar;
	// private TextView dialog;
	private SortAdapter adapter;
	// private ClearEditText mClearEditText;

	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;

	private PinyinComparator pinyinComparator;
	LinearLayout title_head, search_head, search_choose;
	ImageView search_img;
	EditText edit_search;
	RadioButton search_stu, search_class;
	RadioGroup radiogroup;
	TextView cancel;
	Button bn_menu, bn_search;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View localView = inflater.inflate(R.layout.view_chat_member_info,
				container, false);
		initViews(localView);
		return localView;
	}

	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}

	private void initViews(View view) {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) view.findViewById(R.id.sidebar);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}

			}
		});

		sortListView = (ListView) view.findViewById(R.id.chat_member_info_list);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				Toast.makeText(getActivity(),
						((SortModel) adapter.getItem(position)).getName(),
						Toast.LENGTH_SHORT).show();
			}

		});

		SourceDateList = filledData(getResources().getStringArray(R.array.date));

		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);

		// mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		// 根据输入框输入值的改变来过滤搜索
		// mClearEditText.addTextChangedListener(new TextWatcher() {

		// @Override
		// public void onTextChanged(CharSequence s, int start, int before,
		// int count) {
		// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
		// filterData(s.toString());
		// }

		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		//
		// }

		// @Override
		// public void afterTextChanged(Editable s) {
		// }
		// });
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(String[] date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.length; i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);
			sortModel
					.setImg(getResources().getDrawable(R.drawable.ic_launcher));
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	/*
	 * private void filterData(String filterStr) { List<SortModel>
	 * filterDateList = new ArrayList<SortModel>();
	 * 
	 * if (TextUtils.isEmpty(filterStr)) { filterDateList = SourceDateList; }
	 * else { filterDateList.clear(); for (SortModel sortModel : SourceDateList)
	 * { String name = sortModel.getName(); if (name.toUpperCase().indexOf(
	 * filterStr.toString().toUpperCase()) != -1 ||
	 * characterParser.getSelling(name).toUpperCase()
	 * .startsWith(filterStr.toString().toUpperCase())) {
	 * filterDateList.add(sortModel); } } }
	 * 
	 * // 根据a-z进行排序 Collections.sort(filterDateList, pinyinComparator);
	 * adapter.updateListView(filterDateList); }
	 */

	public class SortAdapter extends BaseAdapter implements SectionIndexer {
		private List<SortModel> list = null;

		public SortAdapter(GroupChatFragment memberInfoFragment,
				List<SortModel> list) {
			this.list = list;
		}

		public void updateListView(List<SortModel> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return this.list.size();
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
			ViewHolder holder = new ViewHolder();
			final SortModel mContent = list.get(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.view_list_chat_member_info, null);
				holder.img_photo = (ImageView) convertView
						.findViewById(R.id.memberinfo_photo);
				holder.tvLetter = (TextView) convertView
						.findViewById(R.id.datalog);
				holder.tvTitle = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 根据position获取分类的首字母的char ascii值
			int section = getSectionForPosition(position);

			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == getPositionForSection(section)) {
				holder.tvLetter.setVisibility(View.VISIBLE);
				holder.tvLetter.setText(mContent.getSortLetters());
			} else {
				holder.tvLetter.setVisibility(View.GONE);
			}
			holder.img_photo.setBackgroundDrawable(list.get(position).getImg());
			holder.tvTitle.setText(this.list.get(position).getName());
			// holder.img_photo.setBackgroundResource(this.mlist.get(position).getImg());
			return convertView;
		}

		@Override
		public int getPositionForSection(int section) {
			// TODO Auto-generated method stub
			for (int i = 0; i < getCount(); i++) {
				String sortStr = list.get(i).getSortLetters();
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}

			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			// TODO Auto-generated method stub
			return list.get(position).getSortLetters().charAt(0);
		}

		@Override
		public Object[] getSections() {
			// TODO Auto-generated method stub
			return null;
		}

		public class ViewHolder {
			TextView tvLetter, tvTitle;
			ImageView img_photo;
		}
	}

	public class PinyinComparator implements Comparator<SortModel> {

		public int compare(SortModel o1, SortModel o2) {
			// 这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
			if (o2.getSortLetters().equals("#")) {
				return -1;
			} else if (o1.getSortLetters().equals("#")) {
				return 1;
			} else {
				return o1.getSortLetters().compareTo(o2.getSortLetters());
			}
		}
	}

}
