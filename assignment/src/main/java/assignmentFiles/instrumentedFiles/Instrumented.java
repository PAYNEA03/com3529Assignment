package assignmentFiles.instrumentedFiles;

import java.util.TreeSet;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import assignmentFiles.execution.*;

public class Instrumented {

    public static int daysBetweenTwoDates(int year1, int month1, int day1, int year2, int month2, int day2, Set<Integer> coveredBranches, HashMap<Integer, Boolean> coveredConditions) {
        int days = 0;
        // sanitize month inputs
        if (TestDataGenerator.logCondition(1, month1 < 1, coveredConditions)) {
            TestDataGenerator.coveredBranch(1, coveredBranches);
            month1 = 1;
        } else {
            TestDataGenerator.coveredBranch(2, coveredBranches);
        }
        if (TestDataGenerator.logCondition(2, month2 < 1, coveredConditions)) {
            TestDataGenerator.coveredBranch(3, coveredBranches);
            month2 = 1;
        } else {
            TestDataGenerator.coveredBranch(4, coveredBranches);
        }
        if (TestDataGenerator.logCondition(3, month1 > 12, coveredConditions)) {
            TestDataGenerator.coveredBranch(5, coveredBranches);
            month1 = 12;
        } else {
            TestDataGenerator.coveredBranch(6, coveredBranches);
        }
        if (TestDataGenerator.logCondition(4, month2 > 12, coveredConditions)) {
            TestDataGenerator.coveredBranch(7, coveredBranches);
            month2 = 12;
        } else {
            TestDataGenerator.coveredBranch(8, coveredBranches);
        }
        // sanitize day inputs
        if (TestDataGenerator.logCondition(5, day1 < 1, coveredConditions)) {
            TestDataGenerator.coveredBranch(9, coveredBranches);
            day1 = 1;
        } else {
            TestDataGenerator.coveredBranch(10, coveredBranches);
        }
        if (TestDataGenerator.logCondition(6, day2 < 1, coveredConditions)) {
            TestDataGenerator.coveredBranch(11, coveredBranches);
            day2 = 1;
        } else {
            TestDataGenerator.coveredBranch(12, coveredBranches);
        }
        if (TestDataGenerator.logCondition(7, day1 > daysInMonth(month1, year1, coveredBranches, coveredConditions), coveredConditions)) {
            TestDataGenerator.coveredBranch(13, coveredBranches);
            day1 = daysInMonth(month1, year1, coveredBranches, coveredConditions);
        } else {
            TestDataGenerator.coveredBranch(14, coveredBranches);
        }
        if (TestDataGenerator.logCondition(8, day2 > daysInMonth(month2, year2, coveredBranches, coveredConditions), coveredConditions)) {
            TestDataGenerator.coveredBranch(15, coveredBranches);
            day2 = daysInMonth(month2, year2, coveredBranches, coveredConditions);
        } else {
            TestDataGenerator.coveredBranch(16, coveredBranches);
        }
        // swap dates if year2, month2, day2 is before year1, month1, day1
        if ((TestDataGenerator.logCondition(9, year2 < year1, coveredConditions)) || (TestDataGenerator.logCondition(10, year2 == year1, coveredConditions) && TestDataGenerator.logCondition(11, month2 < month1, coveredConditions)) || (TestDataGenerator.logCondition(12, year2 == year1, coveredConditions) && TestDataGenerator.logCondition(13, month2 == month1, coveredConditions) && TestDataGenerator.logCondition(14, day2 < day1, coveredConditions))) {
            TestDataGenerator.coveredBranch(17, coveredBranches);
            int t = month2;
            month2 = month1;
            month1 = t;
            t = day2;
            day2 = day1;
            day1 = t;
            t = year2;
            year2 = year1;
            year1 = t;
        } else {
            TestDataGenerator.coveredBranch(18, coveredBranches);
        }
        // calculate days
        if (TestDataGenerator.logCondition(17, month1 == month2, coveredConditions) && TestDataGenerator.logCondition(18, year1 == year2, coveredConditions)) {
            TestDataGenerator.coveredBranch(23, coveredBranches);
            days = day2 - day1;
        } else {
            TestDataGenerator.coveredBranch(24, coveredBranches);
            days += daysInMonth(month1, year1, coveredBranches, coveredConditions) - day1;
            days += day2;
            if (TestDataGenerator.logCondition(16, year1 == year2, coveredConditions)) {
                TestDataGenerator.coveredBranch(21, coveredBranches);
                int month = month1 + 1;
                while (TestDataGenerator.logCondition(22, month < month2, coveredConditions)) {
                    TestDataGenerator.coveredBranch(28, coveredBranches);
                    days += daysInMonth(month, year1, coveredBranches, coveredConditions);
                    month++;
                }
            } else {
                TestDataGenerator.coveredBranch(22, coveredBranches);
                int year;
                int month = month1 + 1;
                while (TestDataGenerator.logCondition(19, month <= 12, coveredConditions)) {
                    TestDataGenerator.coveredBranch(25, coveredBranches);
                    days += daysInMonth(month, year1, coveredBranches, coveredConditions);
                    month++;
                }
                month = 1;
                while (TestDataGenerator.logCondition(20, month < month2, coveredConditions)) {
                    TestDataGenerator.coveredBranch(26, coveredBranches);
                    days += daysInMonth(month, year2, coveredBranches, coveredConditions);
                    month++;
                }
                year = year1 + 1;
                while (TestDataGenerator.logCondition(21, year < year2, coveredConditions)) {
                    TestDataGenerator.coveredBranch(27, coveredBranches);
                    days += 365;
                    if (TestDataGenerator.logCondition(15, isLeapYear(year, coveredBranches, coveredConditions), coveredConditions)) {
                        TestDataGenerator.coveredBranch(19, coveredBranches);
                        days++;
                    } else {
                        TestDataGenerator.coveredBranch(20, coveredBranches);
                    }
                    year++;
                }
            }
        }
        return days;
    }

    public static boolean isLeapYear(int year, Set<Integer> coveredBranches, HashMap<Integer, Boolean> coveredConditions) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    public static int daysInMonth(int month, int year, Set<Integer> coveredBranches, HashMap<Integer, Boolean> coveredConditions) {
        int[] daysInMonthNonLeapYear = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        return month == 2 && isLeapYear(year, coveredBranches, coveredConditions) ? 29 : daysInMonthNonLeapYear[month - 1];
    }

    public static Object assignVariables(Map.Entry<String, List> paramList, Set<Integer> coveredBranches, HashMap<Integer, Boolean> coveredConditions) {
        Object result = "empty";
        String methodName = paramList.getKey();
        List methodParams = paramList.getValue();
        if (methodName.equals("daysInMonth")) {;
        System.out.println("********Parsing Method: daysInMonth ****");;
        int month = TestDataGenerator.assignValues("month", methodParams);
        int year = TestDataGenerator.assignValues("year", methodParams);
        try {;
        result = daysInMonth(month, year, coveredBranches, coveredConditions);
        } catch (Exception e) {;
        System.out.println(e);
        System.out.println("Something went wrong passing values to function");
        };
        };
        if (methodName.equals("isLeapYear")) {;
        System.out.println("********Parsing Method: isLeapYear ****");;
        int year = TestDataGenerator.assignValues("year", methodParams);
        try {;
        result = isLeapYear(year, coveredBranches, coveredConditions);
        } catch (Exception e) {;
        System.out.println(e);
        System.out.println("Something went wrong passing values to function");
        };
        };
        if (methodName.equals("daysBetweenTwoDates")) {;
        System.out.println("********Parsing Method: daysBetweenTwoDates ****");;
        int year1 = TestDataGenerator.assignValues("year1", methodParams);
        int month1 = TestDataGenerator.assignValues("month1", methodParams);
        int day1 = TestDataGenerator.assignValues("day1", methodParams);
        int year2 = TestDataGenerator.assignValues("year2", methodParams);
        int month2 = TestDataGenerator.assignValues("month2", methodParams);
        int day2 = TestDataGenerator.assignValues("day2", methodParams);
        try {;
        result = daysBetweenTwoDates(year1, month1, day1, year2, month2, day2, coveredBranches, coveredConditions);
        } catch (Exception e) {;
        System.out.println(e);
        System.out.println("Something went wrong passing values to function");
        };
        };
        return result;
    }
}
