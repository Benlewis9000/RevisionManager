package github.benlewis9000.revisionmanager;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.fusesource.jansi.Ansi;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.fusesource.jansi.Ansi.ansi;

public class RecallManager {

    public static void recall(){

        HashSet<Integer> due = getDueRecalls();
        // Hashset removes duplicates
        // Todo: find out why everything was previously duplicated (could be interval periods?)

        recallIDs(due);

        // TOdo: remove on recall

    }

    /**
     * Generate recall dates for all entries based on the interval periods listed in 'settings.txt', then save to 'recalls.txt'.
     */
    public static void regenerateRecalls(){

        // GET INTERVALS
        Optional<ArrayList<Integer>> intervalsOpt = getIntervals();

        ArrayList<Integer> intervalsArl;
        if (intervalsOpt.isPresent()){

            intervalsArl = intervalsOpt.get();

        }
        else {
            System.out.println( ansi().render("@|red ERROR: No intervals have been loaded."));
            return;
        }

        // GET ENTRIES

        ArrayList<RevisionEntry> entries = RevisionEntry.getEntries();

        // ORGANISE RECALLS

        ArrayList<String> recalls = new ArrayList<>();

        // Cycle RevisionEntry's
        for (RevisionEntry entry : entries){

            recalls.addAll(generateEntryRecalls(entry, intervalsArl));

        }

        // WRITE RECALLS

        saveRecalls(recalls);

    }

    public static void generateRecall(RevisionEntry entry){

        // GET INTERVALS
        Optional<ArrayList<Integer>> intervalsOpt = getIntervals();

        ArrayList<Integer> intervalsArl;
        if (intervalsOpt.isPresent()){

            intervalsArl = intervalsOpt.get();

        }
        else {
            System.out.println( ansi().render("@|red ERROR: No intervals have been loaded."));
            return;
        }

        ArrayList<String> recalls = generateEntryRecalls(entry, intervalsArl);

        // WRITE RECALLS

        saveRecalls(recalls);

    }

    public static HashSet<Integer> getDueRecalls(){

        ArrayList<String> recalls = FileManager.loadFile("recalls.txt");
        HashSet<Integer> due = new HashSet<>();

        LocalDate today = LocalDate.now();

        for (String recall : recalls){

            String[] split = recall.split(";");
            LocalDate recallDue;
            int year = Utils.tryParsePosInt(split[0]);
            int month = Utils.tryParsePosInt(split[1]);
            int day = Utils.tryParsePosInt(split[2]);
            int ID = Utils.tryParsePosInt(split[3]);

            if (year == -1 || month == -1 || day == -1 || ID == -1) continue;

            recallDue = LocalDate.of(year, month, day);
            if (recallDue.isBefore(today) || recallDue.isEqual(today)) due.add(ID);

        }

        return due;

    }

    public static void recallIDs(HashSet<Integer> IDs){

        ArrayList<RevisionEntry> entries = RevisionEntry.getEntries();
        Utils.debug("recallingID's...");

        for(int id : IDs){

            Utils.debug("   " + id);

            for(RevisionEntry entry : entries){

                Utils.debug("   " + entry.getID());

                if (entry.getID() == id){

                    System.out.println( ansi().fg(YELLOW).a("Entry ").reset().a(id).fg(YELLOW).a(" from ").reset().a(entry.getDay()).a("/").a(entry.getMonth()).a("/").a(entry.getYear()).fg(YELLOW));
                    System.out.println( ansi().a("  ").a(entry.getMessage()).reset());

                }

            }

        }

    }

    /**
     * Gets the interval periods listed in 'settings.txt' if present.
     *
     * @return  an optional of either an ArrayList(Integer) of interval periods, or empty if they could not be loaded.
     */
    public static Optional<ArrayList<Integer>> getIntervals(){

        Optional<String> intervalOpt = FileManager.getSetting("intervals");

        if (!intervalOpt.isPresent()){

            System.out.println("@|red ERROR: Could not find 'intervals' in 'settings.txt'");
            return Optional.empty();

        }

        String[] intervalsStr = intervalOpt.get().split(",");
        ArrayList<Integer> intervalsArl = new ArrayList<>();

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
     * Generates the recall lines for a given entry and interval periods.
     *
     * @param entry         Entry to generate recalls for.
     * @param intervalsArl  Interval periods for the entries recalls.
     * @return an ArrayList(String) containing the recall lines for the entry in CSV format.
     */
    public static ArrayList<String> generateEntryRecalls(RevisionEntry entry, ArrayList<Integer> intervalsArl){

        ArrayList<String> recalls = new ArrayList<>();

        LocalDate creation = LocalDate.of(entry.getYear(), entry.getMonth(), entry.getDay());

        for (int interval : intervalsArl){

            Utils.debug("Cycling interval " + interval);

            LocalDate recallDate = creation.plusDays(interval);
            Utils.debug("recallDate: " + recallDate.toString());

            // If recall date has already passed, ignore
            if (recallDate.isAfter(LocalDate.now())) {

                Utils.debug("TRUE");

                recalls.add(recallDate.getYear() + ";" + recallDate.getMonthValue() + ";" + recallDate.getDayOfMonth() + ";" + entry.getID());  // NOTE: No boolean; remove recall on recall
                Utils.debug(recallDate.getYear() + ";" + recallDate.getMonthValue() + ";" + recallDate.getDayOfMonth() + ";" + entry.getID());

            } else Utils.debug("FALSE");

        }

        return recalls;

    }

    /**
     * Saves given recalls to 'recalls.txt'
     * @param recalls ArrayList of recalls to save.
     */
    public static void saveRecalls(ArrayList<String> recalls){

        // Todo make FileManager method SaveToFile

        File file = new File("recalls.txt");
        PrintWriter printer = null;

        try {

            printer = new PrintWriter(file);

            for (String recall : recalls){

                printer.println(recall);
                Utils.debug("Printing recall");

            }

        }
        catch (IOException e){

            e.printStackTrace();
            System.out.println( ansi().render("@|red ERROR: Failed to write recalls to 'recalls.txt'.|@"));

        }
        finally {

            if (printer != null) {
                printer.close();
            }

        }

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





    /*

            When reading recall intervals; (for generateRecalls())
                - in days
                - Sort by descending
                - Start with longest, if generated date would've already passed, break
                -


     */

}
