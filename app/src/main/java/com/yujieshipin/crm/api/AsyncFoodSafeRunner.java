package com.yujieshipin.crm.api;

import android.util.Log;

public class AsyncFoodSafeRunner {
	private static final String TAG = "AsyncFoodSafeRunner";

	public static void request(final String url, final CampusParameters params,
			final String httpMethod, final RequestListener listener) {
		new Thread() {
			@Override
			public void run() {
				try {
					Log.d(TAG, "-->   url: " + url);
					Log.d(TAG, "-->params: " + params);

					String resp = HttpManager.openUrl(url, httpMethod, params,
							params.getValue("pic"));
					listener.onComplete(resp);
				} catch (CampusException e) {
					listener.onError(e);
				}
			}
		}.start();
	}
}
