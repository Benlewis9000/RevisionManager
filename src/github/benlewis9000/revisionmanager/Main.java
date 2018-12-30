package github.benlewis9000.revisionmanager;

import com.sun.jmx.snmp.SnmpBadSecurityLevelException;
import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

import static github.benlewis9000.revisionmanager.Utils.getDebug;

public class Main {

    public static boolean debug = true;
    public static final double VERSION = 0.1;

    public static void main(String[] args) throws IOException {

        // Install Jansi system for coloured text output
        AnsiConsole.systemInstall();

        Utils.debug("Ansi installed(?)");
        Utils.debug("Debug enabled.");

        // Make sure all required files exist
        FileManager.generateFiles();

        // Set debug status to that defined in 'settings.txt'
        Utils.reloadDebug();

        Utils.debug("LocalDate.now(): " + LocalDate.now().toString());

        // Todo: check for overdue entries

        // RecallManager.generateRecalls(); - TODO: Can't generateRecalls, as this will clear recalls due on previous days that may not have ever been loaded/seen by user.
        RecallManager.recall();

        while (true){

            // Declare command line scanner and take nextLine
            Scanner cmdLine = new Scanner(System.in);
            String input = cmdLine.nextLine();

            Optional<Error> commandError = CommandHandler.onCommand(CommandHandler.stringToArgs(input));

            // Try to pass args, catch error from Optional if present.
            if (commandError.isPresent()) commandError.get().printError();

        }

    }

}
