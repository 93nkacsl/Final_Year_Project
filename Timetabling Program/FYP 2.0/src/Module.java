
// 7-4-2016

public class Module {

	private int mId, duration, level, numberOfStudents, mmId;
	private String name, type;

	public Module(int mId, int duration, int level, int numberOfStudents, String name, String type, int mmId) {
		this.mId = mId;
		this.duration = duration;
		this.level = level;
		this.numberOfStudents = numberOfStudents;
		this.name = name;
		this.type = type;
		this.mmId = mmId;
	}

	public void setMId(int mId) {
		this.mId = mId;
	}

	public int getMId() {
		return mId;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getDuration() {
		return duration;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public void setNumberOfStudents(int numberOfStudents) {
		this.numberOfStudents = numberOfStudents;
	}

	public int getNumberOfStudents() {
		return numberOfStudents;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMmId() {
		return mmId;
	}

	public void setMmId(int mmId) {
		this.mmId = mmId;
	}

	public String toString() {
		return mId + " name: " + name + " level: " + level + " nOfStudents: " + numberOfStudents + " duration: "
				+ duration + " type: " + type + " mmId: " + mmId;
	}
}
