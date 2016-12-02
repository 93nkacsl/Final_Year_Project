import java.util.ArrayList;
import java.util.Collections;

//7-4-2016

public class GreedyAlgorithm {
	private Database db;
	// CHECK TO-DO 's in the system for testing and debugging stage.
	private ArrayList<Solution> mySolution;
	private ConstraintChecker cc;
	private DateAndTime d;
	private String days[] = null;

	public GreedyAlgorithm(Database db, ConstraintChecker cc, DateAndTime d, boolean shuffleOrNot) {
		this.db = db;
		this.cc = cc;
		this.d = d;
		mySolution = new ArrayList<>();
		if (shuffleOrNot) {
			days = d.getDays();
		} else {
			days = getRandomOrderedDay();
			while (days.equals(d.getDays())) {
				days = getRandomOrderedDay();
			}
			for (String day : days) {
				System.out.println("DAY: " + day);
			}
		}
		populateSolution();
		generate();
	}

	private String[] getRandomOrderedDay() {
		String[] array = db.getASinglePermutatedDay();
		return array;
	}

	public ArrayList<Solution> getSolution() {
		return mySolution;
	}

	/**
	 * 
	 * This method generates a solution using the greedy algorithm.
	 */
	private void generate() {
		for (Module module : db.getALLModules()) {
			String pOfStudy = getPOfStudyAndCompulsary(module.getMId()).get(0);
			String compulsary = getPOfStudyAndCompulsary(module.getMId()).get(1);
			for (Lecturer lecturer : db.getLecturers(module.getMId())) {
				try {
					Solution s = findTheBest(module, lecturer, pOfStudy, compulsary);
					if (cc.checkSolution(s, mySolution, d.getFinishingTime(), 0)) {
						mySolution.add(s);
					}
				} catch (NullPointerException e) {
				}
			}
			System.out.println("In progress...");
		}

	}

	/**
	 * This is the method which explores the state space and it selects the one
	 * with the best cost function values.
	 * 
	 * @param module
	 * @param lecturer
	 * @param pOfStudy
	 * @param compulsary
	 * @return
	 */
	private Solution findTheBest(Module module, Lecturer lecturer, String pOfStudy, String compulsary) {
		ArrayList<SolutionCF> solutionsCF = new ArrayList<>();
		for (String day : days) {
			for (int st : d.getTimesOfTheDay()) {
				for (Room room : cc.getRooms(db.getRooms(), module)) {
					Solution solution = new Solution(module.getMId(), lecturer.getId(), room.getId(), day, st,
							module.getDuration(), module.getMmId(), module.getType(), module.getLevel(), pOfStudy,
							compulsary, room.getLocation(), room.getCapacity(), module.getNumberOfStudents(),
							room.getType());
					if (cc.checkSolution(solution, mySolution, d.getFinishingTime(), 0)) {
						mySolution.add(solution);
						solutionsCF.add(new SolutionCF(solution, cc.costFunction(mySolution)));
						mySolution.remove(solution);
					}
				}

			}
		}
		Collections.sort(solutionsCF);
		Solution myS = null;
		if (solutionsCF.size() == 0) {
		} else {
			myS = solutionsCF.get(0).getSolution();
		}
		return myS;
	}

	/**
	 * Takes as input a mId and it return a list which contains the pOfStudies
	 * as element one of the list and compulsories and the second element.
	 * pOfStudies and compulsaries are separated by a single comma. TO-DO: the
	 * system must not accept "-" character!
	 * 
	 * @param mId
	 * @return
	 */
	public ArrayList<String> getPOfStudyAndCompulsary(int mId) {
		ArrayList<String> list = new ArrayList<>();
		String pOfStudy = "";
		String compulsary = "";
		for (ProgramOfStudy p : db.getProgramOfStudies(mId)) {
			pOfStudy += p.getName() + "-";
			compulsary += p.getCompulsary() + "-";
		}
		list.add(pOfStudy);
		list.add(compulsary);
		return list;
	}

	/**
	 * Adds the rooms already booked for other purposes. (DONE &CHECKED)
	 */
	public void populateSolution() {
		for (Date date : db.getRoomBooked()) {
			mySolution.add(new Solution(0, 0, date.getId(), date.getDay(), date.getStartTime(), date.getDuration(), 0,
					"", 0, "", "", "", 0, 0, ""));
		}
	}

}
