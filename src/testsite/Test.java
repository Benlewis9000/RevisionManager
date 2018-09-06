package testsite;

import java.io.*;
import java.util.Scanner;

public class Test {

    public static void main (String[] args) throws IOException {

        Scanner scanner = null;

        try {

            scanner = new Scanner( new BufferedReader( new FileReader("test.txt") ) );

            System.out.format(scanner.nextLine() + ".%n");
            System.out.format("The number is %2$0,20.2f.%n", 2.0, scanner.nextDouble());
            System.out.format("Next line.");

        }
        finally {

            if (scanner != null){

                scanner.close();

            }

        }

        Scanner input = new Scanner(System.in);
        PrintWriter printer = null;

        try {

            // When true, append will not overwrite data on file.
            printer = new PrintWriter( new BufferedOutputStream( new FileOutputStream("userOut.txt", true)));

            printer.println("Line 1");
            printer.print("Part 1...");
            printer.println(" of line 2");

            int i = 3;

            printer.format("and this is line %d", i);


        }
        finally {

            if (printer != null){

                printer.close();

            }

        }

    }


}
