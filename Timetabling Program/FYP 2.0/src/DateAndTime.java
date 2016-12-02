import java.util.ArrayList;
import java.util.Random;

//7-4-2016

public class DateAndTime {
	// THe OPENING AND THE CLOSING HOURS FOR THE COLLEGE.
	// FORMAT: xx:xx
	private String startTime, endTime;
	private String[] days = { "mon", "tue", "wed", "thu", "fri" };

	/**
	 * USING THIS CLASS YOU CAN GET ACCESS TO THE DAY AND TIME..
	 * 
	 * @param startTime
	 * @param endTime
	 * @param launchST
	 * @param launchET
	 * @param gapBetweenLectures
	 */
	public DateAndTime(String startTime, String endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}

	/**
	 * Return the hours available in a day.
	 * 
	 * @return
	 */
	public int[] getTimesOfTheDay() {
		ArrayList<Integer> times = new ArrayList<>();
		String st = startTime.split(":")[0];
		String et = endTime.split(":")[0];
		// System.out.println(st);
		for (int i = Integer.parseInt(st); i <= Integer.parseInt(et); i++) {
			// System.out.println(i);
			times.add(i);
		}
		int[] tt = new int[times.size()];
		int xx = 0;
		for (int x : times) {
			tt[xx] = x;
			xx++;
		}
		return tt;
	}

	public String[] getDays() {
		return days;
	}

	/**
	 * Reference:
	 * http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
	 * 
	 * @param ar
	 * @return
	 */
	public static String[] shuffleArray(String[] ar) {
		Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--) {
			int index = rnd.nextInt(i + 1);
			String a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
		return ar;
	}

	public int getFinishingTime() {
		String[] aa = endTime.split(":");
		return Integer.parseInt(aa[0]);
	}

	public int getStartingTime() {
		String[] aa = startTime.split(":");
		return Integer.parseInt(aa[0]);
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}
