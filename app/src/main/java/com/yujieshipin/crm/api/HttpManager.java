package com.yujieshipin.crm.api;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

public class HttpManager {
	// private static final String BOUNDARY = "7cd4a6d158c";
	private static final String TAG = "HttpManager";
	private static final String BOUNDARY = getBoundry();
	private static final String MP_BOUNDARY = "--" + BOUNDARY;
	private static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
	private static final String MULTIPART_FORM_DATA = "multipart/form-data";

	private static final String HTTPMETHOD_POST = "POST";
	public static final String HTTPMETHOD_GET = "GET";

	private static final int SET_CONNECTION_TIMEOUT = 10 * 1000;
	private static final int SET_SOCKET_TIMEOUT = 30 * 1000;

	public static String openUrl(String url, String method,
			CampusParameters params, String file) throws CampusException {
		String result = "";
		try {
			HttpClient client = getNewHttpClient();
			HttpUriRequest request = null;
			ByteArrayOutputStream bos = null;
			CookieStore localCookies = null;
			localCookies = new BasicCookieStore();
			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, localCookies);
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					NetStateManager.getAPN());
			HttpParams httpparams = client.getParams(); 
			StringBuilder user_agent = new StringBuilder();  
			user_agent.append("deviceName:"+android.os.Build.BRAND+" "+android.os.Build.PRODUCT);
			user_agent.append(";deviceType:Android");
			user_agent.append(";localMode:"+android.os.Build.VERSION.SDK);
			user_agent.append(";systemName:"+android.os.Build.USER);
			user_agent.append(";systemVersion:"+android.os.Build.VERSION.RELEASE);
			httpparams.setParameter("http.useragent", user_agent.toString());  
			
			if (method.equals(HTTPMETHOD_GET)) {
				url = url + "?" + Utility.encodeUrl(params);
				HttpGet get = new HttpGet(url);
				request = get;
			} else if (method.equals(HTTPMETHOD_POST)) {
				HttpPost post = new HttpPost(url);
				request = post;
				
				byte[] data = null;
				String _contentType = params.getValue("content-type");

				bos = new ByteArrayOutputStream();
				if (!TextUtils.isEmpty(file)) {
					paramToUpload(bos, params);
					post.setHeader("Content-Type", MULTIPART_FORM_DATA
							+ "; boundary=" + BOUNDARY);
					//Utility.UploadImageUtils.revitionPostImageSize(file);
					imageContentToUpload(bos, file,params.getValue("鏂囦欢鍚?"));
				} else {
					if (_contentType != null) {
						params.remove("content-type");
						post.setHeader("Content-Type", _contentType);
					} else {
						post.setHeader("Content-Type",
								"application/x-www-form-urlencoded");
					}

					String postParam = Utility.encodeParameters(params);
					data = postParam.getBytes("UTF-8");
					bos.write(data);
				}
				data = bos.toByteArray();
				bos.close();
				ByteArrayEntity formEntity = new ByteArrayEntity(data);
				post.setEntity(formEntity);
			} else if (method.equals("DELETE")) {
				request = new HttpDelete(url);
			}
			//Log.d(TAG, "request:" + request.getURI());
			request.addHeader("Accept-Encoding", "gzip"); 
			HttpResponse response = client.execute(request, localContext);
			List<Cookie> respCookieList = localCookies.getCookies();
			for (Cookie ck : respCookieList) {
				Log.d(TAG, "------ck"+ck);
			}
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();
			if (statusCode != 200) {
				result = readHttpResponse(response);
				Log.d(TAG, "statusCode:" + statusCode);
				Log.d(TAG, "response:" + result);
				throw new CampusException(result, statusCode);
			}
			result = readHttpResponse(response);
			Log.d(TAG, "response:" + result);
			return result;
		} catch (Exception e) {
			throw new CampusException(e);
		}
	}

	public static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(params, 5000);
			HttpConnectionParams.setSoTimeout(params, 5000);

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			HttpConnectionParams.setConnectionTimeout(params,
					SET_CONNECTION_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, SET_SOCKET_TIMEOUT);
			HttpClient client = new DefaultHttpClient(ccm, params);
			return client;
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	private static class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					try {
	                    chain[0].checkValidity();
	                } catch (Exception e) {
	                    throw new CertificateException("Certificate not valid or trusted.");
	                }
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	private static void paramToUpload(OutputStream baos,
			CampusParameters params) throws CampusException {
		String key = "";
		for (int loc = 0; loc < params.size(); loc++) {
			key = params.getKey(loc);
			StringBuilder temp = new StringBuilder(10);
			temp.setLength(0);
			temp.append(MP_BOUNDARY).append("\r\n");
			temp.append("content-disposition: form-data; name=\"").append(key)
					.append("\"\r\n\r\n");
			temp.append(params.getValue(key)).append("\r\n");
			byte[] res = temp.toString().getBytes();
			try {
				baos.write(res);
			} catch (IOException e) {
				throw new CampusException(e);
			}
		}
	}

	private static void imageContentToUpload(OutputStream out, String imgpath,String newFileName)
			throws CampusException {
		if (imgpath == null) {
			return;
		}
		StringBuilder temp = new StringBuilder();
		
		temp.append(MP_BOUNDARY).append("\r\n");
		temp.append("Content-Disposition: form-data; name=\"filename\"; filename=\"")
				.append(newFileName).append("\"\r\n");
		String filetype = "image/png";
		temp.append("Content-Type: ").append(filetype).append("\r\n\r\n");
		byte[] res = temp.toString().getBytes();
		FileInputStream input = null;
		try {
			out.write(res);
			input = new FileInputStream(imgpath);
			byte[] buffer = new byte[1024 * 50];
			while (true) {
				int count = input.read(buffer);
				if (count == -1) {
					break;
				}
				out.write(buffer, 0, count);
			}
			//out.write("\r\n".getBytes());
			out.write(("\r\n" + END_MP_BOUNDARY).getBytes());
		} catch (IOException e) {
			throw new CampusException(e);
		} finally {
			if (null != input) {
				try {
					input.close();
				} catch (IOException e) {
					throw new CampusException(e);
				}
			}
		}
	}

	@SuppressLint("DefaultLocale")
	private static String readHttpResponse(HttpResponse response) {
		String result = "";
		HttpEntity entity = response.getEntity();
		InputStream inputStream;
		try {
			
			/*
			inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();
			Header header = response.getFirstHeader("Content-Encoding");
			if (header != null
					&& header.getValue().toLowerCase().indexOf("gzip") > -1) {
				inputStream = new GZIPInputStream(inputStream);
			}
			*/
			boolean isGzip = false;  
            Header[] headers = response.getHeaders("Content-Encoding");  
            for (Header header : headers) {  
                if ("gzip".equals(header.getValue())) {  
                    isGzip = true;  
                    break;  
                }  
            }  
            
            if (isGzip) {  
                // 解压缩  
            	try {
					result = parseGzip(entity);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            } else {  
                // 正常读取  
            	result = EntityUtils.toString(entity, HTTP.UTF_8);  
            }  
            
            /*
			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			result = new String(content.toByteArray());
			*/
			return result;
		} catch (IllegalStateException e) {
		} catch (IOException e) {
		}
		return result;
	}
	
	private static String parseGzip(HttpEntity entity) throws Exception {  
        InputStream in = entity.getContent();  
        GZIPInputStream gzipInputStream = new GZIPInputStream(in);  
        BufferedReader reader = new BufferedReader(new InputStreamReader(  
                gzipInputStream, HTTP.UTF_8));  
        String line = null;  
        StringBuffer sb = new StringBuffer();  
        while ((line = reader.readLine()) != null) {  
            sb.append(line).append("\n");  
        }  
        return sb.toString();  
    }  
	
	public static String getBoundry() {
		StringBuffer _sb = new StringBuffer();
		for (int t = 1; t < 12; t++) {
			long time = System.currentTimeMillis() + t;
			if (time % 3 == 0) {
				_sb.append((char) time % 9);
			} else if (time % 3 == 1) {
				_sb.append((char) (65 + time % 26));
			} else {
				_sb.append((char) (97 + time % 26));
			}
		}
		return _sb.toString();
	}

}
