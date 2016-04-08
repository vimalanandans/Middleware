package com.bosch.upa.spheremanager.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bosch.upa.spheremanager.R;
//import com.bosch.upa.spheremanager.ui.listitems.PipeRecord;
import com.bosch.upa.uhu.pipe.core.PipeRecord;

public class PipeListAdapter extends ArrayAdapter<PipeRecord> {


	private LayoutInflater inflater;
	private List<PipeRecord> objects;

	public PipeListAdapter(Context context,
			List<PipeRecord> objects) {
		super(context, 0, objects);
		inflater = LayoutInflater.from(context);
		this.objects = objects;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//create view here!
		View view;
		PipeRecord record = (PipeRecord) objects.get(position);
		view = (View) inflater.inflate(R.layout.layout_pipelist_entry,
				parent, false);
		TextView textViewName = (TextView) view.findViewById(R.id.pipe_item_name);
		textViewName.setText(record.getPipe().getName());
		if (record.getPassword()==null){
			ImageView lock = (ImageView) view.findViewById(R.id.lock_icon);
			lock.setVisibility(view.GONE);
		}else{
			//show Lock icon
		}
				
		return view;
	}

}
