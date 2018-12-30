package github.benlewis9000.revisionmanager;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.fusesource.jansi.Ansi;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;

import static github.benlewis9000.revisionmanager.FileManager.writeLines;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.fusesource.jansi.Ansi.ansi;

public class RecallManager {

    /*
        Todo: DESIGN RECALL OBJECT - make a Recall object? - 27/12/18
            - toString method
            - parseRecall method (string -> object)
            - method call() i.e. 'execute' recall, print out, due = false, remove from lists etc etc...
                - for (Recall recall : recalls) { recall() }
            - one due date
                - each RevisionEntry takes ArrayList(Recall), can be written out toString
            - vars:
                - relevant RevisionEntry ID <int>
                - due date (recall date) <String?>
                - boolean due <boolean>
     */

    public static void recall(){

        // Todo: remove onRecall ( the command i.e. make automatic ) - 24/12/18

        // Get ID's due for recall (HashSet to ignore duplicates)
        HashSet<Integer> due = getDueRecalls();

        recallIDs(due);

    }

    /**
     * Generate recall dates for all entries based on the interval periods listed in 'settings.txt', then save to 'recalls.txt'.
     */
    public static ArrayList<String> generateRecalls(){

        // Get RevisionEntry's as ArrayList
        ArrayList<RevisionEntry> entries = RevisionEntry.getEntries();

        ArrayList<String> recalls = new ArrayList<>();

        // Cycle RevisionEntry's
        for (RevisionEntry entry : entries){

            recalls.addAll(generateRecalls(entry));

        }

        return recalls;

    }

    /**
     * Generate recalls for a single RevisionEntry based on interval periods listed in 'settings.txt'.
     * @param entry RevisionEntry to generate recalls for.
     */
    public static ArrayList<String> generateRecalls(RevisionEntry entry){

        ArrayList<String> recalls = new ArrayList<>();

        // Get intervals (if available)
        Optional<ArrayList<Integer>> intervalsOpt = getIntervals();

        ArrayList<Integer> intervalsArl;
        if (intervalsOpt.isPresent()){

            intervalsArl = intervalsOpt.get();

        }
        else {
            System.out.println( ansi().render("@|red ERROR: No intervals have been loaded."));
            return recalls;
        }

        recalls = generateRecalls(entry, intervalsArl);

        return recalls;

    }

    /**
     * Generate recall dates for a given entry and given interval periods.
     * @param entry         entry to generate recalls for
     * @param intervalsArl  interval periods for the entries recalls
     * @return an ArrayList(String) containing the recall lines for the entry in CSV format.
     */
    private static ArrayList<String> generateRecalls(RevisionEntry entry, ArrayList<Integer> intervalsArl){

        ArrayList<String> recalls = new ArrayList<>();

        // Get date (at users locale)
        LocalDate creation = LocalDate.of(entry.getYear(), entry.getMonth(), entry.getDay());

        // Generate recall dates for given entry for each interval
        for (int interval : intervalsArl){

            Utils.debug("Cycling interval " + interval);

            // Calculate recall date as LocalDate
            LocalDate recallDate = creation.plusDays(interval);
            Utils.debug("recallDate: " + recallDate.toString());

            // Ensure recall date has not already passed..
            if (recallDate.isAfter(LocalDate.now())) {

                Utils.debug("TRUE");

                // ..add formatted recall to list of recalls
                recalls.add(recallDate.getYear() + ";" + recallDate.getMonthValue() + ";" + recallDate.getDayOfMonth() + ";" + entry.getID());  // NOTE: No boolean; remove recall on recall
                Utils.debug(recallDate.getYear() + ";" + recallDate.getMonthValue() + ";" + recallDate.getDayOfMonth() + ";" + entry.getID());

            } else Utils.debug("FALSE");

        }

        return recalls;

    }

    /**
     * Get a list of the ID's due to be recalled.
     * @return HashSet containing ID's of all due recalls
     */
    public static HashSet<Integer> getDueRecalls(){

        // Loads recalls, line by line
        ArrayList<String> recalls = FileManager.loadFile("recalls.txt");

        HashSet<Integer> due = new HashSet<>();

        // Get current date
        LocalDate today = LocalDate.now();

        for (String recall : recalls){

            String[] split = recall.split(";");
            LocalDate recallDue;

            // Validate that each CSV value is a positive integer
            int year = Utils.tryParsePosInt(split[0]);
            int month = Utils.tryParsePosInt(split[1]);
            int day = Utils.tryParsePosInt(split[2]);
            int ID = Utils.tryParsePosInt(split[3]);
            // ..if not, continue with loop
            if (year == -1 || month == -1 || day == -1 || ID == -1) continue;

            // ..else, create new LocalDate, and add to return if due before or on the current date
            recallDue = LocalDate.of(year, month, day);
            if (recallDue.isBefore(today) || recallDue.isEqual(today)) due.add(ID);

        }

        return due;

    }

    /**
     * Print out recalls for given RevisionEntry ID's
     * @param IDs ID's of RevisionEntry's to recall
     */
    public static void recallIDs(HashSet<Integer> IDs){

        // Todo: take an ArrayList of Strings and return for handling? Allow for a 'None found' message to display. - 30/12/18

        // Get all RevisionEntry's
        ArrayList<RevisionEntry> entries = RevisionEntry.getEntries();
        Utils.debug("recallingID's...");

        System.out.println( ansi().fg(YELLOW).a("Today's recalls:").reset());

        // Cycle each entry ID in given list of ID's
        for(int id : IDs){

            Utils.debug("   " + id);

            // Cycle each entry, and check whether loop ID matches that of found entry
            for(RevisionEntry entry : entries){

                Utils.debug("   " + entry.getID());

                if (entry.getID() == id){

                    // Print recall message
                    System.out.println( ansi().fg(YELLOW).a("   Entry ").reset().a(id).fg(YELLOW).a(" from ").reset().a(entry.getDay()).a("/").a(entry.getMonth()).a("/").a(entry.getYear()).fg(YELLOW));
                    System.out.println( ansi().a("    ").a(entry.getMessage()).reset());

                }

            }

        }

    }

    /**
     * Gets the int interval periods listed in 'settings.txt', if present (else return empty Optional).
     * @return  an optional of either an ArrayList(Integer) of interval periods, or empty if they could not be loaded.
     */
    public static Optional<ArrayList<Integer>> getIntervals(){

        // Get interval periods (if possible) froms settings
        Optional<String> intervalOpt = FileManager.getSetting("intervals");

        // If could not read optionals, print error message and return empty
        if (!intervalOpt.isPresent()){

            System.out.println("@|red ERROR: Could not find 'intervals' in 'settings.txt'");
            return Optional.empty();

        }

        // Split optionals from CSV to individual values
        String[] intervalsStr = intervalOpt.get().split(",");
        ArrayList<Integer> intervalsArl = new ArrayList<>();

        // Parse each value to int (if possible) and add to Integer ArrayList
        for (String str : intervalsStr){

            try {

                int interval = Integer.parseInt(str);
                intervalsArl.add(interval);
                Utils.debug("Added " + interval + " to intervals ArrayList");

            }
            catch (NumberFormatException e){

                e.printStackTrace();
                System.out.println( ansi().render("@|red ERROR: Could not parse " + str + " to integer.|@"));

            }

        }

        return Optional.of(intervalsArl);

    }

    /**
     * Saves given recalls to 'recalls.txt'
     * @param recalls ArrayList of recalls to save.
     */
    public static void saveRecalls(ArrayList<String> recalls){

        writeLines(recalls, "recalls.txt");

    }

    /**
     * Updates 'recalls.txt' to include the given new recalls whilst preserving original recalls.
     *
     * @param newRecalls    The recalls to be added to the file.
     */
    public static void updateRecalls(ArrayList<String> newRecalls){

        // Load lines from recalls.txt
        ArrayList<String> recalls = FileManager.loadFile("recalls.txt");

        // Add new recalls
        recalls.addAll(newRecalls);

        // Save updated version
        saveRecalls(recalls);

    }

}
