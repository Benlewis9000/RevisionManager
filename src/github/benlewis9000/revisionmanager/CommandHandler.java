package github.benlewis9000.revisionmanager;

import java.util.ArrayList;

public class CommandHandler {

    // Returns false if command could not be executed
    // Todo: change return type to an enum of custom errors? e.g. invalid command, not enough args, incorrect usage etc.
    public static boolean onCommand(String[] args){

        if ( args.length == 0 ) return false;

        switch (args[0]){

            case "entry":

                if (args.length == 2){

                    switch (args[1]){

                        case "list":
                            // Todo: List entered args
                            return true;

                    }

                }
                else if (args.length >= 3){

                    switch (args[1]){

                        case "new":
                            // Todo: save to file as an RevisionEntry

                            String message = "";
                            for (int i = 2; i < args.length; i++){

                                if (i == args.length-1) message = message + args[i];
                                else message = message + args[i] + " ";



                            }

                            RevisionEntry entry = new RevisionEntry(RevisionEntry.getTotalEntries() + 1, message);
                            // Automatically saved to file by constructor

                            return true;

                        case "list":
                            // Todo: list RevisionEntry's
                            return true;

                        case "delete":
                            // Todo: delete RevisionEntry's

                            return true;

                        case "view":
                            try {
                                Integer.parseInt(args[2]);
                                System.out.println("Recalling " + args[2] + "...");
                            }
                            catch (NumberFormatException e){
                                System.out.println("ERROR: Please enter a valid integer.");
                            }
                            // Todo: view RevisionEntry
                            return true;

                    }

                }
                else {
                    System.out.println("Insufficient/invalid arguments! Please type \"help\" for correct usage. ");
                    return true;
                }

            case "recall":
                // Todo: check todays date with other entries, recall as needed.
                return true;

            case "help":

                System.out.println("Commands:" +
                        "\n    entry new <entry message>" +
                        "\n        - Enter an entry for today for future review." +
                        "\n    entry view <ID>" +
                        "\n        - View the specified entry, entry date, and review (recall) times." +
                        "\n    entry delete <ID>" +
                        "\n        - Delete the specified entry." +
                        "\n    entry list" +
                        "\n        - List all entry ID's, along with their message." +
                        "\n    recall" +
                        "\n        - Review the entries to recall today.");
                return true;

            case "exit":

                System.exit(0);

            default:
                return false;

        }

    }

    public static String[] stringToArgs(String string) {


        // COnvert input to array of char's
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
    }

}
