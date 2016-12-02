import java.util.ArrayList;
import java.util.Arrays;

//7-4-2016

public class Permutation {
	String[] array;

	/**
	 * This is class that permutates an array of dates. The class is used in the
	 * database (and possibly the GA class).
	 * 
	 * @param array
	 */
	public Permutation(String[] array) {
		this.array = array;
	}

	/**
	 * This method returns a two dimensional array of all possible combination
	 * of days.
	 * 
	 * @return
	 */
	public String[][] permutate() {
		String[][] myArray = new String[120][5];
		int nmbers = 0;
		for (int i = 0; i < array.length; i++) {
			String one = array[i];
			String fourElements[] = removeAnElement(array, one);
			// Four elements
			for (int j = 0; j < fourElements.length; j++) {
				String two = fourElements[j];
				// Three elements
				String threeElement[] = removeAnElement(fourElements, two);
				for (int k = 0; k < threeElement.length; k++) {
					String three = threeElement[k];
					// Two Elements
					String[] twoElement = removeAnElement(threeElement, three);
					for (int l = 0; l < twoElement.length; l++) {
						String four = twoElement[l];
						String[] fiveElement = removeAnElement(twoElement, four);
						for (String s : fiveElement) {
							String[] ar = { one, two, three, four, s };
							myArray[nmbers] = ar;
							nmbers++;
						}
					}
				}
			}
		}
		return myArray;
	}

	/**
	 * This method removes an element from a given array.
	 * 
	 * @param array
	 * @param element
	 * @return
	 */
	public String[] removeAnElement(String[] array, String element) {
		ArrayList<String> copy = new ArrayList<String>(Arrays.asList(array));
		copy.remove(element);
		String[] dest = copy.toArray(new String[copy.size()]);
		return dest;
	}
}