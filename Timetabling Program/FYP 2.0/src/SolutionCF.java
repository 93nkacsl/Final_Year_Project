
// 7-4-2016

public class SolutionCF implements Comparable<SolutionCF> {
	Solution solution;
	int cf;

	public SolutionCF(Solution solution, int cf) {
		this.cf = cf;
		this.solution = solution;
	}

	public int getCF() {
		return cf;
	}

	public Solution getSolution() {
		return solution;
	}

	@Override
	public int compareTo(SolutionCF object) {
		int objectCF = object.getCF();
		return this.cf - objectCF;
	}

}
