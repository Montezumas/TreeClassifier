import java.util.ArrayList;

/**
 * One data instance
 * 
 * @author Robby
 *
 */
public class Data {

	public ArrayList<String> attributeValues;
	public String classification;

	public Data(String line) {
		attributeValues = new ArrayList<String>();

		int attributeIndex = 0;

		while (line.indexOf(',') != -1) {

			if (StaticData.meta.attributeValues.get(attributeIndex).contains(line.substring(0, line.indexOf(',')))) {
				attributeValues.add(line.substring(0, line.indexOf(',')));
			} else {
				System.out.println("Error: '" + line.substring(0, line.indexOf(',')) + "' is not a possible attribute value "
						+ "for " + StaticData.meta.attributes.get(attributeIndex));
				return;
			}

			line = line.substring(line.indexOf(',') + 1);

			attributeIndex++;
		}

		if (attributeValues.size() < StaticData.meta.attributes.size()) {
			// no classification is provided
			attributeValues.add(line);
			classification = null;
		} else {
			// class is provided
			classification = line;
		}
	}

	@Override
	public String toString() {
		String str = "";

		for (int i = 0; i < attributeValues.size(); i++) {
			str += attributeValues.get(i) + ",";
		}

		str += classification;

		return str;
	}

}
