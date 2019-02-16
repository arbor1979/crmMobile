package com.yujieshipin.crm.api;

public class CampusException extends Exception {

	private static final long serialVersionUID = 159174837884154179L;
	private int statusCode = -1;

	public CampusException(String msg) {
		super(msg);
	}

	public CampusException(Exception cause) {
		super(cause);
	}

	public CampusException(String msg, int statusCode) {
		super(msg);
		this.statusCode = statusCode;
	}

	public CampusException(String msg, Exception cause) {
		super(msg, cause);
	}

	public CampusException(String msg, Exception cause, int statusCode) {
		super(msg, cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public CampusException() {
		super();
	}

	public CampusException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public CampusException(Throwable throwable) {
		super(throwable);
	}

	public CampusException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

}
