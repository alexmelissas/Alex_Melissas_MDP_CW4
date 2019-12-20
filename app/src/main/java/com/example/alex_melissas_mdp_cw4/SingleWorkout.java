package com.example.alex_melissas_mdp_cw4;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class SingleWorkout extends AppCompatActivity {

    private String workout_id;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        workout_id = bundle.getString("workout_id");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_workout);

        readWorkout();



        final EditText notesText = (EditText)findViewById(R.id.notesText);
        notesText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String notes = ((TextView)findViewById(R.id.notesText)).getText().toString();
                ContentValues newNotes = new ContentValues();
                newNotes.put("notes",notes);
                getContentResolver().update(WorkoutsContract.WORKOUTS,newNotes,"_id=?",new String[]{workout_id});
            }
        });

        final EditText nameText = (EditText)findViewById(R.id.nameText);
        nameText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
        Cursor recipeCursor = getContentResolver().query(WorkoutsContract.WORKOUTS, new String[]{ "*"},
                "_id = ?",new String[]{workout_id}, null, null);

        if(recipeCursor.moveToFirst()){

            int type = recipeCursor.getInt(1);
            int liked = recipeCursor.getInt(8);
            int fav = recipeCursor.getInt(9);
            String name = recipeCursor.getString(2);
            if(name!="" && name!=null) ((EditText)findViewById(R.id.nameText)).setText(name);
            else ((EditText)findViewById(R.id.nameText)).setText("Name this workout");
            ((TextView)findViewById(R.id.datetimeText)).setText(recipeCursor.getString(3));
            ((TextView)findViewById(R.id.durationText)).setText(recipeCursor.getString(4));
            ((TextView)findViewById(R.id.distanceText)).setText(recipeCursor.getString(5));
            ((TextView)findViewById(R.id.avgspeedText)).setText(recipeCursor.getString(6));
            ((EditText)findViewById(R.id.notesText)).setText(recipeCursor.getString(10));

            //set type
            switch(type){
                case 0: ((ImageView)findViewById(R.id.typeImageSingle)).setImageResource(R.mipmap.walk_icon); break;
                case 1: ((ImageView)findViewById(R.id.typeImageSingle)).setImageResource(R.mipmap.jog_icon); break;
                case 2: ((ImageView)findViewById(R.id.typeImageSingle)).setImageResource(R.mipmap.run_icon); break;
                default: ((ImageView)findViewById(R.id.typeImageSingle)).setImageResource(R.mipmap.walk_icon); break;
            }
        }
    }

    public void onClickFavSingle(View v){

    }

    public void onClickLike(View v){

    }

    public void onClickDelete(View v){

    }

}
