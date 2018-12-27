
package github.benlewis9000.revisionmanager;

import org.fusesource.jansi.Ansi;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.ansi;

public class FileManager {

    /**
     * Ensures all necessary files are generated.
     */
    public static void generateFiles(){

        generateEmpty("entries");
        generateEmpty("recalls");
        generateSettings();

    }

    /**
     * Set the specified setting to the specified value ins 'settings.txt'
     *
     * @param variable Name of variable to set.
     * @param value Value to set to variable.
     */
    public static void setSetting(String variable, String value){

        // Declare Scanner and PrintWriter to be closed in finally{...}
        Scanner scanner = null;
        PrintWriter printer = null;

        try {

            // Aquire settings.txt file, make ArrayList to later hold settings
            File file = new File("settings.txt");
            ArrayList<String> settings = new ArrayList<>();
            boolean foundVariable = false;

            // Initialise scanner
            scanner = new Scanner(file);

            while (scanner.hasNextLine()){

                String nextLine = scanner.nextLine();

                // Check for comments and save straight to settings without trying to read, loop
                if (nextLine.toCharArray()[0] == '#') {

                    Utils.debug("Comment, skipping line (saving)");
                    settings.add(nextLine);
                    continue;

                }

                String[] split = nextLine.split("=");

                // If settings variable matches arg variable, rewrite and save to settings (otherwise save unmodified)
                if (split[0].equalsIgnoreCase(variable)){

                    settings.add(variable + "=" + value);
                    foundVariable = true;

                }
                else settings.add(nextLine);

            }

            if (foundVariable) {

                // Rewrite modified settings
                printer = new PrintWriter(file);

                for (String setting : settings) {

                    printer.println(setting);

                }

            }
            else System.out.println( ansi().render("@|red ERROR: Could not find variable |@" + variable));

        }
        catch (IOException e){

            e.printStackTrace();
            System.out.println( ansi().render("@|red ERROR: Failed to read settings.txt (IOException)|@"));

        }
        finally {

            if (scanner != null) scanner.close();
            if (printer != null) printer.close();

        }

    }

    /**
     * Get the value of the specified setting, if it exists.
     *
     * @param variable  Variable to search for the value of.
     * @return          an optional of either the String value of the given variable, or empty.
     */
    public static Optional<String> getSetting(String variable){

        // Declare Scanner to be closed in finally{...}
        Scanner scanner = null;

        try {

            // Aquire settings.txt file
            File file = new File("settings.txt");

            // Initialise scanner
            scanner = new Scanner(file);

            while (scanner.hasNextLine()){

                String nextLine = scanner.nextLine();

                // Check for comments and skip, loop
                if (nextLine.toCharArray()[0] == '#') {

                    Utils.debug("Comment, skipping line");
                    continue;

                }

                String[] split = nextLine.split("=");

                // If settings variable matches arg variable, rewrite and save to settings (otherwise save unmodified)
                if (split[0].equalsIgnoreCase(variable)) return Optional.of(split[1]);

            }

        }
        catch (IOException e){

            e.printStackTrace();

        }
        finally {

            if (scanner != null) scanner.close();

        }

        return Optional.empty();
    }

    /**
     * Generates a blank file of the given name.
     *
     * @param fileName The name of the .txt file to be created.
     */
    private static void generateEmpty(String fileName){

        PrintWriter printWriter = null;

        try{

            // Create new file according to given fileName
            File file = new File(fileName + ".txt");

            // Check if file exists, if false...
            if (!file.exists()){

                // Generate blank file
                Utils.debug("Generating " + fileName + ".txt...");

                printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));

                printWriter.print("");

            }
            else {

                Utils.debug("Found " + fileName + ".txt");

            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if (printWriter !=null) printWriter.close();
        }

    }

    /**
     * generateSettings
     * Generates a settings file with the default settings.
     */
    private static void generateSettings(){

        // Todo: If settings not found, scan entries.txt for highest ID, and set totalentries to that       <<<------------------------------

        PrintWriter printWriter = null;

        try {

            // Declare settings.txt File
            File settings = new File("settings.txt");

            // Check for existing settings file, if none found, proceed...
            if (!settings.exists()) {

                Utils.debug("Generating settings.txt...");

                // Write default settings to settings.txt
                printWriter = new PrintWriter(new BufferedWriter(new FileWriter("settings.txt")));

                printWriter.println("# This is the settings file. You will likely not need to touch anything in here." +
                        "\n# Anything starting with a '#' is a comment and will be ignored by the program." +
                        "\n#");

                printWriter.println("# Debug mode: print additional debug messages when true;");
                printWriter.println("debug=true");
                printWriter.println("# Totalentries: keeps track of number of entries made, used to ID new entries.");
                printWriter.println("totalentries=0");
                printWriter.println("# Intervals: periods in days after which you will be prompted to recall a previous entry.");
                printWriter.println("intervals=1,14,28,168");
                printWriter.println("# Version: WARNING! Do NOT touch!");
                printWriter.println("version=" + Main.VERSION);

            } else {

                Utils.debug("Found settings.txt");

            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if (printWriter !=null) printWriter.close();
        }

    }

    /**
     * Load each line of a file into an ArrayList of Strings.
     * @param fileName  file to read
     * @return  ArrayList of Strings where each String is a line from given file
     */
    public static ArrayList<String> loadFile(String fileName){

        Scanner scanner = null;

        ArrayList<String> fileLines = new ArrayList<>();

        try {

            // Todo: Turn this into a static loadFile() method for FileManager? (with the comment reading)

            File file = new File(fileName);

            scanner = new Scanner(file);

            while (scanner.hasNextLine()){

                String nextLine = scanner.nextLine();

                if (nextLine.toCharArray()[0] == '#') continue;

                fileLines.add(nextLine);

            }
        }
        catch (IOException e){

            e.printStackTrace();
            System.out.println("@|red Failed to update recalls.");

        }
        finally {
            scanner.close();
        }

        return fileLines;

    }

    /**
     * Write ArrayList of Strings out line by line to specified file.
     * @param lines Strings to be written out as lines
     * @param fileName filename to write lines to
     */
    public static void writeLines(ArrayList<String> lines, String fileName){

        PrintWriter printWriter = null;

        try {

            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));

            for (String line : lines){

                printWriter.println(line);
                Utils.debug("(" + fileName + ") writing out: " + line);

            }

        }
        catch (IOException e){

            System.out.println( ansi().fg(Ansi.Color.RED).a("ERROR: IOException writing lines to " + fileName + ".").reset());
            e.printStackTrace();

        }
        finally {

            if (printWriter != null) printWriter.close();

        }

    }

}
