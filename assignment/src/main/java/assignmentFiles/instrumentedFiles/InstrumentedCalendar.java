package assignmentFiles.instrumentedFiles;

import java.util.TreeSet;
import java.util.Set;
import assignmentFiles.execution.*;

public class InstrumentedCalendar {

    public static int daysBetweenTwoDates(int year1, int month1, int day1, int year2, int month2, int day2, Set<Integer> coveredBranches) {
        int days = 0;
        // sanitize month inputs
        if (TestDataGenerator.logCondition(1, month1 < 1)) {
            TestDataGenerator.coveredBranch(1, coveredBranches);
            month1 = 1;
        }
        if (TestDataGenerator.logCondition(2, month2 < 1)) {
            TestDataGenerator.coveredBranch(2, coveredBranches);
            month2 = 1;
        }
        if (TestDataGenerator.logCondition(3, month1 > 12)) {
            TestDataGenerator.coveredBranch(3, coveredBranches);
            month1 = 12;
        }
        if (TestDataGenerator.logCondition(4, month2 > 12)) {
            TestDataGenerator.coveredBranch(4, coveredBranches);
            month2 = 12;
        }
        // sanitize day inputs
        if (TestDataGenerator.logCondition(5, day1 < 1)) {
            TestDataGenerator.coveredBranch(5, coveredBranches);
            day1 = 1;
        }
        if (TestDataGenerator.logCondition(6, day2 < 1)) {
            TestDataGenerator.coveredBranch(6, coveredBranches);
            day2 = 1;
        }
        if (TestDataGenerator.logCondition(7, day1 > daysInMonth(month1, year1, coveredBranches))) {
            TestDataGenerator.coveredBranch(7, coveredBranches);
            day1 = daysInMonth(month1, year1, coveredBranches);
        }
        if (TestDataGenerator.logCondition(8, day2 > daysInMonth(month2, year2, coveredBranches))) {
            TestDataGenerator.coveredBranch(8, coveredBranches);
            day2 = daysInMonth(month2, year2, coveredBranches);
        }
        // swap dates if year2, month2, day2 is before year1, month1, day1
        if ((TestDataGenerator.logCondition(9, year2 < year1)) || (TestDataGenerator.logCondition(11, year2 == year1) && TestDataGenerator.logCondition(13, month2 < month1)) || (TestDataGenerator.logCondition(15, year2 == year1) && TestDataGenerator.logCondition(17, month2 == month1) && TestDataGenerator.logCondition(19, day2 < day1))) {
            TestDataGenerator.coveredBranch(9, coveredBranches);
            int t = month2;
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
        if (TestDataGenerator.logCondition(24, month1 == month2) && TestDataGenerator.logCondition(26, year1 == year2)) {
            TestDataGenerator.coveredBranch(13, coveredBranches);
            days = day2 - day1;
        } else {
            TestDataGenerator.coveredBranch(14, coveredBranches);
            days += daysInMonth(month1, year1, coveredBranches) - day1;
            days += day2;
            if (TestDataGenerator.logCondition(22, year1 == year2)) {
                TestDataGenerator.coveredBranch(11, coveredBranches);
                int month = month1 + 1;
                while (TestDataGenerator.logCondition(33, month < month2)) {
                    TestDataGenerator.coveredBranch(18, coveredBranches);
                    days += daysInMonth(month, year1, coveredBranches);
                    month++;
                }
            } else {
                TestDataGenerator.coveredBranch(12, coveredBranches);
                int year;
                int month = month1 + 1;
                while (TestDataGenerator.logCondition(28, month <= 12)) {
                    TestDataGenerator.coveredBranch(15, coveredBranches);
                    days += daysInMonth(month, year1, coveredBranches);
                    month++;
                }
                month = 1;
                while (TestDataGenerator.logCondition(29, month < month2)) {
                    TestDataGenerator.coveredBranch(16, coveredBranches);
                    days += daysInMonth(month, year2, coveredBranches);
                    month++;
                }
                year = year1 + 1;
                while (TestDataGenerator.logCondition(31, year < year2)) {
                    TestDataGenerator.coveredBranch(17, coveredBranches);
                    days += 365;
                    if (TestDataGenerator.logCondition(21, isLeapYear(year, coveredBranches))) {
                        TestDataGenerator.coveredBranch(10, coveredBranches);
                        days++;
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

    public static void testingGetterAndSetter(String[] paramList, Set<Integer> coveredBranches) {
        String out;
        String out2;
    }
}
