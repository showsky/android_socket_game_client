package com.miiitv.game.client;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.HandlerThread;
import android.os.IBinder;

import com.miiicasa.game.account.Account;
import com.miiitv.game.client.config.Config;
import com.miiitv.game.service.ClientService;
import com.miiitv.game.upnp.BrowseRegistryListener;

public class App extends Application {
	
	private final static String TAG = "App";
	private static App instance = null;
	private Account account = null;
	public AndroidUpnpService upnpService = null;
	private HandlerThread handlerThread = null;
	public EventHandler eventHandler = null;
	public ClientService clientService = null;
	public BrowseRegistryListener registryListener = new BrowseRegistryListener();
	private ServiceConnection upnpServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Logger.i(TAG, "Upnp onServiceDisconnected()");
			upnpService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Logger.i(TAG, "Upnp onServiceConnected()");
			upnpService = (AndroidUpnpService) service;
		}
	};
	private ServiceConnection clientServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Logger.i(TAG, "Client onServiceDisconnected()");
			if (clientService.isConnect)
				clientService.disConnectServer();
			clientService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Logger.i(TAG, "Client onServiceConnected()");
			clientService = ((ClientService.LocalBinder) service).getService();
		}
	};
	
	public App() {
		Logger.setProject(Config.PROJECT_NAME, Config.DEBUG_MODE);
	}
	
	public static App getInstance() {
		return instance;
	}
	
	private void initUpnp() {
		Logger.d(TAG, "Bind service: AndroidUpnpServiceImpl");
		Intent intent = new Intent(getApplicationContext(), AndroidUpnpServiceImpl.class);
		bindService(intent, upnpServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void initClientService() {
		Logger.d(TAG, "Bind service: ClientService");
		Intent intent = new Intent(getApplicationContext(), ClientService.class);
		bindService(intent, clientServiceConnection, Context.BIND_AUTO_CREATE);
	}
	
	
	private void initHandler() {
		if (handlerThread == null) {
			handlerThread = new HandlerThread(TAG);
			handlerThread.start();
		}
		if (eventHandler == null)
			eventHandler = new EventHandler(handlerThread);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logger.d(TAG, "onCreate()");
		instance = this;
		initUpnp();
		initClientService();
		initHandler();
	}
	
	public Account getAccount() {
		if (account == null)
			account = new Account(getApplicationContext());
		return account;
	}
	
	public void closeApp() {
		Logger.e(TAG, "Close App");
		if (handlerThread != null) {
			handlerThread.quit();
			handlerThread = null;
		}
		eventHandler = null;
		if (upnpService != null) {
			unbindService(upnpServiceConnection);
			upnpService = null;
		}
		if (clientService != null) {
			clientService.unbindService(clientServiceConnection);
			clientService = null;
		}
	}
}
