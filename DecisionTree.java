import java.util.ArrayList;
import java.util.Collections;

import DecisionTree.Node;

/**
 * A train-able decision tree to be used for classification
 * 
 * @author Robby
 *
 */
public class DecisionTree {

	// head of the tree
	private Node head;

	/**
	 * Constructor that will create the tree based on a training set
	 * 
	 * @param set
	 * @throws Exception
	 */
	public DecisionTree(DataSet set, MetaData meta) {
		head = constructTree(set.dataSet, meta.attributes, meta.attributeValues);
	}

	/**
	 * Recursively construct the tree
	 * 
	 * @param examples
	 * @param attributes
	 * @param values
	 * @return the complete decision tree
	 */
	private Node constructTree(ArrayList<Data> examples, ArrayList<String> attributes, ArrayList<ArrayList<String>> values) {
		if(examples.size() == 1) {
			return new Node(null, examples.get(0).classification);
		}
		
		for (int i = 1; i < examples.size(); i++) {
			if (!examples.get(i).classification.equals(examples.get(i - 1).classification)) {
				// there are more than one classifications in examples
				break;
			} else if (i == examples.size() - 1) {
				// create leaf node with only class in examples
				return new Node(null, examples.get(i).classification);
			}
		}

		if (attributes.isEmpty()) {
			// create leaf node with most common class
			return new Node(null, mostCommon(examples));
		}

		ArrayList<Double> gainList = new ArrayList<Double>();

		for (String attri : attributes) {
			gainList.add(gain(examples, StaticData.meta.attributes.indexOf(attri)));
		}

		// this indexOf might cause issues
		int indexForAttriA = gainList.indexOf(Collections.max(gainList));
		String attriA = attributes.get(indexForAttriA);

		Node root = new Node(null, attriA);
		ArrayList<String> posValues = values.get(indexForAttriA);

		for (int i = 0; i < posValues.size(); i++) {
			String vi = posValues.get(i);

			ArrayList<Data> subsetExamples = new ArrayList<Data>();

			for (Data d : examples) {
				if (d.attributeValues.get(StaticData.meta.attributes.indexOf(attriA)).equals(vi)) {
					subsetExamples.add(d);
				}
			}

			// adding new branch
			root.child.add(new Node(vi, null));
			if (subsetExamples.isEmpty()) {
				// adding node data to the branch
				root.child.get(i).value = mostCommon(examples);
			} else {
				// need to clone to avoid changing the DataAnalyzer meta data
				@SuppressWarnings("unchecked")
				ArrayList<String> tempAttr = (ArrayList<String>) attributes.clone();
				@SuppressWarnings("unchecked")
				ArrayList<ArrayList<String>> tempVal = (ArrayList<ArrayList<String>>) values.clone();

				// recursively create subtree
				tempAttr.remove(indexForAttriA);
				tempVal.remove(indexForAttriA);
				Node subTree = constructTree(subsetExamples, tempAttr, tempVal);

				// connect subtree to root
				subTree.input = vi;
				root.child.set(i, subTree);
			}
		}

		return root;
	}

	/**
	 * Finds the most common class for a given set
	 * 
	 * @param set
	 * @return most common class
	 */
	private String mostCommon(ArrayList<Data> set) {
		ArrayList<Integer> counts = new ArrayList<Integer>();

		for (String c : StaticData.meta.classifications) {
			counts.add(countOccurOfClass(c, set));
		}

		return StaticData.meta.classifications.get(counts.indexOf(Collections.max(counts)));
	}

	/**
	 * Does the counts for finding most common class
	 * 
	 * @param classif
	 * @param set
	 * @return count
	 */
	private int countOccurOfClass(String classif, ArrayList<Data> set) {
		int count = 0;

		for (int i = 0; i < set.size(); i++) {
			if (set.get(i).classification.equals(classif)) {
				count++;
			}
		}

		return count;
	}

	/**
	 * Calculate the entropy for a set. Formula: SUM(-p*Log2(p))
	 * 
	 * @param set
	 * @return
	 */
	private double entropy(ArrayList<Data> set) {
		double entropy = 0.0;

		for (String classif : StaticData.meta.classifications) {
			double proportion = ((double) countOccurOfClass(classif, set) / set.size());
			if (proportion != 0.0) {
				entropy += (proportion * (-1.0)) * (Math.log(proportion) / Math.log(2));
			}
		}

		return entropy;
	}

	/**
	 * calculate the gain for splitting on attribute from the attribute index on
	 * the set. Formula: Entropy(set) - SUM((|subset|/|set|)*Entropy(subset))
	 * 
	 * @param set
	 * @param attributeIndex
	 * @return
	 */
	private double gain(ArrayList<Data> set, int attributeIndex) {
		double sum = 0.0;

		for (int i = 0; i < StaticData.meta.attributeValues.get(attributeIndex).size(); i++) {
			ArrayList<Data> temp = new ArrayList<Data>();
			for (Data d : set) {
				if (d.attributeValues.get(attributeIndex).equals(StaticData.meta.attributeValues.get(attributeIndex).get(i))) {
					temp.add(d);
				}
			}

			if (!temp.isEmpty()) {
				sum += (((double) temp.size() / set.size()) * entropy(temp));
			}
		}

		return entropy(set) - sum;
	}

	/**
	 * Classify a data instance that may or may not already be classified with
	 * this decision tree
	 * 
	 * @param input
	 * @return the classification
	 */
	public String classify(Data input) {
		return classify(head, input);
	}

	/**
	 * Recursively classify a data input for this tree
	 * 
	 * @param node
	 * @param input
	 * @return
	 */
	private String classify(Node node, Data input) {
		if (node.child.isEmpty()) {
			// we're at a leaf
			return node.value;
		}

		int indexInInput = StaticData.meta.attributes.indexOf(node.value);

		for (int i = 0; i < node.child.size(); i++) {
			if (node.child.get(i).input.equals(input.attributeValues.get(indexInInput))) {
				return classify(node.child.get(i), input);
			}
		}

		// error occurred
		return null;
	}

	/**
	 * Print the tree depth first
	 */
	public void print() {
		print(head, 0);
	}

	/**
	 * Recursively print this tree in a depth first manner
	 * 
	 * @param node
	 * @param level
	 */
	private void print(Node node, int level) {
		if (node.input != null) {
			for (int i = 0; i < level - 1; i++) {
				System.out.print("    ");
			}

			System.out.println(" = " + node.input);
		}

		for (int i = 0; i < level; i++) {
			System.out.print("    ");
		}

		System.out.println(node.value);

		for (int i = 0; i < node.child.size(); i++) {
			print(node.child.get(i), level + 1);
		}
	}

	// if reduction occurred during the reduce method
	private boolean reduced;

	/**
	 * Reduce unnecessary branches
	 * 
	 * @return
	 */
	public boolean reduce() {
		reduced = false;
		reduce(head);
		return reduced;
	}

	/**
	 * Recursively look at every node to remove unnecessary branches.
	 * 
	 * @param node
	 */
	private void reduce(Node node) {
		if (node.child.isEmpty()) {
			return;
		}

		boolean prune = true;
		Node first = node.child.get(0);
		for (int i = 1; i < node.child.size(); i++) {
			if (!node.child.get(i).value.equals(node.child.get(i - 1).value) || !node.child.get(i).child.isEmpty()) {
				prune = false;
				break;
			}
		}

		if (prune) {
			reduced = true;
			node.value = first.value;
			node.child.clear();
		} else {
			for (int i = 0; i < node.child.size(); i++) {
				reduce(node.child.get(i));
			}
		}
	}

	/**
	 * A node for the tree
	 * 
	 * @author Robby
	 *
	 */
	private class Node {

		// the value on the branch to this node
		public String input;
		// the Nodes that are linked to this Node as children
		public ArrayList<Node> child;
		// the value of the node
		public String value;

		/**
		 * Construct a Node.
		 * 
		 * @param in
		 * @param v
		 */
		public Node(String in, String v) {
			input = in;
			value = v;
			child = new ArrayList<Node>();
		}
	}

}
