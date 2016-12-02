import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

//7-4-2016

public class Database {

	private Connection myConnection;
	private Statement myStatement;
	private String dbName;
	private String[][] permutatedDays;

	/**
	 * 
	 * @param dbName:
	 *            name of the database
	 * @param permutatedDays:
	 *            the permutation of the days which will be used in the
	 *            database.
	 */
	public Database(String dbName, String[][] permutatedDays) {
		this.dbName = dbName;
		this.permutatedDays = permutatedDays;
		createDatabaseAndTables();
	}

	public void deleteAndResetDB() {
		try {
			myStatement.execute("DROP SCHEMA " + dbName + ";");
			myStatement.execute("CREATE DATABASE IF NOT EXISTS " + dbName + ";");
			myConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, "root", "amir");
			createDatabaseAndTables();
		} catch (SQLException e) {

		}

	}

	/**
	 * This method creates a database and four tables if they don't exist. the
	 * four tables are: Lecturer, Available, Module and Room.
	 */
	public void createDatabaseAndTables() {
		try {
			myConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "amir");
			myStatement = myConnection.createStatement();
			myStatement.execute("CREATE DATABASE IF NOT EXISTS " + dbName + ";");
			myConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, "root", "amir");
			myStatement = myConnection.createStatement();
			/**
			 * The Statements:
			 */
			String stLecturerEntity = "CREATE TABLE IF NOT EXISTS lecturer(fName VARCHAR(20) NOT NULL, lName VARCHAR(20) NOT NULL, type ENUM('ta','lec','both'), lId INTEGER AUTO_INCREMENT, primary key(lId));";
			String stModuleEntity = "CREATE TABLE IF NOT EXISTS module(name VARCHAR(60) NOT NULL, duration INT NOT NULL, level INT NOT NULL,numberOfStudents INT NOT NULL, mId INT AUTO_INCREMENT, mmId INT ,type VARCHAR(10) NOT NULL, FOREIGN KEY(mmId) REFERENCES module(mId) ON DELETE CASCADE ON UPDATE CASCADE , CONSTRAINT pk PRIMARY KEY(mId));";
			// Changes the module name attribute to VARCHAR(60)
			// System.out.println(stModuleEntity);

			String stProgramOfStudy = "CREATE TABLE IF NOT EXISTS programOfStudy(name VARCHAR(40),mId INT NOT NULL, compulsary ENUM('yes','no'), PRIMARY KEY(mId,name), FOREIGN KEY(mId) REFERENCES module(mId) ON DELETE CASCADE ON UPDATE CASCADE ); ";
			String stTeachesRelation = "CREATE TABLE IF NOT EXISTS teaches(lId INT, mId INT, numberOfHours INT, FOREIGN KEY(mId) REFERENCES module(mId) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY(lId) REFERENCES lecturer(lId) ON DELETE CASCADE ON UPDATE CASCADE, PRIMARY KEY(mId,lId));";
			String stRoomEntity = "CREATE TABLE IF NOT EXISTS room(rId INT AUTO_INCREMENT, name VARCHAR(100), type VARCHAR(100),capacity INT, location VARCHAR(100), PRIMARY KEY(rId));";
			String stLocatedRelation = "CREATE TABLE IF NOT EXISTS located(mId INT, rId INT, FOREIGN KEY(mId) REFERENCES module(mId) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY(rId) REFERENCES room(rId) ON DELETE CASCADE ON UPDATE CASCADE, PRIMARY KEY(mId,rId));";
			String stRoomBooked = "CREATE TABLE IF NOT EXISTS roomBooked(rId INT, time CHAR(5), day ENUM('mon','tue','wed','thu','fri','sat','sun'),duration INT, FOREIGN KEY(rId) REFERENCES room(rId) ON DELETE CASCADE ON UPDATE CASCADE, PRIMARY KEY(rId,day, time, duration), des VARCHAR(100));";
			String stLecAvail = "CREATE TABLE IF NOT EXISTS lecUnavail(lId INT, time CHAR(5), day ENUM('mon','tue','wed','thu','fri','sat','sun'),duration INT, FOREIGN KEY(lId) REFERENCES lecturer(lId) ON DELETE CASCADE ON UPDATE CASCADE, PRIMARY KEY(lId,day, time, duration));";
			String stDaysAndTimePermutation = "CREATE TABLE IF NOT EXISTS permutation(day1 CHAR(3),day2 CHAR(3),day3 CHAR(3),day4 CHAR(3),day5 CHAR(3), PRIMARY KEY(day1,day2,day3,day4,day5));";

			/**
			 * The execution:
			 */
			myStatement.execute(stLecturerEntity);
			myStatement.execute(stModuleEntity);
			myStatement.execute(stProgramOfStudy);
			myStatement.execute(stTeachesRelation);
			myStatement.execute(stRoomEntity);
			myStatement.execute(stLocatedRelation);
			myStatement.execute(stRoomBooked);
			myStatement.execute(stLecAvail);
			myStatement.execute(stDaysAndTimePermutation);

			// Adding the permutated days to the database:
			for (String[] array : permutatedDays) {
				String statement = "INSERT INTO permutation VALUES('" + array[0] + "', '" + array[1] + "', '" + array[2]
						+ "', '" + array[3] + "','" + array[4] + "');";
				// System.out.println(statement);
				try {
					myStatement.execute(statement);
				} catch (Exception e) {

				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Adds a Lecturer into the database.
	 * 
	 * @param fName
	 * @param lName
	 * @param type
	 */
	public void addLecturer(String fName, String lName, String type) {
		// type is an enum and it has to have lec or ra as value
		String st = "INSERT INTO lecturer(fName,lName,type) VALUES('" + fName + "','" + lName + "','" + type + "');";
		ArrayList<Lecturer> lecturers = getLecturers();
		boolean addOrNot = true;
		for (int i = 0; i < lecturers.size(); i++) {
			if (lecturers.get(i).getFName().equals(fName) && lecturers.get(i).getLName().equals(lName)) {
				System.out.println("The Lecturer " + fName + ", " + lName + " Already exists in the database.");
				new ErrorDialog("The Lecturer Already exists in the system", "Duplicates exists").generateGUI();
				;
				addOrNot = false;
				break;
			}
		}
		try {
			if (addOrNot) {
				myStatement.execute(st);
			}
		} catch (SQLException e) {
			System.out.println("ERROR: addLecturer: " + e.getErrorCode());
		}
	}

	/**
	 * This method modifies the content of the database. where id = 'id'
	 * 
	 * @param id
	 * @param fName
	 * @param lName
	 * @param type
	 */
	public void updateLecturer(int id, String fName, String lName, String type) {
		String st = "UPDATE lecturer SET fName= '" + fName + "' , lName ='" + lName + "' , type= '" + type + "'"
				+ " WHERE lId = " + id + ";";
		// System.out.println(st);
		try {
			myStatement.execute(st);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("updateLecturer: error");
		}
	}

	/**
	 * Remove the lecturer with lId parameter from the database.
	 * 
	 * @param lId
	 */
	public void removeLecturer(int lId) {
		String st = "DELETE FROM lecturer WHERE lId =" + lId + ";";
		// System.out.println(st);
		try {
			myStatement.execute(st);
		} catch (SQLException e) {
			System.out.println("ERROR: removeLecturer: " + e.getErrorCode());
		}
	}

	/**
	 * Returns a (array) list of lecturers object.
	 * 
	 * @return
	 */
	public ArrayList<Lecturer> getLecturers() {
		ArrayList<Lecturer> lecturers = new ArrayList<>();
		String stQuery = "SELECT * FROM lecturer;";
		// System.out.println(stQuery);
		try {
			ResultSet rs = myStatement.executeQuery(stQuery);
			// System.out.println("Lecturers: ");
			while (rs.next()) {
				// System.out.println(rs.getInt("lId") + " " +
				// rs.getString("fName") + " " + rs.getString("lName") + " "
				// + rs.getString("type"));
				lecturers.add(new Lecturer(rs.getInt("lId"), rs.getString("fName"), rs.getString("lName"),
						rs.getString("type")));

			}
		} catch (SQLException e) {
			System.out.println("ERROR: getLecturers: " + e.getErrorCode());
		}
		return lecturers;
	}

	public Lecturer getLecturer(int lId) {
		Lecturer lec = null;
		String stQuery = "SELECT * FROM lecturer WHERE lId=" + lId + ";";
		try {
			ResultSet rs = myStatement.executeQuery(stQuery);
			// System.out.println("Lecturers: ");
			while (rs.next()) {
				// System.out.println(rs.getInt("lId") + " " +
				// rs.getString("fName") + " " + rs.getString("lName") + " "
				// + rs.getString("type"));
				lec = new Lecturer(rs.getInt("lId"), rs.getString("fName"), rs.getString("lName"),
						rs.getString("type"));

			}
		} catch (SQLException e) {
			System.out.println("ERROR: getLecturers: " + e.getErrorCode());
		}
		return lec;
	}

	/**
	 * Adds a module into the database
	 * 
	 * @param mId
	 * @param name
	 * @param duration
	 * @param level
	 * @param numberOfStudents
	 */
	public void addModule(String name, int duration, int level, int numberOfStudents, String type) {
		String st = "INSERT INTO module(name,duration, level, numberOfStudents, type) VALUES('" + name + "'," + duration
				+ "," + level + "," + numberOfStudents + ",'" + type + "');";
		// System.out.println(st);
		boolean addOrNot = true;
		ArrayList<Module> modules = getModules();
		for (Module x : modules) {
			if (x.getName().equals(name) && x.getLevel() == level) {
				addOrNot = false;
				System.out.println("Duplicate exists for the module: " + name + " in level: " + level);
				new ErrorDialog("A duplicate module exists in the system!", "Error").generateGUI();
				break;
			}
		}
		try {
			if (addOrNot) {
				myStatement.execute(st);
			}
		} catch (SQLException e) {
			System.out.println("ERROR: addModule: " + e.getErrorCode());
		}
	}

	/**
	 * Removes a module with mId=mId from the database.
	 * 
	 * @param mId
	 */
	public void removeModule(int mId) {
		String st = "DELETE FROM module WHERE mId = " + mId + ";";
		// System.out.println(st);
		try {
			myStatement.execute(st);
		} catch (SQLException e) {
			System.out.println("ERROR: removeModule: " + e.getErrorCode());
		}
	}

	/**
	 * Returns an (array) list of modules.
	 * 
	 * @return
	 */
	public ArrayList<Module> getModules() {
		ArrayList<Module> modules = new ArrayList<>();
		try {
			ResultSet rs = myStatement.executeQuery("SELECT * FROM module WHERE mmId IS NULL;");
			while (rs.next()) {
				// System.out.println(rs.getString("name"));
				modules.add(new Module(rs.getInt("mId"), rs.getInt("duration"), rs.getInt("level"),
						rs.getInt("numberOfStudents"), rs.getString("name"), rs.getString("type"), rs.getInt("mmId")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modules;
	}

	/**
	 * Returns the tutorials for a module with mId = mId;
	 * 
	 * @param mId
	 * @return
	 */
	public ArrayList<Module> getModulesTutorials(int mId) {
		String st = "Select * FROM module WHERE mmId =" + mId + ";";
		ArrayList<Module> modules = new ArrayList<>();
		try {
			ResultSet rs = myStatement.executeQuery(st);
			while (rs.next()) {
				// System.out.println(rs.getString("name"));
				modules.add(new Module(rs.getInt("mId"), rs.getInt("duration"), rs.getInt("level"),
						rs.getInt("numberOfStudents"), rs.getString("name"), rs.getString("type"), rs.getInt("mmId")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modules;
	}

	public Module getModule(int mId) {
		Module module = null;
		String st = "SELECT * FROM module WHERE mId =" + mId + ";";
		// System.out.println(st);
		try {
			ResultSet rs = myStatement.executeQuery(st);
			while (rs.next()) {
				// System.out.println(rs.getString("name"));
				module = new Module(rs.getInt("mId"), rs.getInt("duration"), rs.getInt("level"),
						rs.getInt("numberOfStudents"), rs.getString("name"), rs.getString("type"), rs.getInt("mmId"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return module;

	}

	/**
	 * Returns an (array) list of modules.
	 * 
	 * @return
	 */
	public ArrayList<Module> getALLModules() {
		ArrayList<Module> modules = new ArrayList<>();
		try {
			ResultSet rs = myStatement.executeQuery("SELECT * FROM module;");
			while (rs.next()) {
				// System.out.println(rs.getString("name"));
				modules.add(new Module(rs.getInt("mId"), rs.getInt("duration"), rs.getInt("level"),
						rs.getInt("numberOfStudents"), rs.getString("name"), rs.getString("type"), rs.getInt("mmId")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modules;
	}

	public void addAdditionalClass(String name, int duration, int level, int numberOfStudents, String type, int mmId) {
		if (mmId == 0) {
			addModule(name, duration, level, numberOfStudents, type);
		} else {
			String st = "INSERT INTO module(name,duration, level, numberOfStudents, type, mmId) VALUES('" + name + "',"
					+ duration + "," + level + "," + numberOfStudents + ",'" + type + "', " + mmId + ");";
			// System.out.println(st);
			boolean addOrNot = true;
			ArrayList<Module> modules = getModules();
			for (Module x : modules) {
				if (x.getName().equals(name) && x.getLevel() == level) {
					addOrNot = false;
					System.out.println("Duplicate exists for the module: " + name + " in level: " + level);
					new ErrorDialog("A module with thesame name exists!", "Error").generateGUI();
					break;
				}
			}
			try {
				if (addOrNot) {
					myStatement.execute(st);
					// addProgramOfStudy(, mId, compulsary);
				}
			} catch (SQLException e) {
				System.out.println("ERROR: addModule: " + e.getErrorCode());
				if (e.getErrorCode() == 1452) {
					new ErrorDialog("No Module with Id: " + mmId + " ,Please check the mmId value!", "Error")
							.generateGUI();
				}
			}
		}
	}

	public void modifyAdditionalClass(int mId, String name, int duration, int level, int numberofStudents, String type,
			int mmId) {
		String st = "UPDATE module SET name = '" + name + "', duration=" + duration + " , level= " + level
				+ ", numberOfStudents = " + numberofStudents + ", type = '" + type + "' , mmId = " + mmId
				+ " WHERE mId = " + mId + ";";
		System.out.println(st);
		try {
			myStatement.execute(st);
		} catch (SQLException e) {
			System.out.println("modifyAdditionalClass(): " + e.getErrorCode());
		}
	}

	/**
	 * Adds a programOfStudy into the database for mId FK.
	 * 
	 * @param name
	 * @param mId
	 * @param compulsary
	 */
	public void addProgramOfStudy(String name, int mId, String compulsary) {
		String st = "INSERT INTO programOfStudy(name,mId,compulsary)VALUES('" + name + "'," + mId + ",'" + compulsary
				+ "');";
		try {
			myStatement.execute(st);
		} catch (SQLIntegrityConstraintViolationException e) {
			System.out.println("Duplicates exist in programOfStudies!");
			new ErrorDialog("Duplicated exists in the database.", "Error").generateGUI();
		} catch (SQLException e) {
			System.out.println("ERROR: addProgramOfStudy: " + e.getErrorCode());
		}
	}

	/**
	 * Removes a programOfStudy with name=name and mId =mId;
	 * 
	 * @param mId
	 * @param name
	 */
	public void removeProgramOfStudy(int mId, String name) {
		String st = "DELETE FROM programOfStudy WHERE mId = " + mId + " AND name= '" + name + "';";
		// System.out.println(st);
		try {
			myStatement.execute(st);
		} catch (SQLException e) {
			System.out.println("ERROR: removeProgramOfStudy: " + e.getErrorCode());
		}
	}

	/**
	 * Returns an (array) list of programOfStudy objects.
	 * 
	 * @return
	 */
	public ArrayList<ProgramOfStudy> getProgramOfStudies() {
		ArrayList<ProgramOfStudy> list = new ArrayList();
		try {
			ResultSet rs = myStatement.executeQuery("SELECT * FROM programOfStudy;");
			while (rs.next()) {
				// System.out.println(rs.getInt("mId")+"
				// "+rs.getString("name")+" "+rs.getString("compulsary"));
				list.add(new ProgramOfStudy(rs.getInt("mId"), rs.getString("name"), rs.getString("compulsary")));
			}

		} catch (SQLException e) {
			System.out.println("ERROR: getProgramOfStudies: " + e.getErrorCode());
		}
		return list;
	}

	/**
	 * Assigns a lecturer to a module. It adds them to a database. TO-DO: make
	 * sure that the numberOfHours <=duration.
	 * 
	 * @param lId
	 * @param mId
	 * @param numberOfHours
	 */
	public void addTeaches_Lecturer_Module(int lId, int mId, int numberOfHours) {
		String st = "INSERT INTO teaches(lId, mId, numberOfHours) VALUES(" + lId + "," + mId + "," + numberOfHours
				+ ");";
		try {
			myStatement.execute(st);
		} catch (MySQLIntegrityConstraintViolationException e) {
			// System.out.println("duplicates exists in the system already!");
			// new ErrorDialog("The lecturer with mId: " + mId + " Already
			// teaches this module!", "Error").generateGUI();
		} catch (SQLException e) {
			System.out.println("Teaches Error: " + e.getErrorCode());
		}
	}

	/**
	 * Removes a lecturer from teaching a module
	 * 
	 * @param mId
	 * @param lId
	 */
	public void removeTeaches_Lecturer_Module(int mId, int lId) {
		String st = "DELETE FROM teaches WHERE mId = " + mId + " AND lId =" + lId + " ;";

		try {
			myStatement.execute(st);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns an (array) list of teaches Object
	 * 
	 * @return
	 */
	public ArrayList<Teaches> getTeaches() {
		ArrayList<Teaches> teaches = new ArrayList<>();
		try {
			ResultSet rs = myStatement.executeQuery("SELECT * FROM teaches;");
			while (rs.next()) {
				teaches.add(new Teaches(rs.getInt("lId"), rs.getInt("mId"), rs.getInt("numberOfHours")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return teaches;
	}

	/**
	 * Returns an (array) list of teaches Object
	 * 
	 * @return
	 */
	public ArrayList<Teaches> getTeaches(int mId) {
		ArrayList<Teaches> teaches = new ArrayList<>();
		try {
			ResultSet rs = myStatement.executeQuery("SELECT * FROM teaches WHERE mId = " + mId + ";");
			while (rs.next()) {
				teaches.add(new Teaches(rs.getInt("lId"), rs.getInt("mId"), rs.getInt("numberOfHours")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return teaches;
	}

	/**
	 * Adds a room to the database.
	 * 
	 * @param name
	 * @param type
	 * @param capacity
	 * @param location
	 */
	public void addRoom(String name, String type, int capacity, String location) {

		String st = "INSERT INTO room(name,type,location, capacity) VALUES('" + name + "',\"" + type + "\",'" + location
				+ "'," + capacity + ");";

		boolean addOrNot = true;

		for (Room room : getRooms()) {
			// System.out.println(room.toString());
			if (room.getName().equals(name) && room.getType().equals(type) && room.getCapacity() == capacity
					&& room.getLocation().equals(location)) {
				addOrNot = false;
			}
		}

		try {
			if (addOrNot) {
				myStatement.execute(st);
			} else {
				System.out.println("Duplicates Exists!");
				new ErrorDialog("A room with thesame name exists in the database", "Error").generateGUI();
			}
		} catch (SQLException e) {
			System.out.println("ERROR: addRoom: " + e.getErrorCode());
			// e.printStackTrace();
		}
	}

	/**
	 * Remove a room from the database where rId=rId;
	 * 
	 * @param rId
	 */
	public void removeRoom(int rId) {
		String st = "DELETE FROM room WHERE rId= " + rId + ";";

		try {
			myStatement.execute(st);
		} catch (SQLException e) {
		}
	}

	public ArrayList<Room> getRooms() {
		ArrayList<Room> rooms = new ArrayList<>();
		try {
			ResultSet rs = myStatement.executeQuery("SELECT * FROM room;");

			while (rs.next()) {
				rooms.add(new Room(rs.getInt("rId"), rs.getString("location"), rs.getInt("capacity"),
						rs.getString("type"), rs.getString("name")));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rooms;
	}

	/**
	 * Adds a (module, rId) to the relation located in the database.
	 * 
	 * @param mId
	 * @param rId
	 */
	public void addLocated_Module_Room(int mId, int rId) {
		String st = "INSERT INTO located(mId, rId) VALUES(" + mId + "," + rId + ");";

		try {
			myStatement.execute(st);
		} catch (MySQLIntegrityConstraintViolationException e) {
			System.out.println("duplicates exists in the system already!");
		}

		catch (SQLException e) {

		}
	}

	/**
	 * Removes a (module, room) from the located relation in the database.
	 * 
	 * @param mId
	 * @param rId
	 */
	public void removeLocated_Module_Room(int mId, int rId) {
		String st = "DELETE FROM located WHERE mId = " + mId + " AND rId = " + rId + " ;";
		System.out.println(st);
	}

	public void addRoomBooked(int rId, String time, String day, int duration, String des) {
		String st = "INSERT INTO roomBooked(rId, day, time,duration, des) VALUES(" + rId + " , '" + day + "','" + time
				+ "'," + duration + ",'" + des + "');";
		// System.out.println(st);
		try {
			myStatement.execute(st);
		} catch (MySQLIntegrityConstraintViolationException e) {
			System.out.println("duplicates exists in the system already!");
		} catch (SQLException e) {
			System.out.println("ERROR: addRoomBooked: " + e.getErrorCode());
		}
	}

	public void removeRoomBooked(int rId, String time, String day, int duration) {
		String st = "DELETE FROM roomBooked WHERE rId =" + rId + " AND time = '" + time + "' AND day = '" + day
				+ "' AND duration = " + duration + " ;";
		// System.out.println(st);
		try {
			myStatement.execute(st);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Date> getRoomBooked() {
		ArrayList<Date> roomBookedDates = new ArrayList<>();
		try {
			ResultSet rs = myStatement.executeQuery("SELECT * FROM roomBooked;");
			while (rs.next()) {
				// System.out.println(rs.getString("time") + " " +
				// rs.getString("day") + " " + rs.getInt("rId") + " "
				// + rs.getInt("duration"));
				roomBookedDates.add(new Date(rs.getInt("rId"), rs.getString("time"), rs.getString("day"),
						rs.getInt("duration"), rs.getString("des")));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return roomBookedDates;
	}

	public ArrayList<Date> getRoomBooked(int rId) {
		ArrayList<Date> roomBookedDates = new ArrayList<>();
		try {
			String st = "SELECT * FROM roomBooked WHERE rId = " + rId + ";";
			System.out.println(st);
			ResultSet rs = myStatement.executeQuery(st);
			while (rs.next()) {
				// System.out.println(rs.getString("time") + " " +
				// rs.getString("day") + " " + rs.getInt("rId") + " "
				// + rs.getInt("duration"));
				roomBookedDates.add(new Date(rs.getInt("rId"), rs.getString("time"), rs.getString("day"),
						rs.getInt("duration"), rs.getString("des")));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return roomBookedDates;
	}

	public void addLecUnavail(int lId, String day, String time, int duration) {
		String st = "INSERT INTO lecUnavail(lId, day, time, duration) VALUES(" + lId + ",'" + day + "','" + time + "',"
				+ duration + ");";
		// System.out.println(st);
		try {
			myStatement.execute(st);
		} catch (SQLIntegrityConstraintViolationException e) {
			System.out.println("Duplicates exists in the table.");
		} catch (SQLException e) {
			System.out.println("ERROR: addAvail: " + e.getErrorCode());
		}
	}

	public void removeLecAvail(int lId, String day, String time, int duration) {
		String st = "DELETE FROM lecUnavail WHERE lId= " + lId + " AND day= '" + day + "' AND time= '" + time
				+ "' AND duration = " + duration + ";";
		System.out.println(st);
		try {
			myStatement.execute(st);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Date> getLecUnAvail() {
		ArrayList<Date> lecAvail = new ArrayList<Date>();
		try {
			ResultSet rs = myStatement.executeQuery("SELECT * FROM lecUnavail");
			while (rs.next()) {
				// System.out.println(rs.getInt("lId") + " " +
				// rs.getString("day") + " " + rs.getString("time") + " "
				// + rs.getInt("duration"));
				lecAvail.add(
						new Date(rs.getInt("lId"), rs.getString("time"), rs.getString("day"), rs.getInt("duration")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lecAvail;
	}

	public ArrayList<Date> getLecUnAvail(int lId) {
		ArrayList<Date> lecAvail = new ArrayList<Date>();
		try {
			String st = "SELECT * FROM lecUnavail WHERE lId = " + lId + ";";
			// System.out.println(st);
			ResultSet rs = myStatement.executeQuery(st);

			while (rs.next()) {
				// System.out.println(rs.getInt("lId") + " " +
				// rs.getString("day") + " " + rs.getString("time") + " "
				// + rs.getInt("duration"));
				lecAvail.add(
						new Date(rs.getInt("lId"), rs.getString("time"), rs.getString("day"), rs.getInt("duration")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lecAvail;
	}

	public ArrayList<Module> getACs() {
		ArrayList<Module> modules = new ArrayList<>();
		try {
			ResultSet rs = myStatement.executeQuery("SELECT * FROM module WHERE mmId IS NOT NULL;");
			while (rs.next()) {
				// System.out.println(rs.getString("name"));
				modules.add(new Module(rs.getInt("mId"), rs.getInt("duration"), rs.getInt("level"),
						rs.getInt("numberOfStudents"), rs.getString("name"), rs.getString("type"), rs.getInt("mmId")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modules;
	}

	public ArrayList<Module> getACs(int mId) {
		ArrayList<Module> modules = new ArrayList<>();
		try {
			ResultSet rs = myStatement.executeQuery("SELECT * FROM module WHERE mmId = " + mId + " ; ");
			while (rs.next()) {
				// System.out.println(rs.getString("name"));
				modules.add(new Module(rs.getInt("mId"), rs.getInt("duration"), rs.getInt("level"),
						rs.getInt("numberOfStudents"), rs.getString("name"), rs.getString("type"), rs.getInt("mmId")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return modules;
	}

	public ArrayList<Lecturer> getLecturers(int mId) {
		ArrayList<Lecturer> lecturers = new ArrayList<>();
		String stQuery = "SELECT lecturer.* FROM lecturer,module,teaches WHERE module.mId=" + mId
				+ "  AND lecturer.lId= teaches.lId AND module.mId = teaches.mId ;";

		// System.out.println(stQuery);
		try {
			ResultSet rs = myStatement.executeQuery(stQuery);
			// System.out.println("Lecturers: ");
			while (rs.next()) {
				// System.out.println(rs.getInt("lId") + " " +
				// rs.getString("fName") + " " + rs.getString("lName") + " "
				// + rs.getString("type"));
				lecturers.add(new Lecturer(rs.getInt("lId"), rs.getString("fName"), rs.getString("lName"),
						rs.getString("type")));

			}
		} catch (SQLException e) {
			System.out.println("ERROR: getLecturers: " + e.getErrorCode());
		}
		return lecturers;
	}

	public ArrayList<ProgramOfStudy> getProgramOfStudies(int mId) {
		ArrayList<ProgramOfStudy> list = new ArrayList();
		try {
			String st = "SELECT * FROM programOfStudy, module WHERE module.mId=" + mId
					+ " AND module.mId= programOfStudy.mId ;";
			// System.out.println(st);
			ResultSet rs = myStatement.executeQuery(st);

			while (rs.next()) {
				// System.out.println(rs.getInt("mId")+"
				// "+rs.getString("name")+" "+rs.getString("compulsary"));
				list.add(new ProgramOfStudy(rs.getInt("mId"), rs.getString("name"), rs.getString("compulsary")));
			}

		} catch (SQLException e) {
			System.out.println("ERROR: getProgramOfStudies: " + e.getErrorCode());
		}
		return list;
	}

	/**
	 * It retrives the number of hours a lecturer teaches a specific module.
	 * 
	 * @param mId
	 * @param lId
	 * @return
	 */
	public int getNumberOfHours(int mId, int lId) {
		int nOfHours = 0;
		String statement = "SELECT * FROM teaches WHERE mId = " + mId + " AND lId = " + lId + " ;";
		try {
			ResultSet rs = myStatement.executeQuery(statement);
			while (rs.next()) {
				nOfHours = rs.getInt("numberOfHours");

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nOfHours;
	}

	/**
	 * Returns an array of all random days.
	 * 
	 * @return
	 */
	public String[][] getDayPermutated() {
		String[][] array = new String[120][5];
		String st = "SELECT * FROM permutation;";
		// System.out.println(st);
		int i = 0;
		try {
			ResultSet rs = myStatement.executeQuery(st);
			while (rs.next()) {
				String[] arr = { rs.getString("day1"), rs.getString("day2"), rs.getString("day3"), rs.getString("day4"),
						rs.getString("day5") };
				array[i] = arr;
				i++;
			}
		} catch (SQLException e) {
			System.out.println("Error");
		}
		return array;
	}

	/**
	 * Returns a single random array of days from the database.
	 * 
	 * @return
	 */
	public String[] getASinglePermutatedDay() {
		String[] array = null;
		String st = "SELECT * FROM permutation ORDER BY RAND() LIMIT 1 ; ";
		// System.out.println(st);
		try {
			ResultSet rs = myStatement.executeQuery(st);
			while (rs.next()) {
				String[] arr = { rs.getString("day1"), rs.getString("day2"), rs.getString("day3"), rs.getString("day4"),
						rs.getString("day5") };
				array = arr;
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return array;

	}

	public void setDBName(String dbName) {
		this.dbName = dbName;
	}

	public Statement getStatement() {
		return myStatement;
	}

	public String getDBName() {
		return dbName;
	}

}
