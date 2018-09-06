package github.benlewis9000.revisionmanager;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;


public class RevisionEntry {

    private final int ID;
    private int day;
    private int month;
    private int year;
    private String message;


    public int getID() {
        return ID;
    }

    /* Cannot set ID beyond constructor */

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * RevisionEntry constructor
     * Construct a new RevisionEntry object.
     *
     * WARNING: Not to be used to making or loading entry's; use newEntry() and loadEntry(int ID)
     *
     * @param ID        Unique ID associated with the RevisionEntry.
     * @param day       Day of creation.
     * @param month     Month of creation.
     * @param year      Year of creation.
     * @param message   User input message, a record of what the entry is for.
     */
    private RevisionEntry (int ID, int day, int month, int year, String message){

        this.ID = ID;
        this.day = day;
        this.month = month;
        this.year = year;
        this.message = message;

    }

    /**
     * RevisionEntry generator
     * Construct a new RevisionEntry and save to file.
     *
     * @param message   User input message, a record of what the entry is for.
     * @return          Return an instance of the RevisionEntry created with the given parameters.
     */
    public static RevisionEntry newEntry(String message){
        // Todo make return Optional (what if file creation fails etc.)

        // Increment totalEntries then use new value as newEntry's ID
        incrementEntries();
        int ID = RevisionEntry.getTotalEntries();

        // Get instance of Calendar, use to get correct date values for newEntry
        Calendar calendar = Calendar.getInstance(Locale.UK);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        // Construct new RevisionEntry
        RevisionEntry newEntry = new RevisionEntry(ID, day, month, year, message);

        // Save newEntry to file
        newEntry.saveToFile();

        // Return newEntry so it can be accessed
        return newEntry;
    }

    /**
     * getTotalEntries
     *
     * @return  Returns the int 'totalentries' in 'settings.txt'
     */
    public static int getTotalEntries(){

        Scanner scanner = null;

        try {

            scanner = new Scanner( new File("settings.txt"));

            while (scanner.hasNextLine()){

                String[] split = scanner.nextLine().split("=");

                if (split[0].equalsIgnoreCase("totalentries")) return Integer.parseInt(split[1]);

            }

        }
        catch (IOException e){

            e.printStackTrace();

        }
        finally {

            if (scanner != null) {
                scanner.close();
            }

        }

        System.out.println( ansi().render("@|red ERROR: Failed to read settings.txt for totalEntries value. |@"));
        System.exit(2);
        return -1;
    }

    /**
     * incrementEntries
     * Increments 'totalentries' in 'settings.txt' by 1.
     */
    public static void incrementEntries (){

        int totalEntries = getTotalEntries() + 1;

        // Save increment to file
        updateTotalEntries(totalEntries);

    }

    /**
     * RevisionEntry loader
     * Construct a RevisionEntry loaded from a file using the given ID, if present.
     *
     * @param ID    Reference ID for the entry to be loaded.
     * @return      Return Optional, either holding the valid requested RevisionEntry, or empty.
     */
    public static Optional<RevisionEntry> loadEntry(int ID){

        Scanner scanner = null;

        try {

            // Load file to scanner
            scanner = new Scanner( new File("entries.txt"));

            while (scanner.hasNextLine()){

                // Parse CVS by splitting line into String array
                String[] split = scanner.nextLine().split(";");

                int loadedID = Integer.parseInt(split[0]);

                // If current iteration has desired ID, generate and return the RevisionEntry
                if (split[0].equalsIgnoreCase(String.valueOf(ID))) {

                    int loadedDay = Integer.parseInt(split[1]);
                    int loadedMonth = Integer.parseInt(split[2]);
                    int loadedYear = Integer.parseInt(split[3]);

                    RevisionEntry entry = new RevisionEntry(loadedID, loadedDay, loadedMonth, loadedYear, split[4]);
                    return Optional.of(entry);


                }

            }

            // If no ID was found and returned...
            System.out.println( ansi().render("@|red ERROR: Please enter a valid ID.|@"));

        }
        catch (IOException e){

            e.printStackTrace();

            System.out.println( ansi().render("@|red ERROR: Failed to load entry from entries.txt.|@"));

        }
        catch (NumberFormatException e){

            e.printStackTrace();

            System.out.println( ansi().render("@|red ERROR: Failed to parse integers in entries.txt.|@"));

        }
        finally {

            if (scanner != null){
                scanner.close();
            }

        }

        // If no entry to the given ID was found, return the empty optional
        return Optional.empty();
    }

    /**
     * updateTotalEntries
     * Updates the int value of 'totalentries' in 'settings.txt' to given value.
     *
     * @param newTotalEntries   New value to assign in settings.txt.
     */
    public static void updateTotalEntries(int newTotalEntries){


        Scanner scanner = null;
        PrintWriter printer = null;

        try {

            File file = new File("settings.txt");

            HashMap<String, String> settings = new HashMap<>();

            scanner = new Scanner(file);

            // Cycle settings, interpret CVS, then save and modifier as needed to Properties
            while (scanner.hasNextLine()) {

                // Take each line and split the variable from the value
                String[] split = scanner.nextLine().split("=");

                // If variable is totalentries, increment and assign, otherwise assign original value
                if (split[0].equalsIgnoreCase("totalentries")){

                    settings.put(split[0], String.valueOf(newTotalEntries));

                }
                else {

                    settings.put(split[0], split[1]);

                }


            }

            Utils.debug("Map is : " + settings);

            printer = new PrintWriter( new BufferedWriter( new FileWriter(file, false)));

            // Save new properties to settings.txt
            for (Map.Entry<String, String> entry : settings.entrySet()){

                printer.println(entry.getKey() + "=" + entry.getValue());

            }


        }
        catch (IOException e){

            e.printStackTrace();

        }
        finally {

            if (printer != null){
                printer.close();
            }

            if (scanner != null){
                scanner.close();
            }

        }


    }

    /**
     * RevisionEntry saveToFile
     * Saves current RevisionEntry object data to 'entries.txt' in the CSV format.
     */
    public void saveToFile(){

        // Todo: Boolean return to show success/failure?

        File entries = new File("entries.txt");
        PrintWriter printer = null;

        try {

            printer = new PrintWriter( new BufferedWriter( new FileWriter(entries, true)));

            System.out.println( ansi().render("@|green Entry " + ID + " has been succesfully recorded!" +
                            "\nYour first recall will be on (todo..)|@"));

            printer.format("%d;%d;%d;%d;%s%n", ID, day, month, year, message);


        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if (printer != null){
                printer.close();
            }
        }

    }

}
