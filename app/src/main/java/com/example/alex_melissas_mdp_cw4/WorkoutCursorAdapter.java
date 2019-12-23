package com.example.alex_melissas_mdp_cw4;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class WorkoutCursorAdapter extends SimpleCursorAdapter {
    private LayoutInflater layoutInflater;
    private Context context;
    private int layout;

    // Define components of the row created
    private class ViewHolder {
        ImageView typeImage;
        TextView dateTimeBox;
        TextView durationBox;
        TextView distanceBox;
        ImageView favButton;

        ViewHolder(View v) {
            typeImage = (ImageView) v.findViewById(R.id.typeImage);
            dateTimeBox = (TextView) v.findViewById(R.id.datetimeBox);
            durationBox = (TextView) v.findViewById(R.id.durationBox);
            distanceBox = (TextView) v.findViewById(R.id.distanceBox);
            favButton = (ImageView) v.findViewById(R.id.favButton);
        }
    }

    // Constructor
    public WorkoutCursorAdapter (Context argContext, int argLayout, Cursor c, String[] from, int[] to) {
        super(argContext, argLayout, c, from, to);
        layoutInflater = LayoutInflater.from(argContext);
        layout = argLayout;
        context = argContext;
    }

    //Inflate new view row
    @Override
    public View newView(Context ctx, Cursor cursor, ViewGroup parent) {
        View v = layoutInflater.inflate(layout, parent, false);
        v.setTag( new ViewHolder(v) );
        return v;
    }

    // Define bindings of data to view components
    @Override
    public void bindView(View v, Context ctx, Cursor c) {
        ViewHolder vh = (ViewHolder) v.getTag();

        int typeIndex = c.getColumnIndex("type");
        int dateTimeIndex = c.getColumnIndex("dateTime");
        int durationIndex = c.getColumnIndex("duration");
        int distanceIndex = c.getColumnIndex("distance");
        int favIndex = c.getColumnIndex("fav");

        switch(c.getInt(typeIndex)){
            case 0: vh.typeImage.setImageResource(R.mipmap.walk_icon);break;
            case 1: vh.typeImage.setImageResource(R.mipmap.jog_icon);break;
            case 2: vh.typeImage.setImageResource(R.mipmap.run_icon);break;
            default:break;
        }

        vh.dateTimeBox.setText(c.getString(dateTimeIndex));

        vh.durationBox.setText(secToDuration(c.getInt(durationIndex)));

        vh.distanceBox.setText(String.format("%02.2f",(c.getFloat(distanceIndex)))+"km");

        if(c.getInt(favIndex)==1) vh.favButton.setImageResource(R.mipmap.heart_on_icon);
        else vh.favButton.setImageResource(R.mipmap.heart_off_icon);
    }

    // Convert raw seconds int to hh:mm:ss format string
    private String secToDuration(int sec){
        int hours = sec/3600;
        int mins = (sec-hours*3600)/60;
        int secs = (sec-hours*3600)%60;
        return /*String.format("%2d",hours)+":"+*/String.format("%02d",mins)+":"+String.format("%02d",secs);
    }
}
