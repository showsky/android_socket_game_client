package com.miiitv.game.client.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.miiicasa.game.account.Account;
import com.miiicasa.game.account.Account.Rank;
import com.miiitv.game.client.Api;
import com.miiitv.game.client.App;
import com.miiitv.game.client.Logger;
import com.miiitv.game.client.R;

public class ClientActivity extends Activity implements OnClickListener {

	private final static String	TAG = "ClientActivity";
	private Context mContext = null;
	private AnimationDrawable animTop = null;
	private AnimationDrawable animButtom = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);
		mContext = this;

		((ImageView) findViewById(R.id.client_login)).setOnClickListener(this);
		ImageView loadingTop = (ImageView) findViewById(R.id.client_load_top);
		ImageView loadingBottom = (ImageView) findViewById(R.id.client_load_buttom);
		animButtom = (AnimationDrawable) loadingBottom.getDrawable();
		animTop = (AnimationDrawable) loadingTop.getDrawable();
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
		case R.id.client_login:
			if ( ! App.getInstance().getAccount().isSyncUser()) {
				login();
			} else {
				new Login().execute(null, null, null);
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Logger.i(TAG, "onActivityResult");
		Session.getActiveSession().onActivityResult((Activity) mContext, requestCode, resultCode, data);
	}

	private void login() {
		Session.openActiveSession((Activity) mContext, true, new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					Logger.d(TAG, "session open");
					Request.executeMeRequestAsync(session, new GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user, Response response) {
							if (user != null) {
								Logger.e(TAG, "Facebook nmae: ", user.getName());
								Logger.e(TAG, "Facebook id: ", user.getId());
								Logger.e(TAG, "Facebook tok:", Session.getActiveSession().getAccessToken());
								new Login().execute(user.getName(), user.getId(), Session.getActiveSession()
										.getAccessToken());
							} else {
								Toast.makeText(mContext, R.string.facebook_user_error, Toast.LENGTH_SHORT).show();
							}
						}
					});
				} else if (state.equals(SessionState.CLOSED_LOGIN_FAILED)) {
					Toast.makeText(mContext, "Fail Facebook", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	class Login extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog	load		= null;
		private String			username	= null;
		private String			facebookID	= null;
		private String			facebookTok	= null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			load = new ProgressDialog(mContext);
			load.setTitle(R.string.client_login_title);
			load.setMessage(getString(R.string.client_login_message));
			load.setCancelable(false);
			load.setCanceledOnTouchOutside(false);
			load.show();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			Logger.d(TAG, "Cancel login");
			if (load != null) {
				load.dismiss();
				load = null;
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean flag = false;
			if (isCancelled()) {
				return flag;
			}
			username = params[0];
			facebookID = params[1];
			facebookTok = params[2];
			Account account = App.getInstance().getAccount();
			if ( ! account.isSyncUser()) {
				account.setFacebookID(facebookID);
				account.setFacebookToken(facebookTok);
				account.setFacebookName(username);
				flag = Api.getInstance().syncUser(facebookID, facebookTok);
				if (flag) {
					account.setSyncUser(true);
				} else {
					return flag;
				}
				account.save();
			}
			Rank rank = Api.getInstance().getRank(account.getFacebookID());
			if (rank != null) {
				flag = true;
				account.rank = rank;
			}

			return flag;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (load != null) {
				load.dismiss();
				load = null;
			}
			if (result) {
				Intent intent = new Intent(mContext, RankActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();
				Toast.makeText(mContext, R.string.client_login_success, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, R.string.client_login_error, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
