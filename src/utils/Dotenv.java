/*
    Name:       Matthew Chen
    Date:       3/31/2023
    Period:     1

    Is this lab fully working?  Yes
    If not, explain: 
    If resubmitting, explain: 
 */

package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;


/**
 * Utility class for reading from an environment file. There was no need to download 
 * a separate jar file, etc. so I wrote my own super simple .env file reader.
 * @author Matthew Chen
 */
public class Dotenv {

    private HashMap<String, String> variables;
    private String path;

    /**
     * Constructor for a Dotenv object. Uses the default path of the 
     * current working directory + .env.
     */
    public Dotenv() throws FileNotFoundException {
        this(System.getProperty("user.dir") + "/.env");
    }


    /**
     * Constructor for a Dotenv object. 
     * @param pathName Path to the environment file. 
     */
    public Dotenv(String pathName) throws FileNotFoundException {
        this.path = pathName;
        this.variables = new HashMap<>();

        this.loadVariables();
    }


    public void setPath(String pathName) throws FileNotFoundException {
        this.path = pathName;
        this.loadVariables();
    }


    public String getPath() {
        return this.path;
    }


    public String get(String name) {
        return this.variables.getOrDefault(name, null);
    }


    private void loadVariables() throws FileNotFoundException {
        // Create a File object for the .env file
        File envFile = new File(this.path);

        try {
            Scanner in = new Scanner(envFile);

            while (in.hasNext()) {
                String line = in.nextLine();

                // .env files are of the form VARIABLE=VALUE
                int equalIdx = line.indexOf('=');
                if (equalIdx == -1)
                    continue;

                String variable = line.substring(0, equalIdx);
                String value = line.substring(equalIdx + 1);
                this.variables.put(variable, value);
            }

            in.close();

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Cannot find .env file");

        }
    }

}
