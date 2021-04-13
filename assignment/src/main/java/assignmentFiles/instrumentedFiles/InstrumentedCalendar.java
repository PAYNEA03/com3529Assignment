package assignmentFiles.instrumentedFiles;

import java.util.TreeSet;
import java.util.Set;

public class InstrumentedCalendar {

    public static int daysBetweenTwoDates(int year1, int month1, int day1, int year2, int month2, int day2, Set<Integer> coveredBranches) {
        int days = 0;
        // sanitize month inputs
        if (month1 < 1) {
            month1 = 1;
            coveredBranch(1, coveredBranches);
        }
        if (month2 < 1) {
            month2 = 1;
            coveredBranch(2, coveredBranches);
        }
        if (month1 > 12) {
            month1 = 12;
            coveredBranch(3, coveredBranches);
        }
        if (month2 > 12) {
            month2 = 12;
            coveredBranch(4, coveredBranches);
        }
        // sanitize day inputs
        if (day1 < 1) {
            day1 = 1;
            coveredBranch(5, coveredBranches);
        }
        if (day2 < 1) {
            day2 = 1;
            coveredBranch(6, coveredBranches);
        }
        if (day1 > daysInMonth(month1, year1, coveredBranches)) {
            day1 = daysInMonth(month1, year1, coveredBranches);
            coveredBranch(7, coveredBranches);
        }
        if (day2 > daysInMonth(month2, year2, coveredBranches)) {
            day2 = daysInMonth(month2, year2, coveredBranches);
            coveredBranch(8, coveredBranches);
        }
        // swap dates if year2, month2, day2 is before year1, month1, day1
        if ((year2 < year1) || (year2 == year1 && month2 < month1) || (year2 == year1 && month2 == month1 && day2 < day1)) {
            int t = month2;
            coveredBranch(9, coveredBranches);
            month2 = month1;
            month1 = t;
            t = day2;
            day2 = day1;
            day1 = t;
            t = year2;
            year2 = year1;
            year1 = t;
        }
        // calculate days
        if (month1 == month2 && year1 == year2) {
            days = day2 - day1;
            coveredBranch(13, coveredBranches);
        } else {
            days += daysInMonth(month1, year1, coveredBranches) - day1;
            coveredBranch(14, coveredBranches);
            days += day2;
            if (year1 == year2) {
                int month = month1 + 1;
                coveredBranch(11, coveredBranches);
                while (month < month2) {
                    days += daysInMonth(month, year1, coveredBranches);
                    month++;
                }
            } else {
                int year;
                coveredBranch(12, coveredBranches);
                int month = month1 + 1;
                while (month <= 12) {
                    days += daysInMonth(month, year1, coveredBranches);
                    month++;
                }
                month = 1;
                while (month < month2) {
                    days += daysInMonth(month, year2, coveredBranches);
                    month++;
                }
                year = year1 + 1;
                while (year < year2) {
                    days += 365;
                    if (isLeapYear(year, coveredBranches)) {
                        days++;
                        coveredBranch(10, coveredBranches);
                    }
                    year++;
                }
            }
        }
        return days;
    }

    public static boolean isLeapYear(int year, Set<Integer> coveredBranches) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    public static int daysInMonth(int month, int year, Set<Integer> coveredBranches) {
        int[] daysInMonthNonLeapYear = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        return month == 2 && isLeapYear(year, coveredBranches) ? 29 : daysInMonthNonLeapYear[month - 1];
    }
}
