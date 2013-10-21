package com.miiitv.game.client;

import android.app.Application;

import com.miiitv.game.client.config.Config;

public class App extends Application {
	
	private final static String TAG = "App";
	
	public App() {
		Logger.setProject(Config.PROJECT_NAME, Config.DEBUG_MODE);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Logger.d(TAG, "onCreate()");
	}
}
