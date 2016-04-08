package com.bosch.upa.spheremanager.ui.listitems;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bosch.upa.spheremanager.R;
import com.bosch.upa.spheremanager.dummy.DummySphere;
import com.bosch.upa.spheremanager.ui.SphereListAdapter.ListItems;

import java.util.List;

public class DeviceListItem implements AbstractDeviceListItem {

	private DummySphere mSphere;

	private String deviceName;
	private String deviceType;
	private boolean isActive;
	private List<DeviceServiceItem> serviceList;
	private List<AbstractInformationListItem> outboundInformation;
	private List<AbstractInformationListItem> inboundInformation;

	public DeviceListItem(DummySphere sphere) {
		this.mSphere = sphere;
	}


	public DeviceListItem(String deviceName, String deviceType,
			boolean isActive, 
			List<DeviceServiceItem> serviceList,
			List<AbstractInformationListItem> outboundInformation,
			List<AbstractInformationListItem> inboundInformation) {
		super();
		this.deviceName = deviceName;
		this.deviceType = deviceType;
		this.isActive = isActive;
		this.serviceList = serviceList;
		this.outboundInformation = outboundInformation;
		this.inboundInformation = inboundInformation;
	}



	public List<DeviceServiceItem> getServiceList() {
		return serviceList;
	}


	public void setServiceList(List<DeviceServiceItem> serviceList) {
		this.serviceList = serviceList;
	}


	public List<AbstractInformationListItem> getOutboundInformation() {
		return outboundInformation;
	}


	public void setOutboundInformation(List<AbstractInformationListItem> outboundInformation) {
		this.outboundInformation = outboundInformation;
	}


	public List<AbstractInformationListItem> getInboundInformation() {
		return inboundInformation;
	}


	public void setInboundInformation(List<AbstractInformationListItem> inboundInformation) {
		this.inboundInformation = inboundInformation;
	}


	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public DummySphere getmSphere() {
		return mSphere;
	}

	@Override
	public int getViewType() {
		return ListItems.SPHERE_ITEM.ordinal();
	}

	public View getView(LayoutInflater layoutInflater, ViewGroup parent) {

		// not used right now!
		View view;
		view = (View) layoutInflater.inflate(R.layout.layout_devicelist_entry,
				parent);
		TextView textView = (TextView) view.findViewById(R.id.device_name);
		textView.setText(deviceName);
		return view;

	}

	@Override
	public String getId() {
		return mSphere.sphereId.toString();
	}

}
