package personal.healthCheck.model;

import java.util.Calendar;
import java.util.Date;

public class IndividualEvent {

	int idDB;
	int position;
	String name;
	String location;
	Date startDate;
	Date endDate;
	Boolean isAllDayEvent;
	Boolean isEnabledEvent;
	
	public IndividualEvent(int idDB, int position, String name,
			String location, Date startDate, Date endDate,
			Boolean isAllDayEvent, Boolean isEnabledEvent) {
		super();
		this.idDB = idDB;
		this.position = position;
		this.name = name;
		this.location = location;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isAllDayEvent = isAllDayEvent;
		this.isEnabledEvent = isEnabledEvent;
	}

	public int getIdDB() {
		return idDB;
	}

	public void setIdDB(int idDB) {
		this.idDB = idDB;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Boolean getIsAllDayEvent() {
		return isAllDayEvent;
	}

	public void setIsAllDayEvent(Boolean isAllDayEvent) {
		this.isAllDayEvent = isAllDayEvent;
	}

	public Boolean getIsEnabledEvent() {
		return isEnabledEvent;
	}

	public void setIsEnabledEvent(Boolean isEnabledEvent) {
		this.isEnabledEvent = isEnabledEvent;
	}

	public String getDisplayTime(){
		//return getFormattedDate(startDate) + "-" +getFormattedDate(endDate)+" " + getFormattedTime(startDate) +"-" +getFormattedTime(endDate);
	if(isAllDayEvent)
		return "All Day"; 
	else
		return getFormattedTime(startDate) +" - " +getFormattedTime(endDate);
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
	
	@Override
	public String toString() {
		return name;
	}
	
}
