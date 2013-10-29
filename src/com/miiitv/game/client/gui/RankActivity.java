package com.miiitv.game.client.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.ProfilePictureView;
import com.miiicasa.game.account.Account.Rank;
import com.miiitv.game.client.App;
import com.miiitv.game.client.Logger;
import com.miiitv.game.client.R;
import com.miiitv.game.client.config.Config;

public class RankActivity extends Activity implements OnClickListener, ConnectListener {

	private final static String TAG = "RankActivity";
	private final static int TIME_OUT = 20 * 1000;
	private Context mContext = null;
	private TextView winTextView = null;
	private TextView loseTextView = null;
	private ProfilePictureView facebookAvatar = null;
	private AnimationDrawable animTop = null;
	private AnimationDrawable animButtom = null;
	private Handler handler = null;
	private ProgressDialog loading = null;
	private Runnable cancel = new Runnable() {
		@Override
		public void run() {
			if (loading != null && loading.isShowing()) {
				loading.dismiss();
				loading = null;
			}
			onFail();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rank);
		Logger.i(TAG, "onCreate");
		mContext = this;
		handler = new Handler();
		facebookAvatar = (ProfilePictureView) findViewById(R.id.rank_facebook_avatar);

		ImageView loadingTop = (ImageView) findViewById(R.id.rank_load_top);
		ImageView loadingBottom = (ImageView) findViewById(R.id.rank_load_buttom);
		animButtom = (AnimationDrawable) loadingBottom.getDrawable();
		animTop = (AnimationDrawable) loadingTop.getDrawable();

		winTextView = (TextView) findViewById(R.id.rank_win);
		loseTextView = (TextView) findViewById(R.id.rank_lose);
		Typeface font = Typeface.createFromAsset(getAssets(), Config.FONT_FACE);
		winTextView.setTypeface(font);
		loseTextView.setTypeface(font);

		((TextView) findViewById(R.id.rank_start)).setOnClickListener(this);
		updateRank();
	}

	private void updateRank() {
		Rank rank = App.getInstance().getAccount().rank;
		if (rank == null)
			return;
		facebookAvatar.setCropped(true);
		facebookAvatar.setProfileId(App.getInstance().getAccount().getFacebookID());
		winTextView.setText(rank.win + getString(R.string.rank_win));
		loseTextView.setText(rank.lost + getString(R.string.rank_lost));
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.i(TAG, "onResume");
		App.getInstance().clientService.listener = this;
		animTop.start();
		animButtom.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Logger.d(TAG, "KeyEvent back");
			
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Logger.i(TAG, "onPause");
		App.getInstance().clientService.listener = null;
		animTop.stop();
		animButtom.stop();
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
			case R.id.rank_start:
				handler.postDelayed(cancel, TIME_OUT);
				new PrePare().execute();
				break;
		}
	}

	@Override
	public void onSuccess() {
		Logger.i(TAG, "onSuccess()");
		handler.removeCallbacks(cancel);
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (loading != null && loading.isShowing()) {
					loading.dismiss();
					loading = null;
				}
				Intent intent = new Intent(mContext, StartActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});
	}

	@Override
	public void onFail() {
		Logger.i(TAG, "onFail()");
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mContext, R.string.rank_sync_error, Toast.LENGTH_SHORT).show();
			}
		});
		App.getInstance().upnpService.getControlPoint().getRegistry().removeListener(App.getInstance().registryListener);
	}
	
	class PrePare extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loading = new ProgressDialog(mContext);
			loading.setTitle(R.string.rank_sync_title);
			loading.setMessage(getString(R.string.rank_sync_message));
			loading.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					handler.post(cancel);
				}
			});
			loading.setCanceledOnTouchOutside(false);
			loading.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			boolean flag = true;
			App.getInstance().clientService.startUpnp();
			return flag;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}
}
