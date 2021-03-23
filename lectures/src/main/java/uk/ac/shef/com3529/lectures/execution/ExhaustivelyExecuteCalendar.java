package uk.ac.shef.com3529.lectures.execution;

import uk.ac.shef.com3529.lectures.Calendar;

public class ExhaustivelyExecuteCalendar {

    static final int REPORT_INTERVAL = 1000000;

    public static void main(String[] args) {
        exhaustivelyExecuteDaysBetweenTwoDates();
    }

    static void exhaustivelyExecuteDaysBetweenTwoDates() {
        long numTestsPerformed = 0;
        long startTime = System.currentTimeMillis();

        for (int day1 = Integer.MIN_VALUE; day1 <= Integer.MAX_VALUE; day1++) {
            for (int month1 = Integer.MIN_VALUE; month1 <= Integer.MAX_VALUE; month1++) {
                for (int year1 = Integer.MIN_VALUE; year1 <= Integer.MAX_VALUE; year1++) {
                    for (int day2 = Integer.MIN_VALUE; day2 <= Integer.MAX_VALUE; day2++) {
                        for (int month2 = Integer.MIN_VALUE; month2 <= Integer.MAX_VALUE; month2++) {
                            for (int year2 = Integer.MIN_VALUE; year2 <= Integer.MAX_VALUE; year2++) {

                                Calendar.daysBetweenTwoDates(day1, month1, year1, day2, month2, year2);
                                long timeElapsed = System.currentTimeMillis() - startTime;
                                numTestsPerformed ++;

                                if (numTestsPerformed % REPORT_INTERVAL == 0) {
                                    System.out.println(numTestsPerformed + ") " +
                                            day1 + "/" + month1 + "/" + year1 + " -> " +
                                            day2 + "/" + month2 + "/" + year2 + " " +
                                            "Time Elapsed: " + timeElapsed + "ms");
                                }

                            }
                        }
                    }
                }
            }
        }
    }



}