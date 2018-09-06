package github.benlewis9000.revisionmanager;

import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static boolean DEBUG = true;

    public static void main(String[] args) throws IOException {

        // Install Jansi system for coloured text output
        AnsiConsole.systemInstall();

        // Make sure all required files exist
        FileManager.generate();

        List<String> lines = Files.readAllLines(Paths.get("settings.txt"));
        System.out.println(lines);


        // Todo: Assign totalEntries from file

        // Todo: check for overdue entries

        while (true){

            // Declare command line scanner and take nextLine
            Scanner cmdLine = new Scanner(System.in);
            String input = cmdLine.nextLine();

            // Try to pass args, if false, default to error message and loop
            if (
            // Convert input to String[] args and pass to CommandHandler
            !CommandHandler.onCommand( CommandHandler.stringToArgs(input) )
                    ) {
                System.out.println("Invalid command. Type \"help\" for a list of commands.");
            }

        }

    }

}
