package com.miiitv.game.client.gui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.miiitv.game.client.Logger;

public class Start extends Activity {
	
	private final static String TAG = "Start";
	private Context mContext = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.i(TAG, "onCreate");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Logger.i(TAG, "onResume");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Logger.i(TAG, "onDestory");
	}
}
