package com.yujieshipin.crm.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

import android.annotation.SuppressLint;

@SuppressLint("NewApi")
public  class Dm5 {
	
	public static String dm5(String parameter) throws IOException{
		return byte2hex( encryptMD5(parameter)).toLowerCase();
	}
	
	
	private static byte[] encryptMD5(String data) throws IOException {
		byte[] bytes = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			bytes = md.digest(data.getBytes("utf-8"));
		} catch (GeneralSecurityException gse) {
			throw new IOException(gse);
		}
		return bytes;
	}

	private static String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toUpperCase());
		}
		return sign.toString();
	}

}
