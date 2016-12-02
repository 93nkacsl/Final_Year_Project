
// 7-4-2016

public class Room implements Comparable<Room> {
	private int rId, capacity;
	private String location, type, name;

	public Room(int rId, String location, int capacity, String type, String name) {
		this.rId = rId;
		this.capacity = capacity;
		this.location = location;
		this.type = type;
		this.name = name;
	}

	public int getId() {
		return rId;
	}

	public void setRId(int rId) {
		this.rId = rId;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return rId + " name: " + name + " location: " + location + " capacity: " + capacity + " type: " + type;
	}

	@Override
	public int compareTo(Room o) {
		int capacity = o.getCapacity();
		return capacity-this.capacity;
	}

}
