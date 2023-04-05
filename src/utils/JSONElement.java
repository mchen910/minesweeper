/*
    Name:       Matthew Chen
    Date:       3/31/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
*/


package utils;

/**
 * This class is for polymorphism purposes. Both JSONObjects and JSONArrays will subclass 
 * this, which allows for every JSONObject to be a HashMap of String keys and JSONElement values, 
 * and for every JSONArray to have an ArrayList of JSONElements. Each JSONElement has a 
 * method to determine what type of element it is, so the user can typecast them accordingly.
 * 
 * @author Matthew Chen
 */
public class JSONElement {
	
	public enum Type {
		// Regular elements
		INTEGER, DOUBLE, STRING, BOOLEAN,
		
		// JSON arrays
		ARRAY,
		
		// JSON objects
		OBJECT
	}
	
	
	private Type type;
	private String value;
	
	private final String ERROR_MSG = "Double quotes can lead to unpredictable results. " + 
			"Please make sure to use single quotes for all JSON";
	
	
	/**
	 * Create a JSON element with its json string and its type (refer to JSONElement.Type).
	 * @param json String representation of JSON
	 * @param type Type of element (Type.OBJECT, Type.ARRAY, etc.)
	 * @throws IllegalArgumentException Throws an exception if the JSON contains double quotes. 
	 * Call JSONElement.cleanup() on the string to format it correctly.
	 */
	public JSONElement(String json, Type type) throws IllegalArgumentException {
		if (json.indexOf("\"") != -1)
			throw new IllegalArgumentException(ERROR_MSG);
		
		this.type = type;
		this.value = json;
	}

	
	/**
	 * Get the JSON string underlying a JSONElement object.
	 * @return JSON string
	 */
	public String getValue() {
		return value;
	}
	
	
	/**
	 * Get the type of JSONElement. Refer to JSONElement.Type for a list of possible types. 
	 * Use this method to figure out what to cast a JSONElement to, so the object/array and 
	 * the JSONElements it contains become accessible.
	 * @return Type of JSONElement
	 */
	public Type getType() {
		return type;
	}
	
	
	/**
	 * Cleanup a JSON string to be able to pass it into the constructors of JSONElement, 
	 * JSONArray, or JSONObject. This static method changes double quotes to single quotes, 
	 * and removes spaces between everything that isn't a string.
	 * @param jsonStr JSON string
	 * @return Formatted string
	 */
	public static String cleanup(String jsonStr) {
		String s = "";
		boolean inQuotes = false;
		
		// Replace double with single quotes
		jsonStr = jsonStr.replace('\"', '\'');
		
		for (int i = 0; i < jsonStr.length(); i++) {
			char c = jsonStr.charAt(i);
			
			if (c == '\'')
				inQuotes = !inQuotes;
			
			if (inQuotes)
				s += c;
			
			else if (!inQuotes && c != ' ')
				s += c;
		}
		
		return s;
	}
	
	
	public String toString() {
		return this.value + ",\n";
	}
}
