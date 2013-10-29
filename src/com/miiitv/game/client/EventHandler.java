package com.miiitv.game.client;

import org.json.JSONArray;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.Toast;

import com.miiitv.game.client.gui.StartListener;
import com.miiitv.game.service.ClientService;

public class EventHandler extends Handler {
	
	private final static String TAG = "EventHandler";
	public StartListener startListener = null;
	
	public EventHandler(HandlerThread handlerThread) {
		super(handlerThread.getLooper());
	}
	
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
			case EventType.TYPE_CONNECT:
				Logger.d(TAG, "EventType: TYPE_CONNECT");
				String serverAddress = (String) msg.obj;
				Logger.d(TAG, "Server address: ", serverAddress);
				ClientService clientService = App.getInstance().clientService;
				if (clientService != null)
					clientService.connectServer(serverAddress);
				break;
			case EventType.TYPE_DEBUG:
				Logger.d(TAG, "EventType: EventType.TYPE_DEBUG");
				String log = (String) msg.obj;
				Toast.makeText(App.getInstance(), log, Toast.LENGTH_SHORT).show();
				break;
			case EventType.TYPE_START:
				Logger.d(TAG, "EventType: EventType.TYPE_START");
				JSONArray options = (JSONArray) msg.obj;
				if (startListener != null)
					startListener.start(options);
				break;
			case EventType.TYPE_LOCK:
				Logger.d(TAG, "EventType: EventType.TYPE_LOCK");
				if (startListener != null)
					startListener.lock();
				break;
			case EventType.TYPE_UNLOCK:
				Logger.d(TAG, "EventType: EventType.TYPE_UNLOCK");
				if (startListener != null)
					startListener.unlock();
				break;
			case EventType.TYPE_END:
				Logger.d(TAG, "EventType: EventType.TYPE_END");
				if (startListener != null)
					startListener.end();
				break;
			case EventType.TYPE_CLOSE:
				if (startListener != null)
					startListener.close();
				break;
		}
	}
}
