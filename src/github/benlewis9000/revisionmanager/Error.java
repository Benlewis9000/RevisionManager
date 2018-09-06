package github.benlewis9000.revisionmanager;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.ansi;

public enum Error {

    CNF("ERROR: Command Not Found. Type \"help\" for a list of commands."),
    IA("ERROR: Insufficient Arguments. Type \"help\" for a list of commands."),
    SC("ERROR: Arguments may not contain semi colons \";\"."),
    NFE("ERROR: Please enter a valid integer."),
    IOE("ERROR: I/O Exception. This ones bad..."),
    FNF("ERROR: File Not Found.");


    String message;

    Error (String message){

        this.message = message;

    }

    public void printError(){

        System.out.println( ansi().fg(RED).a(this.message).reset());

    }

}
