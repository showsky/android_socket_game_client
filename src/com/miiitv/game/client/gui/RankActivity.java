package com.miiitv.game.client.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.miiicasa.game.account.Account.Rank;
import com.miiitv.game.client.App;
import com.miiitv.game.client.Logger;
import com.miiitv.game.client.R;
import com.miiitv.game.client.config.Config;

public class RankActivity extends Activity implements OnClickListener, ConnectListener {

	private final static String TAG = "RankActivity";
	private Context mContext = null;
	private TextView winTextView = null;
	private TextView loseTextView = null;
	private AnimationDrawable animTop = null;
	private AnimationDrawable animButtom = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rank);
		Logger.i(TAG, "onCreate");
		mContext = this;

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
		winTextView.setText(rank.win + getString(R.string.rank_win));
		loseTextView.setText(rank.lost + getString(R.string.rank_lost));
	}

	@Override
	protected void onResume() {
		super.onResume();
		Logger.i(TAG, "onResume");
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
				//TODO:
				break;
		}
	}

	@Override
	public void onSuccess() {
		Logger.i(TAG, "onSuccess()");
	}

	@Override
	public void onFail() {
		Logger.i(TAG, "onFail()");
	}
	
	class PrePare extends AsyncTask<Void, Void, Boolean> {
		
		private ProgressDialog loading = null;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loading = new ProgressDialog(mContext);
			loading.setTitle(R.string.rank_sync_title);
			loading.setMessage(getString(R.string.rank_sync_message));
			loading.setCancelable(false);
			loading.setCanceledOnTouchOutside(false);
			loading.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			boolean flag = true;
			App.getInstance().clientService.startUpnp(RankActivity.this);
			return flag;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
	}
}
