package com.yujieshipin.crm.util;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

import com.yujieshipin.crm.api.CampusParameters;
import com.yujieshipin.crm.api.HttpManager;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.CustomMultipartEntity;
import com.yujieshipin.crm.entity.CustomMultipartEntity.ProgressListener;


public class HttpMultipartPost extends AsyncTask<String, Integer, String> {
	private Context context;  
    public ProgressDialog pd;
    private long totalSize;
    private CampusParameters myParams;
    HttpClient httpClient;
    public HttpMultipartPost(Context context, CampusParameters params) {  
        this.context = context;  
        this.myParams = params;  
    }  
  
    
    @Override  
    protected void onPreExecute() {  
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
        pd.setMessage("正在上传...");  
        pd.setCancelable(true);  
        pd.show();  
        pd.setOnCancelListener(new OnCancelListener() {                
        
			@Override
			public void onCancel(DialogInterface dialog) {
				if(httpClient!=null)
	    			httpClient.getConnectionManager().shutdown();
				
			}
        	  }); 
        
    }  
  
    @Override  
    protected String doInBackground(String... params) {  
        String serverResponse = null;  
  
        httpClient =HttpManager.getNewHttpClient();
        HttpContext httpContext = new BasicHttpContext();  
        String siteUrl = PrefUtility.get(Constants.PREF_LOGIN_URL, "");
        siteUrl="http://119.29.6.239:"+siteUrl;

		String url=siteUrl+"/general/ERP/Interface/mobile/service.php";
        HttpPost httpPost = new HttpPost(url);  
        
        try {  
            CustomMultipartEntity multipartContent = new CustomMultipartEntity(
            		new ProgressListener() {  
                        @Override  
                        public void transferred(long num) {  
                            publishProgress((int) ((num / (float) totalSize) * 100));  
                        }  
                    });  
            // We use FileBody to transfer an image  
            
            for(int i=0;i<myParams.size();i++)
            {
            	String key=myParams.getKey(i);
            	String value=myParams.getValue(key);
            	multipartContent.addPart(key, new StringBody(value, Charset.forName("UTF-8")));   
            	
            }  
            multipartContent.addPart("filename", new FileBody(new File(  
            		myParams.getValue("pic"))));  
           
            totalSize = multipartContent.getContentLength();  
  
            // Send it  
            httpPost.setEntity(multipartContent);  
            
            HttpResponse response = httpClient.execute(httpPost, httpContext);  
            serverResponse = EntityUtils.toString(response.getEntity());  
              
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return serverResponse;  
    }  
  
    @Override  
    protected void onProgressUpdate(Integer... progress) {  
        pd.setProgress((int) (progress[0]));  
        if(pd.getProgress()==100)
        	pd.setMessage("上传完毕，等待返回结果..");
    }  
  
      
  
    @Override  
    protected void onCancelled() {  
        System.out.println("cancle");  
        pd.dismiss(); 
    } 
}
