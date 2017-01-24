package com.example.user1.secretsanta;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

     ApplicationStateClass currentState;
     final int DOWNTIME_OF_DONE_BUTTON_IN_MILLISECONDS = 600; //

    /*
    * The onCreate is called every time the activity is drawn. When the device is rotated, the
    * activity is redrawn. This is why we use the ApplicationStateClass to store the current
    * state of the system so that it will not be affected by device rotations.*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentState = ((ApplicationStateClass)this.getApplication());
        currentState.updateInstance(this);
    }


    public void NEXTClicked(View view){ currentState.addNameToHat(((EditText) findViewById(R.id.enter_name_field)).getText().toString()); }


    public void DONEClicked(View view){

        currentState.doneButtonPressed();
        currentState.setClickableDoneButton(false);
        //doneButton = (Button) view;
        //doneButton.setClickable(false); //impose a short delay before the done button may be clicked again.
        // the idea is that this will prevent a user from accidentally double clicking.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){

                currentState.setClickableDoneButton(true);
                //doneButton.setClickable(true);
            }
        },R.integer.millisecond_delay_to_Done_buttons_responsiveness); //originally set at 600 ms.
    }
}
