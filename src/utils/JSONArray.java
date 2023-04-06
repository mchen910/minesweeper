/*
    Name:       Matthew Chen
    Date:       3/31/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
 */


package utils;

import java.util.ArrayList;


public class JSONArray extends JSONElement {

    public static final char CURLY_OPEN_BRACKET = '{';
    public static final char CURLY_CLOSE_BRACKET = '}';
    public static final char SQUARE_OPEN_BRACKET = '[';
    public static final char SQUARE_CLOSE_BRACKET = ']';
    public static final char COMMA = ',';
    public static final char COLON = ':';
    public static final char PERIOD = '.';
    public static final char SINGLE_QUOTE = '\'';
    public static final char SPECIAL_CHAR = '|';

    /** Underlying representation is an ArrayList */
    private ArrayList<JSONElement> elements;


    /**
     * Constructor for a JSONArray. 
     * @param json JSON string that should begin with an open square bracket and 
     * end with a close square bracket. If not, no elements will get parsed from this string.
     * @throws IllegalArgumentException Throws an exception if the JSON element contains 
     * double quotes.
     */
    public JSONArray(String json) throws IllegalArgumentException {
        super(json, JSONElement.Type.ARRAY);
        this.getElements(json);
    }


    /**
     * Get the JSONElement object at a particular index in a JSONArray. 
     * @param idx Index of the array
     * @return JSONElement at that index
     */
    public JSONElement getJSONElement(int idx) {
        return this.elements.get(idx);
    }


    /**
     * Get the size of the JSONArray.
     * @return The size of the array.
     */
    public int size() {
        return this.elements.size();
    }


    private JSONArray(String json, ArrayList<JSONElement> jsonArr) {
        super(json, JSONElement.Type.ARRAY);
        this.elements = jsonArr;
    }


    private void getElements(String jsonStr) {
        if (!(jsonStr.charAt(0) == SQUARE_OPEN_BRACKET && 
                jsonStr.charAt(jsonStr.length() - 1) == SQUARE_CLOSE_BRACKET))
            return;

        this.elements = new ArrayList<>();

        /**
         * To parse a JSON array
         * 1. Change all the commas within nested objects/arrays to special characters
         * 2. Split by commas
         * 3. Recursively work through the resulting JSON strings.
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
            // Switch the special characters back
            s = s.trim().replace(SPECIAL_CHAR, COMMA);

            this.elements.add(this.getRecursiveObjects(s));
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
            return new JSONObject(builder.toString());
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


    public String toString() {
        String json = "";
        json += "[\n";

        for (JSONElement elem : this.elements) {
            if (elem.getType() == JSONElement.Type.OBJECT)
                json += "\t" + ((JSONObject)elem).toString();
            else if (elem.getType() == JSONElement.Type.ARRAY)
                json += "\t" + ((JSONArray)elem).toString();
            else
                json += "\t" + elem.toString();
        }

        json += "],\n";
        return json;
    }
}
