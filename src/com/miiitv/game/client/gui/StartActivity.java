package com.miiitv.game.client.gui;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
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
	private JSONObject optionsJSON = null;
	
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
		App.getInstance().eventHandler.startListener = this;
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
		App.getInstance().eventHandler.startListener = null;
	}

	@Override
	public void start() {
		Logger.i(TAG, "start()");
		grid.post(new Runnable() {
			@Override
			public void run() {
				if (loading != null && loading.isShowing())
					loading.dismiss();
				adapter = new OptionsAdapter(mContext, optionsJSON);
				grid.setAdapter(adapter);
			}
		});
	}

	@Override
	public void lock() {
		Logger.i(TAG, "lock()");
		startShock();
	}

	@Override
	public void unlock() {
		Logger.i(TAG, "unlock()");
	}

	@Override
	public void end() {
		Logger.i(TAG, "end()");
	}

	@Override
	public void close() {
		Logger.i(TAG, "close()");
	}

	@Override
	public void onSuccess() {
		Logger.i(TAG, "onSuccess");
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
	public void options(JSONObject options) {
		optionsJSON = options;
	}

	@Override
	public void win() {
	}
}
