package com.miiitv.game.client.gui;

import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.miiicasa.game.client.adapter.OptionsAdapter;
import com.miiitv.game.client.App;
import com.miiitv.game.client.Logger;
import com.miiitv.game.client.R;

public class StartActivity extends Activity implements StartListener, ConnectListener {
	
	private final static String TAG = "StartActivity";
	private Context mContext = null;
	private GridView grid = null;
	private RelativeLayout layout = null;
	private OptionsAdapter adapter = null;
	private ProgressDialog loading = null;
	private JSONArray optionsJSON = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.i(TAG, "onCreate");
		setContentView(R.layout.start);
		mContext = this;
		grid = (GridView) findViewById(R.id.start_list);
		layout = (RelativeLayout) findViewById(R.id.start);
		waitLoad();
	}
	
	private void startShock() {
		layout.setBackground(getResources().getDrawable(R.drawable.choice_bg));
		grid.setVisibility(View.GONE);
	}
	
	private void stopShock() {
		layout.setBackground(getResources().getDrawable(R.drawable.bell_bg));
		grid.setVisibility(View.VISIBLE);
	}
	
	private void waitLoad() {
		loading = new ProgressDialog(mContext);
		loading.setTitle(R.string.start_wait_title);
		loading.setMessage(getString(R.string.start_wait_message_1));
		loading.setCancelable(false);
		loading.setCanceledOnTouchOutside(false);
		loading.show();
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
			onFail();
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
	public void start() {
		if (loading != null && loading.isShowing())
			loading.dismiss();
		adapter = new OptionsAdapter(mContext, optionsJSON);
		grid.setAdapter(adapter);
	}

	@Override
	public void lock() {
		startShock();
	}

	@Override
	public void unlock() {
	}

	@Override
	public void end() {
	}

	@Override
	public void close() {
	}

	@Override
	public void onSuccess() {
	}

	@Override
	public void onFail() {
		Logger.i(TAG, "onFail()");
		if (loading != null && loading.isShowing())
			loading.dismiss();
		App.getInstance().upnpService.getControlPoint().getRegistry().removeListener(App.getInstance().registryListener);
		finish();
	}

	@Override
	public void options(JSONArray options) {
		optionsJSON = options;
	}
}