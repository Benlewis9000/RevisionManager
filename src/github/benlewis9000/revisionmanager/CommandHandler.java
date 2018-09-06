package github.benlewis9000.revisionmanager;

import javax.swing.plaf.synth.SynthScrollBarUI;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.ansi;

public class CommandHandler {

    // Returns false if command could not be executed
    // Todo: change return type to an enum of custom errors? e.g. invalid command, not enough args, incorrect usage etc.
    public static boolean onCommand(String[] args){

        if ( args.length == 0 ) return false;

        switch (args[0]){

            case "entry":

                if (args.length >= 3){

                    switch (args[1]){


                        case "new":

                            // Generate a new RevisionEntry

                            String message = "";
                            for (int i = 2; i < args.length; i++){

                                if (i == args.length-1) message = message + args[i];
                                else message = message + args[i] + " ";

                            }

                            // Block entry of ';' char, as it will mess up CSV
                            for (char c : message.toCharArray()){

                                if (c == ';'){

                                    System.out.println( ansi().render("@|red ERROR: Entry message may not contain semicolons (\";\").|@"));

                                    return true;

                                }

                            }

                            RevisionEntry newEntry = RevisionEntry.newEntry(message);
                            // Automatically saved to file by constructor

                            return true;

                        case "delete":

                            // Todo: delete RevisionEntry's (required?)

                            return true;

                        case "view":

                            // View one specified entry

                            try {

                                int ID = Integer.parseInt(args[2]);

                                if (RevisionEntry.loadEntry(ID).isPresent()) {

                                    RevisionEntry loadedEntry = RevisionEntry.loadEntry(ID).get();

                                    System.out.println(ansi().render("@|green Found entry " + ID + ": " +
                                            "\n    Created: |@" + loadedEntry.getDay() + "/" + loadedEntry.getMonth() + "/" + loadedEntry.getYear() +
                                            "\n    @|green Message: |@" + loadedEntry.getMessage() +
                                            "\n    @|green Next recall: todo...|@"));

                                }

                            }
                            catch (NumberFormatException e){
                                System.out.println( ansi().render("@|red ERROR: Please enter a valid integer.|@"));
                            }

                            return true;

                    }

                }
                else if (args.length == 2){

                    switch (args[1]){

                        case "list":

                            Scanner scanner = null;

                            try {

                                scanner = new Scanner( new File("entries.txt"));

                                boolean noneFound = true;

                                while (scanner.hasNextLine()){

                                    noneFound = false;

                                    String[] split = scanner.nextLine().split(";");

                                    System.out.println( ansi().render("@|green Entry |@" + split[0] +
                                            "\n    @|green Created:|@ " + split[1] + "/" + split[2] + "/" + split[3] +
                                            "\n   @|green  Message:|@ " + split[4] + "|@"));

                                }

                                if (noneFound) System.out.println( ansi().render("@|red No entries found.|@"));


                            }
                            catch (IOException e){

                                e.printStackTrace();

                                System.out.println( ansi().render("@|red ERROR: Failed to read settings.txt.|@"));

                            }

                            return true;

                    }

                }
                else {
                    System.out.println("@|red Insufficient/invalid arguments! Please type \"help\" for correct usage.|@");
                    return true;
                }

            case "recall":
                // Todo: check todays date with other entries, recall as needed.
                return true;

            case "help":

                System.out.println("Commands:" +
                        "\n    @|green entry new <entry message>|@" +
                        "\n        - Enter an entry for today for future review." +
                        "\n    @|green entry view <ID>|@" +
                        "\n        - View the specified entry, entry date, and review (recall) times." +
                        "\n    @|green entry delete <ID>|@" +
                        "\n        - Delete the specified entry." +
                        "\n    @|green entry list|@" +
                        "\n        - List all entry ID's, along with their message." +
                        "\n    @|green recall|@" +
                        "\n        - Review the entries to recall today.");
                return true;

            case "exit":

                System.exit(0);

            default:
                return false;

        }

    }

    public static String[] stringToArgs(String string) {


        return string.split(" ");

        /*

        // Convert input to array of char's
        char[] chars = string.toLowerCase().toCharArray();

        ArrayList<String> argsList = new ArrayList<>();

        String stringBuffer = new String();

        boolean endSpace = false;

        // if next char is SPACE, add stringBuffer to argsList, then reset stringBuffer
        // else, add the char to stringBuffer (builds an arg)
        for (char c : chars) {
            if (c == ' ') {
                endSpace = true;
                argsList.add(stringBuffer);
                stringBuffer = "";
            } else {
                endSpace = false;
                stringBuffer = stringBuffer + Character.toString(c);
            }
        }

        // if input doesn't end with a SPACE, add final stringBuffer to argsList
        if (!endSpace) {
            argsList.add(stringBuffer);
        }

        // Convert the ArrayList<String> to String[]
        String[] args = new String[argsList.size()];

        for (int i = 0; i < args.length; i++) {

            args[i] = (String) argsList.toArray()[i];

        }


        return args;

        */


    }

}
