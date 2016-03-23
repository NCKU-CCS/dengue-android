package com.example.dengue.dengue_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class reportAdapter extends BaseAdapter {
    private LayoutInflater reportListInflater;
    private CharSequence[] name;
    private CharSequence[] isDone;

    public reportAdapter(Context context, CharSequence[] name, CharSequence[] isDone){
        reportListInflater = LayoutInflater.from(context);
        this.name = name;
        this.isDone = isDone;
    }

    @Override
    public int getCount() {
        return name.length;
    }

    @Override
    public Object getItem(int position) {
        return name[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item;
        if(convertView == null) {
            convertView = reportListInflater.inflate(R.layout.report_list_item, null);
            item = new Item(
                    (TextView) convertView.findViewById(R.id.reportList_item_name),
                    (TextView) convertView.findViewById(R.id.reportList_item_isDone)
            );


            convertView.setTag(item);
        }
        else {
            item = (Item) convertView.getTag();
        }

        item.name.setText(name[position]);
        item.isDone.setText(isDone[position]);

        return convertView;
    }

    class Item{
        TextView name;
        TextView isDone;

        public Item(TextView name, TextView isDone){
            this.name = name;
            this.isDone = isDone;
        }
    }
}
