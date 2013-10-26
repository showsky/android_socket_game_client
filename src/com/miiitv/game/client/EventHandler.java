package com.miiitv.game.client;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

public class EventHandler extends Handler {
	
	private final static String TAG = "EventHandler";

	public EventHandler(HandlerThread handlerThread) {
		super(handlerThread.getLooper());
	}
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
	}
}
