
// 7-4-2016

/**
 * This is an object from a database.
 * 
 * @author amirnasiri
 *
 */
public class Date implements Comparable<Date> {
	private int id, duration;
	private String time, day, description;

	/**
	 * This is for when a lecturer is unavailable.
	 * 
	 * @param id
	 * @param time
	 * @param day
	 * @param duration
	 */
	public Date(int id, String time, String day, int duration) {
		this.id = id;
		this.time = time;
		this.day = day;
		this.duration = duration;
	}

	/**
	 * This is for when a room is booked.
	 * 
	 * @param id
	 * @param time
	 * @param day
	 * @param duration
	 * @param description
	 */
	public Date(int id, String time, String day, int duration, String description) {
		this.id = id;
		this.time = time;
		this.day = day;
		this.duration = duration;
		this.description = description;
	}

	public int getStartTime() {
		String[] array = time.split(":");
		return Integer.parseInt(array[0]);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String toString() {
		return id + " " + day + " " + time + " " + duration + " " + description;
	}

	@Override
	public int compareTo(Date o) {
		int aa = o.getId();
		return this.id - aa;
	}
	

}
