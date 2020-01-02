package com.example.alex_melissas_mdp_cw4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SingleWorkout extends AppCompatActivity {

    final static public int RESULT_LOAD_IMG = 1;
    private String workout_id;
    private LatLng startLocation, endLocation;

    //Standard onCreate, also read the workout data and attach listeners to textfields
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        workout_id = bundle.getString("workout_id");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_workout);

        readWorkout();
        attachListeners();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("workout_id", workout_id);
        outState.putParcelable("startLocation", startLocation);
        outState.putParcelable("endLocation", endLocation);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        workout_id = inState.getString("workout_id");
        startLocation = inState.getParcelable("startLocation");
        endLocation = inState.getParcelable("endLocation");
        readWorkout();
    }

    // Handle storing/loading image to/from db, and displaying it in the imageView
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ((ImageView)findViewById(R.id.imagePickButton)).setImageBitmap(selectedImage);

                ByteArrayOutputStream compressed = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG,100,compressed);
                byte[] image = compressed.toByteArray();

                ContentValues photo = new ContentValues();
                photo.put("image",image);
                getContentResolver().update(WorkoutsContract.WORKOUTS,photo,"_id=?",new String[]{workout_id});

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(SingleWorkout.this, "Image selection error", Toast.LENGTH_SHORT).show();
            }
        }else Toast.makeText(SingleWorkout.this, "No image selected",Toast.LENGTH_SHORT).show();
    }

    //Attach listeners to textfields, to automatically save changes to db onTextChange
    private void attachListeners(){
        final EditText notesText = (EditText)findViewById(R.id.notesText);
        final EditText nameText = (EditText)findViewById(R.id.nameText);

        notesText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String notes = ((TextView)findViewById(R.id.notesText)).getText().toString();
                ContentValues newNotes = new ContentValues();
                newNotes.put("notes",notes);
                getContentResolver().update(WorkoutsContract.WORKOUTS,newNotes,"_id=?",new String[]{workout_id});
            }
        });

        nameText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) { }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(nameText.getText().toString()=="Name this workout"){return;}
                String name = ((TextView)findViewById(R.id.nameText)).getText().toString();
                ContentValues newName = new ContentValues();
                newName.put("name",name);
                getContentResolver().update(WorkoutsContract.WORKOUTS,newName,"_id=?",new String[]{workout_id});
            }
        });
    }

    // Query db for workout data and fill all relevant Activity fields
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void readWorkout(){
        //Read actual recipe and instructions
        Cursor c = getContentResolver().query(WorkoutsContract.WORKOUTS, new String[]{ "*"},
                "_id = ?",new String[]{workout_id}, null, null);

        if(c.moveToFirst()) {

            int type = c.getInt(1);
            int liked = c.getInt(8);
            int fav = c.getInt(9);

            // TYPE-ICON
            switch (type) {
                case 0:
                    ((ImageView) findViewById(R.id.typeImageSingle)).setImageResource(R.mipmap.walk_icon);
                    break;
                case 1:
                    ((ImageView) findViewById(R.id.typeImageSingle)).setImageResource(R.mipmap.jog_icon);
                    break;
                case 2:
                    ((ImageView) findViewById(R.id.typeImageSingle)).setImageResource(R.mipmap.run_icon);
                    break;
                default:
                    ((ImageView) findViewById(R.id.typeImageSingle)).setImageResource(R.mipmap.walk_icon);
                    break;
            }

            String name = c.getString(2);
            if (name != "" && name != null) ((EditText) findViewById(R.id.nameText)).setText(name);
            else ((EditText) findViewById(R.id.nameText)).setText("Name this workout");

            if (liked == 0) {
                ((CheckBox) findViewById(R.id.likeCheck)).setChecked(false);
            } else {
                ((CheckBox) findViewById(R.id.likeCheck)).setChecked(true);
            }

            if (fav == 0) {
                ((CheckBox) findViewById(R.id.favCheck)).setChecked(false);
            } else {
                ((CheckBox) findViewById(R.id.favCheck)).setChecked(true);
            }

            ((TextView) findViewById(R.id.datetimeText)).setText(c.getString(3).substring(0, 16));
            ((TextView) findViewById(R.id.durationText)).setText(secToDuration(c.getInt(4)));
            ((TextView) findViewById(R.id.distanceText)).setText(String.format("%.2f", c.getFloat(5)) + " km");
            ((TextView) findViewById(R.id.avgspeedText)).setText(String.format("%.2f", c.getFloat(6)) + " km/h");
            ((EditText) findViewById(R.id.notesText)).setText(c.getString(10));

            // IMAGE

            if (c.getBlob(7) == null) return;

            ByteArrayInputStream imageStream = new ByteArrayInputStream(c.getBlob(7));
            Bitmap imageBM = BitmapFactory.decodeStream(imageStream);
            ((ImageView) findViewById(R.id.imagePickButton)).setImageBitmap(imageBM);
        }
    }

    // Convert raw seconds int to hh:mm:ss string
    private String secToDuration(int sec){
        int hours = sec/3600;
        int mins = (sec-hours*3600)/60;
        int secs = (sec-hours*3600)%60;
        return String.format("%2d",hours)+":"+String.format("%02d",mins)+":"+String.format("%02d",secs);
    }

    // Handle favourite checkbox
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onCheckFav(View v){
        int favValue;
        if(((CheckBox)findViewById(R.id.favCheck)).isChecked()) favValue = 1; else favValue = 0;
        ContentValues fav = new ContentValues();
        fav.put("fav",favValue);
        getContentResolver().update(WorkoutsContract.WORKOUTS,fav,"_id=?",new String[]{workout_id});
        readWorkout();
    }

    // Handle like checkbox
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onCheckLike(View v){
        int likeValue;
        if(((CheckBox)findViewById(R.id.likeCheck)).isChecked()) likeValue = 1; else likeValue = 0;
        ContentValues fav = new ContentValues();
        fav.put("liked",likeValue);
        getContentResolver().update(WorkoutsContract.WORKOUTS,fav,"_id=?",new String[]{workout_id});
        readWorkout();
    }

    // Delete row from db
    public void onClickDelete(View v){
        if(getContentResolver().delete(WorkoutsContract.WORKOUTS,"_id = ?",new String[]{workout_id})==0){
            startActivity(new Intent(SingleWorkout.this,MainActivity.class));
        } else Log.d("Delete: ","Error");
    }

    // Prompt user to pick image from files
    public void onClickPickImage(View v){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    // Show map with start and end points of workout
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickMap(View v){
        Cursor l = getContentResolver().query(WorkoutsContract.LOCATIONSOFWORKOUT, null,
                null,new String[]{workout_id}, null, null);
        if(l.moveToFirst()) {
            do{ //if start location make these LatLng the start location
                if(l.getInt(2)==0) startLocation = new LatLng(l.getDouble(0),l.getDouble(1));
                else endLocation = new LatLng(l.getDouble(0),l.getDouble(1));
            }while(l.moveToNext());
        } else startLocation = endLocation = null;

        if(startLocation == null || endLocation == null) return;

        Intent mapIntent = new Intent(SingleWorkout.this, MapsActivity.class);
        mapIntent.putExtra("pin1", startLocation);
        mapIntent.putExtra("pin2", endLocation);
        mapIntent.putExtra("currentOrPast", 1);
        mapIntent.putExtra("whoCalled", "SingleWorkout");
        mapIntent.putExtra("workout_id", workout_id);
        startActivity(mapIntent);
    }

}
