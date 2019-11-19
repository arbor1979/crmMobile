package com.yujieshipin.crm.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.yujieshipin.crm.BuildConfig;
import com.yujieshipin.crm.R;
import com.yujieshipin.crm.activity.ImagesActivity;
import com.yujieshipin.crm.api.CampusAPI;
import com.yujieshipin.crm.base.Constants;
import com.yujieshipin.crm.entity.Question;
import com.yujieshipin.crm.fragment.SchoolBillDetailFragment;
import com.yujieshipin.crm.util.AppUtility;
import com.yujieshipin.crm.util.FileUtility;
import com.yujieshipin.crm.util.PrefUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PopColorListAdapter extends BaseAdapter {
    public JSONArray getMlist() {
        return mlist;
    }

    private JSONArray mlist;
    private Context mContext;
    private Boolean bedit;
    private AQuery aq;
    private Dialog getPictureDiaLog;
    public String getPicturePath() {
        return picturePath;
    }
    private String picturePath;
    private Fragment fragment;
    private String prodid;
    private int curPosition;
    private static final int REQUEST_CODE_TAKE_PICTURE = 3;// //设置图片操作的标志
    private static final int REQUEST_CODE_TAKE_CAMERA = 1;// //设置拍照操作的标志
    private HashMap<Integer, OnFocusChangeListenerImpl> listenerhm=new HashMap();

    public String getProdid() {
        return prodid;
    }

    public PopColorListAdapter(Fragment fragment, JSONArray list, String prodid, Boolean bedit) {
        this.fragment = fragment;
        this.mContext = fragment.getActivity();
        this.mlist = list;
        this.bedit=bedit;
        this.prodid=prodid;
        aq = new AQuery(mContext);
        getPictureDiaLog = new Dialog(mContext, R.style.dialog);
    }
    public Boolean getBedit() {
        return bedit;
    }

    public Fragment getFragment() {
        return fragment;
    }

    @Override
    public int getCount() {
        return mlist.length();
    }

    @Override
    public Object getItem(int position) {
        return mlist.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Person person = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.list_item_color,null);
            person = new Person();
            person.tv_colorname = (TextView)convertView.findViewById(R.id.tv_colorname);
            person.et_colorname = (EditText) convertView.findViewById(R.id.et_colorname);
            person.tv_kucun = (TextView)convertView.findViewById(R.id.tv_kucun);
            person.ll_bgcolor = (LinearLayout)convertView.findViewById(R.id.ll_bgcolor);
            person.et_colornum = (EditText)convertView.findViewById(R.id.et_colornum);
            person.iv_colorimage = (ImageView) convertView.findViewById(R.id.iv_colorimage);
            person.pb_colorimage=(ProgressBar)convertView.findViewById(R.id.pb_colorimage);
            convertView.setTag(person);
        }else{
            person = (Person)convertView.getTag();
        }
        final JSONObject detailItem=mlist.optJSONObject(position);
        if(bedit)
        {
            person.tv_colorname.setVisibility(View.GONE);
            person.et_colorname.setVisibility(View.VISIBLE);
            person.et_colorname.setText(detailItem.optString("name"));
            person.iv_colorimage.setVisibility(View.VISIBLE);
            if(detailItem.optString("colorimage").length()>0)
                aq.id(person.iv_colorimage).progress(R.id.pb_colorimage).image(detailItem.optString("colorimage"), false, true);
            else
                aq.id(person.iv_colorimage).image(R.drawable.pic_add_more);
        }
        else
        {
            person.tv_colorname.setVisibility(View.VISIBLE);
            person.et_colorname.setVisibility(View.GONE);
            person.tv_colorname.setText(detailItem.optString("name"));
            if(detailItem.optString("colorimage").length()>0) {
                person.iv_colorimage.setVisibility(View.VISIBLE);
                aq.id(person.iv_colorimage).progress(R.id.pb_colorimage).image(detailItem.optString("colorimage"), false, true);
            }
            else
                person.iv_colorimage.setVisibility(View.GONE);
        }
        person.tv_kucun.setText(detailItem.optString("kucun"));
        person.ll_bgcolor.setBackgroundColor(Color.parseColor(detailItem.optString("colorvalue")));
        person.et_colornum.setText(detailItem.optString("curnum"));
        PopColorListAdapter.OnFocusChangeListenerImpl listener = listenerhm.get(position);
        if (listener == null) {
            listener = new PopColorListAdapter.OnFocusChangeListenerImpl(position);
            listenerhm.put(position, listener);
        }
        person.et_colornum.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setOnFocusChangeListener(listenerhm.get(position));
                return false;
            }
        });
        person.et_colorname.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setOnFocusChangeListener(listenerhm.get(position));
                return false;
            }
        });
        person.iv_colorimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bedit)
                {
                    if (detailItem.optString("colorimage").equals("") ) {

                        showGetPictureDiaLog(position);
                    }
                    else {
                        showDelOrShowPictureDiaLog(detailItem.optString("colorimage"),position);
                    }
                }
                else
                {
                    ShowPicturesInNewActivity(detailItem.optString("colorimage"));
                }
            }
        });
        return convertView;
    }
    private void ShowPicturesInNewActivity(String imageName)
    {
        ArrayList<String> pictures=new ArrayList<>();
        int m=0,j=0;
        for(int i=0;i<mlist.length();i++)
        {
            JSONObject question1=mlist.optJSONObject(i);
            if(question1.optString("colorimage").length()>0) {
                pictures.add(question1.optString("colorimage"));
                if(imageName.equals(question1.optString("colorimage")))
                    m=j;
                j++;
            }
        }
        Intent intent = new Intent(mContext, ImagesActivity.class);
        intent.putStringArrayListExtra("pics", pictures);
        intent.putExtra("position", m);
        mContext.startActivity(intent);
    }
    class Person{
        TextView tv_colorname;
        EditText et_colorname;
        TextView tv_kucun;
        LinearLayout ll_bgcolor;
        EditText et_colornum;
        ImageView iv_colorimage;
        ProgressBar pb_colorimage;
    }
    private class OnFocusChangeListenerImpl implements View.OnFocusChangeListener {
        private int position;
        public OnFocusChangeListenerImpl(int position) {
            this.position = position;
        }
        @Override
        public void onFocusChange(View arg0, boolean arg1) {
            EditText et = (EditText) arg0;
            JSONObject detailItem = (JSONObject) getItem(position);
            if(arg1) {
                Log.d("", "获得焦点"+position);
            } else {
                Log.d("", "失去焦点"+position);
                try
                {
                    if(et.getId()==R.id.et_colornum ) {
                        int num=0;
                        if(et.getText().toString().length()>0)
                            num = Integer.parseInt(et.getText().toString());
                        if (detailItem.optInt("curnum") != num) {
                            detailItem.put("curnum",num);
                            et.setOnFocusChangeListener(null);
                        }
                    }
                    else if(et.getId()==R.id.et_colorname){
                        String newVal=et.getText().toString();
                        if (detailItem.optString("name") != newVal) {
                            detailItem.put("name",newVal);
                            et.setOnFocusChangeListener(null);
                        }
                    }

                }
                catch(NumberFormatException e)
                {
                    AppUtility.showToastMsg(mContext, "请输入整型数字");
                    et.setText(detailItem.optString("curnum"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public int getCurPosition() {
        return curPosition;
    }

    private void showGetPictureDiaLog(int position) {
        curPosition=position;
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_get_picture, null);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        TextView byCamera = (TextView) view.findViewById(R.id.tv_by_camera);
        TextView byLocation = (TextView) view.findViewById(R.id.tv_by_location);
        getPictureDiaLog.setContentView(view);
        getPictureDiaLog.show();
        Window window = getPictureDiaLog.getWindow();
        window.setGravity(Gravity.BOTTOM);// 在底部弹出
        window.setWindowAnimations(R.style.CustomDialog);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getPictureDiaLog.dismiss();
            }
        });
        //调用系统相机拍照
        byCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(AppUtility.checkPermission((Activity)mContext, 6, Manifest.permission.CAMERA))
                    getPictureByCamera();
                getPictureDiaLog.dismiss();
            }
        });
        //选择本地图片
        byLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(AppUtility.checkPermission((Activity)mContext,7,Manifest.permission.READ_EXTERNAL_STORAGE))
                    getPictureFromLocation();
                getPictureDiaLog.dismiss();
            }
        });
    }
    public void getPictureByCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用android自带的照相机
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            AppUtility.showToastMsg(mContext, mContext.getString(R.string.Commons_SDCardErrorTitle));
            return;
        }
        picturePath = FileUtility.getRandomSDFileName("jpg");
        File mCurrentPhotoFile = new File(picturePath);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", mCurrentPhotoFile)); //Uri.fromFile(tempFile)
        else {
            Uri uri = Uri.fromFile(mCurrentPhotoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        if(fragment!=null)
            fragment.startActivityForResult(intent, REQUEST_CODE_TAKE_CAMERA);
        else
            ((Activity)mContext).startActivityForResult(intent, REQUEST_CODE_TAKE_CAMERA);
    }
    public void getPictureFromLocation() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
            Intent intent;
            intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if(fragment!=null)
                fragment.startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
            else
                ((Activity)mContext).startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } else {
            AppUtility.showToastMsg(mContext, "SD卡不可用");
        }
    }
    private void showDelOrShowPictureDiaLog(final String imageName, final int position) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.view_show_or_del_picture, null);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setVisibility(View.GONE);
        TextView delPicture = (TextView) view.findViewById(R.id.tv_delete);
        TextView showPicture = (TextView) view.findViewById(R.id.tv_show);
        View v = view.findViewById(R.id.view_dividing_line);
        final AlertDialog ad=new AlertDialog.Builder(mContext).setView(view).create();
        Window window = ad.getWindow();
        window.setGravity(Gravity.BOTTOM);// 在底部弹出
        window.setWindowAnimations(R.style.CustomDialog);
        ad.show();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
        //删除图片
        delPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragment!=null)
                    ((SchoolBillDetailFragment)fragment).SubmitDeleteinfo(imageName,position);
                ad.dismiss();
            }
        });
        //显示大图
        showPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPicturesInNewActivity(imageName);
                ad.dismiss();
            }
        });
    }
    public void clearCacheFile(String filename)
    {
        File file=aq.getCachedFile(filename);
        if(file.exists())
            file.delete();
    }

}