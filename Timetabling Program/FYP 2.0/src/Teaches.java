
// 7-4-2016

public class Teaches {
	private int lId, mId, numberOfHours;

	public Teaches(int lId, int mId, int numberOfHours) {
		// TODO Auto-generated constructor stub
		this.numberOfHours = numberOfHours;
		this.lId = lId;
		this.mId = mId;
	}

	public int getNumberOfHours() {
		return numberOfHours;
	}

	public void setNumberOfHours(int numberOfHours) {
		this.numberOfHours = numberOfHours;
	}

	public int getLId() {
		return lId;
	}

	public void setLId(int lId) {
		this.lId = lId;
	}

	public int getMId() {
		return mId;
	}

	public void setMId(int mId) {
		this.mId = mId;
	}

	public String toString() {
		return "lId: " + lId + " mId: " + mId + " nOfHours:" + numberOfHours;
	}

}
