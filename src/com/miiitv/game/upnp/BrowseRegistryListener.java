package com.miiitv.game.upnp;

import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;

import com.miiitv.game.client.Logger;

public class BrowseRegistryListener implements RegistryListener {
	
	private final static String TAG = "BrowseRegistryListener";

	@Override
	public void afterShutdown() {
		Logger.i(TAG, "afterShutdown()");
	}

	@Override
	public void beforeShutdown(Registry arg0) {
		Logger.i(TAG, "beforeShutdown()");
	}

	@Override
	public void localDeviceAdded(Registry arg0, LocalDevice device) {
		Logger.i(TAG, "localDeviceAdded() ", device.getDisplayString());
	}

	@Override
	public void localDeviceRemoved(Registry arg0, LocalDevice device) {
		Logger.i(TAG, "localDeviceRemoved() ", device.getDisplayString());
	}

	@Override
	public void remoteDeviceAdded(Registry arg0, RemoteDevice device) {
		Logger.i(TAG, "remoteDeviceAdded() ", device.getDisplayString());
	}

	@Override
	public void remoteDeviceDiscoveryFailed(Registry arg0, RemoteDevice device, Exception arg2) {
	}

	@Override
	public void remoteDeviceDiscoveryStarted(Registry arg0, RemoteDevice device) {
	}

	@Override
	public void remoteDeviceRemoved(Registry arg0, RemoteDevice device) {
		Logger.i(TAG, "remoteDeviceRemoved() ", device.getDisplayString());
	}

	@Override
	public void remoteDeviceUpdated(Registry arg0, RemoteDevice device) {
		Logger.i(TAG, "remoteDeviceUpdated() ", device.getDisplayString());
	}
}
