package personal.healthCheck.model;

import java.util.List;

import personal.healthCheck.activities.R;
import android.app.Activity;
import android.graphics.Color;
import android.opengl.Visibility;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

//Performance optimised using ConvertView and Holder Pattern 
public class MyArrayAdapter extends ArrayAdapter {

	private final Activity context;
	private final List<IndividualEvent> events;

	static class ViewHolder {
		public TextView textViewEventname;
		public TextView textViewEventTime;
		public TextView textViewEventLocation;
}

	public MyArrayAdapter(Activity context, List<IndividualEvent> events) {
		super(context, R.layout.appointments_listview_list_item_layout, events);
		this.context = context;
		this.events = events;
	}
	
	public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public int getCount() {
        return events.size();
    }


	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}
	
	@Override
	public boolean isEnabled(int position) {
		if(events.get(position).getIsEnabledEvent())
			return true;
		
		return false;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
	//	if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.events_list_view_list_item_layout, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.textViewEventname= (TextView) rowView.findViewById(R.id.textViewEventName);
			viewHolder.textViewEventTime = (TextView) rowView.findViewById(R.id.textViewEventTime);
			viewHolder.textViewEventLocation = (TextView) rowView.findViewById(R.id.textViewEventLocation);
			rowView.setTag(viewHolder);
//		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		if(events.get(position).getIsEnabledEvent()) {
			holder.textViewEventname.setText(events.get(position).getName()) ;
			holder.textViewEventLocation.setText(events.get(position).getLocation());
			holder.textViewEventTime.setText(events.get(position).getDisplayTime());
		}
		else {
			//holder.textViewEventname.
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			param.setMargins(0, 5, 0, 0);
			holder.textViewEventname.setText(events.get(position).getFormattedDate(events.get(position).getStartDate())) ;
			holder.textViewEventname.setBackgroundColor(Color.GRAY);
			holder.textViewEventLocation.setVisibility(View.GONE);
			holder.textViewEventTime.setVisibility(View.GONE);
		}

		/*//set image based on the appointment priority field
		String s = names[position];
		holder.text.setText(s);
		if (s.startsWith("Windows7") || s.startsWith("iPhone")
				|| s.startsWith("Solaris")) {
			holder.image.setImageResource(R.drawable.no);
		} else {
			holder.image.setImageResource(R.drawable.ok);
		}
*/
		return rowView;
	}
	
	public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        // empty implementation
      }

}