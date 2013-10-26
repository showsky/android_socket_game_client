package com.miiitv.game.client.gui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.miiitv.game.client.Logger;
import com.miiitv.game.client.R;

public class Client extends Activity implements OnClickListener {
	
	private final static String TAG = "Client";
	private Context mContext = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);
		mContext = this;
		((ImageView) findViewById(R.id.client_login)).setOnClickListener(this);
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
		mContext = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.client_login:
				break;
		}
	}	
}
