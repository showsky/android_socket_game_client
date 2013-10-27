package com.miiitv.game.service;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.teleal.cling.model.message.header.DeviceTypeHeader;
import org.teleal.cling.model.types.UDADeviceType;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;

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
	private DataOutputStream dos = null;
	public boolean isConnect = false;
	private Connect connect = null;
	public ProgressDialog loading = null;
	private Activity activity = null;
	private ConnectListener listener = null;
	
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
	
	public void startUpnp(ConnectListener listener) {
		if (App.getInstance().upnpService != null) {
			Logger.d(TAG, "Start Upnp");
			this.listener = listener;
			UDADeviceType udaType = new UDADeviceType(UpnpConfig.TYPE_NAME, UpnpConfig.TYPE_VERSION);
			App.getInstance().upnpService.getControlPoint().search(new DeviceTypeHeader(udaType));
		}
	}
	
	public void disConnectServer() {
		if (connect.socket.isConnected()) {
			Logger.w(TAG, "Close connect server");
			try {
				dos.close();
				connect.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			serverAddress = null;
			isConnect = false;
		}
	}
	
	class Connect extends Thread {
		
		public Socket socket = null;	
		
		@Override
		public void run() {
			super.run();
			try {
				Logger.w(TAG, "Connect server: ", serverAddress);
				socket = new Socket(serverAddress, Config.PORT);
				isConnect = true;
				dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				Scanner scanner = new Scanner(new InputStreamReader(socket.getInputStream()));
				scanner.useDelimiter("\n");
				if (listener != null)
					listener.onSuccess();
				while (isConnect) {
					if (scanner.hasNext()) {
						String message = scanner.nextLine();
						Logger.d(TAG, "Receive: ", message);
						Message msg = App.getInstance().eventHandler.obtainMessage();
						msg.what = EventType.TYPE_DEBUG;
						msg.obj = message;
						msg.sendToTarget();
					} else {
						serverAddress = null;
						isConnect = false;
						scanner.close();
						dos.close();
						Logger.e(TAG , "Error connect");
					}
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
				if (listener != null)
					listener.onFail();
			} catch (IOException e) {
				e.printStackTrace();
				if (listener != null)
					listener.onFail();
			}
		}
	}
}
