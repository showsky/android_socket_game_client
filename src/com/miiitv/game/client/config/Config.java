package com.miiitv.game.client.config;

public class Config {

	public final static String PROJECT_NAME = "game_client";
	public final static boolean DEBUG_MODE = true;

	public final static boolean API_READY = false;

	public final static String FONT_FACE = "font.ttf";
	public final static String YAHOO = "/yahoo_hackathon_api";
	public final static String API_URL = "http://192.168.0.101:8888" + YAHOO;
	
	public final static int PORT = 2222;
	public final static String SERVER_IP = "192.168.0.104";

	public final static boolean PROXY = false;
	public final static String PROXY_IP = "192.168.0.100";
	public final static int PROXY_PORT = 8888;
	public final static int DEFAULT_CONNECTION_TIMEOUT = 5000;
	public final static int DEFAULT_SOCKET_TIMEOUT = 5000;
}
