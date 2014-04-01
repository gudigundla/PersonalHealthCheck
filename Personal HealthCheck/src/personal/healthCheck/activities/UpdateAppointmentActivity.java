package personal.healthCheck.activities;

import personal.healthCheck.model.*;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class UpdateAppointmentActivity extends Activity {

	private TextView textViewTime;
	private TextView textViewDate;
    private EditText editTextAppointmentName;
    
	private int mHour;
	private int mMinute;
    private int mYear;
    private int mMonth;
    private int mDay;

	static final int TIME_DIALOG_ID = 0;
    static final int DATE_DIALOG_ID = 1;
    
    String appointmentName;
    int id;
    String dateAndTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_appointment_activity_layout);
		
		Intent intent = getIntent();
		
		appointmentName 	   = intent.getStringExtra("name");
		id			      	   = intent.getIntExtra("id", -1);
		dateAndTime 		   = intent.getStringExtra("dateAndTime");
		
		// capture our View elements
		editTextAppointmentName = (EditText) findViewById(R.id.editTextAppointmnetName);
		textViewTime = (TextView) findViewById(R.id.textViewTime);
		textViewDate = (TextView) findViewById(R.id.textViewDate);
		Button updateButton = (Button) findViewById(R.id.buttonUpdateAppointment);
		Button cancelButton = (Button) findViewById(R.id.buttonCancelAppointmentUpdate);
		
		OnClickListener cancelButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		};
		cancelButton.setOnClickListener(cancelButtonListener);
				
		OnClickListener updateButtonOnClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				//update to database using _id
				String updatedName = editTextAppointmentName.getText().toString();
				String updatedDateAndTime = getDateAndTime(); 
					
				UpdateDatabase(updatedName, updatedDateAndTime, id);
				//start appointments activity
				finish();
				
				Toast.makeText(UpdateAppointmentActivity.this, "Updated !!", Toast.LENGTH_SHORT).show(); 
				
			}

			private void UpdateDatabase(String updatedName, String updatedDateAndTime, int id) {
					MySQLiteOpenHelper myDbHelper = new MySQLiteOpenHelper(UpdateAppointmentActivity.this);
					myDbHelper.openDataBase();
			  		Log.d("Personal HealthCheck-2","Personal HealthCheck database opened !!");

			  		ContentValues args = new ContentValues();
			  	    args.put("Name", updatedName);
			  	    args.put("DateAndTime", updatedDateAndTime);
			  	    myDbHelper.getMyDataBase().update("Appointments", args, "_id" + "=" + id, null);
			  	    myDbHelper.close();
			  	  Log.d("Personal HealthCheck","Database Updated !!");
			}

			private String getDateAndTime() {
				// format: yyyy-MM-dd HH:mm:ss
				return mYear+"-"+pad(mMonth+1)+"-"+mDay+" "+mHour+":"+mMinute+":"+"00";
			}
		};
		updateButton.setOnClickListener(updateButtonOnClickListener );
		
		editTextAppointmentName.setText(appointmentName);
		
	// add a click listener to the textViewTime 
	textViewTime.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	            showDialog(TIME_DIALOG_ID);
	        }
	    });

        // add a click listener to the textViewDate
		textViewDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

		// get the current time
	    final Calendar c = Calendar.getInstance();
	    
	    mHour = c.get(Calendar.HOUR_OF_DAY);
	    mMinute = c.get(Calendar.MINUTE);
	    // get the current date
        //final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

     // initialise variables
        initialiseVariables();
     // display the appointment time and date
        updateDisplay();
	}

	// initialising variables with date and time set before
	private void initialiseVariables() {
		DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date =  iso8601Format.parse(dateAndTime) ;
			mHour = date.getHours();
			mMinute = date.getMinutes();
			
			mYear = date.getYear() + 1900;
			mMonth= date.getMonth();
			mDay = date.getDate();
		} 
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case TIME_DIALOG_ID:
	        return new TimePickerDialog(this,
	                timeSetListener, mHour, mMinute, false);
	    case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                    dateSetListener, mYear, mMonth, mDay);
	    }
	    return null;
	}
	
	// updates the date we display in the TextView
	private void updateDisplay() {
			
		String formattedTime = SimpleTimeFormat(mHour, mMinute);
		textViewTime.setText("Time: " +formattedTime);
				    
	    Format formatter = new SimpleDateFormat("dd  MMM  yyyy");
	    Date date = new Date(mYear-1900, mMonth, mDay);
	    String formatedDate = formatter.format(date);
	    textViewDate.setText("Date: "+formatedDate);
	}
	
	private String SimpleTimeFormat(int hours, int minutes) {
		
		if(hours>12)
			return pad(hours-12) + " : " + pad(minutes) +" PM";
		else if(hours==12)
			return   12 +  " : " + pad(minutes) +" PM";
		else if(hours==0)
			return   12 +  " : " + pad(minutes) +" AM";
		else 
			return pad(hours) +  " : " + pad(minutes) +" AM";
	}

	// the callback received when the user "sets" the time in the dialog
	private TimePickerDialog.OnTimeSetListener timeSetListener =
		    new TimePickerDialog.OnTimeSetListener() {
		        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		            mHour = hourOfDay;
		            mMinute = minute;
		            updateDisplay();
		        }
		    };
		    
	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener dateSetListener =
		            new DatePickerDialog.OnDateSetListener() {

		                public void onDateSet(DatePicker view, int year, 
		                                      int monthOfYear, int dayOfMonth) {
		                    mYear = year;
		                    mMonth = monthOfYear;
		                    mDay = dayOfMonth;
		                    updateDisplay();
		                }
		            };

	private static String pad(int c) {
		        if (c >= 10)
		            return String.valueOf(c);
		        else
		            return "0" + String.valueOf(c);
		    }
}
