
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.prefs.Preferences;

//7-4-2016

public class GA {
	private int population;
	private Database db;
	private ConstraintChecker cc;
	private DateAndTime d;
	private ArrayList<SolutionAndCF> thePopulation;
	private Crossover crossover;
	private double percentile;
	private ArrayList<Solution> finalSolution;

	/**
	 * 
	 * @param population
	 * @param db
	 * @param cc
	 * @param d
	 * @param fraction:
	 *            the per
	 */
	public GA(Preferences pref, int population, Database db, ConstraintChecker cc, DateAndTime d, double percentile,
			boolean singleOrDouble) {
		this.population = population;
		this.db = db;
		this.d = d;
		this.cc = cc;
		this.percentile = percentile;
		boolean greedyValue = pref.getBoolean("greedyValue", true);
		crossover = new Crossover(db, cc, d, 0.3, 0.7);
		GreedyAlgorithm greedy = new GreedyAlgorithm(db, cc, d, greedyValue);
		SA sa = new SA(greedy.getSolution(), cc, population);
		thePopulation = sa.generatePopulation();
		startProcess(thePopulation);
	}

	public void startProcess(ArrayList<SolutionAndCF> thePopulation) {
		int sum = getCostFunctionSum(thePopulation);
		setPopulationProbability(thePopulation, sum);
		ArrayList<SolutionAndCF> offSprings = generateChildren(thePopulation, true);
		System.out.println("Final: " + offSprings.get(0).getCostFunction());
		finalSolution = offSprings.get(0).getSolution();
	}

	public ArrayList<Solution> getSolution() {
		System.out.println("FS: " + finalSolution.size());
		return finalSolution;
	}

	/**
	 * This is a recursive method which find the best solution
	 * 
	 * @param thePopulation
	 * @param singleOrDouble
	 * @return
	 */
	private ArrayList<SolutionAndCF> generateChildren(ArrayList<SolutionAndCF> thePopulation, boolean singleOrDouble) {
		System.out.println("COST: " + thePopulation.get(0).getCostFunction());
		if (thePopulation.size() < 10) {
			return thePopulation;
		} else {
			ArrayList<SolutionAndCF> offSprings = new ArrayList<>();
			int offSpringSize = thePopulation.size() / 2;
			while (offSprings.size() < offSpringSize) {
				ArrayList<Solution> parentOne = null;
				ArrayList<Solution> parentTwo = null;
				Random random = new Random();
				while (parentOne == null || parentTwo == null) {
					int parentOnePos = random.nextInt(getPercentilePos(thePopulation));
					int parentTwoPos = random.nextInt(getPercentilePos(thePopulation));
					parentOne = thePopulation.get(parentOnePos).getSolution();
					parentTwo = thePopulation.get(parentTwoPos).getSolution();
				}
				ArrayList<Solution> child = null;
				if (singleOrDouble) {
					child = crossover.singlePointCrossover(parentOne, parentTwo);
				} else {
					child = crossover.DoublePointCrossover(parentOne, parentTwo);
				}
				ArrayList<SolutionAndCF> sCF = new ArrayList<>();
				sCF.add(new SolutionAndCF(parentOne, cc.costFunction(parentOne)));
				sCF.add(new SolutionAndCF(parentTwo, cc.costFunction(parentTwo)));
				sCF.add(new SolutionAndCF(child, cc.costFunction(child)));
				Collections.sort(sCF);
				offSprings.add(sCF.get(0));
			}
			System.out.println("Size: " + offSprings.size());
			Collections.sort(offSprings);
			return generateChildren(offSprings, singleOrDouble);
		}

	}

	private int getPercentilePos(ArrayList<SolutionAndCF> thePopulation) {
		double pos = thePopulation.size() * percentile;
		return (int) pos;
	}

	/**
	 * This method sets the probability of the solutions in the population.
	 * 
	 * @param sum
	 */
	private void setPopulationProbability(ArrayList<SolutionAndCF> thePopulation, int sum) {
		// ArrayList<SolutionAndCF> copiedPop = new ArrayList<>(thePopulation);
		for (SolutionAndCF solutionAndCF : thePopulation) {
			double probability = (double) solutionAndCF.getCostFunction() / sum;
			solutionAndCF.setProbability(probability);
		}

	}

	/**
	 * This method returns the total cost function of the population.
	 * 
	 * @param thePopulation
	 * @return
	 */
	private int getCostFunctionSum(ArrayList<SolutionAndCF> thePopulation) {
		int sum = 0;
		for (SolutionAndCF sCf : thePopulation) {
			sum += sCf.getCostFunction();
		}
		return sum;
	}

	/**
	 * This method generates a population of size 'population' and it returns it
	 * as a list of solutions.
	 * 
	 * @return
	 */
	public ArrayList<SolutionAndCF> generatePopulation() {
		ArrayList<SolutionAndCF> solutions = new ArrayList<>();
		while (solutions.size() < population) {
			GreedyAlgorithm greedy = new GreedyAlgorithm(db, cc, d, false);
			ArrayList<Solution> theSolution = greedy.getSolution();
			solutions.add(new SolutionAndCF(theSolution, cc.costFunction(theSolution)));
			// System.out.println("Size: "+solutions.size());
		}
		Collections.sort(solutions);
		return solutions;
	}

	public void printTheSolution(ArrayList<Solution> theS) {
		for (Solution ss : theS) {
			System.out.println(ss.getMId() + " , " + ss.getLId() + "  " + ss.getDay() + " , " + ss.getSt() + "-"
					+ ss.getEndTime() + "  rId:" + ss.getRId());
		}
	}
}
