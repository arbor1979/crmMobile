package com.yujieshipin.crm.util;

import java.io.Serializable;
import java.util.Map;

/**
 * 序列化map供Bundle传递map使用
 * Created  on 13-12-9.
 */
public class SerializableMap implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Map<String,String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> lastMsgMap) {
        this.map = lastMsgMap;
    }
}
