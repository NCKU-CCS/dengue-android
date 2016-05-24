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
    private CharSequence[] address;
    private CharSequence[] phone;

    public hospitalAdapter(Context context, CharSequence[] name, CharSequence[] address, CharSequence[] phone) {
        hospitalListInflater = LayoutInflater.from(context);
        this.name = name;
        this.address = address;
        this.phone = phone;
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
                    (TextView) convertView.findViewById(R.id.hospital_item_name),
                    (TextView) convertView.findViewById(R.id.hospital_item_address),
                    (TextView) convertView.findViewById(R.id.hospital_item_phone)
            );


            convertView.setTag(item);
        }
        else {
            item = (Item) convertView.getTag();
        }

        item.name.setText(name[position]);
        item.address.setText(address[position]);
        item.phone.setText(phone[position]);

        return convertView;
    }

    class Item{
        TextView name;
        TextView address;
        TextView phone;

        public Item(TextView name, TextView address, TextView phone){
            this.name = name;
            this.address = address;
            this.phone = phone;
        }
    }
}
