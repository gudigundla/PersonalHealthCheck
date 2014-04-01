package personal.healthCheck.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import personal.healthCheck.model.DayOfWeek;
import personal.healthCheck.model.Event;
import personal.healthCheck.model.Feedback;
import personal.healthCheck.model.MySQLiteOpenHelper;
import personal.healthCheck.model.RepeatByType;
import personal.healthCheck.model.RepeatType;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddNewEventActivity extends Activity {

	private static final int FROM_DATE_DIALOG_ID = 0;
	private static final int TO_DATE_DIALOG_ID = 1;
	private static final int FROM_TIME_DIALOG_ID = 2;
	private static final int TO_TIME_DIALOG_ID = 3;
	private static final int REPEAT_DAILY_MENU = 4;
	private static final int REPEAT_DAILY_TO_DATE_DIALOG_ID = 5;
	private static final int REPEAT_YEARLY_MENU = 6;
	private static final int REPEAT_FIXED_WEEK_DAYS_DIALOG = 7;
	private static final int REPEAT__WEEKLY_MON_WED_FRI_DIALOG_ID = 8;
	private static final int REPEAT_ALL_WEEK_DAYS_DIALOG_ID = 9;
	private static final int REPEAT_TUES_TUES_DIALOG_ID = 10;
	private static final int REPEAT_WEEKLY_DIALOG_ID = 11;
	private static final int REPEAT_WEEKLY_TO_DATE_DIALOG_ID = 12;
	private static final int REPEAT_MONTHLY_DIALOG_ID = 13;
	private static final int REPEAT_MONTHLY_TO_DATE_DIALOG_ID = 14;
	private static final int REPEAT_DAILY_MULTIPLE_TIMES_DIALOG_ID = 15;
	private static final int DAILY_MULTIPLE_TIMES_SET_TIMING_DIALOG_ID = 16;
	private static final int DAILY_MULTIPLE_TIMES_END_TIME_DIALOG_ID = 17;
	Button buttonFromDate;
	Button buttonToDate;
	Button buttonFromTime;
	Button buttonToTime;
	Button buttonAddNewEvent;
	CheckBox checkBoxAllDay;
	RadioButton radioButtonRepeat;
	RadioButton radioButtonDayOfWeek;
	LinearLayout linearLayoutAllDay;
	LinearLayout linearLayoutRepeat;
	LinearLayout linearLayoutNotification;
	TextView textViewRepeatmessage;
	Button buttonRepeatDailyFormattedEndDate;
	Button buttonRepeatFixedDAysWeekEndDate;
	Button buttonRepeatFixedDAysWeekStartDate;
	Button buttonRepeatFixedDaysWeekDone;
	Button buttonRepeatFixedDaysWeekCancel;
	Button repeatWeeklyEndDAteButton;
	Button repeatMonthlyEndDateButton;
	Button repeatWeeklyDialogDoneButton;
	Button repeatWeeklyDialogCancelButton;
	Button repeatMonthlyDialogDoneButton;
	Button repeatMonthlyDialogCancelButton;
	Button buttonToAddMultipleTimingsADay;
	Button buttonDailyMultipleTimesEndDate;
	Date tempDate;
	Date tempDate1;
	Event event;
	Dialog dialogRepeatDaily;
	Dialog dialogyearlyRepeat;
	Dialog dialogRepeatFixedDaysInWeek;
	Dialog dialogRepeatWeekly;
	Dialog dialogRepeatMonthly;
	Dialog dialogRepeatMultipleTimesDaily;
	EditText editViewRepeatByDays;
	EditText editViewRepeatByNumOfWeeks;
	EditText editViewRepeatByNumOfMonths;
	EditText editTextLocation;
	LinearLayout linearLayoutWithDateButtons;
	List<Date> multipleTimings = new ArrayList<Date>(); 
	
	String name, startDateTime, endDateTime, repeatOnDays, repeatAtHours, location, note ;
	int isAllDayEvent, isRepeatedEvent, repeatType, repeatEvery, repeatByType, repeatEndType, feedback;
		
	private OnClickListener allDayListener = new OnClickListener() {
	
		@Override
		public void onClick(View v) {
				
			
			if(event.getIsAllDayEvent())
			{
				checkBoxAllDay.setChecked(false);
				event.setIsAllDayEvent(false);
				buttonFromTime.setVisibility(View.KEEP_SCREEN_ON);
				buttonToTime.setVisibility(View.KEEP_SCREEN_ON);
				
			}
			else {
				checkBoxAllDay.setChecked(true);
				event.setIsAllDayEvent(true);
				Toast.makeText(AddNewEventActivity.this, "All Day Event", Toast.LENGTH_SHORT).show();
				buttonFromTime.setVisibility(View.GONE);
				buttonToTime.setVisibility(View.GONE);
			}
		}
	};
	private OnClickListener repeatListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
		}
	};
	private OnClickListener buttonFromDateListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(FROM_DATE_DIALOG_ID);
		}
	};
	private OnDateSetListener fromDateSetListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			event.setStartDate(new Date(year-1900, monthOfYear, dayOfMonth, event.getStartDate().getHours(), event.getStartDate().getMinutes()));
			event.setEndDate(event.getStartDate());
			updateDateButtonsDisplay();
	}};
	private OnClickListener buttonToDateListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(TO_DATE_DIALOG_ID);
		}
	};
	private OnDateSetListener toDateSetListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			event.setEndDate(new Date(year-1900, monthOfYear, dayOfMonth, event.getEndDate().getHours(), event.getEndDate().getMinutes()));
			updateDateButtonsDisplay();
		}
	};
	private OnClickListener buttonFromTimeListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(FROM_TIME_DIALOG_ID);
		}
	};
	private OnTimeSetListener fromTimeSetListener = new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			
			event.setStartDate(new Date(event.getStartDate().getYear(), event.getStartDate().getMonth(), event.getStartDate().getDate(), hourOfDay, minute));
			event.setEndDate(new Date(event.getEndDate().getYear(), event.getEndDate().getMonth(), event.getEndDate().getDate(), hourOfDay+1, minute));
			updateDateButtonsDisplay();
		}
	};
	private OnClickListener buttonToTimeListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(TO_TIME_DIALOG_ID);
		}
	};
	private OnTimeSetListener toTimeSetListener = new OnTimeSetListener() {
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			event.setEndDate(new Date(event.getEndDate().getYear(), event.getEndDate().getMonth(), event.getEndDate().getDate(), hourOfDay, minute));
			updateDateButtonsDisplay();
		}
	};
	private OnClickListener RepeatOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			openContextMenu( v );
		}
	};
	private OnClickListener repeatDailyDialogEndDateButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(REPEAT_DAILY_TO_DATE_DIALOG_ID);
		}
	};
	private OnDateSetListener repeatDailyDialogEndDateDialogPickerButtonListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			Calendar calender = Calendar.getInstance();
			calender.setTime(event.getEndDate());
			Date date = new Date(year-1900, monthOfYear,dayOfMonth, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE) );
			tempDate= date;
			buttonRepeatDailyFormattedEndDate.setText(getFormattedDate(date));
		}
	};
	private OnClickListener repreatDailyDialogDoneButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//update Event, buttons, update repeatTextView, 
			int numOfDays = tryParse(editViewRepeatByDays.getText().toString());
			if(numOfDays>0 && numOfDays<30) {
			event.setEndDate(tempDate);
			event.setIsReapetedEvent(true);
			event.setRepeatType(RepeatType.DAILY_ONCE);
			updateDateButtonsDisplay();
			populateRepeatOptionSelected();
			event.setRepeatEvery(numOfDays);
			dialogRepeatDaily.dismiss();
			}
			else 
				Toast.makeText(AddNewEventActivity.this, "Enter a valid number between (1-30) to Repeat By !!", Toast.LENGTH_SHORT).show();
				}
	};
	private OnClickListener repreatDailyDialogCancelButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dialogRepeatDaily.dismiss();
		}
	};
	private OnClickListener repreatYearlyDialogDoneButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int numOfDays = tryParse(editViewRepeatByDays.getText().toString());
			if(numOfDays>0 && numOfDays<30) {
			event.setEndDate(tempDate);
			event.setIsReapetedEvent(true);
			event.setRepeatType(RepeatType.YEARLY);
			updateDateButtonsDisplay();
			populateRepeatOptionSelected();
			event.setRepeatEvery(numOfDays);
			dialogyearlyRepeat.dismiss();
			}
			else 
				Toast.makeText(AddNewEventActivity.this, "Enter a valid number between (1-30) to Repeat By !!", Toast.LENGTH_SHORT).show();
					
		}
	};
	private OnClickListener repeatFixedDAysWeekEndDateButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(REPEAT__WEEKLY_MON_WED_FRI_DIALOG_ID);
		}
	};
	private OnDateSetListener repeatFixedWeekDaysDialogEndDateDialogPickerButtonListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(event.getEndDate());
		Date date = new Date(year-1900, monthOfYear,dayOfMonth, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE) );
		tempDate= date;
		buttonRepeatFixedDAysWeekEndDate.setText(getFormattedDate(date));
		}
	};
	private OnClickListener repeatFixedDaysWeekDialogDoneButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			event.setEndDate(tempDate);
			event.setIsReapetedEvent(true);
			event.setRepeatType(RepeatType.EVERY_MON_WED_FRI);
			updateDateButtonsDisplay();
			populateRepeatOptionSelected();
			dialogRepeatFixedDaysInWeek.dismiss();
		}
	};
	private OnClickListener repeatFixedDaysWeekDialogCancelButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dialogRepeatFixedDaysInWeek.dismiss();
		}
	};
	private OnClickListener repeatWeekDaysMonToFriDialogDoneButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			event.setEndDate(tempDate);
			event.setIsReapetedEvent(true);
			event.setRepeatType(RepeatType.EVERY_WEEK_DAY_MON_TO_FRI);
			updateDateButtonsDisplay();
			populateRepeatOptionSelected();
			dialogRepeatFixedDaysInWeek.dismiss();
		}
	};
	private OnClickListener repeatTuesAndThursDialogDoneButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			event.setEndDate(tempDate);
			event.setIsReapetedEvent(true);
			event.setRepeatType(RepeatType.EVERY_TUES_THURS);
			updateDateButtonsDisplay();
			populateRepeatOptionSelected();
			dialogRepeatFixedDaysInWeek.dismiss();
		}
	};
	private OnClickListener repeatWeeklyEndDateButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(REPEAT_WEEKLY_TO_DATE_DIALOG_ID);
		}
	};
	private OnClickListener repeatWeeklyDoneButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//update Event, buttons, update repeatTextView, 
			int numOfWeeks = tryParse(editViewRepeatByNumOfWeeks.getText().toString());
			if(numOfWeeks>0 && numOfWeeks<30) {
			event.setEndDate(tempDate);
			event.setIsReapetedEvent(true);
			event.setRepeatType(RepeatType.WEEKLY);
			updateDateButtonsDisplay();
			populateRepeatOptionSelected();
			event.clearRepeatOnDays();
			populateRepeatByDays();
			event.setRepeatEvery(numOfWeeks);
			dialogRepeatWeekly.dismiss();
			}
			else 
				Toast.makeText(AddNewEventActivity.this, "Enter a valid number between (1-30) to Repeat By !!", Toast.LENGTH_SHORT).show();
				}

		private void populateRepeatByDays() {
			CheckBox checkBox =  (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxSunday);
			if(checkBox.isChecked()) {
				event.setRepeatsOnDays(DayOfWeek.SUNDAY);
				textViewRepeatmessage.append(" sunday");
			}
			checkBox =  (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxMonday);
			if(checkBox.isChecked()) {
				event.setRepeatsOnDays(DayOfWeek.MONDAY);
				textViewRepeatmessage.append(" monday");
			}
			checkBox =  (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxTuesday);
			if(checkBox.isChecked()) {
				event.setRepeatsOnDays(DayOfWeek.TUESDAY);
				textViewRepeatmessage.append(" tuesday");
			}
			checkBox =  (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxWednesday);
			if(checkBox.isChecked()) {
				event.setRepeatsOnDays(DayOfWeek.WEDNUSDAY);
				textViewRepeatmessage.append(" wednesday");
			}
			checkBox =  (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxThursday);
			if(checkBox.isChecked()) {
				event.setRepeatsOnDays(DayOfWeek.THURSDAY);
				textViewRepeatmessage.append(" thursday");
			}
			checkBox =  (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxFriday);
			if(checkBox.isChecked()) {
				event.setRepeatsOnDays(DayOfWeek.FRIDAY);
				textViewRepeatmessage.append(" friday");
			}
			checkBox =  (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxSaturday);
			if(checkBox.isChecked()) {
				event.setRepeatsOnDays(DayOfWeek.SATURDAY);
				textViewRepeatmessage.append(" saturday");
			}
		}
		
	};
	private OnClickListener repeatWeeklyCancelButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dialogRepeatWeekly.dismiss();
		}
	};
	private OnDateSetListener repeatWeeklyDialogEndDateDialogPickerButtonListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar calender = Calendar.getInstance();
			calender.setTime(event.getEndDate());
			Date date = new Date(year-1900, monthOfYear,dayOfMonth, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE) );
			tempDate= date;
			repeatWeeklyEndDAteButton.setText(getFormattedDate(date));
		}
	};
	private OnClickListener repreatYearlyDialogCancelButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dialogyearlyRepeat.dismiss();
		}
	};
	private OnClickListener repeatMonthlyEndDAteButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(REPEAT_MONTHLY_TO_DATE_DIALOG_ID);
		}
	};
	private OnDateSetListener repeatMonthlyDialogEndDateDialogPickerButtonListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar calender = Calendar.getInstance();
			calender.setTime(event.getEndDate());
			Date date = new Date(year-1900, monthOfYear,dayOfMonth, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE) );
			tempDate= date;
			repeatMonthlyEndDateButton.setText(getFormattedDate(date));
		}
	};
	private OnClickListener repeatMonthlyDialogDoneButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int numOfMonths = tryParse(editViewRepeatByNumOfMonths.getText().toString());
			if(numOfMonths>0 && numOfMonths<30) {
			event.setEndDate(tempDate);
			//event.setRepeatEvery(numOfMonths);
			event.setIsReapetedEvent(true);
			event.setRepeatType(RepeatType.MONTHLY);
			updateDateButtonsDisplay();
			populateRepeatOptionSelected();
			radioButtonDayOfWeek = (RadioButton) dialogRepeatMonthly.findViewById(R.id.radioDayOfTheWeek); 
			if(radioButtonDayOfWeek.isChecked())
				event.setRepeatByType(RepeatByType.DAY_OF_THE_WEEK);
			else
				event.setRepeatByType(RepeatByType.DAY_OF_THE_MONTH);

			event.setRepeatEvery(numOfMonths);
			dialogRepeatMonthly.dismiss();
			}
			else 
				Toast.makeText(AddNewEventActivity.this, "Enter a valid number between (1-30) to Repeat By !!", Toast.LENGTH_SHORT).show();
				}
		};
	private OnClickListener repeatMonthlyDialogCancelButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dialogRepeatMonthly.dismiss();
		}
	};
	private OnClickListener buttonToAddMultipleTimingsADayListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(DAILY_MULTIPLE_TIMES_SET_TIMING_DIALOG_ID);
		}
	};
	private OnTimeSetListener dailyMultipleTimesTimePickerSetButtonListener = new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Calendar calender = Calendar.getInstance();
			calender.setTime(event.getStartDate());
			Date date = new Date(calender.get(Calendar.YEAR)-1900, calender.get(Calendar.MONTH),calender.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
			tempDate1= date;
			multipleTimings.add(date);
 			//TODO create and add button
			Button button = new Button(AddNewEventActivity.this);
			LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
			button.setLayoutParams(params );
			button.setText(getFormattedTime(date));
			
			if(multipleTimings.size()>20) {
			//disable add 
				buttonToAddMultipleTimingsADay.setEnabled(false);
			}
			linearLayoutWithDateButtons.addView(button);
				
			
		}
	};
	private OnClickListener buttonDailyMultipleTimesEndDateListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			showDialog(DAILY_MULTIPLE_TIMES_END_TIME_DIALOG_ID);
		}
	};
	private OnDateSetListener dailyMultipleTimesEndDateTimePickerSetButtonListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			Calendar calender = Calendar.getInstance();
			calender.setTime(event.getEndDate());
			Date date = new Date(year-1900, monthOfYear,dayOfMonth, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE) );
			tempDate= date;
			buttonDailyMultipleTimesEndDate.setText(getFormattedDate(date));
		}
	};
	private OnClickListener buttonDailyMultipleDialogDoneListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(multipleTimings.size()>0) {
				event.setIsReapetedEvent(true);
				event.setRepeatType(RepeatType.DAILY_MULTIPLE_TIMES);
				updateDateButtonsDisplay();
				populateRepeatOptionSelected();
				event.setEndDate(tempDate);
				dialogRepeatMultipleTimesDaily.dismiss();
				if(event.getRepeatsAtHours().size()>0) {
					multipleTimings.addAll(event.getRepeatsAtHours());
				}
			} 	else Toast.makeText(AddNewEventActivity.this, "Select atleast one timing !!", Toast.LENGTH_SHORT).show();
				
		}
	};
	private OnClickListener buttonDailyMultipleDialogCancelListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			removeDialog(REPEAT_DAILY_MULTIPLE_TIMES_DIALOG_ID);
			//dialogRepeatMultipleTimesDaily.dismiss();
		}
	};
	private OnClickListener addNewEventButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Add all event details to database
			EditText editTextEventName = (EditText) findViewById(R.id.editTextEventName);
			if(editTextEventName.getText().toString().trim().length()>0) {
			event.setName(editTextEventName.getText().toString());
			
			event.setLocation(editTextLocation.getText().toString());
			
			updateAllDBVariables();
			//Toast.makeText(AddNewEventActivity.this, "updateAllDBVariables !!", Toast.LENGTH_SHORT).show();

			int rowID= insertRowIntoDatabase();
			//Toast.makeText(AddNewEventActivity.this, "insertRowIntoDatabase !!", Toast.LENGTH_SHORT).show();
			if(rowID != -1) {
				finish();
				Toast.makeText(AddNewEventActivity.this, "Event added successfully !!", Toast.LENGTH_SHORT).show();
				
			}
			}
			else Toast.makeText(AddNewEventActivity.this, "Enter a name for the event", Toast.LENGTH_SHORT).show();
				
		}
	};
	private OnClickListener buttonCancelAddingNewEventListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	private OnClickListener updateEventButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			EditText editTextEventName = (EditText) findViewById(R.id.editTextEventName);
			if(editTextEventName.getText().toString().trim().length()>0) {
			event.setName(editTextEventName.getText().toString());
			event.setLocation(editTextLocation.getText().toString());
			
			updateAllDBVariables();
			//Toast.makeText(AddNewEventActivity.this, "updateAllDBVariables !!", Toast.LENGTH_SHORT).show();

			int rowsEffected= updateRowIntoDatabase();
			//Toast.makeText(AddNewEventActivity.this, "insertRowIntoDatabase !!", Toast.LENGTH_SHORT).show();
			if(rowsEffected ==1) {
				finish();
				Toast.makeText(AddNewEventActivity.this, "Event updated successfully !!", Toast.LENGTH_SHORT).show();
				
			}
			}
			else Toast.makeText(AddNewEventActivity.this, "Enter a name for the event", Toast.LENGTH_SHORT).show();
						
		}

		private int updateRowIntoDatabase() {
			MySQLiteOpenHelper myDbHelper = new MySQLiteOpenHelper(AddNewEventActivity.this);
			myDbHelper.openDataBase();
	  		Log.d("Personal HealthCheck-2","Personal HealthCheck database opened !!");

	  		ContentValues updatedValues = new ContentValues(); 
			updatedValues.put("Name", name);
			updatedValues.put("StartDateTime", startDateTime);
			updatedValues.put("EndDateTime", endDateTime);
			updatedValues.put("IsAllDayEvent", isAllDayEvent);
			updatedValues.put("IsRepeatedEvent", isRepeatedEvent);
			updatedValues.put("RepeatType", repeatType);
			updatedValues.put("RepeatEvery", repeatEvery);
			updatedValues.put("RepeatByType", repeatByType);
			updatedValues.put("RepeatsOnDays", repeatOnDays);
			updatedValues.put("RepeatsAtHours", repeatAtHours);
			updatedValues.put("RepeatEndType", repeatEndType);
			updatedValues.put("location", location);
			updatedValues.put("Note", "");
			updatedValues.put("Feedback", feedback);
	  		
	  	    int rowsEffected= myDbHelper.getMyDataBase().update("Events", updatedValues, "_id" + "=" + event.getId(), null);
	  	    myDbHelper.close();
	  	  Log.d("Personal HealthCheck","Database Updated !!");

			return rowsEffected;
		}
	};
			
	//Override Methods
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_event_activity_layout);
		createUIViews();
		
		//add=1 & update=2
		Intent intent = getIntent();
		int addOrUpdate = intent.getIntExtra("AddOrUpdate", -1);

		if(addOrUpdate ==1) {
		populateEventInstanceInCaseOfAddNewEvent();
		buttonAddNewEvent.setOnClickListener(addNewEventButtonListener );
		}
		else {
			buttonAddNewEvent.setText("Update");
			int idDB = intent.getIntExtra("idDB", -1);
			populateEventInstanceInCaseOfUpdateEvent(idDB);
			TextView textViewEventTitle = (TextView) findViewById(R.id.textViewAddEventTitle);
			textViewEventTitle.setText("Update Event");
			buttonAddNewEvent.setOnClickListener(updateEventButtonListener);
		}
		
		populateUI();
		
		registerForContextMenu(linearLayoutRepeat);
		registerForContextMenu(radioButtonRepeat);
		
		//To hide automatic keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		Button buttonCancelAddingNewEvent = (Button) findViewById(R.id.buttonCancelingAddNewEvent);
		buttonCancelAddingNewEvent.setOnClickListener(buttonCancelAddingNewEventListener );
	}
	
	private void populateEventInstanceInCaseOfUpdateEvent(int idDb) {
		MySQLiteOpenHelper myDbHelper = new MySQLiteOpenHelper(this);
		try {			
	  		myDbHelper.openDataBase();
	  		Log.d("Personal HealthCheck-2","Personal HealthCheck database opened !!");
	  		
			String sql = "select * from Events where _id=" + idDb;
	 		Cursor cursor= myDbHelper.getMyDataBase().rawQuery(sql, null);
	 		Log.d("ALERT", "Step 1");
	 		cursor.moveToFirst();

    		Log.d("Personal HealthCheck rowName", cursor.getCount() +" rows");
	 		
	        while (cursor.isAfterLast() == false) {
	        	Boolean isAllDayEvent = false;  
	        	if(cursor.getInt(4)==1) isAllDayEvent=true;  
	        	Boolean isRepeatedEvent = false;
	        	if(cursor.getInt(5)==1) isRepeatedEvent=true;
	        	RepeatType  repeatType = RepeatType.ONE_TIME_EVENT;
	        	
	        	switch (cursor.getInt(6)) {
				case 1:
					repeatType =RepeatType.DAILY_ONCE;
					break;
				case 2:
					repeatType =RepeatType.DAILY_MULTIPLE_TIMES;
					break;
				case 3:
					repeatType =RepeatType.EVERY_WEEK_DAY_MON_TO_FRI;
					break;
				case 4:
					repeatType =RepeatType.EVERY_MON_WED_FRI;
					break;
				case 5:
					repeatType =RepeatType.EVERY_TUES_THURS;
					break;
				case 6:
					repeatType =RepeatType.WEEKLY;
					break;
				case 7:
					repeatType =RepeatType.MONTHLY;
					break;
				case 8:
					repeatType =RepeatType.YEARLY;
					break;
				}
	        	
	        	RepeatByType repeatByType = RepeatByType.DAY_OF_THE_MONTH;
	        	if(cursor.getInt(8)==1) repeatByType = RepeatByType.DAY_OF_THE_WEEK;

	        	List<DayOfWeek> repeatOnDays = new ArrayList<DayOfWeek>();
	        	String[] temps = cursor.getString(9).split("_");
	        	
	        	for (String string : temps) {
					int i = tryParse(string);
					
					switch (i) {
					case 1:
						repeatOnDays.add(DayOfWeek.SUNDAY);
						break;
					case 2:
						repeatOnDays.add(DayOfWeek.MONDAY);
						break;
					case 3:
						repeatOnDays.add(DayOfWeek.TUESDAY);
						break;
					case 4:
						repeatOnDays.add(DayOfWeek.WEDNUSDAY);
						break;
					case 5:
						repeatOnDays.add(DayOfWeek.THURSDAY);
						break;
					case 6:
						repeatOnDays.add(DayOfWeek.FRIDAY);
						break;
					case 7:
						repeatOnDays.add(DayOfWeek.SATURDAY);
						break;
					}
				}
	        	
	        	String[] temp = cursor.getString(10).split("_");
	        	//Toast.makeText(AppointmentsTabActivity.this,  temp.length+ "", Toast.LENGTH_SHORT).show();
	        	List<Date> repeatOnHours = new ArrayList<Date>();
	        	for (String value : temp) {
	        		if(value.trim().length()>0)
	        			repeatOnHours.add(getDateFromString(value.trim()));
				}
	        //	Toast.makeText(AppointmentsTabActivity.this,  "repeatOnHours created", Toast.LENGTH_SHORT).show();
	        	
	        	
	        	event = new Event(idDb, cursor.getString(1), 
	        			getDateFromString(cursor.getString(2)), getDateFromString(cursor.getString(3)), isAllDayEvent, isRepeatedEvent, repeatType, 
	        			cursor.getInt(7), repeatByType, repeatOnDays, repeatOnHours, cursor.getString(12),Feedback.NO_FEEDBACK_YET );
	        	
	        	Log.d("Personal HealthCheck", cursor.getString(1));
	        	cursor.moveToNext();
	        }
	        cursor.close();
			}
		catch(SQLException sqlException){
	 		Log.d("Personal HealthCheck-3",sqlException.toString());
	 		throw sqlException;
	  	}
	 	finally{
	 		myDbHelper.close();
	 	}

	}

	protected int insertRowIntoDatabase() {
		//open DB connection
		MySQLiteOpenHelper myDbHelper = new MySQLiteOpenHelper(AddNewEventActivity.this);
		myDbHelper.openDataBase();
  		Log.d("Personal HealthCheck","Personal HealthCheck database opened !!");
		
		ContentValues initialValues = new ContentValues(); 
		initialValues.put("Name", name);
		initialValues.put("StartDateTime", startDateTime);
		initialValues.put("EndDateTime", endDateTime);
		initialValues.put("IsAllDayEvent", isAllDayEvent);
		initialValues.put("IsRepeatedEvent", isRepeatedEvent);
		initialValues.put("RepeatType", repeatType);
		initialValues.put("RepeatEvery", repeatEvery);
		initialValues.put("RepeatByType", repeatByType);
		initialValues.put("RepeatsOnDays", repeatOnDays);
		initialValues.put("RepeatsAtHours", repeatAtHours);
		initialValues.put("RepeatEndType", repeatEndType);
		initialValues.put("location", location);
		initialValues.put("Note", "");
		initialValues.put("Feedback", feedback);
		
		long rowId = myDbHelper.getMyDataBase().insert("Events", null, initialValues);
		
		myDbHelper.close();
		return (int) rowId;
	}

	protected String getDBFormattedDate(Date date) {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return formater.format(date);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.repeat_type_context_menu_layout, menu);
			menu.setHeaderTitle("Repeat Options");
			
			switch (event.getRepeatType()) {
			case DAILY_ONCE:
				menu.findItem(R.id.itemDaily).setChecked(true);
				break;
			case ONE_TIME_EVENT:
				menu.findItem(R.id.itemOneTimeEvents).setChecked(true);
				break;
			case DAILY_MULTIPLE_TIMES:
				menu.findItem(R.id.itemDailyMultipleTimes).setChecked(true);
				break;
			case EVERY_WEEK_DAY_MON_TO_FRI:
				menu.findItem(R.id.itemEveryWeekDayMonToFri).setChecked(true);
				break;
			case EVERY_MON_WED_FRI:
				menu.findItem(R.id.itemEveryMonWedFri).setChecked(true);
				break;
			case EVERY_TUES_THURS:
				menu.findItem(R.id.itemEveryTuesThurs).setChecked(true);
				break;
			case WEEKLY:
				menu.findItem(R.id.itemWeekly).setChecked(true);
				break;
			case MONTHLY:
				menu.findItem(R.id.itemMonthly).setChecked(true);
				break;
			case YEARLY:
				menu.findItem(R.id.itemYearly).setChecked(true);
				break;
			}
		}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
				
		case R.id.itemOneTimeEvents:
			event.setIsReapetedEvent(true);
			Toast.makeText(AddNewEventActivity.this, "one time event", Toast.LENGTH_SHORT).show();
			textViewRepeatmessage.setText("one Time Event");
			break;
		case R.id.itemDaily:
			showDialog(REPEAT_DAILY_MENU);
			break;
		case R.id.itemDailyMultipleTimes:
			showDialog(REPEAT_DAILY_MULTIPLE_TIMES_DIALOG_ID);
			break;
		case R.id.itemEveryMonWedFri:
			showDialog(REPEAT_FIXED_WEEK_DAYS_DIALOG);
			break;
		case R.id.itemEveryWeekDayMonToFri:
			showDialog(REPEAT_ALL_WEEK_DAYS_DIALOG_ID);
			break;
		case R.id.itemEveryTuesThurs:
			showDialog(REPEAT_TUES_TUES_DIALOG_ID);
			break;
		case R.id.itemWeekly:
			showDialog(REPEAT_WEEKLY_DIALOG_ID);
			break;
		case R.id.itemMonthly:
			showDialog(REPEAT_MONTHLY_DIALOG_ID);
			break;
		case R.id.itemYearly:
			showDialog(REPEAT_YEARLY_MENU);
			break;
		}
	return super.onContextItemSelected(item);
}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		Calendar calender = Calendar.getInstance();
		
		switch (id) {
		case FROM_DATE_DIALOG_ID:
			calender.setTime(event.getStartDate());
			return new DatePickerDialog(this, fromDateSetListener, 
					  calender.get(Calendar.YEAR), 
					  calender.get(Calendar.MONTH), 
					  calender.get(Calendar.DATE));
		case TO_DATE_DIALOG_ID:
			calender.setTime(event.getEndDate());
			return new DatePickerDialog(this, toDateSetListener , 
					  calender.get(Calendar.YEAR), 
					  calender.get(Calendar.MONTH), 
					  calender.get(Calendar.DATE));
		case FROM_TIME_DIALOG_ID:
			calender.setTime(event.getStartDate());
			return new TimePickerDialog(this, fromTimeSetListener , 
					calender.get(Calendar.HOUR_OF_DAY), 
					calender.get(Calendar.MINUTE),
					false);
		case TO_TIME_DIALOG_ID:
			calender.setTime(event.getEndDate());
			return new TimePickerDialog(this, toTimeSetListener  , 
					calender.get(Calendar.HOUR_OF_DAY), 
					calender.get(Calendar.MINUTE),
					false);
		case REPEAT_DAILY_MENU:
			calender.setTime(event.getEndDate());
			tempDate=event.getEndDate();
			dialogRepeatDaily = new Dialog(this);
			dialogRepeatDaily.setContentView(R.layout.repeat_daily_custom_dialog_layout);
			dialogRepeatDaily.setTitle("REPEAT DAILY");
			Button buttonRepeatDailyDateFrom = (Button) dialogRepeatDaily.findViewById(R.id.buttonRepeatFixedDaysStartsOn);
			buttonRepeatDailyDateFrom.setText(getFormattedDate(event.getStartDate()));	
			buttonRepeatDailyFormattedEndDate =  (Button) dialogRepeatDaily.findViewById(R.id.buttonRepeatDailyFormattedEndDate);
			buttonRepeatDailyFormattedEndDate.setText(getFormattedDate(event.getEndDate()));
			buttonRepeatDailyFormattedEndDate.setOnClickListener(repeatDailyDialogEndDateButtonListener);
			dialogRepeatDaily.findViewById(R.id.buttonRepeatDailyDone).setOnClickListener(repreatDailyDialogDoneButtonListener);
			dialogRepeatDaily.findViewById(R.id.buttonRepeatDailyCancel).setOnClickListener(repreatDailyDialogCancelButtonListener );
			TextView textView = (TextView)dialogRepeatDaily.findViewById(R.id.textViewRepeatDailyTextMessage);
			textView.setText("Repeat by (1-30) days");
			editViewRepeatByDays = (EditText) dialogRepeatDaily.findViewById(R.id.editTextRepeatByDays);
			return dialogRepeatDaily;
		case REPEAT_DAILY_TO_DATE_DIALOG_ID:
			calender.setTime(event.getStartDate());
			return new DatePickerDialog(this, repeatDailyDialogEndDateDialogPickerButtonListener , 
					  calender.get(Calendar.YEAR), 
					  calender.get(Calendar.MONTH), 
					  calender.get(Calendar.DATE));
		case REPEAT_YEARLY_MENU:
			calender.setTime(event.getStartDate());
			tempDate=event.getEndDate();
			dialogyearlyRepeat = new Dialog(this);
			dialogyearlyRepeat.setContentView(R.layout.repeat_daily_custom_dialog_layout);
			dialogyearlyRepeat.setTitle("REPEAT YEARLY");
			Button buttonRepeatYearlyDateFrom = (Button) dialogyearlyRepeat.findViewById(R.id.buttonRepeatFixedDaysStartsOn);
			buttonRepeatYearlyDateFrom.setText(getFormattedDate(event.getStartDate()));			
			buttonRepeatDailyFormattedEndDate =  (Button) dialogyearlyRepeat.findViewById(R.id.buttonRepeatDailyFormattedEndDate);
			buttonRepeatDailyFormattedEndDate.setText(getFormattedDate(event.getEndDate()));
			buttonRepeatDailyFormattedEndDate.setOnClickListener(repeatDailyDialogEndDateButtonListener);
			dialogyearlyRepeat.findViewById(R.id.buttonRepeatDailyDone).setOnClickListener(repreatYearlyDialogDoneButtonListener );
			dialogyearlyRepeat.findViewById(R.id.buttonRepeatDailyCancel).setOnClickListener(repreatYearlyDialogCancelButtonListener );
			TextView textView1 = (TextView)dialogyearlyRepeat.findViewById(R.id.textViewRepeatDailyTextMessage);
			textView1.setText("Reapeat by (1-30) years");
			editViewRepeatByDays = (EditText) dialogyearlyRepeat.findViewById(R.id.editTextRepeatByDays);
			return dialogyearlyRepeat;
		case REPEAT_FIXED_WEEK_DAYS_DIALOG:
			tempDate=event.getEndDate();
			dialogRepeatFixedDaysInWeek = new Dialog(this);
			dialogRepeatFixedDaysInWeek.setContentView(R.layout.repeat_fixed_days_a_week_custom_dialog);
			dialogRepeatFixedDaysInWeek.setTitle("REPEAT EVERY MON, WED & FRI");
			buttonRepeatFixedDAysWeekStartDate = (Button) dialogRepeatFixedDaysInWeek.findViewById(R.id.buttonRepeatFixedDaysStartsOn);
			buttonRepeatFixedDAysWeekStartDate.setText(getFormattedDate(event.getStartDate()));
			buttonRepeatFixedDAysWeekEndDate = (Button) dialogRepeatFixedDaysInWeek.findViewById(R.id.buttonRepeatFixedDaysEndOn);
			buttonRepeatFixedDAysWeekEndDate.setText(getFormattedDate(event.getEndDate()));
			buttonRepeatFixedDAysWeekEndDate.setOnClickListener(repeatFixedDAysWeekEndDateButtonListener);
			buttonRepeatFixedDaysWeekDone = (Button) dialogRepeatFixedDaysInWeek.findViewById(R.id.buttonRepeatFixedDaysDialogDone);
			buttonRepeatFixedDaysWeekDone.setOnClickListener(repeatFixedDaysWeekDialogDoneButtonListener );
			buttonRepeatFixedDaysWeekCancel = (Button) dialogRepeatFixedDaysInWeek.findViewById(R.id.buttonRepeatFixedDaysDialogCancel);
			buttonRepeatFixedDaysWeekCancel.setOnClickListener(repeatFixedDaysWeekDialogCancelButtonListener  );
			return dialogRepeatFixedDaysInWeek;
		case REPEAT__WEEKLY_MON_WED_FRI_DIALOG_ID:
			calender.setTime(event.getEndDate());
			return new DatePickerDialog(this, repeatFixedWeekDaysDialogEndDateDialogPickerButtonListener , 
					  calender.get(Calendar.YEAR), 
					  calender.get(Calendar.MONTH), 
					  calender.get(Calendar.DATE));
		case REPEAT_ALL_WEEK_DAYS_DIALOG_ID:
			tempDate=event.getEndDate();
			dialogRepeatFixedDaysInWeek = new Dialog(this);
			dialogRepeatFixedDaysInWeek.setContentView(R.layout.repeat_fixed_days_a_week_custom_dialog);
			dialogRepeatFixedDaysInWeek.setTitle("REPEAT All WEEK DAYS- MON TO FRI");
			buttonRepeatFixedDAysWeekStartDate = (Button) dialogRepeatFixedDaysInWeek.findViewById(R.id.buttonRepeatFixedDaysStartsOn);
			buttonRepeatFixedDAysWeekStartDate.setText(getFormattedDate(event.getStartDate()));
			buttonRepeatFixedDAysWeekEndDate = (Button) dialogRepeatFixedDaysInWeek.findViewById(R.id.buttonRepeatFixedDaysEndOn);
			buttonRepeatFixedDAysWeekEndDate.setText(getFormattedDate(event.getEndDate()));
			buttonRepeatFixedDAysWeekEndDate.setOnClickListener(repeatFixedDAysWeekEndDateButtonListener);
			buttonRepeatFixedDaysWeekDone = (Button) dialogRepeatFixedDaysInWeek.findViewById(R.id.buttonRepeatFixedDaysDialogDone);
			buttonRepeatFixedDaysWeekDone.setOnClickListener(repeatWeekDaysMonToFriDialogDoneButtonListener );
			buttonRepeatFixedDaysWeekCancel = (Button) dialogRepeatFixedDaysInWeek.findViewById(R.id.buttonRepeatFixedDaysDialogCancel);
			buttonRepeatFixedDaysWeekCancel.setOnClickListener(repeatFixedDaysWeekDialogCancelButtonListener  );
			return dialogRepeatFixedDaysInWeek;
		case REPEAT_TUES_TUES_DIALOG_ID:
			tempDate=event.getEndDate();
			dialogRepeatFixedDaysInWeek = new Dialog(this);
			dialogRepeatFixedDaysInWeek.setContentView(R.layout.repeat_fixed_days_a_week_custom_dialog);
			dialogRepeatFixedDaysInWeek.setTitle("REPEAT EVERY TUES & THURS");
			buttonRepeatFixedDAysWeekStartDate = (Button) dialogRepeatFixedDaysInWeek.findViewById(R.id.buttonRepeatFixedDaysStartsOn);
			buttonRepeatFixedDAysWeekStartDate.setText(getFormattedDate(event.getStartDate()));
			buttonRepeatFixedDAysWeekEndDate = (Button) dialogRepeatFixedDaysInWeek.findViewById(R.id.buttonRepeatFixedDaysEndOn);
			buttonRepeatFixedDAysWeekEndDate.setText(getFormattedDate(event.getEndDate()));
			buttonRepeatFixedDAysWeekEndDate.setOnClickListener(repeatFixedDAysWeekEndDateButtonListener);
			buttonRepeatFixedDaysWeekDone = (Button) dialogRepeatFixedDaysInWeek.findViewById(R.id.buttonRepeatFixedDaysDialogDone);
			buttonRepeatFixedDaysWeekDone.setOnClickListener(repeatTuesAndThursDialogDoneButtonListener  );
			buttonRepeatFixedDaysWeekCancel = (Button) dialogRepeatFixedDaysInWeek.findViewById(R.id.buttonRepeatFixedDaysDialogCancel);
			buttonRepeatFixedDaysWeekCancel.setOnClickListener(repeatFixedDaysWeekDialogCancelButtonListener  );
			return dialogRepeatFixedDaysInWeek;
		case REPEAT_WEEKLY_DIALOG_ID:
			tempDate=event.getEndDate();
			dialogRepeatWeekly = new Dialog(this);
			dialogRepeatWeekly.setContentView(R.layout.repeat_weekly_custom_dialog_layout);
			dialogRepeatWeekly.setTitle("REPEAT WEEKLY");
			editViewRepeatByNumOfWeeks = (EditText) dialogRepeatWeekly.findViewById(R.id.editTextRepeatByWeeks);
			Button repeatWeeklyStartDateButton =  (Button) dialogRepeatWeekly.findViewById(R.id.buttonRepeatWeeklyStartsOn);
			repeatWeeklyStartDateButton.setText(getFormattedDate(event.getStartDate()));
			repeatWeeklyEndDAteButton = (Button) dialogRepeatWeekly.findViewById(R.id.buttonRepeatWeeklyEndOn);
			repeatWeeklyEndDAteButton.setText(getFormattedDate(event.getEndDate()));
			repeatWeeklyEndDAteButton.setOnClickListener(repeatWeeklyEndDateButtonListener);
			repeatWeeklyDialogDoneButton = (Button) dialogRepeatWeekly.findViewById(R.id.buttonRepeatWeeklyDialogDone);
			repeatWeeklyDialogDoneButton.setOnClickListener(repeatWeeklyDoneButtonListener);
			repeatWeeklyDialogCancelButton =  (Button) dialogRepeatWeekly.findViewById(R.id.buttonRepeatWeeklyDialogCancel);
			repeatWeeklyDialogCancelButton.setOnClickListener(repeatWeeklyCancelButtonListener);
			return dialogRepeatWeekly;
		case REPEAT_WEEKLY_TO_DATE_DIALOG_ID:
			calender.setTime(event.getEndDate());
			return new DatePickerDialog(this, repeatWeeklyDialogEndDateDialogPickerButtonListener , 
					  calender.get(Calendar.YEAR), 
					  calender.get(Calendar.MONTH), 
					  calender.get(Calendar.DATE));
		case REPEAT_MONTHLY_DIALOG_ID:
			dialogRepeatMonthly = new Dialog(this);
			tempDate=event.getEndDate();
			dialogRepeatMonthly.setContentView(R.layout.repeat_monthly_custom_dialog_layout);
			dialogRepeatMonthly.setTitle("REPEAT MONTHLY");
			editViewRepeatByNumOfMonths = (EditText) dialogRepeatMonthly.findViewById(R.id.editTextRepeatByMonths);
			Button repeatMonthlyStartDateButton =  (Button) dialogRepeatMonthly.findViewById(R.id.buttonRepeatMonthlyStartsOn);
			repeatMonthlyStartDateButton.setText(getFormattedDate(event.getStartDate()));
			repeatMonthlyEndDateButton = (Button) dialogRepeatMonthly.findViewById(R.id.buttonRepeatMonthlyEndsOn);
			repeatMonthlyEndDateButton.setOnClickListener(repeatMonthlyEndDAteButtonListener );
			repeatMonthlyDialogDoneButton = (Button) dialogRepeatMonthly.findViewById(R.id.buttonRepeatMonthlyDialogDone);
			repeatMonthlyDialogDoneButton.setOnClickListener(repeatMonthlyDialogDoneButtonListener );
			repeatMonthlyEndDateButton.setText(getFormattedDate(event.getEndDate()));
			repeatMonthlyDialogCancelButton = (Button) dialogRepeatMonthly.findViewById(R.id.buttonRepeatMonthlyDialogCancel);
			repeatMonthlyDialogCancelButton.setOnClickListener(repeatMonthlyDialogCancelButtonListener );
			return dialogRepeatMonthly;
		case REPEAT_MONTHLY_TO_DATE_DIALOG_ID:
			calender.setTime(event.getEndDate());
			return new DatePickerDialog(this, repeatMonthlyDialogEndDateDialogPickerButtonListener  , 
					  calender.get(Calendar.YEAR), 
					  calender.get(Calendar.MONTH), 
					  calender.get(Calendar.DATE));
		case REPEAT_DAILY_MULTIPLE_TIMES_DIALOG_ID:
			dialogRepeatMultipleTimesDaily = new Dialog(this);
			tempDate = event.getEndDate();
			dialogRepeatMultipleTimesDaily.setTitle("DAILY MULTIPLE TIMES");
			dialogRepeatMultipleTimesDaily.setContentView(R.layout.daily_multiple_times_custom_layout);
			buttonToAddMultipleTimingsADay = (Button) dialogRepeatMultipleTimesDaily.findViewById(R.id.buttonToAddMutipleTimingsADay);
			buttonToAddMultipleTimingsADay.setOnClickListener(buttonToAddMultipleTimingsADayListener );
			linearLayoutWithDateButtons = (LinearLayout) dialogRepeatMultipleTimesDaily.findViewById(R.id.linearLayoutWithDateButtons);
			Button buttonDailyMultipleTimesStartDate = (Button) dialogRepeatMultipleTimesDaily.findViewById(R.id.buttonMultipleDailyStartsOn);
			buttonDailyMultipleTimesStartDate.setText(getFormattedDate(event.getStartDate()));
			buttonDailyMultipleTimesEndDate = (Button) dialogRepeatMultipleTimesDaily.findViewById(R.id.buttonMultipleDailyEndDate);
			buttonDailyMultipleTimesEndDate.setText(getFormattedDate(event.getEndDate()));
			buttonDailyMultipleTimesEndDate.setOnClickListener(buttonDailyMultipleTimesEndDateListener );
			Button buttonDailyMultipleDialogDone = (Button) dialogRepeatMultipleTimesDaily.findViewById(R.id.buttonMultipleTmesDailyDone);
			buttonDailyMultipleDialogDone.setOnClickListener(buttonDailyMultipleDialogDoneListener );
			Button buttonDailyMultipleDialogCancel = (Button) dialogRepeatMultipleTimesDaily.findViewById(R.id.buttonMultipleTmesDailyCancel);
			buttonDailyMultipleDialogCancel.setOnClickListener(buttonDailyMultipleDialogCancelListener  );
			if(event.getRepeatsAtHours().size()>0) {
				for (Date date : event.getRepeatsAtHours()) {
				//TODO create and add button
				Button button = new Button(AddNewEventActivity.this);
				LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
				button.setLayoutParams(params);
				button.setText(getFormattedTime(date));
				//multipleTimings.addAll(event.getRepeatsAtHours());
				if(multipleTimings.size()>20) {
				//disable add 
					buttonToAddMultipleTimingsADay.setEnabled(false);
				}
				linearLayoutWithDateButtons.addView(button);
				}
			}
			return dialogRepeatMultipleTimesDaily;
		case DAILY_MULTIPLE_TIMES_SET_TIMING_DIALOG_ID:
			calender.setTime(event.getStartDate());
			return new TimePickerDialog(this, dailyMultipleTimesTimePickerSetButtonListener, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), false);
		case DAILY_MULTIPLE_TIMES_END_TIME_DIALOG_ID:
			calender.setTime(event.getEndDate());
			return new DatePickerDialog(this, dailyMultipleTimesEndDateTimePickerSetButtonListener   , 
					  calender.get(Calendar.YEAR), 
					  calender.get(Calendar.MONTH), 
					  calender.get(Calendar.DATE));
			
		default:
			return null;
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		
		switch (id) {
		case REPEAT_DAILY_MENU:
			if(event.getRepeatType()== RepeatType.DAILY_ONCE && event.getRepeatEvery()>0) 
			editViewRepeatByDays.setText(""+event.getRepeatEvery());
			break;
		case REPEAT_YEARLY_MENU:
			if(event.getRepeatType() == RepeatType.YEARLY && event.getRepeatEvery()>0)
				editViewRepeatByDays.setText(""+event.getRepeatEvery());
			break;
		case REPEAT_WEEKLY_DIALOG_ID:
			if(event.getRepeatType() == RepeatType.WEEKLY && event.getRepeatEvery()>0)
				editViewRepeatByNumOfWeeks.setText(""+event.getRepeatEvery());
			if(event.getRepeatsOnDays().size()>0)
				populateCheckBoxes();
			else {
				CheckBox checkBoxSunday =  (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxSunday);
				checkBoxSunday.setChecked(true);
				}
			break;
		case REPEAT_MONTHLY_DIALOG_ID:
			if(event.getRepeatByType() == RepeatByType.DAY_OF_THE_WEEK)
				radioButtonDayOfWeek.setChecked(true);
			if(event.getRepeatType() == RepeatType.MONTHLY && event.getRepeatEvery()>0)
				editViewRepeatByNumOfMonths.setText(""+event.getRepeatEvery());
			break;
			
		}
	}
	
	private void populateCheckBoxes() {
		
		CheckBox checkBox;
		if(event.getRepeatsOnDays().contains(DayOfWeek.SUNDAY))
		{
			checkBox = (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxSunday);
			checkBox.setChecked(true);
		}
		if(event.getRepeatsOnDays().contains(DayOfWeek.MONDAY))
		{
			checkBox = (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxMonday);
			checkBox.setChecked(true);
		}
		if(event.getRepeatsOnDays().contains(DayOfWeek.TUESDAY))
		{
			 checkBox = (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxTuesday);
			checkBox.setChecked(true);
		}
		if(event.getRepeatsOnDays().contains(DayOfWeek.WEDNUSDAY))
		{
			 checkBox = (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxWednesday);
			checkBox.setChecked(true);
		}
		if(event.getRepeatsOnDays().contains(DayOfWeek.THURSDAY))
		{
			 checkBox = (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxThursday);
			checkBox.setChecked(true);
		}
		if(event.getRepeatsOnDays().contains(DayOfWeek.FRIDAY))
		{
			 checkBox = (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxFriday);
			checkBox.setChecked(true);
		}
		if(event.getRepeatsOnDays().contains(DayOfWeek.SATURDAY))
		{
			 checkBox = (CheckBox) dialogRepeatWeekly.findViewById(R.id.checkBoxSaturday);
			checkBox.setChecked(true);
		}
		
	}

	//Helper Methods
	
	private void updateDateButtonsDisplay() {
	
		buttonFromDate.setText(getFormattedDate(event.getStartDate()));
		buttonToDate.setText(getFormattedDate(event.getEndDate()));
		buttonFromTime.setText(getFormattedTime(event.getStartDate()));
		buttonToTime.setText(getFormattedTime(event.getEndDate()));
	}
	
	private void createUIViews() {
		 buttonFromDate = (Button) findViewById(R.id.buttonFromDate);
		 buttonToDate = (Button) findViewById(R.id.buttonToDate);
		 buttonFromTime = (Button) findViewById(R.id.buttonFromTime);
		 buttonToTime = (Button) findViewById(R.id.buttonToTime);
		 checkBoxAllDay = (CheckBox) findViewById(R.id.checkBoxAllDay);
		 radioButtonRepeat = (RadioButton) findViewById(R.id.radioButtonRepeat);
		 linearLayoutAllDay = (LinearLayout) findViewById(R.id.linearLayoutAllDay);
		 linearLayoutRepeat = (LinearLayout) findViewById(R.id.linearLayoutRepeat);
		 textViewRepeatmessage = (TextView) findViewById(R.id.textViewRepreatMessage);
		 buttonAddNewEvent = (Button) findViewById(R.id.buttonAddNewEvent);
		 editTextLocation = (EditText) findViewById(R.id.editTextLocation);
	}

	private void populateUI() {
		
		//Populates event name EditView 
		EditText editTextName = (EditText) findViewById(R.id.editTextEventName);
		editTextName.setText(event.getName());
		
		//Populates Date, Time Buttons
		updateDateButtonsDisplay();
		buttonFromDate.setOnClickListener(buttonFromDateListener);
		buttonToDate.setOnClickListener(buttonToDateListener );
		buttonFromTime.setOnClickListener(buttonFromTimeListener );
		buttonToTime.setOnClickListener(buttonToTimeListener);
		if(event.getIsAllDayEvent()){
			buttonFromTime.setVisibility(View.GONE);
			buttonToTime.setVisibility(View.GONE);
		}
		//Populates All Day CheckBox
		checkBoxAllDay.setOnClickListener(allDayListener);
		linearLayoutAllDay.setOnClickListener(allDayListener);
		checkBoxAllDay.setChecked(event.getIsAllDayEvent());
		
		//Populates Repeat menu
		radioButtonRepeat.setOnClickListener(repeatListener);	
		linearLayoutRepeat.setOnClickListener(RepeatOnClickListener);
		radioButtonRepeat.setChecked(true);
		populateRepeatOptionSelected();
		
		editTextLocation.setText(event.getLocation());
		
		}
		
	private void populateRepeatOptionSelected() {
		switch (event.getRepeatType()) {
		case ONE_TIME_EVENT:
			textViewRepeatmessage.setText("One-time event");
			break;
		case DAILY_ONCE:
			textViewRepeatmessage.setText("Daily");
			break;
		case DAILY_MULTIPLE_TIMES:
			textViewRepeatmessage.setText("Daily multiple times");
			break;
		case EVERY_WEEK_DAY_MON_TO_FRI:
			textViewRepeatmessage.setText("Every week day- monday to friday");
			break;	
		case EVERY_MON_WED_FRI:
			textViewRepeatmessage.setText("Every monday, wednesday & friday");
			break;
		case EVERY_TUES_THURS:
			textViewRepeatmessage.setText("Every tuesdays & thursdays");
			break;
		case WEEKLY:
			textViewRepeatmessage.setText("Weekly");
			break;
		case MONTHLY:
			textViewRepeatmessage.setText("Monthly");
			break;
		case YEARLY:
			textViewRepeatmessage.setText("Yearly");
			break;
	} }

	private String getFormattedTime(Date date) {
		
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		
		int hours = calender.get(Calendar.HOUR);
		int minutes = calender.get(Calendar.MINUTE);
		int am_pm = calender.get(Calendar.AM_PM);
		String am_pm_value;
		if(am_pm ==0)
		am_pm_value = "AM";
		else  am_pm_value = "PM";
		if(hours==0) hours=12;
					
		return addPadding(hours)+":" + addPadding(minutes) + " "+am_pm_value;
	}
	
	private static String addPadding(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

	private String getFormattedDate(Date date) {
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		int dayOfWeek = calender.get(Calendar.DAY_OF_WEEK);
		String dayOfWeekValue = getDayOfWeek(dayOfWeek);
		int month = calender.get(Calendar.MONTH);
		String Month = getMonth(month);
		int day = calender.get(Calendar.DATE);
		int year = calender.get(Calendar.YEAR);
		return dayOfWeekValue+", " + day + " " + Month + " " +year;
	}

	private String getMonth(int month) {
		switch (month) {
		case 0:
			return "Jan";
		case 1:
			return "Feb";
		case 2:
			return "March";
		case 3:
			return "April";
		case 4:
			return "May";
		case 5:
			return "June";
		case 6:
			return "July";
		case 7:
			return "Aug";
		case 8:
			return "Sept";
		case 9:
			return "Oct";
		case 10:
			return "Nov";
		case 11:
			return "Dec";
		}
		return null;
	}

	private String getDayOfWeek(int dayOfWeek) {
		switch (dayOfWeek) {
		case 1:
			return "Sunday";
		case 2:
			return "Monday";
		case 3:
			return "Tuesday";
		case 4:
			return "Wednusday";
		case 5:
			return "Thursday";
		case 6:
			return "Friday";
		case 7:
			return "Saturday";
		}
		return null;
	}
	
	public static int tryParse(String text) {
		  try {
		    return Integer.parseInt(text);
		  } catch (Exception e) {
		    return -1;
		  }
		}


	private void populateEventInstanceInCaseOfAddNewEvent() {
		Intent intent = getIntent();
		int id = intent.getIntExtra("id", -1);
		Date startDate = new Date();
		Date endDate = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate(), startDate.getHours()+1, startDate.getMinutes());
		
		event = new Event(id, "", startDate, endDate, false, false, RepeatType.ONE_TIME_EVENT, 5, RepeatByType.DAY_OF_THE_MONTH, new ArrayList<DayOfWeek>(), new ArrayList<Date>(), "", Feedback.NO_FEEDBACK_YET);
	}

	private void updateAllDBVariables() {
		
		name = event.getName();
		startDateTime = getDBFormattedDate(event.getStartDate());
		endDateTime = getDBFormattedDate(event.getEndDate());
		if(event.getIsAllDayEvent()) isAllDayEvent =1; else isAllDayEvent=0;
		if(event.getIsReapetedEvent()) isRepeatedEvent=1; else isRepeatedEvent=0;
		repeatEvery = event.getRepeatEvery();
		
		switch (event.getRepeatType()) {
		case ONE_TIME_EVENT:
			repeatType=0;
			break;
		case DAILY_ONCE:
			repeatType=1;
			break;
		case DAILY_MULTIPLE_TIMES:
			repeatType=2;
			break;
		case  EVERY_WEEK_DAY_MON_TO_FRI:
			repeatType=3;
			break;
		case EVERY_MON_WED_FRI:
			repeatType=4;
			break;
		case EVERY_TUES_THURS:
			repeatType=5;
			break;
		case WEEKLY:
			repeatType=6;
			break;
		case MONTHLY:
			repeatType=7;
			break;
		case YEARLY:
			repeatType=8;
			break;
	}
	
	switch (event.getRepeatByType()) {
	case DAY_OF_THE_MONTH:
		repeatByType=0;
		break;
	case DAY_OF_THE_WEEK:
		repeatByType=1;
		break;
	}
	
	repeatOnDays = getDBFormattedRepeatOnDays();
	repeatAtHours = getDBFormattedRepeatAtHours();
	repeatEndType=0;
	
	location = event.getLocation().trim();
	switch (event.getFeedback()) {
	case NO_FEEDBACK_YET:
		feedback =0;
		break;
	case COMPLETED_LATELY:
		feedback=1;
		break;
	case COMPLETED_ON_TIME:
		feedback=2;
		break;
	case MISSED_COMPLETELY:
		feedback=3;
		break;
	}
	
	}

	private String getDBFormattedRepeatAtHours() {
		repeatAtHours="";
			for (Date date : multipleTimings) {
				repeatAtHours += getDBFormattedDate(date) + "_"; 
			}
		return repeatAtHours;
	}

	private String getDBFormattedRepeatOnDays() {
		repeatOnDays="";
		for (DayOfWeek dayOfWeek : event.getRepeatsOnDays()) {
			switch (dayOfWeek) {
			case SUNDAY:
				repeatOnDays += "1_";
				break;
			case MONDAY:
				repeatOnDays += "2_";
				break;
			case TUESDAY:
				repeatOnDays += "3_";
				break;
			case WEDNUSDAY:
				repeatOnDays += "4_";
				break;
			case THURSDAY:
				repeatOnDays += "5_";
				break;
			case FRIDAY:
				repeatOnDays += "6_";
				break;
			case SATURDAY:
				repeatOnDays += "7_";
				break;
			}
			 
		}
	return repeatOnDays;
	}
	private Date getDateFromString(String dateAndTime) {
		Date date = null; 
		DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    		try {
    			 date =  iso8601Format.parse(dateAndTime) ;
    			 Calendar calendar = Calendar.getInstance();
    			 calendar.setTime(date);
    			 //calendar.add(Calendar.MONTH, 1);
    			 date = calendar.getTime();
    			} 
    		catch (ParseException e) {
    		Log.d("Personal HealthCheck", e.toString());
    		}
    		return date;
	}

	
}