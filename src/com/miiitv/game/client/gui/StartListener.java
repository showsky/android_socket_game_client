package com.miiitv.game.client.gui;

import org.json.JSONArray;

public interface StartListener {
	
	public void options(JSONArray options);
	public void start();
	public void lock();
	public void unlock();
	public void end();
	public void close();
}
