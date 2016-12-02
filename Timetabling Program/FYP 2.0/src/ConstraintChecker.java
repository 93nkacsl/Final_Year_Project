import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// 7-4-2016

public class ConstraintChecker {
	private DateAndTime d;
	private ArrayList<Date> lecturerUnavailable;
	private int cw1, cw2, cw3, cw4, cw5, x, cw8, cw9, cw10;
	private String launchSt, launchEt;

	public ConstraintChecker(int cw1, int cw2, int cw3, int cw4, int cw5, int cw8, int cw9, DateAndTime d,
			ArrayList<Date> lecturerUnavailable, int x, int cw10, String launchSt, String launchEt) {
		this.d = d;
		this.lecturerUnavailable = lecturerUnavailable;
		this.cw1 = cw1;
		this.cw2 = cw2;
		this.cw3 = cw3;
		this.cw4 = cw4;
		this.cw5 = cw5;
		this.cw8 = cw8;
		this.cw9 = cw9;
		this.x = x;
		this.cw10 = cw10;
		this.launchEt = launchEt;
		this.launchSt = launchSt;
	}

	public int costFunction(ArrayList<Solution> mySolutions) {
		int rValue = 0;
		rValue += constraintOneTwoThree(mySolutions, "yes", "yes", cw1);
		rValue += constraintOneTwoThree(mySolutions, "yes", "no", cw2);
		rValue += constraintOneTwoThree(mySolutions, "no", "no", cw3);
		rValue += constraintFour(mySolutions, cw4);
		rValue += constraintFive(mySolutions, cw5, x, "yes");
		rValue += constraintEight(mySolutions, cw8);
		rValue += constraintNine(mySolutions, cw9, "yes", "yes");
		rValue += constraintTen(mySolutions);
		return rValue;
	}

	/**
	 * Returning a cost function value.
	 * 
	 * @param cw
	 * @param mySolutions
	 * @return
	 */
	public String getConstraintValue(int cw, ArrayList<Solution> mySolutions) {
		if (cw == 1) {
			return "Constraint One: " + constraintOneTwoThree(mySolutions, "yes", "yes", cw1);
		} else if (cw == 2) {
			return "Constraint Two: " + constraintOneTwoThree(mySolutions, "yes", "no", cw2);
		} else if (cw == 3) {
			return "Constraint Three: " + constraintOneTwoThree(mySolutions, "no", "no", cw3);
		} else if (cw == 4) {
			return "Constraint Four: " + constraintFour(mySolutions, cw4);
		} else if (cw == 5) {
			return "Constraint Five: " + constraintFive(mySolutions, cw5, x, "yes");
		} else if (cw == 8) {
			return "Constraint Eight: " + constraintEight(mySolutions, cw8);
		} else if (cw == 9) {
			return "Constraint Nine: " + constraintNine(mySolutions, cw9, "yes", "yes");
		} else {
			return "Constraint Ten: " + constraintTen(mySolutions);
		}
	}

	/**
	 * Constraint One, Two and Three: "yes", "yes" is constraint one, "yes" "no"
	 * is the second constraint and "no" "no" is the third constraint.
	 * 
	 * 
	 * @param mySolutions
	 * @return
	 */
	private int constraintOneTwoThree(ArrayList<Solution> mySolutions, String compolsaryOne, String compulsaryTwo,
			int cw1) {
		int counter = 0;
		for (Solution solutionOne : mySolutions) {
			String[] s1POfStudies = solutionOne.getPOfStudies().split("-");
			String[] s1Compulsaries = solutionOne.getCompulsaries().split("-");
			int s1St = solutionOne.getSt();
			int s1Et = solutionOne.getEndTime();
			for (Solution solutionTwo : mySolutions) {
				int s2St = solutionTwo.getSt();
				int s2Et = solutionTwo.getEndTime();
				if (solutionOne.getLevel() == solutionTwo.getLevel() && solutionOne.getMId() != 0
						&& solutionTwo.getMId() != 0 && solutionOne.getDay().equals(solutionTwo.getDay())
						&& !solutionOne.equals(solutionTwo)) {
					String[] s2POfStudies = solutionTwo.getPOfStudies().split("-");
					String[] s2Compolsaries = solutionTwo.getCompulsaries().split("-");
					for (int s1 = 0; s1 < s1POfStudies.length; s1++) {
						for (int s2 = 0; s2 < s2POfStudies.length; s2++) {
							if (s1POfStudies[s1].equals(s2POfStudies[s2]) && s1Compulsaries[s1].equals(compolsaryOne)
									&& s2Compolsaries[s2].equals(compulsaryTwo)
									&& checkTwoEventsClash(s1St, s1Et, s2St, s2Et)) {
								counter += cw1;
							}
						}
					}
				}
			}
		}
		return counter / 2;

	}

	/**
	 * Checks whether the tutorial clashes with the module.
	 * 
	 * @param mySolutions
	 * @param cw4
	 * @return
	 */
	private int constraintFour(ArrayList<Solution> mySolutions, int cw4) {
		int rValue = 0;
		for (Solution module : mySolutions) {
			if (module.getMmId() == 0 && module.getMId() != 0) {
				for (Solution tutorial : mySolutions) {
					if (tutorial.getMmId() == module.getMId() && tutorial.getDay().equals(module.getDay())) {
						int moduleSt = module.getSt();
						int moduleEt = module.getEndTime();
						int tutSt = tutorial.getSt();
						int tutEt = tutorial.getEndTime();
						if (checkTwoEventsClash(moduleSt, moduleEt, tutSt, tutEt)) {
							rValue += cw4;
						}
					}
				}
			}
		}
		return rValue;
	}

	/**
	 * A programOfStudy should not have more than x hours of in the same day.
	 * 
	 * @param mySolutions
	 * @param cw5
	 * @return
	 */
	private int constraintFive(ArrayList<Solution> mySolutions, int cw5, int x, String compulsary) {
		int rValue = 0;
		Set<String> pOfStdies = new HashSet<String>();
		for (Solution module : mySolutions) {
			String[] pOfStudy = module.getPOfStudies().split("-");
			for (String p : pOfStudy) {
				pOfStdies.add(p);
			}
		}
		HashMap<String, Integer> hashmap = new HashMap<>();
		// ADDING day-pofStudy,0 format to a hashmap
		for (String day : d.getDays()) {
			for (String pOfStudy : pOfStdies) {
				hashmap.put(day + "-" + pOfStudy, 0);
			}
		}
		for (Solution module : mySolutions) {
			if (module.getMId() != 0) {
				String[] mPOfStudies = module.getPOfStudies().split("-");
				String[] mCompulsaries = module.getCompulsaries().split("-");
				for (int i = 0; i < mPOfStudies.length; i++) {
					if (mCompulsaries[i].equals(compulsary)) {
						int dur = hashmap.get(module.getDay() + "-" + mPOfStudies[i]);
						hashmap.put(module.getDay() + "-" + mPOfStudies[i], dur + module.getDuration());

					}
				}

			}

		}
		for (Map.Entry<String, Integer> entry : hashmap.entrySet()) {
			if (entry.getValue() > x) {
				rValue += cw5;
			}
		}
		return rValue;
	}

	/**
	 * This constraints whether a lecturer has something scheduled when there
	 * he/she is unavailable.
	 * 
	 * @param mySolutions
	 * @param cw8
	 * @return
	 */
	private int constraintEight(ArrayList<Solution> mySolutions, int cw8) {
		int rValue = 0;
		for (Solution module : mySolutions) {
			for (Date date : lecturerUnavailable) {
				if (module.getLId() == date.getId()) {
					if (date.getDay().equals(module.getDay())) {
						int mSt = module.getSt();
						int mEt = module.getEndTime();
						int dSt = date.getStartTime();
						int dEt = date.getStartTime() + date.getDuration();
						if (checkTwoEventsClash(mSt, mEt, dSt, dEt)) {
							rValue += cw8;
						}
					}
				}
			}
		}
		return rValue;
	}

	/**
	 * This constraint checks whether a compulsory module clashes with a
	 * tutorial of the same program of study. ("yes","no") as default.
	 * 
	 * @param mySolutions
	 * @param cw9
	 * @return
	 */
	private int constraintNine(ArrayList<Solution> mySolutions, int cw9, String moduleCompulsary,
			String tutorialCompulsary) {
		int rValue = 0;
		HashSet<String> set = new HashSet<>();
		for (Solution module : mySolutions) {
			if (module.getMmId() == 0 && module.getMId() != 0) {
				for (Solution tutorial : mySolutions) {
					if (tutorial.getMmId() != 0) {
						if (module.getLevel() == tutorial.getLevel() && tutorial.getDay().equals(module.getDay())) {
							String[] tutPOfStudies = tutorial.getPOfStudies().split("-");
							String[] tutCompulsaries = tutorial.getCompulsaries().split("-");
							String[] modulePOfStudies = module.getPOfStudies().split("-");
							String[] moduleCompulsaries = module.getCompulsaries().split("-");
							for (int i = 0; i < modulePOfStudies.length; i++) {
								for (int y = 0; y < tutPOfStudies.length; y++) {
									if (modulePOfStudies[i].equals(tutPOfStudies[y])
											&& moduleCompulsaries[i].equals(moduleCompulsary)
											&& tutCompulsaries[y].equals(tutorialCompulsary)) {
										int mSt = module.getSt();
										int mEt = module.getEndTime();
										int tSt = tutorial.getSt();
										int tEt = tutorial.getEndTime();
										if (checkTwoEventsClash(mSt, mEt, tSt, tEt)) {
											set.add(module.getMId() + "-" + tutorial.getMId());
										}
									}
								}
							}
						}
					}
				}

			}
		}
		rValue = cw9 * set.size();
		return rValue;
	}

	private int constraintTen(ArrayList<Solution> mySolution) {
		int total = 0;
		int laSt = Integer.parseInt(launchSt.split(":")[0]);
		int laEt = Integer.parseInt(launchEt.split(":")[0]);
		for (Solution s : mySolution) {
			if (s.getMId() != 0) {
				int st = s.getSt();
				int et = s.getEndTime();
				if (checkTwoEventsClash(laSt, laEt, st, et)) {
					total += cw10;
				}
			}
		}
		return total;
	}

	/**
	 * ALL THE CONDITIONS THAT HAS TO BE CHECKED FOR A SOLUTION TO BE VALID.
	 * 
	 * @param solution
	 * @param mySolutions
	 * @param closingTime
	 * @return
	 */
	public boolean checkSolution(Solution solution, ArrayList<Solution> mySolutions, int closingTime, int print) {
		boolean rValue = false;
		if (checkIfLecturerIsTeaching(mySolutions, solution) && checkIfRoomIsBookedAlready(mySolutions, solution)
				&& checkIfPassedClosingTime(solution.getSt(), solution.getEndTime(), closingTime)
				&& checkIfSolutionAlreadyIn(mySolutions, solution)
				&& checkIfModuleWithOtherLecturerInProgress(solution, mySolutions)) {

			rValue = true;
		}
		if (print == 1) {
			System.out.println("CheckIfLecturerIsTeaching: " + checkIfLecturerIsTeaching(mySolutions, solution));
			System.out.println("checkIfRoomIsBookedAlready: " + checkIfRoomIsBookedAlready(mySolutions, solution));
			System.out.println("checkIfPassedClosingTime: "
					+ checkIfPassedClosingTime(solution.getSt(), solution.getEndTime(), closingTime));
			System.out.println("checkIfSolutionAlreadyIn: " + checkIfSolutionAlreadyIn(mySolutions, solution));
			System.out.println("checkIfModuleWithOtherLecturerInProgress: "
					+ checkIfModuleWithOtherLecturerInProgress(solution, mySolutions));

		}
		return rValue;
	}

	/**
	 * Returns true if two events clash.
	 * 
	 * Reference:
	 * http://stackoverflow.com/questions/325933/determine-whether-two-date-
	 * ranges-overlap
	 * 
	 * @param stOne
	 * @param etOne
	 * @param stTwo
	 * @param etTwo
	 * @return
	 */
	public boolean checkTwoEventsClash(int stOne, int etOne, int stTwo, int etTwo) {
		if ((stOne < etTwo) && (etOne > stTwo)) {
			return true;
		} else
			return false;
	}

	/**
	 * Returns false if the lecturer is already teaching, Otherwise true.
	 * 
	 * @param mySolution
	 * @param solution
	 * @return
	 */
	public boolean checkIfLecturerIsTeaching(ArrayList<Solution> mySolutions, Solution solution) {
		boolean rValue = true;
		try {
			for (Solution mySS : mySolutions) {
				if (mySS.getDay().equals(solution.getDay()) && solution.getLId() == mySS.getLId()) {
					int stSS = mySS.getSt();
					int etSS = mySS.getEndTime();
					int solutionSt = solution.getSt();
					int solutionEt = solution.getEndTime();
					if (checkTwoEventsClash(solutionSt, solutionEt, stSS, etSS)) {
						rValue = false;
						break;
					}
				}
			}
		} catch (NullPointerException e) {
		}
		return rValue;
	}

	/**
	 * Returns false if a room is already booked.
	 * 
	 * @param mySolutions
	 * @param solution
	 * @return
	 */
	public boolean checkIfRoomIsBookedAlready(ArrayList<Solution> mySolutions, Solution solution) {
		boolean rValue = true;
		// System.out.println(solution);
		for (Solution mySS : mySolutions) {
			if (solution.getDay().equals(mySS.getDay()) && solution.getRId() == mySS.getRId()) {
				int stSS = mySS.getSt();
				int etSS = mySS.getEndTime();
				int stSolution = solution.getSt();
				int etSolution = solution.getEndTime();
				if (checkTwoEventsClash(stSS, etSS, stSolution, etSolution)) {
					rValue = false;
					break;
				}
			}
		}
		return rValue;
	}

	/**
	 * returns true if time is not passed the closing time, otherwise false.
	 * 
	 * @param st
	 * @param et
	 * @param closingTime
	 * @return
	 */
	public boolean checkIfPassedClosingTime(int st, int et, int closingTime) {
		if (st >= closingTime || et > closingTime) {
			return false;
		} else
			return true;
	}

	/**
	 * If the solution is already added then return false.
	 * 
	 * @param mySolutions
	 * @param solution
	 * @return
	 */
	public boolean checkIfSolutionAlreadyIn(ArrayList<Solution> mySolutions, Solution solution) {
		boolean rValue = true;
		for (Solution ss : mySolutions) {
			if (ss.getLId() == solution.getLId() && ss.getMId() == solution.getMId()) {
				rValue = false;
			}
		}
		return rValue;
	}

	/**
	 * If a module of the same mId is in progress it would not be allowed to go
	 * on.
	 * 
	 * @param solution
	 * @param mySolutions
	 * @return
	 */
	public boolean checkIfModuleWithOtherLecturerInProgress(Solution solution, ArrayList<Solution> mySolutions) {
		boolean rValue = true;
		for (Solution ss : mySolutions) {
			if (ss.getDay().equals(solution.getDay()) && ss.getMId() == solution.getMId()) {
				int solutionSt = solution.getSt();
				int solutionEt = solution.getEndTime();
				int ssSt = ss.getSt();
				int ssEt = ss.getEndTime();
				if (checkTwoEventsClash(ssSt, ssEt, solutionSt, solutionEt)) {
					rValue = false;
				}
			}
		}
		return rValue;
	}

	/**
	 * Returns the room where m.nOfStudents>room.capacity and type=type. (DONE)
	 * 
	 * @param rooms
	 * @param m
	 * @return
	 */
	public ArrayList<Room> getRooms(ArrayList<Room> rooms, Module m) {
		ArrayList<Room> roomsFiltered = new ArrayList<>();
		for (Room room : rooms) {
			for (String type : room.getType().split("-")) {
				if (room.getCapacity() >= m.getNumberOfStudents()
						&& type.toLowerCase().equals(m.getType().toLowerCase())) {
					roomsFiltered.add(room);
				}
			}
		}
		if (roomsFiltered.isEmpty()) {

		}

		return roomsFiltered;
	}

}
