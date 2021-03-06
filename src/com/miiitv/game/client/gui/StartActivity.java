package com.miiitv.game.client.gui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.miiicasa.game.client.adapter.OptionsAdapter;
import com.miiitv.game.client.App;
import com.miiitv.game.client.EventType;
import com.miiitv.game.client.Logger;
import com.miiitv.game.client.R;

public class StartActivity extends Activity implements StartListener, ConnectListener, SensorEventListener {
	
	private final static String TAG = "StartActivity";
	private Context mContext = null;
	private GridView grid = null;
	private RelativeLayout layout = null;
	private OptionsAdapter adapter = null;
	private ProgressDialog loading = null;
	private JSONObject optionsJSON = null;
	private SensorManager manager = null;
	private Vibrator myVibrator = null;
	private Sensor sensor = null;
	private float mAccelLast = 0;
	private float mAccelCurrent = 0;
	private float mAccel = 0;
	private boolean keyLock = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.i(TAG, "onCreate");
		setContentView(R.layout.start);
		mContext = this;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		grid = (GridView) findViewById(R.id.start_list);
		layout = (RelativeLayout) findViewById(R.id.start);
		myVibrator = (Vibrator) getApplication().getSystemService(Context.VIBRATOR_SERVICE);
		waitLoad();
	}
	
	private final void register() {
        manager.registerListener(this, sensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }
	
	private final void unregister() {
		manager.unregisterListener(this);
	}
	
	private void startShock() {
		register();
		layout.setBackground(getResources().getDrawable(R.drawable.bell_bg));
		grid.setVisibility(View.GONE);
	}
	
	private void stopShock() {
		unregister();
		layout.setBackground(getResources().getDrawable(R.drawable.choice_bg));
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
		Logger.i(TAG, "onResume");
		App.getInstance().clientService.connectListener = this;
		App.getInstance().clientService.startListener = this;
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
		App.getInstance().clientService.connectListener = null;
		App.getInstance().clientService.startListener = null;
	}

	@Override
	public void start() {
		Logger.i(TAG, "start()");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (loading != null && loading.isShowing())
					loading.dismiss();
				keyLock = false;
				Toast.makeText(mContext, R.string.start_start, Toast.LENGTH_SHORT).show();
				startShock();
			}
		});
	}

	@Override
	public void lock() {
		Logger.i(TAG, "lock()");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				layout.setBackground(getResources().getDrawable(R.drawable.lock_bg));
			}
		});
	}

	@Override
	public void unlock() {
		Logger.i(TAG, "unlock()");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				stopShock();
				myVibrator.vibrate(2000);
			}
		});
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
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (loading != null && loading.isShowing())
					loading.dismiss();
				App.getInstance().upnpService.getControlPoint().getRegistry().removeListener(App.getInstance().registryListener);
				finish();
			}
		});
	}

	@Override
	public void options(JSONObject options) {
		Logger.d(TAG, "options() ", options.toString());
		optionsJSON = options;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adapter = new OptionsAdapter(mContext, optionsJSON);
				grid.setAdapter(adapter);
			}
		});
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Logger.d(TAG, "onItemClick: ", String.valueOf(position));
				if (keyLock)
					return;
				JSONObject json = new JSONObject();
				try {
					json.put("type", EventType.TYPE_ANSWER);
					json.put("data", position + 1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				App.getInstance().clientService.sendMessage(json.toString());
				keyLock = true;
			}
		});
	}

	@Override
	public void win() {
		Logger.d(TAG, "win()");
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mContext, R.string.start_win, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt(x * x + y * y + z * z);
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta; // perform low-cut filter

        if (mAccel > 12) {
        	Logger.d(TAG, "Accel: ", String.valueOf(mAccel));
        	JSONObject json = new JSONObject();
        	try {
				json.put("type", EventType.TYPE_SHOCK);
			} catch (JSONException e) {
				e.printStackTrace();
			}
        	App.getInstance().clientService.sendMessage(json.toString());
        }
	}
}
