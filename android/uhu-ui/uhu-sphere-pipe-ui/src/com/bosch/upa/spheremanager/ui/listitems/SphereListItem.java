package com.bosch.upa.spheremanager.ui.listitems;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bosch.upa.spheremanager.R;
import com.bosch.upa.spheremanager.ui.SphereListAdapter.ListItems;
import com.bosch.upa.uhu.api.objects.UhuSphereInfo;
import com.bosch.upa.uhu.sphere.api.UhuSphereType;

public class SphereListItem implements AbstractSphereListItem {

	//private DummySphere mSphere;
    private UhuSphereInfo mSphere;

	public SphereListItem(UhuSphereInfo sphere) {
		this.mSphere = sphere;
	}

	public UhuSphereInfo  getmSphere() {
		return mSphere;
	}

	@Override
	public int getViewType() {
		return ListItems.SPHERE_ITEM.ordinal();
	}

	@Override
	public View getView(LayoutInflater layoutInflater, ViewGroup parent) {
		View view;
		view = (View) layoutInflater.inflate(R.layout.layout_spherelist_entry,
				parent);
		TextView textView = (TextView) view.findViewById(R.id.sphere_item_name);

		textView.setText(mSphere.getSphereName());

        // FIXME: use as part of UhuSphereInfo to find the sphere info
        // make it bold for owner sphere
        if(mSphere.isThisDeviceOwnsSphere())
        {
            textView.setTypeface(null, Typeface.BOLD);
        }

        ImageView img = (ImageView)view.findViewById(R.id.sphere_icon);

        // TODO move this to a utility class
        if(mSphere.getSphereType().equals(UhuSphereType.UHU_SPHERE_TYPE_HOME)) {
            img.setImageResource(R.drawable.ic_home_sphere);
        }
        else if(mSphere.getSphereType().equals(UhuSphereType.UHU_SPHERE_TYPE_DEFAULT)) {
            img.setImageResource(R.drawable.ic_default_sphere);
        }
        else if(mSphere.getSphereType().equals(UhuSphereType.UHU_SPHERE_TYPE_CAR)) {
            img.setImageResource(R.drawable.ic_car_sphere);
        }
        else if(mSphere.getSphereType().equals(UhuSphereType.UHU_SPHERE_TYPE_OFFICE)) {
            img.setImageResource(R.drawable.ic_office_sphere);
        }
        else if(mSphere.getSphereType().equals(UhuSphereType.UHU_SPHERE_TYPE_HOME_CONTROL)) {
            img.setImageResource(R.drawable.ic_home_control_sphere);
        }
        else if(mSphere.getSphereType().equals(UhuSphereType.UHU_SPHERE_TYPE_HOME_ENTERTAINMENT)) {
            img.setImageResource(R.drawable.ic_home_entertainment_sphere);
        }
        else if(mSphere.getSphereType().equals(UhuSphereType.UHU_SPHERE_TYPE_HOME_SECURITY)) {
            img.setImageResource(R.drawable.ic_home_security_sphere);
        }
        else{
            // do nothing
        }

        boolean sphereActive = true;

        //TODO Get the sphere active or not
        //sphereActive  = mSphere.getSphereStatus();

        if (sphereActive) {
			textView.setTextAppearance(textView.getContext(), R.style.ListItem);
		} else {
			textView.setTextAppearance(textView.getContext(),
					R.style.InactiveListItem);
		}
		//Disabled because Control and member devices are not defined yet
//		textView = (TextView) view.findViewById(R.id.sphere_item_details);
//		String detailString = "";
//		if (mSphere.isControlled) {
//			detailString = "Control Device";
//		} else {
//			detailString = "Member";
//		}
//		textView.setText(detailString);
		return view;
	}
	
	@Override
	public View getViewSelectSphere(LayoutInflater layoutInflater, ViewGroup parent) {
		View view;
		view = (View) layoutInflater.inflate(R.layout.layout_spherelist_select,
				parent);
		RadioButton sphereEntry = (RadioButton) view.findViewById(R.id.sphere_select_entry);
		sphereEntry.setText(mSphere.getSphereName());
		return view;
	}
	@Override
	public String getId() {
		return mSphere.getSphereID().toString();
	}

}
