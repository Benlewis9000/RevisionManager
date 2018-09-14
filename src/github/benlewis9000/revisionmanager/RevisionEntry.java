package github.benlewis9000.revisionmanager;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
     * Construct a new RevisionEntry and save to file.
     *
     * @param message   User input message, a record of what the entry is for.
     * @return          an instance of the RevisionEntry created with the given parameters.
     */
    public static RevisionEntry newEntry(String message){
        // Todo make return Optional (what if file creation fails etc.)

        // Increment totalEntries then use new value as newEntry's ID
        incrementEntries();
        int ID = RevisionEntry.getTotalEntries();

        // Get instance of Calendar, use to get correct date values for newEntry
        LocalDate today = LocalDate.now();

        int day = today.getDayOfMonth();
        int month = today.getMonthValue();
        int year = today.getYear();

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

        Optional<String> totalEntries = FileManager.getSetting("totalentries");

        if (totalEntries.isPresent()) {

            try {

                return Integer.parseInt(totalEntries.get());

            }
            catch (NumberFormatException e) {

                e.printStackTrace();
                System.out.println("@|red ERROR: Could no parse 'totalentries' in 'settings.txt' to integer.");
                System.exit(2);
                return -1;

            }

        }
        else {

            // Exit, as program can no longer accurately ID entries
            System.out.println(ansi().render("@|red ERROR: Failed to read settings.txt for totalEntries value. |@"));
            System.exit(2);
            return -1;

        }

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

        FileManager.setSetting("totalentries", String.valueOf(newTotalEntries));

    }

    /**
     * RevisionEntry saveToFile
     * Saves current RevisionEntry object data to 'entries.txt' in the CSV format.
     */
    public void saveToFile(){

        // Todo: Boolean return to show success/failure?

        // Todo: add edit function to entries, therefore have this check for and remove previous entries of the same ID

        File entries = new File("entries.txt");
        PrintWriter printer = null;

        try {

            printer = new PrintWriter( new BufferedWriter( new FileWriter(entries, true)));

            System.out.println( ansi().render("@|green Entry |@" + ID + "@|green  has been succesfully recorded!" +
                            "\nYour first recall will be on |@(todo..)"));

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

    /**
     * getEntries
     * Read all entries saved to 'entries.txt' and return as an ArrayList.
     *
     * @return  Returns an ArrayList(RevisionEntry) of all entries in 'entries.txt'.
     */
    public static ArrayList<RevisionEntry> getEntries(){

        Scanner scanner = null;
        ArrayList<RevisionEntry> entries = new ArrayList<>();

        try {

            File file = new File("entries.txt");

            scanner = new Scanner(file);

            int line = 0;

            while (scanner.hasNextLine()){

                line++;
                String nextLine = scanner.nextLine();

                String[] split = nextLine.split(";");

                int id;
                int day;
                int month;
                int year;

                try {

                    id = Integer.parseInt(split[0]);
                    day = Integer.parseInt(split[1]);
                    month = Integer.parseInt(split[2]);
                    year = Integer.parseInt(split[3]);


                }
                catch (NumberFormatException e) {

                    e.printStackTrace();
                    System.out.println( ansi().render("@|red ERROR: Failed to read " + line));
                    continue;

                }

                RevisionEntry entry = new RevisionEntry(id, day, month, year, split[4]);
                entries.add(entry);
                Utils.debug("getEntries found RevisionEntry " + id);

            }

        }
        catch (IOException e){

            e.printStackTrace();

        }

        return entries;

    }

}
