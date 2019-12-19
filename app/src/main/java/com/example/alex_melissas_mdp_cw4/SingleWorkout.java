package com.example.alex_melissas_mdp_cw4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SingleWorkout extends AppCompatActivity {

    private String workout_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        workout_id = bundle.getString("workout_id");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_workout);
        readWorkout();
    }

    protected void readWorkout(){

    }
}
