
// 7-4-2016

public class Lecturer {
	private String fName, lName, type;
	private int id;

	/**
	 * A Lecturer Object with id, first name and lastname
	 * 
	 * @param fName
	 * @param lName
	 * @param id
	 */
	public Lecturer(int id, String fName, String lName, String type) {
		this.fName = fName;
		this.lName = lName;
		this.id = id;
		this.type = type;
	}

	public Lecturer() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFName() {
		return fName;
	}

	public void setFName(String fName) {
		this.fName = fName;
	}

	public String getLName() {
		return lName;
	}

	public void setLName(String lName) {
		this.lName = lName;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public String toString() {
		return "lId: " + id + ", fName: " + fName + " ,lName: " + lName + ", type: " + type;
	}
}
