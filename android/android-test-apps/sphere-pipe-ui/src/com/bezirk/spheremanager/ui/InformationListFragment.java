package com.bezirk.spheremanager.ui;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.spheremanager.DeviceListActivity;
import com.bezirk.spheremanager.R;
import com.bezirk.spheremanager.SphereListActivity;
import com.bezirk.spheremanager.dummy.DummyContent;
import com.bezirk.starter.MainService;

/**
 * A fragment representing a single sphere detail screen. This fragment is
 * either contained in a {@link SphereListActivity} in two-pane mode (on
 * tablets) or a {@link DeviceListActivity} on handsets.
 */
public class InformationListFragment extends ListFragment {
    public static final String TAG = "InformationListFragment";
    private String filterSetting = "inbound";
    private BezirkDeviceInfo item;


    public InformationListFragment() {

    }

    public void setFilter(String filterSetting) {
        this.filterSetting = filterSetting;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SphereListItem sphere = (SphereListItem) DummyContent.ITEM_MAP
        //		.get(getActivity().getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID));

        String sphereID = getActivity().getIntent().getStringExtra(DeviceListFragment.ARG_ITEM_ID);

        BezirkSphereInfo sphereInfo = null;

        BezirkSphereAPI api = MainService.getSphereHandle();

        if (api != null) {
            sphereInfo = api.getSphere(sphereID);
        } else {
            Log.e(TAG, "MainService is not available");
        }

        item = sphereInfo.getDeviceList()
                .get(getActivity().getIntent().getIntExtra(DeviceListActivity.ARG_DEVICE_ID, 0));

        Toast.makeText(getActivity().getApplicationContext(), "FIXME InformationListFragment : " +
                "create for pipe inbound", Toast.LENGTH_SHORT);

        InformationListAdapter adapter = new InformationListAdapter(getActivity()
                .getApplicationContext(), DummyContent.informationListInbound, filterSetting);

		/* Vimal. commented for integration
        if (filterSetting.equals("inbound")) {

			adapter = new InformationListAdapter(getActivity()
					.getApplicationContext(), item.getInboundInformation(), filterSetting);
		} else {
			adapter = new InformationListAdapter(getActivity()
					.getApplicationContext(), item.getOutboundInformation(), filterSetting);
		}
		*/
        //TODO: Handle selections

        setListAdapter(adapter);


    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView lv = getListView();
        // FOR USABILITY STUDY: if we test version 1.x we need to make information not editable

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final CheckBox information_active = (CheckBox) view
                        .findViewById(R.id.check_information);
                Toast.makeText(getActivity().getApplicationContext(), "FIX Pipe Policy 2", Toast.LENGTH_SHORT).show();
			/*	List<AbstractInformationListItem> inbound = item.getInboundInformation();
				List<AbstractInformationListItem> outbound = item.getOutboundInformation();
				if (information_active.isChecked()) {
					information_active.setChecked(false);
					if (filterSetting.equals("inbound")) {
						InformationListItem informationItem = (InformationListItem) inbound
								.get(position);
						informationItem.setActive(false);
						inbound.set(position, informationItem);
					} else {
						InformationListItem informationItem = (InformationListItem) outbound
								.get(position);
						informationItem.setActive(false);
						outbound.set(position, informationItem);
					}
				} else {
					information_active.setChecked(true);
					if (filterSetting.equals("inbound")) {
						InformationListItem informationItem = (InformationListItem) inbound.get(position);
						informationItem.setActive(true);
						inbound.set(position, informationItem);			
						} else {
						InformationListItem informationItem = (InformationListItem) outbound
								.get(position);
						informationItem.setActive(true);
						outbound.set(position, informationItem);
					}
				}
				*/

            }
        });


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v(TAG, "onViewCreated");

    }

    @Override
    public void onListItemClick(ListView listView, View view, int position,
                                long id) {
        super.onListItemClick(listView, view, position, id);

    }

}
