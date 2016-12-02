
// 7-4-2016

public class ProgramOfStudy {
	private int mId;
	private String name, compulsary;

	// name mId compulsary
	public ProgramOfStudy(int mId, String name, String compulsary) {
		this.compulsary = compulsary;
		this.name = name;
		this.mId = mId;
	}

	public int getMId() {
		return mId;
	}

	public void setMId(int mId) {
		this.mId = mId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCompulsary(String compulsary) {
		this.compulsary = compulsary;
	}

	public String getCompulsary() {
		return compulsary;
	}
	public String toString(){
		return mId+" name: "+name+" compulsary: "+compulsary;
	}
}
