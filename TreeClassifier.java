import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main class to interface the decision tree with users.
 * 
 * @author Robby
 *
 */
public class TreeClassifier {

	private static Scanner kb = new Scanner(System.in);
	private static String fileName;
	private static DataSet testDataSet;

	public static void main(String args[]) {
		while (true) {
			System.out.print(
					  "1) Train a new decision tree\n"
					+ "2) Classify a file based on a trained decision tree\n"
					+ "3) Print a trained decision tree to the console\n"
					+ "4) Check decision tree accuracy\n"
					+ "5) Reduce the tree\n"
					+ "6) Quit\nChoose an option (1-6): ");

			String response = kb.nextLine();
			System.out.println();

			switch (response) {
			case "1":
				train();
				break;
			case "2":
				classifyFile();
				break;
			case "3":
				printTree();
				break;
			case "4":
				checkAccuracy();
				break;
			case "5":
				reduce();
				break;
			case "6":
				kb.close();
				return;
			default:
				System.out.println("Not a valid option.");
				break;
			}

			System.out.println();
		}
	}

	private static void train() {
		System.out.print("Meta file name: ");
		fileName = kb.nextLine();
		StaticData.meta = new MetaData(fileName);
		if (StaticData.meta.classifications.isEmpty()) {
			StaticData.meta = null;
			return;
		}

		System.out.print("Training data file: ");
		fileName = kb.nextLine();
		StaticData.trainingSet = new DataSet(fileName);
		if (StaticData.trainingSet.dataSet.isEmpty()) {
			StaticData.trainingSet = null;
			return;
		}

		StaticData.tree = new DecisionTree(StaticData.trainingSet, StaticData.meta);
		System.out.println("Tree created!");
	}

	private static void classifyFile() {
		if (StaticData.meta == null || StaticData.trainingSet == null) {
			System.out.println("Please train a decision tree first");
			return;
		}
		System.out.print("Input file: ");
		fileName = kb.nextLine();
		testDataSet = new DataSet(fileName);
		if (testDataSet.dataSet.isEmpty()) {
			testDataSet = null;
			return;
		}
		testDataSet.classifyDataSet();
		System.out.println("Classification completed!");

		System.out.print("Output file: ");
		fileName = kb.nextLine();
		System.out.println("Outputting to file...");
		File out = new File(fileName);
		try {
			FileWriter writer = new FileWriter(out);
			writer.write(testDataSet.toString());
			writer.close();
		} catch (IOException e) {
			System.out.print("Output file could not be writen to");
		}

		System.out.println("Output file classification completed!");
	}

	private static void printTree() {
		if (StaticData.meta == null || StaticData.trainingSet == null) {
			System.out.println("Please train a decision tree first.");
			return;
		}
		StaticData.tree.print();
	}

	private static void checkAccuracy() {
		if (StaticData.meta == null || StaticData.trainingSet == null) {
			System.out.println("Please train a decision tree first.");
			return;
		}
		System.out.print("Testing file: ");
		fileName = kb.nextLine();
		testDataSet = new DataSet(fileName);
		if (testDataSet.dataSet.isEmpty()) {
			testDataSet = null;
			return;
		}
		System.out.printf("Labeling accuracy: %.3f%%\n", testDataSet.accuracy() * 100);
	}

	private static void reduce() {
		if (StaticData.meta == null || StaticData.trainingSet == null) {
			System.out.println("Please train a decision tree first.");
			return;
		}
		int count = 0;
		while (StaticData.tree.reduce())
			count++;
		System.out.println("Reduced the tree " + count + " times!");
	}

}
