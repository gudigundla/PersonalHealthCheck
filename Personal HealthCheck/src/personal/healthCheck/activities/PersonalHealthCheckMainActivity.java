package personal.healthCheck.activities;

import personal.healthCheck.model.*;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class PersonalHealthCheckMainActivity extends Activity {
    
    private OnClickListener appointmentsOnclickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(PersonalHealthCheckMainActivity.this,AddNewEventActivity.class);
			startActivity(intent);
		}
	};

	@Override
    public void onCreate(Bundle savedInstanceState) {
       	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        TextView textViewAppointments = (TextView) findViewById(R.id.textViewAppointments);
        textViewAppointments.setOnClickListener(appointmentsOnclickListener );
       
        //Copies database from Assets to device ONLY when the app is run for the first time 
        CopyDataBaseToDevice();
        
    }

	private void CopyDataBaseToDevice() {
		MySQLiteOpenHelper myDbHelper = new MySQLiteOpenHelper(this);
		 
    try {
         	myDbHelper.createDataBase();
        } 
      
    catch (IOException ioe) {
  		Log.d("Personal HealthCheck-1",ioe.toString());
 		throw new Error("Unable to create database");
 	}
  	
 	finally{
 		myDbHelper.close();
 	}	
	}
}