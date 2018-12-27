package github.benlewis9000.revisionmanager;

import org.fusesource.jansi.Ansi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.fusesource.jansi.Ansi.ansi;

public class Utils {


    public static void debug(String str){

        if (Main.debug) {

            System.out.println( ansi().fg(YELLOW).a(str).reset());

        }

    }

    public static boolean getDebug(){

        Scanner settingsScanner = null;

        boolean debug = true;

        try {

            settingsScanner = new Scanner( new File("settings.txt"));

            while (settingsScanner.hasNextLine()){

                String[] split = settingsScanner.nextLine().split("=");

                if (split[0].equalsIgnoreCase("debug")){

                    System.out.println(" THIS ONE:" + split[1]);
                    return Boolean.valueOf(split[1]);

                }

            }

        }
        catch (IOException e){

            e.printStackTrace();

            System.out.println( ansi().fg(RED).a("ERROR: Failed to read settings.txt.\nDefault to debug mode."));

        }
        finally {
            settingsScanner.close();
        }

        return debug;
    }

    public static void reloadDebug(){

        Main.debug = getDebug();

    }

    public static int tryParsePosInt(String text) {

        try {

            return Integer.parseInt(text);

        } catch (NumberFormatException e) {

            System.out.println( ansi().fg(RED).a("ERROR: Failed to parse ").a(text).a(" to Integer."));
            return -1;

        }

    }

}
