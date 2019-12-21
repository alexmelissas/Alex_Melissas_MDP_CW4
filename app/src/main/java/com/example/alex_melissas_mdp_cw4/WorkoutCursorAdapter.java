package com.example.alex_melissas_mdp_cw4;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

// https://stackoverflow.com/questions/18474634/android-custom-simplecursoradapter-with-image-from-file-with-path-in-database
public class WorkoutCursorAdapter extends SimpleCursorAdapter {
    private LayoutInflater mLayoutInflater;
    private Context context;
    private int layout;

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

    public WorkoutCursorAdapter (Context ctx, int layout, Cursor c, String[] from, int[] to) {
        super(ctx, layout, c, from, to);
        this.context = ctx;
        this.layout = layout;
        mLayoutInflater = LayoutInflater.from(ctx);
    }


    @Override
    public View newView(Context ctx, Cursor cursor, ViewGroup parent) {
        View v = mLayoutInflater.inflate(layout, parent, false);
        v.setTag( new ViewHolder(v) );
        return v;
    }

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
        vh.durationBox.setText(c.getString(durationIndex));
        vh.distanceBox.setText(c.getString(distanceIndex)+"km");

        if(c.getInt(favIndex)==1) vh.favButton.setImageResource(R.mipmap.heart_on_icon);
        else vh.favButton.setImageResource(R.mipmap.heart_off_icon);

        //vh.imageView.setImageBitmap ( mySetImage ( sFileAndPath_Image ) );
    }
}
