package com.miiitv.game.network;

public class NetworkException extends Exception {
	
	private static final long serialVersionUID = 1465479415225741417L;
	private final static String TAG = "NetworkException";
	public enum TYPE {
		NETWORK_ERROR,
		API_FAIL,
		HTTP_FAIL,
	};
	private static String[] message = {
		"network error",
		"api fail",
		"http status code no 200 ok",
	};
	private TYPE type = null;
	private int errno = 0;
	private String errmsg = null;
	
	public NetworkException(TYPE type) {
		super(message[type.ordinal()]);
		this.type = type;
	}
	
	public NetworkException(int errno, String errmsg, TYPE type) {
		super("[" + errno + "] " + errmsg);
		this.type = type;
		this.errno = errno;
		this.errmsg = errmsg;
	}
	
	public NetworkException(String message, TYPE type) {
		super(message);
		this.type = type;
	}
	
	public TYPE getType() {
		return type;
	}
	
	public int getErrno() {
		return errno;
	}
	
	public String getErrmsg() {
		return errmsg;
	}
}
