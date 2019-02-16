package com.yujieshipin.crm.api;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.util.Base64;
import com.yujieshipin.crm.util.PrefUtility;

public class CampusAPI {
	

	public static final String HTTP_METHOD = "POST";
	public static final String HTTP_METHOD2 = "GET";

	public static String commonQuestionUrl = "http://www.dandian.net/company/ICampus-faq.php"; // 常见问题
	public static String contractUrl = "http://www.dandian.net/company/ICampus-contract.php"; // 常见问题
	public static String aboutusUrl = "http://www.dandian.net/company/ICampus-aboutus.php"; // 常见问题

	public static String DOWNLOAD_DONE = "http://laoshi.dandian.net/KeJianCounter.php";// 提交下载完成数据
	public static String DOWNLOAD_DELETE = "http://laoshi.dandian.net/KeJianDelete.php";// 提交删除已下载文件数据

	public static void request(final String url, final CampusParameters params,
			final String HTTP_METHOD, RequestListener listener) {
		String domain=PrefUtility.get(Constants.PREF_SCHOOL_DOMAIN,"");
		
		AsyncFoodSafeRunner.request("http://" + domain + "appserver.php" + url,
				params, HTTP_METHOD, listener);
	}

	/**
	 * 用户登录验证
	 * 
	 * @param params
	 * @param listener
	 */
	public static void loginCheck(CampusParameters params,
			RequestListener listener) {
		// request("?action=logincheck", params, HTTP_METHOD, listener);
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/GetUserPwdIsRight.php", params,
				HTTP_METHOD, listener);
	}
	/**
	 * 用户提交设备信息及百度ID
	 * 
	 * @param params
	 * @param listener
	 */
	public static void postBaiDuId(CampusParameters params,
			RequestListener listener) {
		// request("?action=logincheck", params, HTTP_METHOD, listener);
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/BaiDuSdk_Input.php", params,
				HTTP_METHOD, listener);
	}
	/**
	 * 返回用户所有信息,根据周次获取用户上课记录
	 * 
	 * @param params
	 * @param listener
	 */
	public static void initInfo(CampusParameters params,
			RequestListener listener) {
		int week = PrefUtility.getInt(Constants.PREF_SELECTED_WEEK, 0);
		if(week==0)
			request("?action=initinfo&zip=1", params, HTTP_METHOD,
					listener);
		else
			request("?action=initinfo&zip=1&WEEK=" + week, params, HTTP_METHOD,
				listener);
	}

	/***
	 * 功能描述:修改意见反馈信息
	 * 
	 * @author linrr 2013-12-16 下午2:11:20
	 * 
	 * @param params
	 * @param listener
	 */
	public static void feedback(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/SendSMS_GUESTBOOK_ALL.php", params,
				HTTP_METHOD, listener);
	}

	/**
	 * 功能描述:班级通知信息
	 * 
	 * @author linrr 2013-12-17 上午10:22:03
	 * 
	 * @param params
	 * @param listener
	 */
	public static void noticeClass(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/SendSMS_CLASS_NOTIFY.php", params,
				HTTP_METHOD, listener);
	}

	/**
	 * 功能描述:上传文件
	 * 
	 * @author linrr 2013-12-17 上午10:23:00
	 * 
	 * @param params
	 * @param listener
	 */
	public static void uploadFiles(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/upload.php?action=base64", params,
				HTTP_METHOD, listener);
	}
	
	public static void uploadFilesNoBase64(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/upload.php", params,
				HTTP_METHOD, listener);
	}
	/**
	 * 功能描述:修改学生考勤信息
	 * 
	 * @author yanzy 2013-12-4 上午9:47:08
	 * 
	 * @param params
	 * @param listener
	 */
	public static void Changeinfo(CampusParameters params,
			RequestListener listener) {
		String action = params.getValue("action");
		request("?action=" + action, params, HTTP_METHOD, listener);
	}

	/**
	 * 功能描述:加载学生头像
	 * 
	 * @author yanzy 2013-12-9 上午10:09:57
	 * 
	 * @param params
	 * @param listener
	 */
	public static void downLoadStudentPic(CampusParameters params,
			RequestListener listener) {
		String picUrl = params.getValue("picUrl");
		System.out.println("---------------------ApiPicUrl:" + picUrl);
		AsyncFoodSafeRunner.request(picUrl, params, HTTP_METHOD, listener);
	}

	public static void getTeacherInfo(CampusParameters params,
			RequestListener listener) {
		// String user_code = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		// requestData("?JSON=1&DATA="+user_code, params, HTTP_METHOD,
		// listener);
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/GetTeacherInfo.php?IsZip=1", params,
				HTTP_METHOD, listener);
	}
	//最近一次聊天记录
	public static void getLast_ATOALL(CampusParameters params,
			RequestListener listener) {
		// String user_code = PrefUtility.get(Constants.PREF_CHECK_CODE, "");
		// requestData("?JSON=1&DATA="+user_code, params, HTTP_METHOD,
		// listener);
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/SendSMS_GETLast_ATOALL.php", params,
				HTTP_METHOD, listener);
	}
	/**
	 * 功能描述: 聊天发送消息
	 * 
	 * @author yanzy 2013-12-16 上午10:02:20
	 * 
	 * @param params
	 * @param listener
	 */
	public static void smsSend(CampusParameters params, RequestListener listener) {
		String url = "http://laoshi.dandian.net/SendSMS_MSG_ATOB.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}
	/**
	 * 功能描述: 聊天更新已读状态
	 * 
	 * @author QiaoLin 2014-7-9 下午22:36:20
	 * 
	 * @param params
	 * @param listener
	 */
	public static void updatesmsState(CampusParameters params, RequestListener listener) {
		String url = "http://laoshi.dandian.net/GeSmsStatus.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}
	/**
	 * 功能描述: 聊天更新已读状态
	 * 
	 * @author QiaoLin 2014-7-14 下午14:21:20
	 * 
	 * @param params
	 * @param listener
	 */
	public static void postGPS(CampusParameters params, RequestListener listener) {
		String url = "http://laoshi.dandian.net/IOSLData_Input.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}
	/**
	 * 功能描述:下载聊天记录
	 * 
	 * @author yanzy 2013-12-23 下午5:15:56
	 * 
	 * @param params
	 * @param listener
	 */
	public static void smsDownLoad(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/SendSMS_LIST_ATOB.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}

	/**
	 * 功能描述:提交已经下载课件的数据
	 * 
	 * @author zhuliang 2013-12-24 上午11:18:05
	 * 
	 * @param params
	 * @param listener
	 */
	public static void sendDownloadDoneData(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(DOWNLOAD_DONE, params, HTTP_METHOD,
				listener);
	}

	/**
	 * 功能描述:提交删除下载文件的数据
	 * 
	 * @author zhuliang 2013-12-24 上午11:18:23
	 * 
	 * @param params
	 * @param listener
	 */
	public static void sendDownloadDeleteData(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(DOWNLOAD_DELETE, params, HTTP_METHOD,
				listener);
	}

	/**
	 * 功能描述:获取最后一条聊天记录
	 * 
	 * @author yanzy 2013-12-23 下午5:16:09
	 * 
	 * @param params
	 * @param listener
	 */
	public static void getLastChatMsg(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/SendSMS_GetLast_ATOB.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}

	// /**
	// * 功能描述:发送群消息
	// *
	// * @author zhuliang 2014-1-14 下午3:59:57
	// *
	// * @param params
	// * @param listener
	// */
	// public static void sendGroupMsg(CampusParameters params,RequestListener
	// listener){
	// String url = "http://laoshi.dandian.net/SendSMS_QUN.php";
	// AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	// }
	/**
	 * 功能描述:获取校内内容项
	 * 
	 * @author shengguo 2014-4-14 下午5:25:18
	 * 
	 * @param params
	 *            datetime //:1397112337 用户校验码
	 * @param listener
	 */
	public static void getSchool(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/InterfaceStudent/XUESHENG.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}

	/**
	 * 功能描述:获取校内详情列表
	 * 
	 * @author shengguo 2014-4-26 上午10:07:12
	 * 
	 * @param params
	 * @param Interface
	 * @param listener
	 */
	public static void getSchoolItem(CampusParameters params, String Interface,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/InterfaceStudent/" + Interface;
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}

	/**
	 * 功能描述:获取校内详情列表的详情
	 * 
	 * @author shengguo 2014-4-26 上午10:07:55
	 * 
	 * @param params
	 * @param Interface
	 * @param listener
	 */
	public static void getSchoolChild(CampusParameters params,
			String Interface, RequestListener listener) {
		String url = "http://laoshi.dandian.net/InterfaceStudent/" + Interface;
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD2, listener);
	}

	/**
	 * 功能描述:学生身份：点某一节课后的接口数据来源
	 * 
	 * @author shengguo 2014-4-26 上午10:07:55
	 * 
	 * @param params
	 * @param listener
	 */
	public static void getCourseAndTeacherInfo(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/GetCourseAndTeacherInfo.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD2, listener);
	}

	/**
	 * 功能描述:获取课件列表
	 * 
	 * @author shengguo 2014-4-28 上午11:11:42
	 * 
	 * @param params
	 * @param Interface
	 *            接口名
	 * @param listener
	 */
	public static void getDownloadSubject(CampusParameters params,
			String Interface, RequestListener listener) {
		String url = "http://laoshi.dandian.net/" + Interface;
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}

	/**
	 * 功能描述:获取测验状态
	 * 
	 * @author shengguo 2014-4-28 下午5:39:06
	 * 
	 * @param params
	 * @param Interface
	 * @param listener
	 */
	public static void getCeyanStatus(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/GetCeyanStatus.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD2, listener);
	}

	/**
	 * 功能描述:获取测验数据,保存测验结果
	 * 
	 * @author shengguo 2014-4-28 下午5:39:06
	 * 
	 * @param params
	 * @param Interface
	 * @param listener
	 */
	public static void getCeyanInfo(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/GetCeyanInfo.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD2, listener);
	}

	/**
	 * 功能描述:学生获取,保存评价信息
	 * 
	 * @author shengguo 2014-4-30 上午11:31:05
	 * 
	 * @param params
	 * @param listener
	 */
	public static void getPingjiaByStudent(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/GetPingjiaByStudent.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD2, listener);
	}

	/**
	 * 功能描述:学生获取,保存评价信息
	 * 
	 * @author shengguo 2014-4-30 上午11:31:05
	 * 
	 * @param params
	 * @param listener
	 */
	public static void saveTeacherZongJie(CampusParameters params,
			RequestListener listener) {
		request("?action=changezongjieinfo", params, HTTP_METHOD, listener);
	}

	/**
	 * 功能描述:保存学生总结
	 * 
	 * @author shengguo 2014-5-15 下午12:02:46
	 * 
	 * @param params
	 * @param listener
	 */
	public static void saveStudentZongJie(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/GetPingjiaByStudent.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}

	/**
	 * 功能描述:检测更新
	 * 
	 * @author shengguo 2014-6-3 下午3:54:36
	 * 
	 */
	public static void versionDetection(CampusParameters params,
			RequestListener listener) {
		String url = "http://laoshi.dandian.net/update.php";
		AsyncFoodSafeRunner.request(url, params, HTTP_METHOD, listener);
	}
	
	public static void getAddressFromBaidu(double latitude, double longitude, RequestListener listener) {
		String url = String.format("http://api.map.baidu.com/geocoder/v2/?ak=cR269G15Gov4OaRZ1Tko1Hu4&coordtype=wgs84ll&callback=renderReverse&location=%s,%s&output=json&pois=1", latitude, longitude);
		AsyncFoodSafeRunner.request(url, new CampusParameters(), HTTP_METHOD2, listener);
	}
	
	public static void getAlbumList(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/AlbumDownload.php?IsZip=1", params,
				HTTP_METHOD, listener);
	}
	public static void getAlbumDetailList(CampusParameters params,
			RequestListener listener) {
		AsyncFoodSafeRunner.request(
				"http://laoshi.dandian.net/AlbumDownloadDetail.php?IsZip=1", params,
				HTTP_METHOD, listener);
	}
	public static void httpPost(JSONObject jsonParam,final Handler mHandler,final int completeCode)
	{
			
		CampusParameters params = new CampusParameters();
		Iterator<String> iterator = jsonParam.keys();
		while(iterator.hasNext()){
            String key = (String) iterator.next();
            String value="";
            try {
				value = jsonParam.getString(key);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            params.add(key, value);
		}
		
		String siteUrl = PrefUtility.get(Constants.PREF_LOGIN_URL, "");
        siteUrl="http://119.29.6.239:"+siteUrl;
		String url=siteUrl+"/general/ERP/Interface/mobile/service.php";
		RequestListener listener=new RequestListener() {

			@Override
			public void onIOException(IOException e) {

			}

			@Override
			public void onError(CampusException e) {
				Message msg = new Message();
				msg.what = -1;
				msg.obj = e.getMessage();
				mHandler.sendMessage(msg);
			}

			@Override
			public void onComplete(String response) {
				Message msg = new Message();
				msg.what = completeCode;
				msg.obj = response;
				mHandler.sendMessage(msg);
			}
		};
		AsyncFoodSafeRunner.request(
				url, params,
				HTTP_METHOD, listener);
			
	}
}
