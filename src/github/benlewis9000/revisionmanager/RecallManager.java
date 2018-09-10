package github.benlewis9000.revisionmanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.ansi;

public class RecallManager {

    public static void regenerateRecalls(){

        /*
            Todo:
                - lift time intervals from settings
                - go through entries and calculate new recall dates for each ID

         */

        // GET INTERVALS
        Optional<String> intervalOpt = FileManager.getSetting("intervals");

        if (!intervalOpt.isPresent()){

            System.out.println("@|red ERROR: Could not find 'intervals' in 'settings.txt'");
            return;

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

        ////////////////


        // GET ENTRIES

        ArrayList<RevisionEntry> entries = RevisionEntry.getEntries();

        // ORGANISE RECALLS

        ArrayList<String> recalls = new ArrayList<>();

        // Cycle RevisionEntry's
        for (RevisionEntry entry : entries){

            LocalDate creation = LocalDate.of(entry.getYear(), entry.getMonth(), entry.getDay());
            Utils.debug("Cycling entry ################ " + entry.getID());
            Utils.debug("Creation: " + creation.toString());

            // Cycle intervals
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

        }

        // WRITE RECALLS

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

    public static void recallDue(){

        /*
            Todo:
                - go through recalls.txt
                - Make arraylist of ID's to recall
                - Add recall if date is present or past, AND has not been called.
         */

    }

    /*

            When reading recall intervals; (for generateRecalls())
                - in days
                - Sort by descending
                - Start with longest, if generated date would've already passed, break
                -


     */

}
