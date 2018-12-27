package github.benlewis9000.revisionmanager;

import javax.swing.plaf.synth.SynthScrollBarUI;
import javax.swing.text.html.Option;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import static github.benlewis9000.revisionmanager.RecallManager.saveRecalls;
import static org.fusesource.jansi.Ansi.ansi;

public class CommandHandler {

    // Todo: Make Command an object?

    // Todo: convert Error's to Exception's and handle

    /**
     * Handle commands and call correct method.
     * @param args  arguments passed by user
     * @return  An Optional where an error is included should it be occur.
     */
    public static Optional<Error> onCommand(String[] args){

        if ( args.length == 0 ) return Optional.of(Error.CNF);

        switch (args[0]){

            case "entry":

                return onEntry(args);

            case "recall":

                return onRecall(args);

            case "help":

               return onHelp();

            case "exit":

                System.exit(0);

            default:
                return Optional.of(Error.CNF);

        }

    }

    /**
     * Handle 'entry' commands, call specified methods.
     * @param args
     * @return
     */
    public static Optional<Error> onEntry(String[] args){

        if (args.length >= 3){

            switch (args[1]){

                case "new":

                    return onEntry_new(args);

                case "view":

                    return onEntry_view(args);

                case "delete":

                    // Todo: delete RevisionEntry's (required?)

                    return onEntry_delete(args);

            }

        }
        else if (args.length == 2){

            switch (args[1]){

                case "list":

                    return onEntry_list();

            }

        }

        return Optional.of(Error.IA);

    }

    /**
     * Attempt to write a new entry with given args.
     * @param args  entry message
     * @return  Optional containing any error that should occur.
     */
    public static Optional<Error> onEntry_new(String[] args){

        // Generate a new RevisionEntry

        String message = "";
        for (int i = 2; i < args.length; i++){

            if (i == args.length-1) message = message + args[i];
            else message = message + args[i] + " ";

        }

        // Block entry of ';' char, as it will mess up CSV
        for (char c : message.toCharArray()){

            if (c == ';'){

                return Optional.of(Error.SC);

            }

        }

        RevisionEntry.newEntry(message);
        // Automatically saved to file by constructor

        return Optional.empty();

    }

    /**
     * View a specified entry.
     * @param args  ID of desired entry
     * @return  Optional containing any error that should occur.
     */
    public static Optional<Error> onEntry_view(String[] args){

        // View one specified entry

        try {

            // Extract ID from args
            int ID = Integer.parseInt(args[2]);

            // Check ID is valid
            if (RevisionEntry.loadEntry(ID).isPresent()) {

                // Load RevisionEntry with corresponding ID
                RevisionEntry loadedEntry = RevisionEntry.loadEntry(ID).get();

                // Print RevisionEntry data
                System.out.println(ansi().render("@|green Found entry " + ID + ": " +
                        "\n    Created: |@" + loadedEntry.getDay() + "/" + loadedEntry.getMonth() + "/" + loadedEntry.getYear() +
                        "\n    @|green Message: |@" + loadedEntry.getMessage() +
                        "\n    @|green Next recall: todo...|@"));

                return Optional.empty();

            }

            else return Optional.empty();

        }
        catch (NumberFormatException e){

            // Catch NumberFormatException and return as Error enum Optional
            return Optional.of(Error.NFE);

        }

    }

    public static Optional<Error> onEntry_delete(String[] args){

        // TODO...

        return Optional.empty();

    }

    /**
     * List all entries.
     * @return Optional containing any error that should occur.
     */
    public static Optional<Error> onEntry_list(){

        // Todo: account for recall periods, ignore recalled? -arg parameters?

        Scanner scanner = null;

        try {

            // Read entries.txt
            scanner = new Scanner( new File("entries.txt"));

            boolean found = false;

            while (scanner.hasNextLine()){

                // Set found true as soon as first entry is found
                found = true;

                String[] split = scanner.nextLine().split(";");

                System.out.println( ansi().render("@|green Entry |@" + split[0] +
                        "\n    @|green Created:|@ " + split[1] + "/" + split[2] + "/" + split[3] +
                        "\n   @|green  Message:|@ " + split[4]));

            }

            if (!found) System.out.println( ansi().render("@|red No entries found.|@"));

            return Optional.empty();


        }
        catch (IOException e){

            e.printStackTrace();

            return Optional.of(Error.IOE);

        }
        finally {

            if (scanner != null) scanner.close();

        }



    }

    /**
     * Trigger recall system and list due recalls.
     * @param args option of whether to generate recalls or execute recall (TEMPORARY - to be cleaned, treated like 'entry __' command)
     * @return Empty Optional
     */
    public static Optional<Error> onRecall(String[] args){

        // Todo: change args to switch statement? length==1 being 'null'? UPDATE - not possible to switch null - 27/12/18

        // If no additional args, print recalls.
        if (args.length == 1){

            RecallManager.recall();
            return Optional.empty();

        }

        // If stated, generate recalls - Todo: Automate this system? Lengthy - room for faster algorithm? - 26/12/18
        else if (args[1].equalsIgnoreCase("generate")) {

            // Generate list of recall dates and ID's - Todo: Recalls as objects with a toString and parseRecall (parse entry too?) - 27/12/18
            System.out.println("Generating recalls...");
            ArrayList<String> recalls = RecallManager.generateRecalls();

            // Save generated recalls to 'recalls.txt'
            saveRecalls(recalls);

        }

        return Optional.empty();

    }

    /**
     * List commands and usage.
     * @return  Optional containing any error that should occur.
     */
    public static Optional<Error> onHelp(){

        System.out.println( ansi().render("Commands:" +
                "\n    @|green entry new <entry message>|@" +
                "\n        - Enter an entry for today for future review." +
                "\n    @|green entry view <ID>|@" +
                "\n        - View the specified entry, entry date, and review (recall) times." +
                "\n    @|green entry delete <ID>|@" +
                "\n        - Delete the specified entry." +
                "\n    @|green entry list|@" +
                "\n        - List all entry ID's, along with their message." +
                "\n    @|green recall|@" +
                "\n        - Review the entries to recall today."));

        return Optional.empty();

    }

    /**
     * Split a string separated by spaces into array of String args
     * @param string String to separate
     * @return String array of args
     */
    public static String[] stringToArgs(String string) {

        return string.split(" ");

    }

}
