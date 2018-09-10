package testsite;

import java.io.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.Scanner;

public class Test {

    public static void main (String[] args) throws IOException {


        LocalDate today = LocalDate.now();

        System.out.format("%d;%d;%d%n", today.getDayOfMonth(), today.getMonthValue(), today.getYear());
        System.out.format("%td;%<tm;%<tY%n", today);

        today.plusMonths(6);

        LocalDate recall_1 = today.plusDays(1);
        LocalDate recall_2 = today.plusWeeks(2);
        LocalDate recall_3 = today.plusMonths(6);

        System.out.println(String.valueOf(23553));


    }


}
