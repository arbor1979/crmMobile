package com.yujieshipin.crm.entity;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="ContactsFriends")
public class ContactsFriends  implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8593074903250248639L;
	@DatabaseField(generatedId=true)
	private int id;
	@DatabaseField
	private String friendsName;
	@DatabaseField
	private String friendsMember;
	
	/*public static List<ContactsFriends> toList(JSONArray ja) {
		List<ContactsFriends> result = new ArrayList<ContactsFriends>();
		ContactsFriends info = null;
		
		if (ja != null && ja.length() > 0) {
			for (int i = 0; i < ja.length(); i++) {
				try {
					info = new ContactsFriends(ja.getString(i).toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result.add(info);
			}
			return result;
		}else{
			System.out.println("没有ContactsFriends数据");
			return null;
		}
	}*/

	public ContactsFriends(){
		
	}
	
	public ContactsFriends(String classname){
		this.friendsName = classname;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFriendsName() {
		return friendsName;
	}
	public void setFriendsName(String friendsName) {
		this.friendsName = friendsName;
	}
	public String getFriendsMember() {
		return friendsMember;
	}
	public void setFriendsMember(String friendsMember) {
		this.friendsMember = friendsMember;
	}
	
}
