/*
    Name:       Matthew Chen
    Date:       3/31/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
*/


package utils;

import java.util.HashMap;
import java.util.Map;


public class JSONObject extends JSONElement {
	
	public static final char CURLY_OPEN_BRACKET = '{';
	public static final char CURLY_CLOSE_BRACKET = '}';
	public static final char SQUARE_OPEN_BRACKET = '[';
	public static final char SQUARE_CLOSE_BRACKET = ']';
	public static final char COMMA = ',';
	public static final char COLON = ':';
	public static final char PERIOD = '.';
	public static final char SINGLE_QUOTE = '\'';
	public static final char SPECIAL_CHAR = '|';
	
	/** Underlying representation is a HashMap */
	private HashMap<String, JSONElement> objects;
	
	
	public JSONObject(String json) throws IllegalArgumentException {
		super(json, JSONElement.Type.OBJECT);
		this.getObjects(json);
	}
	
	
	public JSONElement getJSONElement(String key) {
		return this.objects.getOrDefault(key, null);
	}

	
	private void getObjects(String jsonStr) {
		if (!(jsonStr.charAt(0) == CURLY_OPEN_BRACKET && 
			  jsonStr.charAt(jsonStr.length() - 1) == CURLY_CLOSE_BRACKET))
			return;
		
		
		this.objects = new HashMap<>();
		
		if (jsonStr.equals("{}"))
			return;
		
		/**
		 * Steps to parsing JSON
		 * 1. Remove the first and last brackets
		 * 2. Identify the commas within JSON arrays and change those to some other character
		 * 3. Split the string through its commas
		 * 4. For each portion of a string, split through its first colon to get the key and the value
		 * 5. Recursively work through the JSON string and add to the objects HashMap
		 */
		
		StringBuilder builder = new StringBuilder(jsonStr);
		builder.deleteCharAt(0);
		builder.deleteCharAt(builder.length() - 1);
		
		int bracketCount = 0;
		for (int i = 0; i < builder.length(); i++) {
			if (builder.charAt(i) == SQUARE_OPEN_BRACKET || builder.charAt(i) == CURLY_OPEN_BRACKET) 
				bracketCount++;
			
			else if (builder.charAt(i) == SQUARE_CLOSE_BRACKET || builder.charAt(i) == CURLY_CLOSE_BRACKET)
				bracketCount--;
			
			if (bracketCount > 0) {
				if (builder.charAt(i) == COMMA)
					builder.setCharAt(i, SPECIAL_CHAR);
			}
		}
		
		for (String s : builder.toString().split("" + COMMA)) {
			s = s.trim();
			
			int colonIdx = s.indexOf(COLON);
			String key = s.substring(1, colonIdx - 1);
			String value = s.substring(colonIdx + 1);
			
			// Switch the special characters back
			value = value.replace(SPECIAL_CHAR, COMMA);
			
			this.objects.put(key, this.getRecursiveObjects(value));
		}
		
	}
	
	
	private JSONElement getRecursiveObjects(String jsonStr) {
		/**
		 * 3 cases for jsonStr:
		 * Case 1: jsonStr is another object
		 * Case 2: jsonStr is an array of more arrays/objects/ints/doubles/Strings
		 * Case 3: jsonStr is a regular int/double/String
		 */
		
		StringBuilder builder = new StringBuilder(jsonStr);
		
		// Case 1: Object
		if (builder.charAt(0) == CURLY_OPEN_BRACKET && 
			builder.charAt(builder.length() - 1) == CURLY_CLOSE_BRACKET) {
			return new JSONObject(jsonStr);
		}
		
		
		// Case 2: Array
		if (builder.charAt(0) == SQUARE_OPEN_BRACKET && 
			builder.charAt(builder.length() - 1) == SQUARE_CLOSE_BRACKET) {
			return new JSONArray(jsonStr);
		}
		

		if (builder.charAt(0) == SINGLE_QUOTE)
			return new JSONElement(jsonStr.substring(1, jsonStr.length() - 1), JSONElement.Type.STRING);
			
		if (builder.indexOf("" + PERIOD) != -1)
			return new JSONElement(jsonStr, JSONElement.Type.DOUBLE);
			
		return new JSONElement(jsonStr, JSONElement.Type.INTEGER);

	}
	
	
	@Override
	public String toString() {
		String json = "";
		
		json += "{\n";
		
		for (Map.Entry<String,JSONElement> elem : this.objects.entrySet()) {
			JSONElement value = elem.getValue();
			json += "\t" + elem.getKey() + ": ";
			if (value.getType() == JSONElement.Type.OBJECT)
				json += ((JSONObject)value).toString();
			else if (value.getType() == JSONElement.Type.ARRAY)
				json += ((JSONArray)value).toString();
			else
				json += value.toString();
			json += '\n';
		};
		
		json += "},\n";
		
		return json;
		
	}
	
}
