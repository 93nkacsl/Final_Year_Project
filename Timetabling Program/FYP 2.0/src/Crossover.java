import java.util.ArrayList;
import java.util.Collections;

//7-4-2016

public class Crossover {
	private Database db;
	private ConstraintChecker cc;
	private DateAndTime d;
	private double crossoverLPos, crossoverRPos;

	public Crossover(Database db, ConstraintChecker cc, DateAndTime d, double crossoverLPos, double crossoverRPos) {
		this.cc = cc;
		this.d = d;
		this.db = db;
		this.crossoverLPos = crossoverLPos;
		this.crossoverRPos = crossoverRPos;
	}

	/**
	 * This method performs a single point crossover, the position of the
	 * crossover is determined by the 'position' variable. The decision on which
	 * side is taken from which parent is determined by the boolean value: if
	 * true left side will come from the first parent and right will cone from
	 * the right parent. The position value must be non negative.
	 * 
	 * @param parentOne
	 * @param parentTwo
	 * @param position
	 * @param leftOrRight
	 */
	public ArrayList<Solution> singlePointCrossover(ArrayList<Solution> parentOne, ArrayList<Solution> parentTwo) {
		Collections.sort(parentOne);
		Collections.sort(parentTwo);
		// This is what you should return.
		ArrayList<Solution> theSolution = new ArrayList<>();
		// This method adds the roomBooked stuff.
		populateSolution(theSolution);

		ArrayList<Solution> copiedParentOne = new ArrayList<>(parentOne);
		ArrayList<Solution> copiedParentTwo = new ArrayList<>(parentTwo);
		copiedParentOne = removeNonModules(copiedParentOne);
		copiedParentTwo = removeNonModules(copiedParentTwo);

		double position = checkPositioValue(crossoverLPos);
		int crossOverPos = findCrossoverPosition(parentOne.size(), position);

		// System.out.println(position + " " + crossOverPos + " " +
		// parentOne.size());
		// left side will come from parentOne.

		for (int x = 0; x < crossOverPos; x++) {
			theSolution.add(copiedParentOne.get(x));
		}
		for (int i = 0; i < copiedParentTwo.size(); i++) {
			if (cc.checkSolution(copiedParentTwo.get(i), theSolution, d.getFinishingTime(), 0)) {
				theSolution.add(copiedParentTwo.get(i));
			} else {
				mutate(copiedParentTwo.get(i), theSolution);
			}
		}

		Collections.sort(theSolution);
		return theSolution;
	}

	// Greedy Algorithm:
	private void mutate(Solution solution, ArrayList<Solution> theSolution) {
		try {
			Solution newSolution = findTheBestPositionForTheSolution(solution, theSolution);
			// System.out.println("New Solution: "+newSolution);
			if (newSolution != null) {
				theSolution.add(newSolution);
			}
			// System.out.println("Cost function:
			// "+cc.costFunction(theSolution));
		} catch (NullPointerException e) {

		}
	}

	private Solution findTheBestPositionForTheSolution(Solution solution, ArrayList<Solution> theSolution) {
		ArrayList<SolutionCF> solutionsCF = new ArrayList<>();
		for (String day : d.getDays()) {
			for (int st : d.getTimesOfTheDay()) {
				for (Room room : cc.getRooms(db.getRooms(), db.getModule(solution.getMId()))) {
					Solution s = new Solution(solution.getMId(), solution.getLId(), room.getId(), day, st,
							solution.getDuration(), solution.getMmId(), solution.getType(), solution.getLevel(),
							solution.getPOfStudies(), solution.getCompulsaries(), room.getLocation(),
							room.getCapacity(), solution.getNOfStudents(), room.getType());
					if (cc.checkSolution(s, theSolution, d.getFinishingTime(), 0)) {
						theSolution.add(s);
						solutionsCF.add(new SolutionCF(s, cc.costFunction(theSolution)));
						theSolution.remove(s);
						// System.out.println("New Solution: " + s);
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
	 * This method removes the bookedrooms from the solutions.
	 * 
	 * @param theSolution
	 * @return
	 */
	private ArrayList<Solution> removeNonModules(ArrayList<Solution> theSolution) {
		ArrayList<Solution> theUpdatedSolution = new ArrayList<>();
		for (Solution ss : theSolution) {
			if (ss.getMId() != 0) {
				theUpdatedSolution.add(ss);
			}
		}
		return theUpdatedSolution;
	}

	/**
	 * This method returns the crossover position.
	 * 
	 * @param parentOneSize
	 * @param position
	 * @return
	 */
	public int findCrossoverPosition(int parentOneSize, double position) {
		return (int) Math.round(parentOneSize * position);
	}

	/**
	 * This method checks whether the position number is between 0 and 1,
	 * otherwise it sets it as 0.5
	 * 
	 * @param position
	 * @return
	 */
	public double checkPositioValue(double position) {
		double pos = position;
		if (position > 1 || position < 0) {
			System.out.println("SinglePointCrossover: Incorrecnt position value, position set to 0.5");
			pos = 0.5;
		}
		return pos;
	}

	/**
	 * Adds the rooms already booked for other purposes. (DONE &CHECKED)
	 */
	public void populateSolution(ArrayList<Solution> theSolution) {
		for (Date date : db.getRoomBooked()) {
			theSolution.add(new Solution(0, 0, date.getId(), date.getDay(), date.getStartTime(), date.getDuration(), 0,
					"", 0, "", "", "", 0, 0, ""));
		}
	}

	/**
	 * This method performs a two point crossover, the positions are determined
	 * by lPos (non-negative) and rPos(non-negative).
	 * 
	 * @param parentOne
	 * @param parentTwo
	 * @param lPos
	 * @param rPos
	 * @param leftOrRight
	 * @return
	 */
	public ArrayList<Solution> DoublePointCrossover(ArrayList<Solution> parentOne, ArrayList<Solution> parentTwo) {
		ArrayList<Solution> theSolution = new ArrayList<>();
		// Adding the already booked Rooms.
		populateSolution(theSolution);

		ArrayList<Solution> copiedParentOne = removeNonModules(parentOne);
		ArrayList<Solution> copiedParentTwo = removeNonModules(parentTwo);

		copiedParentOne = removeNonModules(copiedParentOne);
		copiedParentTwo = removeNonModules(copiedParentTwo);

		Collections.sort(copiedParentOne);
		Collections.sort(copiedParentTwo);

		int myLPos = findCrossoverPosition(copiedParentOne.size(), crossoverLPos);
		int myRPos = findCrossoverPosition(copiedParentOne.size(), crossoverRPos);
		// System.out.println(copiedParentOne.size() + "-" + myLPos + "-" +
		// myRPos);

		int currentPosition = 0;

		// The left side of the first parent.
		for (int pos = 0; pos < myLPos; pos++) {
			theSolution.add(copiedParentOne.get(pos));
			currentPosition++;
		}

		// The middle part of the second parent.
		for (int pos = currentPosition; pos < myRPos; pos++) {
			if (cc.checkSolution(copiedParentTwo.get(pos), theSolution, d.getFinishingTime(), 0)) {
				theSolution.add(copiedParentTwo.get(pos));
				currentPosition++;
			} else {
				mutate(copiedParentTwo.get(pos), theSolution);
				currentPosition++;
			}
		}

		// The rest of parent one:
		for (int pos = 0; pos < copiedParentOne.size(); pos++) {

			if (cc.checkSolution(copiedParentOne.get(pos), theSolution, d.getFinishingTime(), 0)) {
				theSolution.add(copiedParentOne.get(pos));
			} else {
				mutate(copiedParentOne.get(pos), theSolution);
			}
		}

		return theSolution;

	}

}
