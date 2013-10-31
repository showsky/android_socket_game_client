package com.miiitv.game.client;

public class EventType {

	public final static int TYPE_DEBUG = 0x0000;
	
	//	receive
	public final static int TYPE_CONNECT = 0x0001;
	public final static int TYPE_START = 0x0002;
	public final static int TYPE_LOCK = 0x0003;
	public final static int TYPE_UNLOCK = 0x0004;
	public final static int TYPE_END = 0x0005;
	public final static int TYPE_CLOSE = 0x0006;
	public final static int TYPE_OPTIONS = 0x0007;
	public final static int TYPE_WIN = 0x0008;
	
	//	send
	public final static int TYPE_SHOCK = 0x1001;
	public final static int TYPE_ANSWER = 0x102;
}
