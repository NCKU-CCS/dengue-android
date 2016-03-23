package com.example.dengue.dengue_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class hospitalAdapter extends BaseAdapter {
    private LayoutInflater hospitalListInflater;
    private CharSequence[] name;

    public hospitalAdapter(Context context, CharSequence[] name){
        hospitalListInflater = LayoutInflater.from(context);
        this.name = name;
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
            convertView = hospitalListInflater.inflate(R.layout.hospital_item, null);
            item = new Item(
                    (TextView) convertView.findViewById(R.id.hospital_item_name)
            );


            convertView.setTag(item);
        }
        else {
            item = (Item) convertView.getTag();
        }

        item.name.setText(name[position]);

        return convertView;
    }

    class Item{
        TextView name;

        public Item(TextView name){
            this.name = name;
        }
    }
}
