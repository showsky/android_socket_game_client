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
import com.miiitv.game.upnp.BrowseRegistryListener;

public class App extends Application {
	
	private final static String TAG = "App";
	private static App instance = null;
	private Account account = null;
	private AndroidUpnpService upnpService = null;
	private HandlerThread handlerThread = null;
	private EventHandler eventHandler = null;
	private BrowseRegistryListener registryListener = new BrowseRegistryListener();
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			upnpService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			upnpService = (AndroidUpnpService) service;
			upnpService.getRegistry().addListener(registryListener);
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
		Intent intent = new Intent(this, AndroidUpnpServiceImpl.class);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void initHandler() {
		if (handlerThread == null)
			handlerThread = new HandlerThread(TAG);
		if (eventHandler == null)
			eventHandler = new EventHandler(handlerThread);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logger.d(TAG, "onCreate()");
		instance = this;
		initUpnp();
		initHandler();
	}
	
	public Account getAccount() {
		if (account == null)
			account = new Account(getApplicationContext());
		return account;
	}
}
