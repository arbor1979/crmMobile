package com.example.androidgifdemo;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.yujieshipin.crm.R;

public class ExpressionUtil {
	//public static String zhengze = "f0[0-9]{2}|f10[0-7]"; // 正则表达式，用来判断消息内是否有表情
	public static String zhengze = "\\[f\\d{3}\\]"; // 正则表达式，用来判断消息内是否有表情
	
    public static SpannableString getExpressionString(Context context, String str, Hashtable<Integer, GifDrawalbe> cache, Vector<GifDrawalbe> drawables){
    	SpannableString spannableString = new SpannableString(str);
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);		//通过传入的正则表达式来生成一个pattern
        try {
            dealExpression(context, spannableString, sinaPatten, 0, cache, drawables);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return spannableString;
    }
    
	public static void dealExpression(Context context, SpannableString spannableString, Pattern patten, int start, Hashtable<Integer, GifDrawalbe> cache, Vector<GifDrawalbe> drawables) throws Exception {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			String fileName = key.substring(1, key.length() - 1);
			if (matcher.start() < start) {
				continue;
			}
			Field field = R.drawable.class.getDeclaredField(fileName);
			int id = Integer.parseInt(field.get(null).toString());	
			if (id != 0) {
				GifDrawalbe mSmile = null;
				if (cache.containsKey(id)) {
					mSmile = cache.get(id);
				} else {
					mSmile = new GifDrawalbe(context, id);
					cache.put(id, mSmile);
				}
				ImageSpan span = new ImageSpan(mSmile, ImageSpan.ALIGN_BOTTOM);
				int mstart = matcher.start();
				int end = mstart + key.length();
				spannableString.setSpan(span, mstart, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				if (!drawables.contains(mSmile))
					drawables.add(mSmile);
			}
		}
	}
}