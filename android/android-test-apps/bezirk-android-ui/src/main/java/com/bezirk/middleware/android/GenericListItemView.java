/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
