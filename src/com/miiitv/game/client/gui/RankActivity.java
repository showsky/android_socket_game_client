package com.miiitv.game.client.gui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.miiicasa.game.account.Account.Rank;
import com.miiitv.game.client.App;
import com.miiitv.game.client.Logger;
import com.miiitv.game.client.R;

public class RankActivity extends Activity implements OnClickListener {

	private final static String TAG = "Bank";
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
				break;
		}
	}
}
