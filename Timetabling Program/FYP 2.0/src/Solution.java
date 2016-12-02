
// 7-4-2016

public class Solution implements Comparable<Solution> {
	private int rId, mId, lId, duration, st, mmId, level, roomCapacity, nOfStudents;
	private String day, compulsary, programOfStudies, type, location, roomType;

	public Solution(int mId, int lId, int rId, String day, int st, int duration, int mmId, String type, int level,
			String programOfStudies, String compulsary, String location, int roomCapacity, int nOfStudents,
			String roomType) {
		this.mId = mId;
		this.lId = lId;
		this.rId = rId;
		this.st = st;
		this.roomCapacity = roomCapacity;
		this.nOfStudents = nOfStudents;
		this.duration = duration;
		this.day = day;
		this.mmId = mmId;
		this.level = level;
		this.programOfStudies = programOfStudies;
		this.type = type;
		this.compulsary = compulsary;
		this.location = location;
		this.roomType = roomType;

	}

	public int getNOfStudents() {
		return nOfStudents;
	}

	public int getRCapacity() {
		return roomCapacity;
	}

	public String getType() {
		return type;
	}

	public String getPOfStudies() {
		return programOfStudies;
	}

	public String getCompulsaries() {
		return compulsary;
	}

	public int getLevel() {
		return level;
	}

	public int getMmId() {
		return mmId;
	}

	public int getMId() {
		return mId;
	}

	public int getEndTime() {
		return st + duration;
	}

	public int getLId() {
		return lId;
	}

	public int getRId() {
		return rId;
	}

	public int getSt() {
		return st;
	}

	public int getDuration() {
		return duration;
	}

	public String getDay() {
		return day;
	}

	public String getLocation() {
		return location;
	}

	public String getRType() {
		return roomType;
	}

	public String toString() {
		return "mId: " + mId + ", lId: " + lId + ", rId: " + rId + ", day: " + day + ", st: " + st + ", duration: "
				+ duration + ", mmId:" + mmId + " compulsary: " + compulsary + ", level: " + level + ", POfStudy: "
				+ programOfStudies + ", Location: " + location + ", type: " + type + ", Room Type: " + roomType;
	}

	public void print() {
		System.out.println("day: " + day + " rId: " + rId + " Time: " + st + " ET: " + getEndTime());
	}

	@Override
	public int compareTo(Solution o) {
		int mId = o.getMId();
		return this.mId-mId;
	}

}
