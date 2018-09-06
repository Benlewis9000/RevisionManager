package github.benlewis9000.revisionmanager;

import java.io.*;

public class FileManager {

    // Ensures essential files are always generated
    public static void generate(){

        try {

            File entries = new File("entries.txt");

            if (!entries.exists()){

                Utils.debug("Generating entries.txt...");

                PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter("entries.txt")));

                printWriter.print("");

                printWriter.close();

            }
            else {

                Utils.debug("Found entries.txt");

            }

            File recalls = new File("recalls.txt");

            if (!recalls.exists()){

                Utils.debug("Generating recalls.txt...");

                PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter("recalls.txt")));

                printWriter.print("");

                printWriter.close();

            }
            else {

                Utils.debug("Found recalls.txt");

            }

        }
        catch (IOException e){
            e.printStackTrace();
        }

        try {

            File settings = new File("settings.txt");

            if (!settings.exists()) {

                Utils.debug("Generating settings.txt...");

                PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter("settings.txt")));

                printWriter.println("debug=true");
                printWriter.println("totalentries=0"); // todo

                printWriter.close();

            } else {

                Utils.debug("Found settings.txt");

            }
        }
        catch (IOException e){
            e.printStackTrace();
        }


    }

}
