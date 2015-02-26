package com.yaldaco.daycalendar.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.roomorama.caldroid.SquareTextView;
import com.yaldaco.daycalendar.R;

import java.util.ArrayList;

/**
 * Created by Nasr_M on 2/1/2015.
 */
public class GridViewAdapter extends BaseAdapter {

    ArrayList<String> gridList;
    Context context;

    public GridViewAdapter(Context context, ArrayList<String> gridList) {
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

        SquareTextView cell = (SquareTextView) convertView;

        cell.setTextSize(14);

        if((position < 8) || (position % 8 == 0)) {
            cell.setTextAppearance(context, R.style.text_20_bold);
            cell.setClickable(false);
        }else if(cellText == "")
            cell.setClickable(false);

        if(position<8)
            cell.setBackgroundColor(Color.GREEN);

        if (position % 8 == 7 || position%8 ==6)
            cell.setTextColor(Color.RED);
        else
            cell.setTextColor(Color.BLACK);

        cell.setText(cellText);
        return convertView;
    }


}
