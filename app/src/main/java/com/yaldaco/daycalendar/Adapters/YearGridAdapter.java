package com.yaldaco.daycalendar.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yaldaco.daycalendar.R;

import java.util.ArrayList;

/**
 * Created by Nasr_M on 2/14/2015.
 */
public class YearGridAdapter extends BaseAdapter{

    ArrayList<String> gridList;
    Context context;

    public YearGridAdapter(Context context, ArrayList<String> gridList) {
        this.gridList = gridList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return gridList.size();
    }

    @Override
    public String getItem(int position) {
        return gridList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String cellText = getItem(position);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = infalInflater.inflate(android.R.layout.simple_list_item_1, null);
            convertView = infalInflater.inflate(R.layout.square_date_cell, null);
        }

        TextView cell = (TextView) convertView;

//        cell.setTextSize(25);

        cell.setText(cellText);
        return convertView;
    }
}
