package com.miiitv.game.client;

import android.util.Log;

public class Logger {
	
	private static boolean debug = false;
	private static String projectName = "miiicasa";
	private static StringBuilder contain = null;
	
	public static void setProject(String projectName, boolean debug) {
		Logger.projectName = projectName;
		Logger.debug = debug;
		if (debug)
			contain = new StringBuilder();
	}
	
	public static boolean isDebug() {
		return debug;
	}
	
	private static void mergeMessages(String TAG, String... messages) {
		contain.setLength(0);
		contain.append("[");
		contain.append(TAG);
		contain.append("] ");
		int length = messages.length;
		for (int i = 0; i < length; i++) {
			contain.append(messages[i]);
		}
	}
	
	public static void d(String TAG, String... messages) {
		if (debug) {
			mergeMessages(TAG, messages);
			Log.d(projectName, contain.toString());
		}
	}
	
	public static void i(String TAG, String... messages) {
		if (debug) {
			mergeMessages(TAG, messages);
			Log.i(projectName, contain.toString());
		}
	}
	
	public static void e(String TAG, String... messages) {
		if (debug) {
			mergeMessages(TAG, messages);
			Log.e(projectName, contain.toString());
		}
	}
	
	public static void w(String TAG, String... messages) {
		if (debug) {
			mergeMessages(TAG, messages);
			Log.w(projectName, contain.toString());
		}
	}
	
	public static void v(String TAG, String... messages) {
		if (debug) {
			mergeMessages(TAG, messages);
			Log.v(projectName, contain.toString());
		}
	}
}
