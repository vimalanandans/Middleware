package com.bezirk.controlui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bezirk.controlui.R;

import java.util.List;

/**
 * Created by mca7kor
 */
public class DialogSphereList extends DialogFragment {
    private List<String> sphereList;
    private OnSphereSelectCallback callback;

    DialogSphereList(){
        //default constructor
    }

    public DialogSphereList(List<String> sphereList, OnSphereSelectCallback callback) {
        this.sphereList = sphereList;
        this.callback = callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sphere_list,container,false);
        ListView sphereListView = (ListView)view.findViewById(R.id.sphereList);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,sphereList);
        sphereListView.setAdapter(listAdapter);
        getDialog().setTitle("Select Sphere");
        sphereListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callback.onSphereSelectCallback(position);
                getDialog().cancel();
            }
        });
        return view;
    }

    /**
     * Interface to give callback to called activity
     */
    interface OnSphereSelectCallback{
        void onSphereSelectCallback(int position);
    }
}
