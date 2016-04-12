package com.bezirk.spheremanager.dummy;

import java.util.ArrayList;
import java.util.UUID;

import com.bezirk.spheremanager.ui.listitems.DeviceListItem;

public class DummySphere {

	public UUID sphereId;
	public String sphereName;
	public boolean isControlled;
	public boolean isActive;
	public ArrayList<DeviceListItem> deviceList;

	public DummySphere(UUID id, String name, boolean controlled,
			boolean active, ArrayList<DeviceListItem> deviceList) {
		sphereId = id;
		sphereName = name;
		isControlled = controlled;
		isActive = active;
		this.deviceList = deviceList;
	}

}
