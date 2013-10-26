package com.miiicasa.game.account;

import android.content.Context;
import android.content.SharedPreferences;

public class Account {

	private final static String TAG = "Account";
	private final static String FACEBOOK_ID = "facebook_id";
	private final static String FACEBOOK_TOKEN = "facebook_token";
	public String facebookID = null;
	public String facebookToken = null;
	public String facebookName = null;
	public String facebookAvatarURL = null;
	private SharedPreferences setting = null;
	private SharedPreferences.Editor editor = null;
	
	public Account(Context context) {
		setting = context.getSharedPreferences(TAG, 0);
		editor = setting.edit();
	}
	
	public void setFacebookID(String facebookID) {
		this.facebookID = facebookID;
		editor.putString(FACEBOOK_ID, facebookID);
		editor.commit();
	}
	
	public void setFacebookToken(String facebokToken) {
		this.facebookToken = facebokToken;
		editor.putString(FACEBOOK_TOKEN, facebokToken);
		editor.commit();
	}
	
	public String getFacebookID() {
		if (facebookID == null)
			return setting.getString(FACEBOOK_ID, null);
		return facebookID;
	}
	
	public String getFacebookToken() {
		if (facebookToken == null)
			return setting.getString(FACEBOOK_TOKEN, null);
		return facebookToken;
	}
}