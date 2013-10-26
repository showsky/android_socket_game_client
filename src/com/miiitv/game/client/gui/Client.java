package com.miiitv.game.client.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.miiitv.game.client.App;
import com.miiitv.game.client.Logger;
import com.miiitv.game.client.R;

public class Client extends Activity implements OnClickListener {

	private final static String	TAG			= "Client";
	private Context				mContext	= null;
	private App					mApp		= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);
		mContext = this;
		mApp = (App) getApplication();

		((ImageView) findViewById(R.id.client_login)).setOnClickListener(this);
		ImageView loadingTop = (ImageView) findViewById(R.id.client_load_top);
		((AnimationDrawable) loadingTop.getDrawable()).start();
		ImageView loadingBottom = (ImageView) findViewById(R.id.client_load_buttom);
		((AnimationDrawable) loadingBottom.getDrawable()).start();
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
			login();
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
					// isRunLogin = false;
					// StorageCache.getInstance().clearLoginData();
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
			Account account;
			if (isCancelled()) {
				return flag;
			}
			username = params[0];
			facebookID = params[1];
			facebookTok = params[2];

			if (TextUtils.isEmpty(username) || TextUtils.isEmpty(facebookID) || TextUtils.isEmpty(facebookTok))
				return flag;
			account = mApp.getAccount();
			account.setFacebookID(facebookID);
			account.setFacebookToken(facebookTok);

			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (load != null) {
				load.dismiss();
				load = null;
			}
		}
	}
}
