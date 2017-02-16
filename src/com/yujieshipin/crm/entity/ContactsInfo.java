package com.yujieshipin.crm.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class ContactsInfo {
	
//	List<String> friendsList;
//	String groupInfo;
//	String friendsInfo;
	List<ContactsFriends>  contactsFriendsList;
	List<ContactsMember> contactsMemberList;
	List<ContactsGroup> contactsGroupList;
	List<ContactsMemberTeacher> contactsMemberTeacherList;
	Map<String, ContactsMember> linkManDic;
	public Map<String, ContactsMember> getLinkManDic() {
		return linkManDic;
	}

	public void setLinkManDic(Map<String, ContactsMember> linkManDic) {
		this.linkManDic = linkManDic;
	}

	public ContactsInfo() {

	}

	public ContactsInfo(JSONObject jo) {
		contactsFriendsList = new ArrayList<ContactsFriends>();
		JSONArray ja = jo.optJSONArray("好友分组");
		ContactsFriends contactsFriends = null;
		if(ja != null && ja.length() > 0){
			for(int i = 0; i < ja.length(); i++){
				contactsFriends = new ContactsFriends();
				String result = ja.optString(i);
				contactsFriends.setFriendsName(result);
				JSONObject memberJO = jo.optJSONObject("老师好友信息");
				contactsFriends.setFriendsMember(memberJO.optString(result));
				contactsFriendsList.add(contactsFriends);
			}
		}
		
		contactsMemberList = new ArrayList<ContactsMember>();
		contactsMemberTeacherList = new ArrayList<ContactsMemberTeacher>();	
        linkManDic = new HashMap<String, ContactsMember>();  
         
        JSONArray jaData = jo.optJSONArray("数据源_用户信息列表");
		if (jaData != null && jaData.length() > 0) {
			for (int i = 0; i < jaData.length(); i++) {
				JSONObject joData = jaData.optJSONObject(i);
				String userNumber = joData.optString("用户唯一码");
				
				ContactsMember contactsMember = new ContactsMember(joData);
				contactsMemberList.add(contactsMember);
				linkManDic.put(userNumber, contactsMember);
				if (userNumber.indexOf("老师") > -1) {
					ContactsMemberTeacher contactsMemberTeacher = new ContactsMemberTeacher(joData);
					contactsMemberTeacherList.add(contactsMemberTeacher);
				}
				
			}
		}
		
		
		contactsGroupList = new ArrayList<ContactsGroup>();
		JSONArray jaGroup = jo.optJSONArray("老师班级群");
		ContactsGroup contactsGroup = null;
		if (jaGroup != null && jaGroup.length() > 0) {
			for(int i = 0; i < jaGroup.length(); i++){
				JSONObject joGroup = jaGroup.optJSONObject(i);
				JSONObject joGroupMem = jo.optJSONObject("老师群成员信息");
				contactsGroup = new ContactsGroup(joGroup);
				String groupId = contactsGroup.getGroupId();
				contactsGroup.setGroupMember(joGroupMem.optString(groupId));
				contactsGroupList.add(contactsGroup);
			}
		}
		
		
		
		/*friendsList = new ArrayList<String>();
		JSONArray ja = jo.optJSONArray("好友分组");
		for(int i = 0; i < ja.length(); i++){
			String result = ja.optString(i);
			friendsList.add(result);
		}
		
		friendsInfo = jo.optString("老师好友信息");
		
		groupInfo = jo.optString("老师群成员信息");
		
		contactsMemberList = new ArrayList<ContactsMember>();
		JSONArray jaData = jo.optJSONArray("数据源_用户信息列表");
		for (int i = 0; i < jaData.length(); i++) {
			JSONObject joData = jaData.optJSONObject(i);
			ContactsMember contactsMember = new ContactsMember(joData);
			contactsMemberList.add(contactsMember);
		}

		contactsGroupList = new ArrayList<ContactsGroup>();
		JSONArray jaGroup = jo.optJSONArray("老师班级群");
		for (int i = 0; i < jaGroup.length(); i++) {
			JSONObject joGroup = jaGroup.optJSONObject(i);
			ContactsGroup contactsGroup = new ContactsGroup(joGroup);
			contactsGroupList.add(contactsGroup);
		}*/
	}
	public ContactsInfo(Object obj) {
		net.minidev.json.JSONObject jo=(net.minidev.json.JSONObject) obj;
		contactsFriendsList = new ArrayList<ContactsFriends>();
		net.minidev.json.JSONArray ja = (net.minidev.json.JSONArray) jo.get("好友分组");
		ContactsFriends contactsFriends = null;
		if(ja != null && ja.size() > 0){
			for(int i = 0; i < ja.size(); i++){
				contactsFriends = new ContactsFriends();
				String result = (String)ja.get(i);
				contactsFriends.setFriendsName(result);
				net.minidev.json.JSONObject memberJO = (net.minidev.json.JSONObject) jo.get("老师好友信息");
				contactsFriends.setFriendsMember(memberJO.get(result).toString());
				contactsFriendsList.add(contactsFriends);
			}
		}
		
		contactsMemberList = new ArrayList<ContactsMember>();
        linkManDic = new HashMap<String, ContactsMember>();  
         
        net.minidev.json.JSONArray jaData = (net.minidev.json.JSONArray) jo.get("数据源_用户信息列表");
		if (jaData != null && jaData.size() > 0) {
			for (int i = 0; i < jaData.size(); i++) {
				net.minidev.json.JSONObject joData = (net.minidev.json.JSONObject) jaData.get(i);
				String userNumber = (String)joData.get("用户唯一码");
				
				ContactsMember contactsMember = new ContactsMember(joData);
				contactsMemberList.add(contactsMember);
				linkManDic.put(userNumber, contactsMember);
				
				
			}
		}
		
		
	}
	public List<ContactsFriends> getContactsFriendsList() {
		return contactsFriendsList;
	}

	public void setContactsFriendsList(List<ContactsFriends> contactsFriendsList) {
		this.contactsFriendsList = contactsFriendsList;
	}

	public List<ContactsMember> getContactsMemberList() {
		return contactsMemberList;
	}

	public void setContactsMemberList(List<ContactsMember> contactsMemberList) {
		this.contactsMemberList = contactsMemberList;
	}

	public List<ContactsGroup> getContactsGroupList() {
		return contactsGroupList;
	}

	public void setContactsGroupList(List<ContactsGroup> contactsGroupList) {
		this.contactsGroupList = contactsGroupList;
	}

	public List<ContactsMemberTeacher> getContactsMemberTeacherList() {
		return contactsMemberTeacherList;
	}

	public void setContactsMemberTeacherList(
			List<ContactsMemberTeacher> contactsMemberTeacherList) {
		this.contactsMemberTeacherList = contactsMemberTeacherList;
	}

	

	/*public List<String> getFriendsList() {
		return friendsList;
	}

	public void setFriendsList(List<String> friendsList) {
		this.friendsList = friendsList;
	}

	public String getGroupInfo() {
		return groupInfo;
	}

	public void setGroupInfo(String groupInfo) {
		this.groupInfo = groupInfo;
	}

	public String getFriendsInfo() {
		return friendsInfo;
	}

	public void setFriendsInfo(String friendsInfo) {
		this.friendsInfo = friendsInfo;
	}

	public List<ContactsGroup> getContactsGroupList() {
		return contactsGroupList;
	}

	public void setContactsGroupList(List<ContactsGroup> contactsGroupList) {
		this.contactsGroupList = contactsGroupList;
	}

	public List<ContactsMember> getContactsMemberList() {
		return contactsMemberList;
	}

	public void setContactsMemberList(List<ContactsMember> contactsMemberList) {
		this.contactsMemberList = contactsMemberList;
	}*/

}
