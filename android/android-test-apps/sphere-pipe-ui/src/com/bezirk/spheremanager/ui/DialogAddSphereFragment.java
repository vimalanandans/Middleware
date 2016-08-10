package com.bezirk.spheremanager.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bezirk.sphere.api.SphereType;
import com.bezirk.spheremanager.R;

import java.util.ArrayList;
import java.util.List;

public class DialogAddSphereFragment extends DialogFragment {
    /**
     * A dummy implementation of the {@link SphereListFragment.Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static addNewSphereCallback sDummyCallbacks = new addNewSphereCallback() {

        @Override
        public void addNewSphere(String name, String type) {

        }

    };
    List<SphereTypeModel> sphereTypeList = new ArrayList<SphereTypeModel>();
    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private addNewSphereCallback mCallbacks = sDummyCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof addNewSphereCallback)) {
            throw new IllegalStateException(
                    "Activity must implement DialogAddSpherefragment's callbacks.");
        }

        mCallbacks = (addNewSphereCallback) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.dialog_add_sphere_fragment,
                container);

        TextView dialogText = (TextView) view.findViewById(R.id.dialog_text);
        dialogText.setText("Please enter the name of the new sphere:");
        getDialog().setTitle("Add sphere");
        getDialog().setCanceledOnTouchOutside(false);
        Button add = (Button) view.findViewById(R.id.add_button);
        add.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText newSphereField = (EditText) view
                        .findViewById(R.id.new_sphere_name);

                String name = newSphereField.getText().toString();
                if (name.contains(",")) {
                    printToast("Spl character(,) are not allowed in sphere Name");
                    return;
                }

                Spinner mySpinner = (Spinner) view.findViewById(R.id.new_sphere_type);

                SphereTypeModel itemModel = (SphereTypeModel) mySpinner.getSelectedItem();

                String type = itemModel.sphereTypeText;

                mCallbacks.addNewSphere(name, type);

                dismiss();
            }
        });

        Button cancel = (Button) view.findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();

            }
        });

        // sphere type list data
        // Don't add the default sphere here. default sphere is added by stack itself
        sphereTypeList.add(new SphereTypeModel(SphereType.BEZIRK_SPHERE_TYPE_HOME, R.drawable.ic_home_sphere));
        sphereTypeList.add(new SphereTypeModel(SphereType.BEZIRK_SPHERE_TYPE_CAR, R.drawable.ic_car_sphere));
        sphereTypeList.add(new SphereTypeModel(SphereType.BEZIRK_SPHERE_TYPE_HOME_ENTERTAINMENT, R.drawable.ic_home_entertainment_sphere));
        sphereTypeList.add(new SphereTypeModel(SphereType.BEZIRK_SPHERE_TYPE_HOME_CONTROL, R.drawable.ic_home_control_sphere));
        sphereTypeList.add(new SphereTypeModel(SphereType.BEZIRK_SPHERE_TYPE_HOME_SECURITY, R.drawable.ic_home_security_sphere));

        // create sphere type list spinner

        Spinner mySpinner = (Spinner) view.findViewById(R.id.new_sphere_type);

        mySpinner.setAdapter(new SphereTypeAdapter(view.getContext(), R.layout.addsphererow, sphereTypeList));

        return view;

    }

    private void printToast(final String toastMsg) {
        Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_SHORT).show();
    }

    public interface addNewSphereCallback {
        public void addNewSphere(String name, String type);
    }

    class SphereTypeModel {
        public String sphereTypeText;
        public int sphereTypeIcon;

        SphereTypeModel(String sphereTypeText, int sphereTypeIcon) {
            this.sphereTypeText = sphereTypeText;
            this.sphereTypeIcon = sphereTypeIcon;

        }
    }

    public class SphereTypeAdapter extends ArrayAdapter<SphereTypeModel> {

        Context listViewContext = null;

        public SphereTypeAdapter(Context context, int textViewResourceId,
                                 List<SphereTypeModel> objects) {
            super(context, textViewResourceId, objects);
            listViewContext = context;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

//return super.getView(position, convertView, parent);
            SphereTypeModel itemData = sphereTypeList.get(position);


            LayoutInflater inflater = LayoutInflater.from(listViewContext);

            View row = inflater.inflate(R.layout.addsphererow, parent, false);

            TextView label = (TextView) row.findViewById(R.id.sphere_type_text);
            label.setText(itemData.sphereTypeText);

            ImageView icon = (ImageView) row.findViewById(R.id.sphere_type_icon);
            icon.setImageResource(itemData.sphereTypeIcon);

            return row;
        }
    }
}