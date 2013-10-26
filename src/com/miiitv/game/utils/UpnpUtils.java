package com.miiitv.game.utils;

import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.ManufacturerDetails;
import org.teleal.cling.model.meta.ModelDetails;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;

import android.text.TextUtils;

import com.miiitv.game.client.config.UpnpConfig;

public class UpnpUtils {

	private final static String TAG = "UpnpUtils";
	
	public static LocalDevice createDevice(String facebookID) throws ValidationException {
		if (TextUtils.isEmpty(facebookID))
			throw new IllegalArgumentException("facebook id == null");
		DeviceIdentity identity = new DeviceIdentity(UDN.valueOf(facebookID));
		DeviceType type = new UDADeviceType(UpnpConfig.TYPE_NAME, UpnpConfig.TYPE_VERSION);
		DeviceDetails details =
			new DeviceDetails(
                UpnpConfig.DEVICE_DESCRIPTION,
                new ManufacturerDetails(UpnpConfig.MANUFACTURE),
                new ModelDetails(
            		UpnpConfig.MODE_NAME,
                    UpnpConfig.MODE_DESCRITPION,
                    UpnpConfig.MODE_VERSION
            )
        );
		return new LocalDevice(identity, type, details, new LocalService[0]);
	}
}
