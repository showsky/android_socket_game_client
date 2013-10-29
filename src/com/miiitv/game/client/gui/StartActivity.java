package com.miiitv.game.client.gui;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

import com.miiitv.game.client.App;
import com.miiitv.game.client.Logger;
import com.miiitv.game.client.R;

public class StartActivity extends Activity implements StartListener, ConnectListener {
	
	private final static String TAG = "StartActivity";
	private Context mContext = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.i(TAG, "onCreate");
		setContentView(R.layout.start);
		mContext = this;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		App.getInstance().clientService.listener = this;
		Logger.i(TAG, "onResume");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Logger.i(TAG, "onDestory");
		mContext = null;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			App.getInstance().clientService.disConnectServer();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Logger.i(TAG, "onPause");
		App.getInstance().clientService.listener = null;
	}

	@Override
	public void start(JSONArray options) {
		// TODO Auto-generated method stub
	}

	@Override
	public void lock() {
		// TODO Auto-generated method stub
	}

	@Override
	public void unlock() {
		// TODO Auto-generated method stub
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSuccess() {
	}

	@Override
	public void onFail() {
		Logger.i(TAG, "onFail()");
		App.getInstance().upnpService.getControlPoint().getRegistry().removeListener(App.getInstance().registryListener);
		finish();
	}
}
