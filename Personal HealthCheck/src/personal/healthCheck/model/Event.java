package personal.healthCheck.model;

import java.util.Date;
import java.util.List;

public class Event {

	private	int id;
	private	String name;
	private Date startDate;
	private Date endDate;
	private Boolean isAllDayEvent;
	private Boolean isReapetedEvent;
	private RepeatType repeatType;
	private int repeatEvery;
	private RepeatByType repeatByType;
	private List<DayOfWeek> repeatsOnDays;
	private List<Date> repeatsAtHours;
	private String location;
	private Feedback feedback;

	//constructor
	public Event(int id, String name, Date startDate,
			Date endDate, Boolean isAllDayEvent, Boolean isReapetedEvent,
			RepeatType repeatType, int repeatEvery, RepeatByType repeatByType,
			List<DayOfWeek> repeatsOnDays, List<Date> repeatsAtHours,
			String location, Feedback feedback) {
		super();
		this.id = id;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isAllDayEvent = isAllDayEvent;
		this.isReapetedEvent = isReapetedEvent;
		this.repeatType = repeatType;
		this.repeatEvery = repeatEvery;
		this.repeatByType = repeatByType;
		this.repeatsOnDays = repeatsOnDays;
		this.repeatsAtHours = repeatsAtHours;
		this.location = location;
		this.feedback = feedback;
	}

	@Override
	public String toString() {
		return name;
	}

	//setters and getters

	public Feedback getFeedback() {
		return feedback;
	}
	
	public void clearRepeatOnDays() {
		this.repeatsOnDays.clear();
	}

	public void setFeedback(Feedback feedback) {
		this.feedback = feedback;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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



	public Boolean getIsReapetedEvent() {
		return isReapetedEvent;
	}



	public void setIsReapetedEvent(Boolean isReapetedEvent) {
		this.isReapetedEvent = isReapetedEvent;
	}



	public RepeatType getRepeatType() {
		return repeatType;
	}



	public void setRepeatType(RepeatType repeatType) {
		this.repeatType = repeatType;
	}



	public int getRepeatEvery() {
		return repeatEvery;
	}



	public void setRepeatEvery(int repeatEvery) {
		this.repeatEvery = repeatEvery;
	}



	public RepeatByType getRepeatByType() {
		return repeatByType;
	}



	public void setRepeatByType(RepeatByType repeatByType) {
		this.repeatByType = repeatByType;
	}



	public List<DayOfWeek> getRepeatsOnDays() {
		return repeatsOnDays;
	}



	public void setRepeatsOnDays(DayOfWeek dayOfWeek) {
		this.repeatsOnDays.add(dayOfWeek);
	}



	public List<Date> getRepeatsAtHours() {
		return repeatsAtHours;
	}



	public void setRepeatsAtHours(Date date) {
		this.repeatsAtHours.add(date) ;
	}


	public String getLocation() {
		return location;
	}



	public void setLocation(String location) {
		this.location = location;
	}
	
	

}

