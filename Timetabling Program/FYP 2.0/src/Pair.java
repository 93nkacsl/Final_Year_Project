
// 7-4-2016

public class Pair {
	int mId, lId;

	public Pair(int mId, int lId) {
		this.mId = mId;
		this.lId = lId;
	}

	public int getMId() {
		return mId;
	}

	public int getLId() {
		return lId;
	}

	public String toString() {
		return "( " + mId + " , " + lId + " )";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lId;
		result = prime * result + mId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (lId != other.lId)
			return false;
		if (mId != other.mId)
			return false;
		return true;
	}
}
