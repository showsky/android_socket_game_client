package com.miiitv.game.client.gui;

import org.json.JSONObject;

public interface StartListener {
	
	public void options(JSONObject options);
	public void start();
	public void lock();
	public void unlock();
	public void win();
	public void end();
	public void close();
}
