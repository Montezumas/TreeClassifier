import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A class that represents a data set of data can be used on labeled or
 * unlabeled data
 * 
 * @author Robby
 *
 */
public class DataSet {

	public ArrayList<Data> dataSet;

	public DataSet(String fileName) {
		dataSet = new ArrayList<Data>();

		File dataSetFile = new File(fileName);

		try {
			Scanner reader = new Scanner(dataSetFile);

			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				Data newDataLine = new Data(line);
				if(newDataLine.attributeValues.isEmpty()) {
					break;
				}
				
				dataSet.add(newDataLine);
			}

			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: data set file not found");
			return;
		}
	}
	
	/**
	 * Classify the data set based on the decision tree.
	 */
	public void classifyDataSet() {
		for(Data d : dataSet) {
			d.classification = StaticData.tree.classify(d);
		}
	}
	
	/**
	 * Calculate accuracy for classifying based on the decision tree.
	 * @return
	 */
	public double accuracy() {
		int correct = 0;
		
		for(Data d : dataSet) {
			if(d.classification.equals(StaticData.tree.classify(d)))
				correct++;
		}

		return ((double)correct/dataSet.size());
	}

	@Override
	public String toString() {
		String str = "";

		for (int i = 0; i < dataSet.size(); i++) {
			str += dataSet.get(i).toString();

			if (i < dataSet.size() - 1) {
				str += "\n";
			}
		}

		return str;
	}

}
