import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

//7-4-2016

public class SA {
	private ArrayList<Solution> mySolutions;
	private ConstraintChecker cc;
	private double initialTemperature, coolingRate;
	private int population;

	/**
	 * This is the second constructor Which will be used in the GA class.
	 * 
	 * @param population
	 */
	public SA(ArrayList<Solution> mySolutions, ConstraintChecker cc, int population) {
		this.population = population;
		this.mySolutions = new ArrayList<>(mySolutions);
		this.cc = cc;
	}

	/**
	 * This is the main constructor for the class.
	 * 
	 * @param mySolutions
	 * @param cc
	 * @param initialTemperature
	 * @param coolingRate
	 */
	public SA(ArrayList<Solution> mySolutions, ConstraintChecker cc, double initialTemperature, double coolingRate) {
		this.mySolutions = new ArrayList<>(mySolutions);
		this.cc = cc;
		this.coolingRate = coolingRate;
		this.initialTemperature = initialTemperature;
	}

	/**
	 * This method will be used in the Genetic Algorithm class.
	 * 
	 * @return
	 */
	public ArrayList<SolutionAndCF> generatePopulation() {
		ArrayList<SolutionAndCF> solutions = new ArrayList<>();
		ArrayList<Solution> solution = new ArrayList<>(mySolutions);
		solutions.add(new SolutionAndCF(solution, cc.costFunction(solution)));
		while (solutions.size() < population) {
			solution = getNeighborConfiguration(solution, 1);
			Collections.sort(solution);
			if (!solutions.contains(new SolutionAndCF(solution, cc.costFunction(solution)))) {
				solutions.add(new SolutionAndCF(solution, cc.costFunction(solution)));
			} else {
				System.out.println("SA:generatePopulation: Already In");
			}
		}

		Collections.sort(solutions);
		return solutions;
	}

	/**
	 * This is the method for simulated annealing.
	 * 
	 * Reference-1:
	 * http://www.theprojectspot.com/tutorial-post/simulated-annealing-algorithm
	 * -for-beginners/6 Reference-2:
	 * http://katrinaeg.com/simulated-annealing.html
	 * 
	 * @param mySolutions
	 * @param changes
	 * @param initialTemperature
	 */
	ArrayList<Solution> anneal(ArrayList<Solution> mySolutions, int changes, double initialTemperature,
			double coolingRate) {
		ArrayList<Solution> best = new ArrayList<>(mySolutions);
		// Current best:
		ArrayList<Solution> current = new ArrayList<>(mySolutions);
		// Config -> init_config
		double temperature = initialTemperature;
		// initial Temperature
		while (temperature > 1) {
			System.out.println("Temperature: " + temperature);
			ArrayList<Solution> neighbour = getNeighborConfiguration(current, changes);
			if (neighbour.equals(current)) {
			} else {
				int currentCF = cc.costFunction(current);
				int newConfigCF = cc.costFunction(neighbour);
				if (newConfigCF < currentCF) {
					current = neighbour;
				} else if (getProbability(currentCF, newConfigCF, temperature) > Math.random()) {
					current = neighbour;
				}
				if (currentCF < cc.costFunction(best)) {
					best = current;
				}
				if (newConfigCF < cc.costFunction(best)) {
					best = neighbour;
				}
				temperature *= 1 - coolingRate;
			}

		}

		System.out.println("Best CF: " + cc.costFunction(best));
		System.out.println("Greedy: " + cc.costFunction(mySolutions));
		return best;
	}

	public double getProbability(int configCF, int newConfigCF, double temperature) {
		return Math.exp(((configCF - newConfigCF) / temperature));
	}

	/**
	 * It creates a new configuration by changing depending on the number of
	 * neighbor changes.
	 * 
	 * to-do: check if the lecturer is not teaching.... (TESTING)
	 * 
	 * @param mySolutions
	 * @param changes
	 *            :(1, 2, 3)
	 * @return
	 */
	public ArrayList<Solution> getNeighborConfiguration(ArrayList<Solution> mySolutions, int changes) {
		ArrayList<Solution> methodSolutions = new ArrayList<>(mySolutions);
		ArrayList<Solution> theSolution = null;
		HashMap<Pair, ArrayList<Pair>> map = getNeighbour(mySolutions);
		List<Pair> mapKeys = new ArrayList<>(map.keySet());
		Random random = new Random();
		if (changes == 1) {
			while (theSolution == null) {
				theSolution = changeTwo(map, mapKeys, random, methodSolutions);
			}
		} else if (changes == 2) {
			while (theSolution == null) {
				theSolution = changeThree(map, mapKeys, random, methodSolutions);
			}
		} else if (changes == 3) {
			while (theSolution == null) {
				theSolution = changeFour(map, mapKeys, random, methodSolutions);
			}
		}
		return theSolution;
	}

	/**
	 * This method changes four neighbors in the solution.
	 * 
	 * @param map
	 * @param mapKeys
	 * @param random
	 * @param methodSolutions
	 * @return
	 */
	private ArrayList<Solution> changeFour(HashMap<Pair, ArrayList<Pair>> map, List<Pair> mapKeys, Random random,
			ArrayList<Solution> methodSolutions) {
		ArrayList<Solution> solutionnnnn = new ArrayList<>(methodSolutions);
		Pair pOne = mapKeys.get(random.nextInt(mapKeys.size()));
		ArrayList<Pair> moduleOnePairs = map.get(pOne);
		Pair pTwo = moduleOnePairs.get(random.nextInt(moduleOnePairs.size()));
		Solution moduleOne = null;
		Solution moduleTwo = null;
		for (Solution ss : methodSolutions) {
			if (pOne.getMId() == ss.getMId() && pOne.getLId() == ss.getLId()) {
				moduleOne = ss;
			}
			if (pTwo.getMId() == ss.getMId() && pTwo.getLId() == ss.getLId()) {
				moduleTwo = ss;
			}
		}
		solutionnnnn.remove(moduleOne);
		solutionnnnn.remove(moduleTwo);

		ArrayList<Solution> changedSolutions = changeTwoSolution(moduleOne, moduleTwo);
		if (!cc.checkIfLecturerIsTeaching(solutionnnnn, changedSolutions.get(0))
				|| !cc.checkIfLecturerIsTeaching(solutionnnnn, changedSolutions.get(1))) {
			solutionnnnn = null;
		}
		try {
			solutionnnnn.add(changedSolutions.get(0));
			solutionnnnn.add(changedSolutions.get(1));

		} catch (NullPointerException e) {
			// System.out.println("Lecturer(s) Teaching Already");
		}
		//
		// The third one
		//
		// System.out.println();
		// System.out.println();
		Solution thirdSolution = null;
		try {
			HashMap<Pair, ArrayList<Pair>> myMap = getNeighbour(solutionnnnn);
			Solution myModuleTwo = changedSolutions.get(1);
			solutionnnnn.remove(myModuleTwo);
			// System.out.println("Second Module: " + myModuleTwo);
			ArrayList<Pair> pairsToChangeWithModuleTwo = myMap
					.get(new Pair(myModuleTwo.getMId(), myModuleTwo.getLId()));
			Pair thirdPair = pairsToChangeWithModuleTwo.get(random.nextInt(pairsToChangeWithModuleTwo.size()));

			Solution thirdModule = null;
			for (Solution ss : solutionnnnn) {
				if (ss.getMId() == thirdPair.getMId() && ss.getLId() == thirdPair.getLId()) {
					thirdModule = ss;
				}
			}
			// System.out.println("Third Module: " + thirdModule);
			solutionnnnn.remove(thirdModule);
			ArrayList<Solution> myChangedSolutions = changeTwoSolution(myModuleTwo, thirdModule);
			if (!cc.checkIfLecturerIsTeaching(solutionnnnn, myChangedSolutions.get(0))
					|| !cc.checkIfLecturerIsTeaching(solutionnnnn, myChangedSolutions.get(1))) {
				solutionnnnn = null;
			}
			solutionnnnn.add(myChangedSolutions.get(0));
			solutionnnnn.add(myChangedSolutions.get(1));
			// System.out.println("New Module Two" + myChangedSolutions.get(0));
			// System.out.println("New Module Three: " +
			// myChangedSolutions.get(1));
			thirdSolution = myChangedSolutions.get(1);
		} catch (NullPointerException e) {
			// System.out.println("Lecturer(s) Teaching Already");

		}

		// System.out.println();
		// System.out.println();

		try {
			HashMap<Pair, ArrayList<Pair>> myMap = getNeighbour(solutionnnnn);

			// System.out.println("Size: " + solutionnnnn.size());
			// System.out.println("Third Solution: " + thirdSolution);
			ArrayList<Pair> pairs = myMap.get(new Pair(thirdSolution.getMId(), thirdSolution.getLId()));

			solutionnnnn.remove(thirdSolution);
			// System.out.println("Size: " + solutionnnnn.size());

			Pair fourthPair = pairs.get(random.nextInt(pairs.size()));

			Solution fourthSolution = null;

			for (Solution ss : solutionnnnn) {
				if (ss.getMId() == fourthPair.getMId() && ss.getLId() == fourthPair.getLId()) {
					fourthSolution = ss;
				}
			}
			// System.out.println("Fourth Solution: " + fourthSolution);
			solutionnnnn.remove(fourthSolution);
			// System.out.println("Size: " + solutionnnnn.size());
			ArrayList<Solution> myChangedSolutions = changeTwoSolution(thirdSolution, fourthSolution);

			if (!cc.checkIfLecturerIsTeaching(solutionnnnn, myChangedSolutions.get(0))
					|| !cc.checkIfLecturerIsTeaching(solutionnnnn, myChangedSolutions.get(1))) {
				solutionnnnn = null;
			}

			// System.out.println("New Third Module: " +
			// myChangedSolutions.get(0));
			// System.out.println("New Fourth Module: " +
			// myChangedSolutions.get(1));

			solutionnnnn.add(myChangedSolutions.get(0));
			solutionnnnn.add(myChangedSolutions.get(1));

			// System.out.println("Size: " + solutionnnnn.size());

		} catch (NullPointerException e) {
			// System.out.println("Lecturer is teaching!");
		}

		return solutionnnnn;
	}

	/**
	 * This method returns a new configuration by changing three Teaching
	 * Activities positions.
	 *
	 * @param map-
	 *            the possible neighbor changes.
	 * @param mapKeys-
	 *            The keys for the hashmap above.
	 * @param random
	 * @param methodSolutions
	 * @return
	 */
	private ArrayList<Solution> changeThree(HashMap<Pair, ArrayList<Pair>> map, List<Pair> mapKeys, Random random,
			ArrayList<Solution> methodSolutions) {
		ArrayList<Solution> solutionnnnn = new ArrayList<>(methodSolutions);
		Pair pOne = mapKeys.get(random.nextInt(mapKeys.size()));
		ArrayList<Pair> moduleOnePairs = map.get(pOne);
		Pair pTwo = moduleOnePairs.get(random.nextInt(moduleOnePairs.size()));
		Solution moduleOne = null;
		Solution moduleTwo = null;
		for (Solution ss : methodSolutions) {
			if (pOne.getMId() == ss.getMId() && pOne.getLId() == ss.getLId()) {
				moduleOne = ss;
			}
			if (pTwo.getMId() == ss.getMId() && pTwo.getLId() == ss.getLId()) {
				moduleTwo = ss;
			}
		}

		solutionnnnn.remove(moduleOne);
		solutionnnnn.remove(moduleTwo);
		ArrayList<Solution> changedSolutions = changeTwoSolution(moduleOne, moduleTwo);
		if (!cc.checkIfLecturerIsTeaching(solutionnnnn, changedSolutions.get(0))
				|| !cc.checkIfLecturerIsTeaching(solutionnnnn, changedSolutions.get(1))) {
			solutionnnnn = null;
		}
		try {
			solutionnnnn.add(changedSolutions.get(0));
			solutionnnnn.add(changedSolutions.get(1));
		} catch (NullPointerException e) {
			// System.out.println("Lecturer(s) Teaching Already");
		}
		//
		// The third one
		//
		try {
			HashMap<Pair, ArrayList<Pair>> myMap = getNeighbour(solutionnnnn);
			Solution myModuleTwo = changedSolutions.get(1);
			solutionnnnn.remove(myModuleTwo);
			ArrayList<Pair> pairsToChangeWithModuleTwo = myMap
					.get(new Pair(myModuleTwo.getMId(), myModuleTwo.getLId()));
			Pair thirdPair = pairsToChangeWithModuleTwo.get(random.nextInt(pairsToChangeWithModuleTwo.size()));

			Solution thirdModule = null;
			for (Solution ss : solutionnnnn) {
				if (ss.getMId() == thirdPair.getMId() && ss.getLId() == thirdPair.getLId()) {
					thirdModule = ss;
				}
			}
			solutionnnnn.remove(thirdModule);
			ArrayList<Solution> myChangedSolutions = changeTwoSolution(myModuleTwo, thirdModule);
			if (!cc.checkIfLecturerIsTeaching(solutionnnnn, myChangedSolutions.get(0))
					|| !cc.checkIfLecturerIsTeaching(solutionnnnn, myChangedSolutions.get(1))) {
				solutionnnnn = null;
			}
			solutionnnnn.add(myChangedSolutions.get(0));
			solutionnnnn.add(myChangedSolutions.get(1));
		} catch (NullPointerException e) {
			// System.out.println("Lecturer(s) Teaching Already");

		}

		return solutionnnnn;
	}

	/**
	 * This method if used in the getNeighborConfiguration method, I have
	 * created it in order to check whether the lecturer is teaching or not
	 * before we make the change. Therefore, the method will run recursively
	 * until it find a change. (DONE-TESTED AND COMPLETELY CORRECT.)
	 * 
	 * @param map
	 * @param mapKeys
	 * @param random
	 * @param methodSolutions
	 * @return
	 */
	private ArrayList<Solution> changeTwo(HashMap<Pair, ArrayList<Pair>> map, List<Pair> mapKeys, Random random,
			ArrayList<Solution> methodSolutions) {
		ArrayList<Solution> solutionnnnn = new ArrayList<>(methodSolutions);
		Pair pOne = mapKeys.get(random.nextInt(mapKeys.size()));
		ArrayList<Pair> moduleOnePairs = map.get(pOne);
		Pair pTwo = moduleOnePairs.get(random.nextInt(moduleOnePairs.size()));
		Solution moduleOne = null;
		Solution moduleTwo = null;
		for (Solution ss : methodSolutions) {
			if (pOne.getMId() == ss.getMId() && pOne.getLId() == ss.getLId()) {
				moduleOne = ss;
			}
			if (pTwo.getMId() == ss.getMId() && pTwo.getLId() == ss.getLId()) {
				moduleTwo = ss;
			}
		}
		solutionnnnn.remove(moduleOne);
		solutionnnnn.remove(moduleTwo);
		ArrayList<Solution> changedSolutions = changeTwoSolution(moduleOne, moduleTwo);

		if (!cc.checkIfLecturerIsTeaching(solutionnnnn, changedSolutions.get(0))
				|| !cc.checkIfLecturerIsTeaching(solutionnnnn, changedSolutions.get(1))) {
			solutionnnnn = null;
		}
		try {
			solutionnnnn.add(changedSolutions.get(0));
			solutionnnnn.add(changedSolutions.get(1));
		} catch (NullPointerException e) {
			// System.out.println("Lecturer(s) Teaching Already");
		}
		return solutionnnnn;

	}

	/**
	 * This method changes two solutions and it returns an array of solutions,
	 * first being the changed old solution and the second changed old solution.
	 * 
	 * @param moduleone
	 * @param moduletwo
	 * @return
	 */
	private ArrayList<Solution> changeTwoSolution(Solution moduleone, Solution moduletwo) {
		if (moduleone.getMId() != 0 && moduletwo.getMId() != 0) {
			ArrayList<Solution> solution = new ArrayList<>();
			Solution solutionOne = new Solution(moduleone.getMId(), moduleone.getLId(), moduletwo.getRId(),
					moduletwo.getDay(), moduletwo.getSt(), moduleone.getDuration(), moduleone.getMmId(),
					moduleone.getType(), moduleone.getLevel(), moduleone.getPOfStudies(), moduleone.getCompulsaries(),
					moduletwo.getLocation(), moduletwo.getRCapacity(), moduleone.getNOfStudents(),
					moduletwo.getRType());
			Solution solutionTwo = new Solution(moduletwo.getMId(), moduletwo.getLId(), moduleone.getRId(),
					moduleone.getDay(), moduleone.getSt(), moduletwo.getDuration(), moduletwo.getMmId(),
					moduletwo.getType(), moduletwo.getLevel(), moduletwo.getPOfStudies(), moduletwo.getCompulsaries(),
					moduleone.getLocation(), moduleone.getRCapacity(), moduletwo.getNOfStudents(),
					moduleone.getRType());
			solution.add(solutionOne);
			solution.add(solutionTwo);

			return solution;
		} else {
			System.out.println("Error with the changeTwoSolution() method.");
			return null;
		}
	}

	/**
	 * Returns an array of a pair of neighbor changes that could be made in the
	 * system.
	 * 
	 * @param mySolutions
	 * @return
	 */
	private HashMap<Pair, ArrayList<Pair>> getNeighbour(ArrayList<Solution> mySolutions) {
		HashMap<Pair, ArrayList<Pair>> pairs = new HashMap<>();
		for (Solution sOne : mySolutions) {
			if (sOne.getMId() != 0) {
				for (Solution sTwo : mySolutions) {
					if (sTwo.getMId() != 0 && sOne != sTwo) {
						if (checkRType(sOne.getType(), sTwo.getRType().split("-"))
								&& checkRType(sTwo.getType(), sOne.getRType().split("-"))
								&& checkRcapacity(sOne.getNOfStudents(), sTwo.getRCapacity())
								&& checkRcapacity(sTwo.getNOfStudents(), sOne.getRCapacity())
								&& sOne.getDuration() == sTwo.getDuration()) {
							Pair key = new Pair(sOne.getMId(), sOne.getLId());
							ArrayList<Pair> thePair = pairs.get(key);
							if (thePair == null) {
								thePair = new ArrayList<>();
							}
							thePair.add(new Pair(sTwo.getMId(), sTwo.getLId()));
							pairs.put(key, thePair);

						}

					}
				}

			}
		}
		return pairs;

	}

	/**
	 * Takes in a type and a rType, return true if type is in RType array.
	 * 
	 * @param type
	 * @param rType
	 */
	private boolean checkRType(String type, String[] rType) {
		boolean rValue = false;
		for (String ss : rType) {
			if (ss.equals(type)) {
				rValue = true;
				break;
			}
		}
		return rValue;

	}

	/**
	 * returns true if moduleNOFStudents<=rCapacity.
	 * 
	 * @param moduleNOfStudents
	 * @param rCapacity
	 * @return
	 */
	public boolean checkRcapacity(int moduleNOfStudents, int rCapacity) {
		boolean rValue = false;
		if (moduleNOfStudents <= rCapacity) {
			rValue = true;
		}
		return rValue;

	}

	/**
	 * This method returns the final solution from the simulated annealing
	 * process.
	 * 
	 * @return
	 */
	public ArrayList<Solution> getSolution() {
		return mySolutions;
	}

	/**
	 * This method is used for development and testing stage of the software.
	 * 
	 * @param myMap
	 */
	public void printHashmap(HashMap<Pair, ArrayList<Pair>> myMap) {
		for (Entry<Pair, ArrayList<Pair>> pair : myMap.entrySet()) {
			System.out.println(pair.getKey() + " : " + pair.getValue());
		}
	}

	public void printSolution(ArrayList<Solution> mySolutions) {
		for (Solution ss : mySolutions) {
			System.out.println(ss);
		}
	}

	public int getGreedyCostFunction() {
		return cc.costFunction(mySolutions);
	}

}