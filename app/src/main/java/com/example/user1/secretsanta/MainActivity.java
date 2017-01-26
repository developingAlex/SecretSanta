package com.example.user1.secretsanta;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

     ApplicationStateClass currentState;

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

    public void DRAWClicked(View view){
        currentState.redraw();
    }

    public void NEXTClicked(View view){ currentState.addNameToHat(((EditText) findViewById(R.id.enter_name_field)).getText().toString()); }

    public void RESETClicked(View view){
        currentState.resetGame(false);
    }
    public void DONEClicked(View view){
        int doneButtonsDelayinMS = -1;
        doneButtonsDelayinMS = getResources().getInteger(R.integer.millisecond_delay_to_Done_buttons_responsiveness);
        if (currentState.DEBUGGING){
            doneButtonsDelayinMS = 0;
        }
        assert (doneButtonsDelayinMS != -1);

        currentState.doneButtonPressed();
        currentState.setClickableDoneButton(false);
        // this will prevent a user from accidentally double clicking.
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){

                currentState.setClickableDoneButton(true);
            }
        },doneButtonsDelayinMS); //originally set at 600 ms in the config.xml resource file.
    }
}
