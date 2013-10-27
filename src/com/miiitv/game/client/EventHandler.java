package com.miiitv.game.client;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.Toast;

import com.miiitv.game.service.ClientService;

public class EventHandler extends Handler {
	
	private final static String TAG = "EventHandler";

	public EventHandler(HandlerThread handlerThread) {
		super(handlerThread.getLooper());
	}
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
			case EventType.TYPE_CONNECT:
				Logger.i(TAG, "EventType: TYPE_CONNECT");
				String serverAddress = (String) msg.obj;
				Logger.d(TAG, "Server address: ", serverAddress);
				ClientService clientService = App.getInstance().clientService;
				if (clientService != null)
					clientService.connectServer(serverAddress);
				break;
			case EventType.TYPE_DEBUG:
				String log = (String) msg.obj;
				Toast.makeText(App.getInstance(), log, Toast.LENGTH_SHORT).show();
				break;
		}
	}
}
