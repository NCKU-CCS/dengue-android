package com.example.dengue.dengue_android;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportAdapter extends BaseAdapter {

    private LayoutInflater reportListInflater;

    private CharSequence[] id;
    private CharSequence[] url;
    private CharSequence[] type;
    private CharSequence[] address;
    private CharSequence[] description;
    private CharSequence[] date;
    private CharSequence[] status;
    private Activity Main;


    public ReportAdapter(Context context, CharSequence[] id,CharSequence[] url,CharSequence[] type,CharSequence[] address,CharSequence[] description,CharSequence[] date,CharSequence[] status, Activity mMain){
        reportListInflater = LayoutInflater.from(context);
        this.id = id;
        this.url = url;
        this.type = type;
        this.address = address;
        this.description = description;
        this.date = date;
        this.status = status;
        this.Main = mMain;
    }

    @Override
    public int getCount() {
        return id.length;
    }

    @Override
    public Object getItem(int position) {
        return id[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item;
        if(convertView == null) {
            convertView = reportListInflater.inflate(R.layout.report_item, null);
            item = new Item(
                    (ImageView) convertView.findViewById(R.id.reportList_check_type),
                    (TextView) convertView.findViewById(R.id.reportList_check_location),
                    (TextView) convertView.findViewById(R.id.reportList_check_description),
                    (TextView) convertView.findViewById(R.id.reportList_check_date),
                    (ImageView) convertView.findViewById(R.id.reportList_check_img)
                    );


            convertView.setTag(item);
        }
        else {
            item = (Item) convertView.getTag();
        }
        switch(type[position].toString())
        {
            case "住家容器":
                item.type.setImageDrawable(Main.getResources().getDrawable(R.drawable.home_bottle));
                break;
            case "戶外容器":
                item.type.setImageDrawable(Main.getResources().getDrawable(R.drawable.outdoor_bottle));
                break;
            case "戶外髒亂處":
                item.type.setImageDrawable(Main.getResources().getDrawable(R.drawable.outdoor_grass));
                break;
            default:
                item.type.setImageDrawable(Main.getResources().getDrawable(R.drawable.outdoor_bottle));
                break;
        }
        //item.type.setImageDrawable(Main.getResources().getDrawable(R.drawable.outdoor_bottle));
        item.address.setText(address[position]);
        item.description.setText(description[position]);
        item.date.setText(date[position]);
        item.img.setImageDrawable(Main.getResources().getDrawable(R.drawable.bite));

        return convertView;
    }

    class Item{
        ImageView type;
        TextView address;
        TextView description;
        TextView date;
        ImageView img;

        public Item(ImageView type, TextView address, TextView description, TextView date, ImageView img){
            this.type = type;
            this.address = address;
            this.description = description;
            this.date = date;
            this.img = img;
        }
    }
}

