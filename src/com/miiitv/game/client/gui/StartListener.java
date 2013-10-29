package com.miiitv.game.client.gui;

import org.json.JSONArray;

public interface StartListener {
	
	public void start(JSONArray options);
	public void lock();
	public void unlock();
	public void end();
	public void close();
}
