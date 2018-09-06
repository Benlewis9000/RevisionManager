package github.benlewis9000.revisionmanager;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.fusesource.jansi.Ansi.ansi;

public class RevisionEntry {

    //private static int totalEntries = -1;
    private int ID;
    private int day;
    private int month;
    private int year;
    private String message;

    // Scann settings.txt for true value of totalentries
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

    public static void incrementEntries (){

        int totalEntries = getTotalEntries() + 1;

        // Save increment to file
        updateTotalEntries(totalEntries);

    }

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

    public RevisionEntry (int ID, String message){

        incrementEntries();

        // Todo: Make object take correct date values

        Date date = new Date();

        Calendar calendar = Calendar.getInstance(Locale.UK);

        this.ID = ID;
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.month = calendar.get(Calendar.MONTH);
        this.year = calendar.get(Calendar.YEAR);
        this.message = message;

        this.saveToFile();

    }

    public RevisionEntry (int ID, int day, int month, int year, String message){

        this.ID = ID;
        this.day = day;
        this.month = month;
        this.year = year;
        this.message = message;

    }

    public void saveToFile(){

        File entries = new File("entries.txt");
        PrintWriter printer = null;

        try {

            SimpleDateFormat date = new SimpleDateFormat("dd;mm;YYYY");

            printer = new PrintWriter( new BufferedWriter( new FileWriter(entries, true)));

            System.out.println( ansi().render("@|green Recorded: " + ID + ";" + day+ ";" + month+ ";" + year + ";" +message + "|@"));

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

    public static void updateTotalEntries(int newTotalEntries){


        Scanner scanner = null;
        PrintWriter printer = null;

        try {

            File file = new File("settings.txt");

            /*
            // Debug
            scanner = new Scanner(file);
            System.out.println("From debug");
            while (scanner.hasNextLine()) {
                System.out.println("foobar");
                System.out.println(scanner.nextLine());
            }
            */

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

            System.out.println("Map is : " + settings);

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

}
