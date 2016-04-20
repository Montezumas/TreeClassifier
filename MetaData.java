import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A class that describes meta data for a data type
 * 
 * @author Robby
 *
 */
public class MetaData {

	public ArrayList<String> attributes;
	public ArrayList<String> classifications;

	// the list at index i in attributeValues is the list of possible values for
	// an attribute at index i in the attributes list
	public ArrayList<ArrayList<String>> attributeValues;

	public MetaData(String fileName) {
		attributes = new ArrayList<String>();
		attributeValues = new ArrayList<ArrayList<String>>();
		classifications = new ArrayList<String>();

		File metaFile = new File(fileName);

		try {
			Scanner reader = new Scanner(metaFile);

			while (reader.hasNextLine()) {
				String line = reader.nextLine();

				if (reader.hasNextLine()) {
					// reading attributes
					if(line.indexOf(':') == -1) {
						System.out.println("This is not a meta file");
						break;
					}
					attributes.add(line.substring(0, line.indexOf(':')));
					attributeValues.add(new ArrayList<String>());

					line = line.substring(line.indexOf(':') + 1);

					while (line.indexOf(',') != -1) {
						attributeValues.get(attributes.size() - 1).add(line.substring(0, line.indexOf(',')));
						line = line.substring(line.indexOf(',') + 1);
					}

					attributeValues.get(attributes.size() - 1).add(line);

				} else {
					// reading classifications
					line = line.substring(line.indexOf(':') + 1);

					while (line.indexOf(',') != -1) {
						classifications.add(line.substring(0, line.indexOf(',')));
						line = line.substring(line.indexOf(',') + 1);
					}

					classifications.add(line);
				}
			}

			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error: meta data file not found");
			return;
		}
	}

	@Override
	public String toString() {
		String str = "";

		for (int i = 0; i < attributes.size(); i++) {
			String attr = attributes.get(i);
			ArrayList<String> possibleValues = attributeValues.get(i);

			str += attr + ":";

			for (int j = 0; j < possibleValues.size(); j++) {
				str += possibleValues.get(j);

				if (j < possibleValues.size() - 1) {
					str += ",";
				}
			}
			str += "\n";
		}

		str += "class:";

		for (int i = 0; i < classifications.size(); i++) {
			str += classifications.get(i);

			if (i < classifications.size() - 1) {
				str += ",";
			}
		}

		return str;
	}

}
