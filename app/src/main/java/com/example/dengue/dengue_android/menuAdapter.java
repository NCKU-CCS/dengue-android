package com.example.dengue.dengue_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class menuAdapter extends BaseAdapter {
    private LayoutInflater MenuListInflater;
    private int[] name;
    private int[] img;

    public menuAdapter(Context context, int[] name, int[] img){
        MenuListInflater = LayoutInflater.from(context);
        this.name = name;
        this.img = img;
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
            convertView = MenuListInflater.inflate(R.layout.menu_item, null);
            item = new Item(
                    (TextView) convertView.findViewById(R.id.menu_item_name),
                    (ImageView) convertView.findViewById(R.id.menu_item_img)
            );


            convertView.setTag(item);
        }
        else {
            item = (Item) convertView.getTag();
        }

        item.name.setText(name[position]);
        item.img.setBackgroundResource(img[position]);

        return convertView;
    }

    class Item{
        TextView name;
        ImageView img;

        public Item(TextView name, ImageView img){
            this.name = name;
            this.img = img;
        }
    }
}
