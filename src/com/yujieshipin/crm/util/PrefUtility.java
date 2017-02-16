package com.yujieshipin.crm.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.yujieshipin.crm.CampusApplication;

public class PrefUtility {

	private static SharedPreferences pref;

	public static SharedPreferences getPref() {
		if (pref == null) {
			pref = PreferenceManager
					.getDefaultSharedPreferences(CampusApplication.getContext());
		}
		return pref;
	}

	public static void put(String name, String value) {
		SharedPreferences.Editor edit = getPref().edit();
//		edit.clear();
		edit.remove(name);
		edit.putString(name, value);
		edit.commit();
	}

	public static void put(String name, Long value) {
		SharedPreferences.Editor edit = getPref().edit();
		edit.remove(name);
		edit.putLong(name, value);
		edit.commit();
	}

	public static void put(String name, Boolean value) {
		SharedPreferences.Editor edit = getPref().edit();
		edit.remove(name);
		edit.putBoolean(name, value);
		edit.commit();
	}

	public static void put(String name, int value) {
		SharedPreferences.Editor edit = getPref().edit();
		edit.remove(name);
		edit.putInt(name, value);
		edit.commit();
	}
	public static boolean contains(String name) {
		return getPref().contains(name);
	}

	public static boolean getBoolean(String name, boolean defaultValue) {
		return getPref().getBoolean(name, defaultValue);
	}
	public static int getInt(String name, int defaultValue) {
		return getPref().getInt(name, defaultValue);
	}

	public static Long getLong(String name, Long defaultValue) {
		return getPref().getLong(name, defaultValue);
	}

	public static String get(String name, String defaultValue) {
		return getPref().getString(name, defaultValue);
	}

	public static void putObject(String name, Object value) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 将Product对象放到OutputStream中
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(value);
			String productBase64 = new String(Base64.encode(baos.toByteArray()));
			SharedPreferences.Editor edit = getPref().edit();
			// 将编码后的字符串写到base64.xml文件中
			edit.putString(name, productBase64);
			edit.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public static Object getObject(String name) 
	{
		Object product=null;
		try {
		String Base64Str = getPref().getString(name, "");
		// 对Base64格式的字符串进行解码
		byte[] base64Bytes = Base64.decode(Base64Str.getBytes());
		ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		// 从ObjectInputStream中读取Product对象
		product = (Object) ois.readObject();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return product;
	}
	
	public static <T extends Enum<T>> void putEnum(Enum<T> value) {
		String key = value.getClass().getName();
		put(key, value.name());
		enums.put(key, value);
	}

	public static void clearEnum(Class<?> cls) {
		String key = cls.getName();
		put(key, (String) null);
	}

	private static Map<String, Object> enums = new HashMap<String, Object>();

	@SuppressWarnings({ "unchecked" })
	public static <T extends Enum<T>> T getEnum(Class<T> cls, T defaultValue) {

		String key = cls.getName();

		T result = (T) enums.get(key);
		if (result == null) {
			result = PrefUtility.getPrefEnum(cls, defaultValue);
			enums.put(key, result);
		}

		return result;
	}

	private static <T extends Enum<T>> T getPrefEnum(Class<T> cls,
			T defaultValue) {

		T result = null;

		String pref = get(cls.getName(), null);

		if (pref != null) {
			try {
				result = Enum.valueOf(cls, pref);
			} catch (Exception e) {
				clearEnum(cls);
				AppUtility.report(e);
			}
		}

		if (result == null) {
			result = defaultValue;
		}

		return result;
	}

	// 12-30 03:04:49.125: W/System.err(20443):
	// device:ffffffff-b588-0cd1-ffff-ffffb12a7939

	private static String[] deviceIds = {
			"ffffffff-b588-0cd1-ffff-ffffb12a7939",
			"00000000-582e-8c83-ffff-ffffb12a7939",
			"ffffffff-a7af-71df-0033-c5870033c587",
			"00000000-2e56-36d7-ffff-ffffb12a7939" };
	private static Boolean testDevice;

	public static boolean isTestDevice() {

		if (testDevice == null) {
			testDevice = isEmulator() || isTestDevice(getDeviceId());
		}

		return testDevice;
	}

	public static boolean isEmulator() {
		return "sdk".equals(Build.PRODUCT);
	}

	private static boolean isTestDevice(String deviceId) {

		for (int i = 0; i < deviceIds.length; i++) {
			if (deviceIds[i].equals(deviceId)) {
				return true;
			}
		}
		return false;
	}

	private static String deviceId;

	public static String getDeviceId() {

		if (deviceId == null) {
			Context context = CampusApplication.getContext();

			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String tmDevice, tmSerial, androidId;
			tmDevice = "" + tm.getDeviceId();
			tmSerial = "" + tm.getSimSerialNumber();
			androidId = ""
					+ android.provider.Settings.Secure.getString(
							context.getContentResolver(),
							android.provider.Settings.Secure.ANDROID_ID);

			UUID deviceUuid = new UUID(androidId.hashCode(),
					((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
			deviceId = deviceUuid.toString();

		}

		System.err.println("device:" + deviceId);

		return deviceId;
	}
}
