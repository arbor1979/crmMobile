package com.yujieshipin.crm.api;

import java.io.IOException;

public interface RequestListener {
	public void onComplete(String response);

	public void onIOException(IOException e);

	public void onError(CampusException e);
}
