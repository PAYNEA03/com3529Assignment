package assignmentFiles.execution;

import assignmentFiles.instrumentedFiles.*;
import com.github.javaparser.ast.expr.Expression;

import java.io.File;
import java.util.*;
import java.lang.reflect.Method;


public class TestDataGenerator {

    static final int ITERATIONS = 750;
    static final int MIN_INT = -10;
    static final int MAX_INT = 10;


    public static void randomBranchGeneration(Instrument classMethods) throws Exception {

//        Instrumented.testingGetterAndSetter();
        Random r = new Random();
        Set<Integer> coveredBranches = new TreeSet<>();

        for (int i=0; i < ITERATIONS; i ++) {
            // add value to hashmap
            for (List li : classMethods.methodDetails.values()) {
                for (Object h : li) {
                    HashMap t = (HashMap) h;
                    t.put("value", randomInt(r));
                }
            }

            Object result = Instrumented.assignVariables(classMethods.methodDetails, coveredBranches);
            System.out.println("-> " + result);

        }

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Branch Coverage: " + coveredBranches.size() + "/14");
        System.out.println("Covered Branch IDs: " + coveredBranches);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        System.out.println("");

//        for (Map.Entry<String, List> entry : classMethods.methodDetails.entrySet()) {
//            String key = entry.getKey();
//            List value = entry.getValue();
//            System.out.println("Key: "+ key);
//            System.out.println("Vals: " + value);
//        }

//        for (Map.Entry<Integer, Expression> entry : classMethods.ifStmts.entrySet()) {
//            Integer key = entry.getKey();
//            Expression value = entry.getValue();
//            System.out.println("logCondition Key: "+ key);
//            System.out.println("Vals: " + value);
//
////            if not a method call example isLeapYear(year, coveredBranches), breakdown condition
//            if (!value.isMethodCallExpr()) {
//                System.out.println("Left: " + value.asBinaryExpr().getLeft());
//                System.out.println("Right: " + value.asBinaryExpr().getRight());
//                System.out.println("Operator: " + value.asBinaryExpr().getOperator());
//                System.out.println("");
//            }
//        }

//        Random r = new Random();
//        Set<Integer> coveredBranches = new TreeSet<>();

//        for (int i=0; i < ITERATIONS; i ++) {

//            @todo get methods to test
//              * create variables for that methods parameters
//              * call method with variables

    }

    public static void searchBasedGeneration(Instrument classMethods) {

    }

    public static int assignValues(String name, List value) {
        int randomValue = 0;
        for (Object i: value) {
            HashMap h = (HashMap) value;
            if (h.get("paramName").equals(name)) {
                randomValue = (int) h.get("value");
            }
        }
        return randomValue;
    }

    /** randomInt is taken from the week5 lectures of the RandomlyTestTriangle class and
     * has not been changed in any form
     * */
    static int randomInt(Random r) {
        if (MIN_INT == Integer.MIN_VALUE && MAX_INT == Integer.MAX_VALUE) {
            return r.nextInt();
        } else {
            return r.nextInt((MAX_INT - MIN_INT + 1)) + MIN_INT;
        }
    }

    /** coveredBranch is taken from the week5 lectures of the RandomlyTestTriangle class and
     * has not been changed in any form
     * */
    public static void coveredBranch(int id, Set<Integer> coveredBranches) {
        if (!coveredBranches.contains(id)) {
            System.out.println("* covered new branch: " + id);
            coveredBranches.add(id);
        }
    }

//    @todo see assignment brief, mentioned this may be needed for Search based method?
    public static boolean logCondition(int id, Boolean condition) {
        System.out.println(condition);
        boolean result = condition;
        // ... log the id somewhere, along with the result,
        // thereby storing whether the condition was executed
        // as true or false, for computing coverage later on...
        return result;
    }


//    public static Object assignVariables(HashMap<String, List> paramList, Set<Integer> coveredBranches) {
//        Object result = "Empty";
//
//        for (Map.Entry<String, List> methodEntry : paramList.entrySet()) {
//            String methodName = methodEntry.getKey();
//            List methodParams = methodEntry.getValue();
//
//            if (methodName.equals("daysInMonth")) {
//                int month = TestDataGenerator.assignValues("month", methodParams);
//                int year = TestDataGenerator.assignValues("year", methodParams);
//
//                result = daysInMonth(month,year,coveredBranches);
//
//            } else if (methodName.equals("isLeapYear")) {
//                int year = TestDataGenerator.assignValues("year", methodParams);;
//                result = isLeapYear(year,coveredBranches);
//
//            } else if (methodName.equals("daysBetweenTwoDates")) {
//                int year1 = TestDataGenerator.assignValues("year1", methodParams);;
//                int month1 = TestDataGenerator.assignValues("month1", methodParams);;
//                int day1 = TestDataGenerator.assignValues("day1", methodParams);;
//                int year2 = TestDataGenerator.assignValues("year2", methodParams);;
//                int month2 = TestDataGenerator.assignValues("month2", methodParams);;
//                int day2 = TestDataGenerator.assignValues("day2", methodParams);;
//
//                result = daysBetweenTwoDates(year1, month1, day1, year2, month2, day2, coveredBranches);
//            }
//        }
//
//        return result;
//    }

}
