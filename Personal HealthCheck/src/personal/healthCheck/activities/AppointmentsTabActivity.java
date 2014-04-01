package personal.healthCheck.activities;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

//import org.achartengine.GraphicalView;

import personal.healthCheck.model.DayOfWeek;
import personal.healthCheck.model.Event;
import personal.healthCheck.model.Feedback;
import personal.healthCheck.model.IndividualEvent;
import personal.healthCheck.model.MyArrayAdapter;
import personal.healthCheck.model.MySQLiteOpenHelper;
import personal.healthCheck.model.PieChartStats;
import personal.healthCheck.model.RepeatByType;
import personal.healthCheck.model.RepeatType;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

public class AppointmentsTabActivity extends TabActivity implements OnClickListener {

	private static final String LIST1_TAB_TAG = "Events";
	private static final String LIST3_TAB_TAG = "Monthly Stats";
	private static int year;
	
	// The listView, linearLayout tabs
    private ListView listViewAppointments;
    private LinearLayout eventsLinearLayout;

    private List<Event> Events;
    private static List<IndividualEvent> events;
    private static List<IndividualEvent> lableEvents;
    private TabHost tabHost;
//    private GraphicalView graphicalView;
    
    private TextView textViewYear;
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	private OnItemClickListener listViewEventsListItemOnClickListsner = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
		//	Toast.makeText(AppointmentsTabActivity.this, position+ " ", Toast.LENGTH_SHORT).show();
			openContextMenu(view);
		}
	};
	private OnClickListener textViewPreviousListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			year--;
			Toast.makeText(AppointmentsTabActivity.this, ""+ year , Toast.LENGTH_SHORT).show();
			setYearTextView();
			updateListView();
		}

	};
	private OnClickListener textViewNextListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			year++;
			Toast.makeText(AppointmentsTabActivity.this, ""+ year , Toast.LENGTH_SHORT).show();
			setYearTextView();
			updateListView();
		}
	};

	private void updateListView() {
		Events = getEvents();
  		events = getIndividualEvents();
  		sortAppointmentsByDateAndTime();
  		addLablesInBetweenEvents();
  		events.addAll(lableEvents);
  		sortAppointmentsByDateAndTime();
  		int position = getTodaysEventsPosition();
  		//listViewAppointments.
  		listViewAppointments.setAdapter(new MyArrayAdapter(AppointmentsTabActivity.this, events));
  		listViewAppointments.setSelectionFromTop(position,0);
  		listViewAppointments.setOnItemClickListener(listViewEventsListItemOnClickListsner );
	}

	
		@Override
        public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
            //Copies database from Assets folder to device ONLY when the app is run for the first time 
            CopyDataBaseToDevice();
        }

    	@Override
    	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    		super.onCreateContextMenu(menu, v, menuInfo);
    
    		/*AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    		Adapter adapter = listViewAppointments.getAdapter();
    		Object item = adapter.getItem(info.position);*/
    		
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.events_list_view_items_context_menu_layout, menu);

    	}
    	int tempPosition = -1;
    	@Override
    	public boolean onContextItemSelected(MenuItem item) {
        	
    	
    		switch (item.getItemId()) {
			case R.id.itemAddNewEvent:
				Intent intentAdd = new Intent(AppointmentsTabActivity.this, AddNewEventActivity.class);
				intentAdd.putExtra("AddOrUpdate", 1);
				startActivity(intentAdd);
				break;
			case R.id.itemDeleteEvent:
				AdapterContextMenuInfo infoDelete = (AdapterContextMenuInfo) item.getMenuInfo();
				tempPosition = infoDelete.position;
				//Object itemClicked = listViewAppointments.getAdapter().getItem(info.position);
				item.setTitle("Delete- "+ events.get(tempPosition).getName());
				item.getSubMenu().findItem(R.id.itemDeleteAllAfterThis).setTitle("All occurences after- " +  getFormattedDate(events.get(tempPosition).getStartDate()));
				break;
			case R.id.itemEditEvent:
				AdapterContextMenuInfo infoEdit = (AdapterContextMenuInfo) item.getMenuInfo();
				Intent intentEdit = new Intent(AppointmentsTabActivity.this, AddNewEventActivity.class);
				intentEdit.putExtra("AddOrUpdate", 2);
				intentEdit.putExtra("idDB", events.get(infoEdit.position).getIdDB());
				startActivity(intentEdit);
				break;
			case R.id.itemDeleteAllAfterThis:
				int rowsEffected= deleteAllOccurencesAfterThis(tempPosition);
				//Toast.makeText(AddNewEventActivity.this, "insertRowIntoDatabase !!", Toast.LENGTH_SHORT).show();
				if(rowsEffected ==1) {
					updateListView();
					Toast.makeText(AppointmentsTabActivity.this, "deleted all further occurences", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.itemDeleteWhole:
				int rowsDeleted= deleteWholeEvent(tempPosition);
				if(rowsDeleted >0) {
				updateListView();
				Toast.makeText(AppointmentsTabActivity.this, "deleted all events", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(AppointmentsTabActivity.this, "deleted none", Toast.LENGTH_SHORT).show();
				break;
		}
    		return super.onContextItemSelected(item);
    	}
    	
    	private int deleteWholeEvent(int tempPosition) {
			MySQLiteOpenHelper myDbHelper = new MySQLiteOpenHelper(AppointmentsTabActivity.this);
			myDbHelper.openDataBase();
	  		Log.d("Personal HealthCheck-2","Personal HealthCheck database opened !!");

	  	    int rowsEffected= myDbHelper.getMyDataBase().delete("Events", "_id=" + events.get(tempPosition).getIdDB() ,null);
	  	    myDbHelper.close();
	  	  Log.d("Personal HealthCheck","Database Updated !!");

			return rowsEffected;
		}

		private int deleteAllOccurencesAfterThis(int tempPosition) {
			MySQLiteOpenHelper myDbHelper = new MySQLiteOpenHelper(AppointmentsTabActivity.this);
			myDbHelper.openDataBase();
	  		Log.d("Personal HealthCheck-2","Personal HealthCheck database opened !!");

	  		Date startDate = events.get(tempPosition).getStartDate();
	  		Date endDate = events.get(tempPosition).getEndDate();
	  		Date reqDate = new Date(startDate.getYear(), startDate.getMonth(), startDate.getDate(), endDate.getHours(), endDate.getMinutes(), endDate.getSeconds());
	  		
	  		ContentValues updatedValues = new ContentValues(); 
			updatedValues.put("EndDateTime", getDBFormattedDate(reqDate));
	  		
	  	    int rowsEffected= myDbHelper.getMyDataBase().update("Events", updatedValues, "_id" + "=" + events.get(tempPosition).getIdDB(), null);
	  	    myDbHelper.close();
	  	  Log.d("Personal HealthCheck","Database Updated !!");

			return rowsEffected;
		}
		
		protected String getDBFormattedDate(Date date) {
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return formater.format(date);
			
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

    	
    	public String getFormattedDate(Date date) {
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
    	
    	private int getTodaysEventsPosition() {
    		Calendar calendar = Calendar.getInstance();
    		Date todaysDate = new Date(year-1900 ,calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0);
    		for (int i = 0; i < events.size(); i++) {
    			if(todaysDate.compareTo(events.get(i).getStartDate())<=0)
    				return i;
			}
    		return 0;
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


		private void addLablesInBetweenEvents() {
    		lableEvents = new ArrayList<IndividualEvent>();
    		Date tempDate = null;
    		
    		if(events.size()>0) {
        		tempDate = events.get(0).getStartDate();
    			Calendar calendar = Calendar.getInstance();
				calendar.setTime(events.get(0).getStartDate());
				Date date = new Date(calendar.get(Calendar.YEAR) -1900 ,calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0);
				IndividualEvent indiEvent = new IndividualEvent(0, 0, "", "", date, date, false, false);
				lableEvents.add(indiEvent);
    		}
    		for (IndividualEvent event : events) {
			if(!areSameDayEvents(tempDate, event.getStartDate())){
    			Calendar calendar = Calendar.getInstance();
				calendar.setTime(event.getStartDate());
				Date date = new Date(calendar.get(Calendar.YEAR) -1900 ,calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),0,0);
				int idDB =0;
//				if(areSameDayEvents(date, todaysDate))
//					idDB = -1;
				IndividualEvent indiEvent = new IndividualEvent(idDB, 0, "label", "Label", date, date, false, false);
				lableEvents.add(indiEvent);
				tempDate= event.getStartDate();	
    		}}}

		private boolean areSameDayEvents(Date tempDate, Date startDate) {
			
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(tempDate);
			
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(startDate);
			
			if(calendar1.get(Calendar.DAY_OF_MONTH) != calendar2.get(Calendar.DAY_OF_MONTH))
				return false;
			else if(calendar1.get(Calendar.MONTH) != calendar2.get(Calendar.MONTH)) 
				return false;
			else if(calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR))
				return true;
			
			return false;
		}

		@Override
    	protected void onResume() {
    		super.onResume();
            setContentView(R.layout.appointments_tab_activity_layout);
            year = Calendar.getInstance().get(Calendar.YEAR);
            
      		tabHost = getTabHost();
      		
      		Events = getEvents();
      		events = getIndividualEvents();
      		sortAppointmentsByDateAndTime();
      		addLablesInBetweenEvents();
      		events.addAll(lableEvents);
      		sortAppointmentsByDateAndTime();
      		int position = getTodaysEventsPosition();
      		eventsLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutEvents);
      		      		
      		TextView textViewPrevious = (TextView) findViewById(R.id.textViewPrevious);
      		textViewPrevious.setOnClickListener(textViewPreviousListener );
      		
      		TextView textViewNext = (TextView) findViewById(R.id.textViewNext);
      		textViewNext.setOnClickListener(textViewNextListener  );
      		
      		// setup list view 1
      		listViewAppointments = (ListView) findViewById(R.id.list);
      		listViewAppointments.setAdapter(new MyArrayAdapter(this, events));
      		listViewAppointments.setSelectionFromTop(position,0);
      		listViewAppointments.setOnItemClickListener(listViewEventsListItemOnClickListsner );
      		//listViewAppointments.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, events));
      		
      		registerForContextMenu(listViewAppointments);
      		
      /*		//monthly stats tab
      		if(graphicalView==null) {
      		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.pieChartForMonth);
      		graphicalView = new PieChartStats().getPieChartForMonth(this); 
      		linearLayout.addView(graphicalView, new LayoutParams
      				(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
      		}
      		else graphicalView.repaint();
 */     			
      		
      		// add views to tab host
      		tabHost.addTab(tabHost.newTabSpec(LIST1_TAB_TAG).setIndicator(LIST1_TAB_TAG).setContent(new TabContentFactory() {
      			public View createTabContent(String arg0) {
      				return eventsLinearLayout;
      			}
      		}));
      		
      		tabHost.addTab(tabHost.newTabSpec(LIST3_TAB_TAG).setIndicator(LIST3_TAB_TAG).setContent(R.id.linearLayoutMonthlyStats));
      		setYearTextView();
      	}

		private void setYearTextView() {
			textViewYear = (TextView) findViewById(R.id.textViewYear);
			textViewYear.setText(""+ year);
		}
    	
    	private List<IndividualEvent> getIndividualEvents() {
			events = new ArrayList<IndividualEvent>();
			
			for (Event event : Events) {
				
					//indiEvent = new IndividualEvent();
					switch (event.getRepeatType()) {
					case DAILY_ONCE:
						//Toast.makeText(AppointmentsTabActivity.this, event.getRepeatEvery() + " Daily event", Toast.LENGTH_SHORT).show();
						for (Date date = event.getStartDate(); date.compareTo(event.getEndDate())<=0 ; ) {
							if(date.getYear() + 1900 == year) {
							IndividualEvent indiEvent = new IndividualEvent(event.getId(), Events.indexOf(event), 
									event.getName(), event.getLocation(), date, event.getEndDate(), event.getIsAllDayEvent(), true);					
							
							events.add(indiEvent);
							}
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							calendar.add(Calendar.DAY_OF_MONTH, event.getRepeatEvery());
							date = calendar.getTime();
						}
						break;
					case EVERY_MON_WED_FRI:
						for (Date date = event.getStartDate(); date.compareTo(event.getEndDate())<=0 ; ) {
							
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
							if(dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY)
							{
								if(date.getYear() + 1900 == year) {
								IndividualEvent indiEvent = new IndividualEvent(event.getId(), Events.indexOf(event), 
										event.getName(), event.getLocation(), date, event.getEndDate(), event.getIsAllDayEvent(), true);					
								events.add(indiEvent);
								}
							}
							calendar.add(Calendar.DAY_OF_MONTH, 1);
							date = calendar.getTime();
						}
						break;
					case EVERY_TUES_THURS:
						for (Date date = event.getStartDate(); date.compareTo(event.getEndDate())<=0 ; ) {
							
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
							if(dayOfWeek == Calendar.TUESDAY || dayOfWeek == Calendar.THURSDAY)
							{
								if(date.getYear() + 1900 == year) {
								IndividualEvent indiEvent = new IndividualEvent(event.getId(), Events.indexOf(event), 
										event.getName(), event.getLocation(), date, event.getEndDate(), event.getIsAllDayEvent(), true);					
								events.add(indiEvent);
								}
							}
							calendar.add(Calendar.DAY_OF_MONTH, 1);
							date = calendar.getTime();
						}
						break;
					case EVERY_WEEK_DAY_MON_TO_FRI:
						for (Date date = event.getStartDate(); date.compareTo(event.getEndDate())<=0 ; ) {
							
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
							if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY)
							{
								if(date.getYear() + 1900 == year) {
								IndividualEvent indiEvent = new IndividualEvent(event.getId(), Events.indexOf(event), 
										event.getName(), event.getLocation(), date, event.getEndDate(), event.getIsAllDayEvent(), true);					
								events.add(indiEvent);
								}
							}
							calendar.add(Calendar.DAY_OF_MONTH, 1);
							date = calendar.getTime();
						}
						break;
					case WEEKLY:
						for (Date date = event.getStartDate(); date.compareTo(event.getEndDate())<=0 ; ) {
							
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
							int reqDayOdWeek =0;
							for (DayOfWeek day : event.getRepeatsOnDays()) {
								switch (day) {
								case SUNDAY:
									reqDayOdWeek=Calendar.SUNDAY;
									break;
								case MONDAY:
									reqDayOdWeek=Calendar.MONDAY;
									break;
								case TUESDAY:
									reqDayOdWeek=Calendar.TUESDAY;
									break;
								case WEDNUSDAY:
									reqDayOdWeek=Calendar.WEDNESDAY;
									break;
								case THURSDAY:
									reqDayOdWeek=Calendar.THURSDAY;
									break;
								case FRIDAY:
									reqDayOdWeek=Calendar.FRIDAY;
									break;
								case SATURDAY:
									reqDayOdWeek=Calendar.SATURDAY;
									break;
								}
							if(dayOfWeek == reqDayOdWeek ) {
								if(date.getYear() + 1900 == year) {
								IndividualEvent indiEvent = new IndividualEvent(event.getId(), Events.indexOf(event), 
										event.getName(), event.getLocation(), date, event.getEndDate(), event.getIsAllDayEvent(), true);					
								events.add(indiEvent);
								}
							}}
							if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
							calendar.add(Calendar.DAY_OF_MONTH, (event.getRepeatEvery() - 1)*7 +1);
							else 
							calendar.add(Calendar.DAY_OF_MONTH, 1);
							date = calendar.getTime();
						}
						break;
					case MONTHLY:
						//int reqDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
						switch (event.getRepeatByType()) {
						case DAY_OF_THE_MONTH:
							//Toast.makeText(AppointmentsTabActivity.this, "Day of month", Toast.LENGTH_SHORT).show();
							for (Date date = event.getStartDate(); date.compareTo(event.getEndDate())<0; ) {
								Calendar calendar1 = Calendar.getInstance();
								calendar1.setTime(date);
								date = calendar1.getTime();
								if(date.getYear() + 1900 == year) {
									IndividualEvent indiEvent = new IndividualEvent(event.getId(), Events.indexOf(event), 
											event.getName(), event.getLocation(), date, event.getEndDate(), event.getIsAllDayEvent(), true);					
								events.add(indiEvent);
								}
							//	Toast.makeText(AppointmentsTabActivity.this, date.toString() + "", Toast.LENGTH_SHORT).show();
								calendar1.add(Calendar.MONTH , event.getRepeatEvery());
								date = calendar1.getTime();
							}
							break;
					case DAY_OF_THE_WEEK:
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(event.getStartDate());
						int reqWeekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
						int reqDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
						int startMonth = calendar.get(Calendar.MONTH);
							for (Date date = event.getStartDate(); date.compareTo(event.getEndDate())<=0 ; ) {
								//Toast.makeText(AppointmentsTabActivity.this, date.toString() + "", Toast.LENGTH_SHORT).show();
								Calendar calendar1 = Calendar.getInstance();
								calendar1.setTime(date);
								int weekOfMonth = calendar1.get(Calendar.WEEK_OF_MONTH);
								int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK);
								int currentMonth = calendar1.get(Calendar.MONTH);
								int diff = currentMonth- startMonth;
								if(dayOfWeek == reqDayOfWeek && weekOfMonth == reqWeekOfMonth && diff % event.getRepeatEvery() == 0) {
									if(date.getYear() + 1900 == year) {
									IndividualEvent indiEvent = new IndividualEvent(event.getId(), Events.indexOf(event), 
											event.getName(), event.getLocation(), date, event.getEndDate(), event.getIsAllDayEvent(), true);					
									events.add(indiEvent);
									}
								}
								
								calendar1.add(Calendar.WEEK_OF_MONTH, 1);
								date = calendar1.getTime();
							}
							break;
						}
						break;
					case YEARLY:
						for (Date date = event.getStartDate(); date.compareTo(event.getEndDate())<=0 ; ) {
							if(date.getYear() + 1900 == year) {
							IndividualEvent indiEvent = new IndividualEvent(event.getId(), Events.indexOf(event), 
									event.getName(), event.getLocation(), date, event.getEndDate(), event.getIsAllDayEvent(), true);					
							events.add(indiEvent);
							}
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							calendar.add(Calendar.YEAR, event.getRepeatEvery());
							date = calendar.getTime();
						}
						break;
					case DAILY_MULTIPLE_TIMES:
						for (Date date = event.getStartDate(); date.compareTo(event.getEndDate())<=0 ; ) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(date);
							
							for (Date reqDate : event.getRepeatsAtHours()) {
								Calendar reqCalendar = Calendar.getInstance();
								reqCalendar.setTime(reqDate);
								
							Date startDate = new Date(calendar.get(Calendar.YEAR) -1900, calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),
									 reqCalendar.get(Calendar.HOUR_OF_DAY), reqCalendar.get(Calendar.MINUTE));
							Date endDate = new Date(calendar.get(Calendar.YEAR)-1900, calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),
									reqCalendar.get(Calendar.HOUR_OF_DAY)+1, reqCalendar.get(Calendar.MINUTE));
								
							if(startDate.getYear() + 1900 == year) {
							IndividualEvent indiEvent = new IndividualEvent(event.getId(), Events.indexOf(event), event.getName(), event.getLocation(), startDate,
										endDate, event.getIsAllDayEvent(), true);
								events.add(indiEvent);
							}
							}
							
/*							if(date.getYear() + 1900 == year) {
							IndividualEvent indiEvent = new IndividualEvent(event.getId(), Events.indexOf(event), 
									event.getName(), event.getLocation(), date, event.getEndDate(), event.getIsAllDayEvent(), true);					
							events.add(indiEvent);
							}
*/							calendar.add(Calendar.DAY_OF_MONTH, 1);
							date = calendar.getTime();
						}
					break;
					case ONE_TIME_EVENT:
						//Toast.makeText(AppointmentsTabActivity.this, "Not a repeated event", Toast.LENGTH_SHORT).show();
						if(event.getStartDate().getYear() + 1900 == year) {
						IndividualEvent indiEvent = new IndividualEvent(event.getId(), Events.indexOf(event), event.getName(), event.getLocation(), event.getStartDate(),
														event.getEndDate(), event.getIsAllDayEvent(), true);
						events.add(indiEvent);	
						}
						break;
					}
				}
		    		
    		return events;
		}

		@Override
    	public boolean onCreateOptionsMenu(Menu menu) {
    		MenuInflater menuInflater = getMenuInflater();
    		menuInflater.inflate(R.menu.options_menu_for_tabbed_activity, menu);
    		return true;
    	}
    	
    	@Override
    	public boolean onOptionsItemSelected(MenuItem item) {
    	
    		switch (item.getItemId()) {
			case R.id.itemAddNewEvent:
				Intent intentAdd = new Intent(AppointmentsTabActivity.this, AddNewEventActivity.class);
				intentAdd.putExtra("AddOrUpdate", 1);
				startActivity(intentAdd);
				break;
			case R.id.itemExitApp:
				finish();
    	}
    		
    		return super.onOptionsItemSelected(item);
    	}
    	
    	private List<Event> getEvents() {
    		List<Event> events = new ArrayList<Event>() ;
    		MySQLiteOpenHelper myDbHelper = new MySQLiteOpenHelper(this);
    		try {			
    	  		myDbHelper.openDataBase();
    	  		Log.d("Personal HealthCheck-2","Personal HealthCheck database opened !!");
    	  		
    			String sql = "select * from Events";
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
    	        	
    	        	Event event = new Event(cursor.getInt(0), cursor.getString(1), 
    	        			getDateFromString(cursor.getString(2)), getDateFromString(cursor.getString(3)), isAllDayEvent, isRepeatedEvent, repeatType, 
    	        			cursor.getInt(7), repeatByType, repeatOnDays, repeatOnHours, cursor.getString(12),Feedback.NO_FEEDBACK_YET );
    	        	
    	        	Log.d("Personal HealthCheck", cursor.getString(1));
            		events.add(event);
    	        	//}
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
    		return events;
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

    	public static int tryParse(String text) {
  		  try {
  		    return Integer.parseInt(text);
  		  } catch (Exception e) {
  		    return -1;
  		  }
  		}
    	
    	private  static void sortAppointmentsByDateAndTime() {
    		Collections.sort(events, new Comparator<IndividualEvent>(){
    			 
                public int compare(IndividualEvent event1, IndividualEvent event2) {
                
                	return event1.getStartDate().compareTo(event2.getStartDate());
                   
              } }); }

	
    	
}