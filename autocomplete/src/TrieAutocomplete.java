
import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;


/**
 * General trie/priority queue algorithm for implementing Autocompletor
 *
 *
 */
public class TrieAutocomplete implements Autocompletor {

	protected Node myRoot;

	public TrieAutocomplete(String[] terms, double[] weights) {
		if (terms == null || weights == null)
			throw new NullPointerException("One or more arguments null");
		myRoot = new Node('-', null, 0);
		for (int i = 0; i < terms.length; i++) {
			add(terms[i], weights[i]);
		}
	}

	private void add(String word, double weight) {
		if(word == null){
			throw new NullPointerException("The word cannot be null");
		}
		if(weight < 0){
			throw new IllegalArgumentException("A word may not have a negative weight");
		}
		Node current = myRoot;
		for(int i = 0; i < word.length(); i++){
			if(current.mySubtreeMaxWeight < weight){
				current.mySubtreeMaxWeight = weight;
			}
			char nextCh = word.charAt(i);
			if (!current.children.containsKey(nextCh)){
				current.children.put(nextCh, new Node(word.charAt(i), current, weight));
			}
			current = current.children.get(nextCh);
		}
		current.isWord = true;
		if(current.isWord && current.myWeight > weight){
			current.myWeight = weight;
			while(current != null){
				double maxWeight = -1;
				for(Character ch : current.children.keySet()){
					if(current.children.get(ch).mySubtreeMaxWeight > maxWeight){
						maxWeight = current.children.get(ch).mySubtreeMaxWeight;
					}
				}
				current.mySubtreeMaxWeight = maxWeight;
				current = current.parent;
			}
		}
		else{
			current.isWord = true;
			current.myWeight = weight;
			current.myWord = word;
		}
	}

	@Override
	public String[] topKMatches(String prefix, int k) {
		if(prefix == null){
			throw new NullPointerException("The prefix cannot be null");
		}
		Node current = navigateToPrefix(prefix);
		if(current == null){
			return new String[0];
		}
		PriorityQueue<Node> pq = new PriorityQueue<Node>(k, new Node.ReverseSubtreeMaxWeightComparator());
		PriorityQueue<Node> minMatches = new PriorityQueue<Node>(k);
		pq.add(current);
		while(!pq.isEmpty()){
			current = pq.remove();
			if(minMatches.size() == k && minMatches.peek().myWeight < current.myWeight && current.isWord){
				minMatches.poll();
				minMatches.add(current);
			}
			else if(current.isWord){
				minMatches.add(current);
			}
			for(Character ch : current.children.keySet()){
				pq.add(current.children.get(ch));
			}
		}
		return queueToStringArray(minMatches, k);
	}

	public String[] queueToStringArray(PriorityQueue<Node> a, int k){
		ArrayList<Node> maxes = new ArrayList<Node>();
		while(!a.isEmpty()){
			maxes.add(a.poll());
		}
		Collections.reverse(maxes); //makes the nodes in order from largest weight to smallest
		String[] ret = new String[Math.min(maxes.size(), k)];
		for(int i = 0; i < Math.min(maxes.size(), k); i++){
			ret[i] = maxes.get(i).myWord;
		}
		return ret;
	}

	@Override
	public String topMatch(String prefix) {
		if(prefix == null){
			throw new NullPointerException("The prefix cannot be null");
		}
		Node current = myRoot;
		current = navigateToPrefix(prefix);
		if(current == null){
			return "";
		}
		if(current.mySubtreeMaxWeight == current.myWeight && current.isWord){
			return current.myWord;
		}
		while(current.mySubtreeMaxWeight != current.myWeight && !current.isWord){
			for(Character ch : current.children.keySet()){
				if(current.children.get(ch).mySubtreeMaxWeight == current.mySubtreeMaxWeight){
					current = current.children.get(ch);
					break;
				}
			}
		}
		return current.myWord;
	}

	public Node navigateToPrefix(String prefix){
		if(prefix == null){
			throw new NullPointerException("The prefix cannot be null");
		}
		Node current = myRoot;
		for(int i = 0; i < prefix.length(); i++){
			char nextCh = prefix.charAt(i);
			if(current.children.containsKey(nextCh)){
				current = current.children.get(nextCh);
			}
			else{
				return null;
			}
		}
		return current;
	}
}
