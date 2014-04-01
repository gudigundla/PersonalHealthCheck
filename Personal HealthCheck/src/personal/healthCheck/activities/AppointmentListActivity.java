/*package personal.healthCheck.activities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import personal.healthCheck.model.Appointment;
import personal.healthCheck.model.MyArrayAdapter;
import personal.healthCheck.model.MySQLiteOpenHelper;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class AppointmentListActivity extends ListActivity {
	
	private final int CONFIRM_DELETE =0;
	List<Appointment> Appointments;

	private OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			
			Intent intent = getRequiredIntent(position-1); 			
			startActivity(intent);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.appointments_list_activity_layout);

	ListView listView = getListView();
	listView.setTextFilterEnabled(true);
	View header = getLayoutInflater().inflate(R.layout.appointmnets_header_layout, null);
	
	Button buttonAddNewAppointment = (Button) header.findViewById(R.id.buttonAddNewAppointment);
	OnClickListener buttonAddNewAppointmentListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(AppointmentListActivity.this, PersonalHealthCheckMainActivity.class);
			startActivity(intent);
			
		}
	};
	buttonAddNewAppointment.setOnClickListener(buttonAddNewAppointmentListener );
	
	listView.addHeaderView(header, null, false);

	listView.setOnItemClickListener(itemClickListener );
	registerForContextMenu(getListView());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//setContentView(R.layout.appointments_list_activity_layout);
		Appointments = getAppointments();
		
		sortAppointmentsByDateAndTime();
		
		//ListAdapter adapter = new ArrayAdapter<Appointment>(this, R.layout.hellolistview,Appointments);
		ListAdapter adapter = new MyArrayAdapter(this, Appointments);
		setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.listview_context_menu_layout, menu);
	}
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.d("Personal HealthCheck", "bad menuInfo", e);
            return false;
        }
        int position = (int) getListAdapter().getItemId(info.position);
        Log.d("", "id = " + position);
		
		switch (item.getItemId()) {
		case R.id.itemEdit:
			Intent intent = getRequiredIntent(position-1);
			startActivity(intent);
			break;
		case R.id.itemDelete:
			showDialog(CONFIRM_DELETE);

		default:
			break;
		}
        return true;
    }
	
	@Override
	protected Dialog onCreateDialog(int id) {
	
	switch (id) {
	case CONFIRM_DELETE:
		AlertDialog.Builder builder = new Builder(this);
		return builder
			   .setMessage("Click OK to confirm delete !! ")
			   .setNegativeButton("NO", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(AppointmentListActivity.this, "Deleted !!", Toast.LENGTH_SHORT).show();
					
				}
			}).setPositiveButton("YES", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(AppointmentListActivity.this, "Great !!", Toast.LENGTH_SHORT).show();
				}
			})
			.create();
	}
	return null;
	}
	
	private List<Appointment> getAppointments() {
		List<Appointment> appointments = new ArrayList<Appointment>() ;

		MySQLiteOpenHelper myDbHelper = new MySQLiteOpenHelper(this);
		try {			
	  		myDbHelper.openDataBase();
	  		Log.d("Personal HealthCheck-2","Personal HealthCheck database instance created !!");
	  		
			String sql = "select * from Appointments";
	 		Cursor cursor= myDbHelper.getMyDataBase().rawQuery(sql, null);
	 		Log.d("ALERT", "Step 1");
	 		cursor.moveToFirst();
	 		
	        while (cursor.isAfterLast() == false) {
	        	appointments.add(new Appointment(cursor.getString(1), cursor.getInt(0), cursor.getString(2)));
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
		return appointments;
	}
	
	private void sortAppointmentsByDateAndTime() {
		Collections.sort(Appointments, new Comparator<Appointment>(){
			 
            public int compare(Appointment appointment1, Appointment appointment2) {
               Date date1 = getDateFromString(appointment1.getDateAndTime());
               Date date2 = getDateFromString(appointment2.getDateAndTime());
              
               return date1.compareTo(date2);
          }

	private Date getDateFromString(String dateAndTime) {
				Date date = null ; 
				DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        		try {
	        			 date =  iso8601Format.parse(dateAndTime) ;
	        			} 
	        		catch (ParseException e) {
	        		Log.d("Personal HealthCheck", e.toString());
	        		}
	        		return date;
			}
	});
		
	}

	private Intent getRequiredIntent(int position) {
		Intent intent = new Intent(AppointmentListActivity.this, UpdateAppointmentActivity.class);
		intent.putExtra("name", Appointments.get(position).getName());
		intent.putExtra("id", Appointments.get(position).getId());
		intent.putExtra("dateAndTime", Appointments.get(position).getDateAndTime());
		
		return intent;
	}
}*/