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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class SingleWorkout extends AppCompatActivity {

    final static public int RESULT_LOAD_IMG = 1;
    private String workout_id;

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

    public void onClickPickImage(View v){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // SAVE BITMAP TO BLOB IN DB

                ((ImageView)findViewById(R.id.imageView)).setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();}
                //Toast.makeText(PostImage.this, "Something went wrong", Toast.LENGTH_LONG).show();

        }else {
            //Toast.makeText(PostImage.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void readWorkout(){
        //Read actual recipe and instructions
        Cursor c = getContentResolver().query(WorkoutsContract.WORKOUTS, new String[]{ "*"},
                "_id = ?",new String[]{workout_id}, null, null);

        if(c.moveToFirst()){

            int type = c.getInt(1);
            int liked = c.getInt(8);
            int fav = c.getInt(9);

            //set type
            switch(type){
                case 0: ((ImageView)findViewById(R.id.typeImageSingle)).setImageResource(R.mipmap.walk_icon); break;
                case 1: ((ImageView)findViewById(R.id.typeImageSingle)).setImageResource(R.mipmap.jog_icon); break;
                case 2: ((ImageView)findViewById(R.id.typeImageSingle)).setImageResource(R.mipmap.run_icon); break;
                default: ((ImageView)findViewById(R.id.typeImageSingle)).setImageResource(R.mipmap.walk_icon); break;
            }

            String name = c.getString(2);
            if(name!="" && name!=null) ((EditText)findViewById(R.id.nameText)).setText(name);
            else ((EditText)findViewById(R.id.nameText)).setText("Name this workout");

            if(liked==0) { ((CheckBox)findViewById(R.id.likeCheck)).setChecked(false); }
            else { ((CheckBox)findViewById(R.id.likeCheck)).setChecked(true); }

            if(fav==0) { ((CheckBox)findViewById(R.id.favCheck)).setChecked(false); }
            else { ((CheckBox)findViewById(R.id.favCheck)).setChecked(true); }

            ((TextView)findViewById(R.id.datetimeText)).setText(c.getString(3));
            ((TextView)findViewById(R.id.durationText)).setText(secToDuration(c.getInt(4)));
            ((TextView)findViewById(R.id.distanceText)).setText(String.format("%.2f",c.getFloat(5))+" km");
            ((TextView)findViewById(R.id.avgspeedText)).setText(String.format("%.2f",c.getFloat(6))+" km/h");
            ((EditText)findViewById(R.id.notesText)).setText(c.getString(10));
        }
    }

    private String secToDuration(int sec){
        int hours = sec/3600;
        int mins = (sec-hours*3600)/60;
        int secs = (sec-hours*3600)%60;
        return String.format("%2d",hours)+":"+String.format("%02d",mins)+":"+String.format("%02d",secs);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onCheckFav(View v){
        int favValue;
        if(((CheckBox)findViewById(R.id.favCheck)).isChecked()) favValue = 1; else favValue = 0;
        ContentValues fav = new ContentValues();
        fav.put("fav",favValue);
        getContentResolver().update(WorkoutsContract.WORKOUTS,fav,"_id=?",new String[]{workout_id});
        readWorkout();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onCheckLike(View v){
        int likeValue;
        if(((CheckBox)findViewById(R.id.likeCheck)).isChecked()) likeValue = 1; else likeValue = 0;
        ContentValues fav = new ContentValues();
        fav.put("liked",likeValue);
        getContentResolver().update(WorkoutsContract.WORKOUTS,fav,"_id=?",new String[]{workout_id});
        readWorkout();
    }

    public void onClickDelete(View v){
        if(getContentResolver().delete(WorkoutsContract.WORKOUTS,"_id = ?",new String[]{workout_id})==0){
            startActivity(new Intent(SingleWorkout.this,MainActivity.class));
        } else Log.d("Delete: ","Error");
    }

}
