package com.yujieshipin.crm.entity;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 沟通聊天界面数据
 * @author hfthink
 *
 */
@DatabaseTable(tableName="ChatMsg")
public class ChatMsg {
	@DatabaseField(generatedId=true)
	private int id;
	
	/**
	 * 消息发送人
	 */
	
	@DatabaseField
	private String hostid; 
	/**
	 * 消息接收人
	 */
	@DatabaseField
	private String toid; 
	
	@DatabaseField
	private String toname; 
	/**
	 * 聊天内容
	 */
	@DatabaseField
	private String content;
	
	/**
	 * 聊天时间
	 */
	@DatabaseField
	private Date time;
	
	/**
	 * 判断消息是否是登录人发送 1我发送的 0对方发送的
	 */
	@DatabaseField
	private int msgFlag;
	
	@DatabaseField
	private String type;
	
	@DatabaseField
	private String remoteimage;
	
	@DatabaseField
	private String msg_id;
	
	public String getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}
	public String getRemoteimage() {
		return remoteimage;
	}
	public void setRemoteimage(String remoteimage) {
		this.remoteimage = remoteimage;
	}
	public String getSendstate() {
		return sendstate;
	}
	public void setSendstate(String sendstate) {
		this.sendstate = sendstate;
	}
	@DatabaseField
	private String sendstate;
	public ChatMsg(){
		sendstate="";
		remoteimage="";
		msg_id="";
	}
	
//	public static List<ChatMsg> toList(JSONArray ja) {
//		List<ChatMsg> result = new ArrayList<ChatMsg>();
//		ChatMsg info = null;
//		if (ja.length() == 0) {
//			return null;
//		} else {
//			for (int i = 0; i < ja.length(); i++) {
//				JSONObject jo = ja.optJSONObject(i);
//				info = new ChatMsg(jo);
//				result.add(info);
//			}
//			return result;
//		}
//	
//	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getToid() {
		return toid;
	}
	public void setToid(String toid) {
		this.toid = toid;
	}
	
	public String getToname() {
		return toname;
	}
	public void setToname(String toname) {
		this.toname = toname;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public int getMsgFlag() {
		return msgFlag;
	}
	public void setMsgFlag(int msgFlag) {
		this.msgFlag = msgFlag;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getHostid() {
		return hostid;
	}
	public void setHostid(String hostid) {
		this.hostid = hostid;
	}
}
