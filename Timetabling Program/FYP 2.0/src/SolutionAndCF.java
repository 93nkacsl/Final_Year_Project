import java.util.ArrayList;

//7-4-2016

public class SolutionAndCF implements Comparable<SolutionAndCF> {
	private int costFunction;
	private ArrayList<Solution> theSolution;
	private double probability;

	public SolutionAndCF(ArrayList<Solution> theSolution, int costFunction) {
		this.theSolution = theSolution;
		this.costFunction = costFunction;
		this.probability = 0;
	}

	public SolutionAndCF(ArrayList<Solution> theSolution, int costFunction, double probability) {
		this.theSolution = theSolution;
		this.costFunction = costFunction;
		this.probability = probability;
	}

	public int getCostFunction() {
		return costFunction;
	}

	public ArrayList<Solution> getSolution() {
		return theSolution;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	public int compareTo(SolutionAndCF o) {
		int x = o.getCostFunction();
		return this.getCostFunction() - x;
	}

}
