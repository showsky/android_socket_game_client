package com.miiitv.game.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.model.message.header.DeviceTypeHeader;
import org.teleal.cling.model.types.UDADeviceType;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.miiitv.game.client.App;
import com.miiitv.game.client.EventType;
import com.miiitv.game.client.Logger;
import com.miiitv.game.client.config.Config;
import com.miiitv.game.client.config.UpnpConfig;
import com.miiitv.game.client.gui.ConnectListener;

public class ClientService extends Service {
	
	private final static String TAG = "ClientService";
	private LocalBinder binder = new LocalBinder();
	public String serverAddress = null;
	public boolean isConnect = false;
	private Connect connect = null;
	public ProgressDialog loading = null;
	public ConnectListener listener = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		Logger.i(TAG, "onBind");
		return binder;
	}
	
	public class LocalBinder extends Binder {
		public ClientService getService() {
			return ClientService.this;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void connectServer(String serverAddress) {
		this.serverAddress = serverAddress;
		connect = new Connect();
		connect.start();
	}
	
	public void startUpnp(boolean isMock) {
		if (isMock) {
			connectServer(Config.SERVER_IP);
		} else {
			if (App.getInstance().upnpService != null) {
				Logger.d(TAG, "Start Upnp");
				AndroidUpnpService upnpService = App.getInstance().upnpService;
				upnpService.getRegistry().addListener(App.getInstance().registryListener);
				UDADeviceType udaType = new UDADeviceType(UpnpConfig.TYPE_NAME, UpnpConfig.TYPE_VERSION);
				upnpService.getControlPoint().search(new DeviceTypeHeader(udaType));
			}
		}
	}
	
	public void disConnectServer() {
		if (connect.socket.isConnected()) {
			Logger.w(TAG, "Close connect server");
			connect.stopConnect();
			connect = null;
			serverAddress = null;
			isConnect = false;
		}
	}
	
	class Connect extends Thread {
		
		private Socket socket = null;
		public Scanner scanner = null;
		public PrintStream ps = null;
		
		private void stopConnect() {
			serverAddress = null;
			isConnect = false;
			if (scanner != null)
				scanner.close();
			try {
				if (ps != null)
					ps.close();
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			serverAddress = null;
			if (listener != null)
				listener.onFail();
		}
		
		private void dispath(String receive) {
			if (TextUtils.isEmpty(receive))
				return;
			try {
				JSONObject json = new JSONObject(receive);
				int type = json.getInt("type");
				Message message = App.getInstance().eventHandler.obtainMessage();
				message.what = type;
				switch (type) {
					case EventType.TYPE_OPTIONS:
						Logger.i(TAG, "Type: EventType.TYPE_OPTIONS");
						message.obj = json.getJSONArray("data");
						break;
					case EventType.TYPE_START:
					case EventType.TYPE_LOCK:
					case EventType.TYPE_UNLOCK:
					case EventType.TYPE_END:
					case EventType.TYPE_CLOSE:
						break;
				}
				message.sendToTarget();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			super.run();
			try {
				if (serverAddress == null)
					return;
				Logger.w(TAG, "Connect server: ", serverAddress);
				socket = new Socket(serverAddress, Config.PORT);
				isConnect = true;
				ps = new PrintStream(socket.getOutputStream());
				ps.println(App.getInstance().getAccount().toString());
				scanner = new Scanner(new InputStreamReader(socket.getInputStream()));
				scanner.useDelimiter("\n");
				if (listener != null)
					listener.onSuccess();
				while (isConnect) {
					if (scanner.hasNext()) {
						String message = scanner.nextLine();
						Logger.d(TAG, "Receive: ", message);
						dispath(message);
					} else {
						stopConnect();
						Logger.e(TAG , "Error connect");
					}
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
				stopConnect();
			} catch (IOException e) {
				e.printStackTrace();
				stopConnect();
			}
		}
	}
}
