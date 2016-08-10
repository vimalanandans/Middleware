package com.bezirk.spheremanager.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bezirk.device.BezirkDeviceType;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.spheremanager.R;

import java.util.List;

public class DeviceListAdapter extends ArrayAdapter<BezirkDeviceInfo> {

    private final Context context;
    private final List<BezirkDeviceInfo> devices;
    private LayoutInflater inflater;

    public DeviceListAdapter(Context context, List<BezirkDeviceInfo> devices) {
        super(context, 0, devices);
        this.devices = devices;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        BezirkDeviceInfo item = devices.get(position);
        view = (View) inflater.inflate(R.layout.layout_devicelist_entry,
                parent, false);
        // set icon for device types
        ImageView imageView = (ImageView) view.findViewById(R.id.device_type);
        // FIXME : create utility function to add the below
        if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_SMARTPHONE)) {
            imageView.setImageResource(R.drawable.ic_smartphone);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_TABLET)) {
            imageView.setImageResource(R.drawable.ic_tablet);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_FAN)) {
            imageView.setImageResource(R.drawable.ic_fan);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_LIGHT)) {
            imageView.setImageResource(R.drawable.ic_light);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_PRINTER)) {
            imageView.setImageResource(R.drawable.ic_printer);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_THERMOSTAT)) {
            imageView.setImageResource(R.drawable.ic_thermostat);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_PC)) {
            imageView.setImageResource(R.drawable.ic_pc);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_WASHING_MACHINE)) {
            imageView.setImageResource(R.drawable.ic_washingmachine);
        } else if (item.getDeviceType().startsWith("Chainsaw")) { //sorry no chainsaw now
            imageView.setImageResource(R.drawable.ic_chainsaw);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_TV)) {
            imageView.setImageResource(R.drawable.ic_tv);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_COFFEE)) {
            imageView.setImageResource(R.drawable.ic_coffee);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_HEATING)) {
            imageView.setImageResource(R.drawable.ic_heating);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_MICROWAVE)) {
            imageView.setImageResource(R.drawable.ic_microwave);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_GAME)) {
            imageView.setImageResource(R.drawable.ic_controller);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_CAR)) {
            imageView.setImageResource(R.drawable.ic_car);
        } else if (item.getDeviceType().equalsIgnoreCase(BezirkDeviceType.BEZIRK_DEVICE_TYPE_CLOUD)) {
            imageView.setImageResource(R.drawable.ic_cloud);
        } else {
            // do nothing
        }
        TextView device_name = (TextView) view.findViewById(R.id.device_name);

        device_name.setText(item.getDeviceName());

        // this device is owner for the sphere
        if (item.getDeviceRole() == BezirkDeviceInfo.BezirkDeviceRole.BEZIRK_CONTROL) {
            device_name.setTypeface(null, Typeface.BOLD);
        }

        TextView device_active = (TextView) view
                .findViewById(R.id.device_active);

        final String activeString;

        if (item.isDeviceActive()) {
            activeString = "Active";
            device_active.setTextColor(Color.rgb(120, 220, 90));
        } else {
            activeString = "Inactive";
        }

        device_active.setText(activeString);
        return view;
    }

}
