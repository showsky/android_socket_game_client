package com.miiicasa.game.account;

import org.json.JSONException;
import org.json.JSONObject;

import com.miiitv.game.client.EventType;

import android.content.Context;
import android.content.SharedPreferences;

public class Account {

	private final static String TAG = "Account";
	private final static String CONFIG_FACEBOOK_ID = "facebook_id";
	private final static String CONFIG_FACEBOOK_TOKEN = "facebook_token";
	private final static String CONFIG_FACEBOOK_NAME = "facebook_name";
	private final static String CONFIG_SYNC_USER = "sync_user";
	public final static String WIN = "win";
	public final static String LOSE = "lose";
	public final static String SCORE = "score";
	public String facebookID = null;
	public String facebookToken = null;
	public String facebookName = null;
	public String facebookAvatarURL = null;
	private SharedPreferences setting = null;
	private SharedPreferences.Editor editor = null;
	public Rank rank = null;
	
	public static class Rank {
	
		public int win = 0;
		public int lost = 0;
		public int score = 0;
	}
	
	public Account(Context context) {
		setting = context.getSharedPreferences(TAG, 0);
		editor = setting.edit();
	}
	
	public void setFacebookID(String facebookID) {
		this.facebookID = facebookID;
		editor.putString(CONFIG_FACEBOOK_ID, facebookID);
	}
	
	public void setFacebookToken(String facebokToken) {
		this.facebookToken = facebokToken;
		editor.putString(CONFIG_FACEBOOK_TOKEN, facebokToken);
	}
	
	public String getFacebookID() {
		if (facebookID == null)
			return setting.getString(CONFIG_FACEBOOK_ID, null);
		return facebookID;
	}
	
	public String getFacebookToken() {
		if (facebookToken == null)
			return setting.getString(CONFIG_FACEBOOK_TOKEN, null);
		return facebookToken;
	}
	
	public String getFacebokName() {
		if (facebookName == null)
			return setting.getString(CONFIG_FACEBOOK_NAME, null);
		return facebookName;
	}
	
	public void setFacebookName(String facebookName) {
		this.facebookName = facebookName;
		editor.putString(CONFIG_FACEBOOK_NAME, facebookName);
	}
	
	public void save() {
		editor.commit();
	}
	
	public void setSyncUser(boolean isSyncUser) {
		editor.putBoolean(CONFIG_SYNC_USER, isSyncUser);
	}
	
	public boolean isSyncUser() {
		return setting.getBoolean(CONFIG_SYNC_USER, false);
	}
	
	public boolean isFacebookAuth() {
		if (getFacebookToken() == null) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		try {
			json.put("type", EventType.TYPE_JOIN);
			JSONObject dataJSON = new JSONObject();
			dataJSON.put("facebook_id", getFacebookID());
			dataJSON.put("facebook_name", getFacebokName());
			if (rank != null) {
				dataJSON.put("win", rank.win);
				dataJSON.put("lose", rank.lost);
			}
			json.putOpt("data", dataJSON);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
}
