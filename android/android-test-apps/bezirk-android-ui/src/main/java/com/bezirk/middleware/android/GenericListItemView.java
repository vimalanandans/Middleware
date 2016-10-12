package com.bezirk.middleware.android;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bezirk.middleware.android.ui.R;
import com.bezirk.middleware.core.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GenericListItemView extends ArrayAdapter<DataModel> {
    private static final Logger logger = LoggerFactory.getLogger(GenericListItemView.class);
    private final Activity context;

    private final List<DataModel> data;

    private final ItemToggleListener itemToggleListener;

    public GenericListItemView(Activity parentContext, List<DataModel> listModel, ItemToggleListener toggleListener) {

        super(parentContext, R.layout.list_item, listModel);

        context = parentContext;

        data = new ArrayList(listModel);

        itemToggleListener = toggleListener;
    }

    @NonNull
    @Override
    public View getView(final int position, View view, @NonNull ViewGroup parent) {


        LayoutInflater inflater = context.getLayoutInflater();

        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        TextView txtItemTitleView = (TextView) rowView.findViewById(R.id.mainItemTitle);

        TextView txtItemHintView = (TextView) rowView.findViewById(R.id.mainItemHint);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_image);

        ImageView warningIcon = (ImageView) rowView.findViewById(R.id.warningIcon);

        imageView.setImageResource(data.get(position).getImageId());

        txtItemTitleView.setText(data.get(position).getTitleText());

        txtItemHintView.setText(data.get(position).getHintText());

        /* On / off button */
        if (data.get(position).isToggleButtonEnable()) {
            final ToggleButton toggle = (ToggleButton) rowView.findViewById(R.id.listToggleButton);
            if (ValidatorUtility.isObjectNotNull(toggle)) {
                toggle.setVisibility(View.VISIBLE);
                // data.get(position).setToggleButtonState(BezirkRestCommsManager.getInstance().isStarted());
                data.get(position).setToggleButtonState(false);
                toggle.setChecked(data.get(position).isToggleButtonState());
                toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        data.get(position).setToggleButtonState(toggle.isChecked());
                        logger.trace("Button pressed! > " + data.get(position).isToggleButtonState());
                        if (itemToggleListener == null) {
                            logger.trace("activity not registered with Toggle button listener " +
                                    data.get(position).isToggleButtonState());
                        } else {
                            itemToggleListener.onItemToggleListener(position, toggle.isChecked());
                        }
                    }
                });
            }

        }

        if (warningIcon == null) {
            logger.trace("Warning icon is null");
        } else {
            if (data.get(position).isIcon()) {
                warningIcon.setVisibility(View.VISIBLE);
            } else {
                warningIcon.setVisibility(View.GONE);
            }
        }


        return rowView;
    }

    /**
     * toggle listener to notifyback the click
     */
    public interface ItemToggleListener {
        void onItemToggleListener(int position, boolean checkStatus);
    }
}
