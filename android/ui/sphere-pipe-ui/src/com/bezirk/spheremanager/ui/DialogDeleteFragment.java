package com.bezirk.spheremanager.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bezirk.spheremanager.R;

public class DialogDeleteFragment extends DialogFragment {
	private String text;

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_delete_fragment,
				container);
		TextView dialogText = (TextView) view.findViewById(R.id.dialog_text);
		dialogText.setText(text);
		getDialog().setTitle("Delete");

		Button delete = (Button) view.findViewById(R.id.delete_button);
		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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

		return view;

	}
}