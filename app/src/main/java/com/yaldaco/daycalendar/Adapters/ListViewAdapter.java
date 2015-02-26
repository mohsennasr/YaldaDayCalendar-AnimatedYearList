package com.yaldaco.daycalendar.Adapters;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yaldaco.daycalendar.R;
import com.yaldaco.daycalendar.Utility.MyDB;

import java.util.ArrayList;

/**
 * Created by Nasr_M on 1/28/2015.
 */
public class ListViewAdapter extends BaseAdapter {

    ArrayList<String> listStrings;
    Context context;
    MyDB dB;
    String dateIndex;

    public ListViewAdapter(Context context, ArrayList<String> listStrings, MyDB dB, String dateIndex) {
        this.listStrings = listStrings;
        this.context = context;
        this.dB = dB;
        this.dateIndex = dateIndex;
    }

    @Override
    public int getCount() {
        return listStrings.size();
    }

    @Override
    public Object getItem(int position) {
        return listStrings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final String[] itemText = {listStrings.get(position)};
        Button view, edit, remove;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.item_text);
        view = (Button) convertView.findViewById(R.id.view_bt);
        edit = (Button) convertView.findViewById(R.id.edit_bt);
        remove = (Button) convertView.findViewById(R.id.remove_bt);

        edit.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Edit Button Clicked . . .", Toast.LENGTH_SHORT).show();
                final EditText input = new EditText(context);
                input.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                input.setText(itemText[0]);
                AlertDialog.Builder editNote = new AlertDialog.Builder(context);
                editNote.setTitle("ویرایش یادداشت");
                editNote.setView(input);

                editNote.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        itemText[0] = input.getText().toString();
                        if (itemText[0].isEmpty()) {
                            listStrings.remove(position);
                            notifyDataSetChanged();
                            dB.removeNote(dateIndex, listStrings);
                        } else {
                            listStrings.set(position, itemText[0]);
                            notifyDataSetChanged();
                            dB.updateNote(dateIndex, String.valueOf(position), itemText[0]);
                        }
                    }
                });
                editNote.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                editNote.show();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Remove Button Clicked . . .", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder deleteNote = new AlertDialog.Builder(context);
                deleteNote.setTitle("آیا از حذف یادداشت مطمئن هستید؟");

                deleteNote.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        listStrings.remove(position);
                        notifyDataSetChanged();
                        dB.removeNote(dateIndex, listStrings);
                    }
                });
                deleteNote.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                deleteNote.show();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "View Button Clicked . . .", Toast.LENGTH_SHORT).show();
                final TextView input = new TextView(context);
                input.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                final String[] value = {new String()};
                final boolean[] mo2 = {false};
                value[0] = listStrings.get(position);
                input.setText(value[0]);

                AlertDialog.Builder inputNote = new AlertDialog.Builder(context);
                inputNote.setTitle("مشاهده یادداشت");
                inputNote.setView(input);

                inputNote.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                inputNote.show();
            }
        });

        txtListChild.setText(itemText[0]);
        return convertView;
    }
}
