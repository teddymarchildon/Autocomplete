import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 
 * Using a sorted array of Term objects, this implementation uses binary search to find the
 * top term(s).
 * 
 *
 */
public class BinarySearchAutocomplete implements Autocompletor {

	Term[] myTerms;

	public BinarySearchAutocomplete(String[] terms, double[] weights) {
		if (terms == null || weights == null)
			throw new NullPointerException("One or more arguments null");
		myTerms = new Term[terms.length];
		for (int i = 0; i < terms.length; i++) {
			myTerms[i] = new Term(terms[i], weights[i]);
		}
		Arrays.sort(myTerms);
	}

	public static int firstIndexOf(Term[] a, Term key, Comparator<Term> comparator) {
		if(a.length == 0){
			return -1;
		}
		int low = 0;
		int high = a.length;
		boolean isFound = false;
		int found = a.length;
		while(high - low > 1){
			int mid = (low + high) / 2;
			if(comparator.compare(a[mid], key) == 0){
				high = mid;
				found = mid;
				isFound = true;
				continue;
			}
			else if(comparator.compare(a[mid], key) < 0){
				low = mid;
			}
			else{
				high = mid;
			}
		}

		if(low < found && comparator.compare(a[low], key) == 0){
			return low;
		}
		else if(isFound){
			return found;
		}
		return -1;
	}

	public static int lastIndexOf(Term[] a, Term key, Comparator<Term> comparator) {
		if(a.length == 0){
			return -1;
		}
		int low = 0;
		int high = a.length;
		int found = 0;
		boolean isFound = false;
		while(high - low > 1){
			int mid = (low + high) / 2;
			if(comparator.compare(a[mid], key) == 0){
				low = mid;
				found = mid;
				isFound = true;
				continue;
			}
			else if(comparator.compare(a[mid], key) < 0){
				low = mid;
			}
			else{
				high = mid;
			}
		}
		if(high > found && comparator.compare(a[high - 1], key) == 0){
			return high - 1;
		}
		else if(isFound){
			return found;
		}
		return -1;
	}

	public String[] topKMatches(String prefix, int k) {
		if(prefix == null){
			throw new NullPointerException("The prefix argument can't be null");
		}
		int firstDex = firstIndexOf(myTerms, new Term(prefix, 1), new Term.PrefixOrder(prefix.length()));
		if(firstDex == -1){
			String[] s = {};
			return s;
		}
		int lastDex = lastIndexOf(myTerms, new Term(prefix, 1), new Term.PrefixOrder(prefix.length()));
		PriorityQueue<Term> pq = new PriorityQueue<Term>(k, new Term.WeightOrder());
		for(int i = firstDex; i <= lastDex; i++){
			if(!myTerms[i].getWord().startsWith(prefix)) continue;
			if (pq.size() < k) {
				pq.add(myTerms[i]);
			} else if (pq.peek().getWeight() < myTerms[i].getWeight()) {
				pq.remove();
				pq.add(myTerms[i]);
			}
		}
		int numResults = Math.min(k, pq.size());
		String[] ret = new String[numResults];
		for (int i = 0; i < numResults; i++) {
			ret[numResults - 1 - i] = pq.remove().getWord();
		}
		return ret;
	}

	@Override
	public String topMatch(String prefix){
		if(prefix == null){
			throw new NullPointerException("The argument is null");
		}
		int firstDex = firstIndexOf(myTerms, new Term(prefix, 1), new Term.PrefixOrder(prefix.length()));
		if(firstDex == -1){
			return "";
		}
		int lastDex = lastIndexOf(myTerms, new Term(prefix, 1), new Term.PrefixOrder(prefix.length()));
		String maxTerm = "";
		double maxWeight = -1;
		for(int i = firstDex; i <= lastDex; i++) {
			if(myTerms[i].getWeight() > maxWeight && myTerms[i].getWord().startsWith(prefix)){
				maxTerm = myTerms[i].getWord();
				maxWeight = myTerms[i].getWeight();
			}
		}
		return maxTerm;
	}
}
