package assignmentFiles.execution;

//import assignmentFiles.instrumentedFiles.*;
import assignmentFiles.subjectFiles.Triangle;
import com.github.javaparser.ast.expr.Expression;

import java.util.*;

public class TestDataGenerator {

    static final int ITERATIONS = 750;
    static final int MIN_INT = -10;
    static final int MAX_INT = 10;

    public static void randomBranchGeneration(Instrument classMethods) {

        System.out.println(classMethods.path);

        for (Map.Entry<String, List> entry : classMethods.methodDetails.entrySet()) {
            String key = entry.getKey();
            List value = entry.getValue();
            System.out.println("Key: "+ key);
            System.out.println("Vals: " + value);
        }

        System.out.println("");

        for (Map.Entry<Integer, Expression> entry : classMethods.ifStmts.entrySet()) {
            Integer key = entry.getKey();
            Expression value = entry.getValue();
            System.out.println("logCondition Key: "+ key);
            System.out.println("Vals: " + value);

//            if not a method call example isLeapYear(year, coveredBranches), breakdown condition
            if (!value.isMethodCallExpr()) {
                System.out.println("Left: " + value.asBinaryExpr().getLeft());
                System.out.println("Right: " + value.asBinaryExpr().getRight());
                System.out.println("Operator: " + value.asBinaryExpr().getOperator());
                System.out.println("");
            }
        }

//        Random r = new Random();
//        Set<Integer> coveredBranches = new TreeSet<>();

//        for (int i=0; i < ITERATIONS; i ++) {

//            @todo get methods to test
//              * create variables for that methods parameters
//              * call method with variables
//            int side1 = randomInt(r);
//            int side2 = randomInt(r);
//            int side3 = randomInt(r);
//            System.out.println((i+1) + ": [" + side1 + ", " + side2 + ", " + side3 + "]");
//            Triangle.Type result = instrumentedClassify(side1, side2, side3, coveredBranches);
//            System.out.println("-> " + result);
//            Object var = 2;
//
////            InstrumentedCalendar.daysBetweenTwoDates(var,var,var,var,var,var,var);
//        }
//
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        System.out.println("Branch Coverage: " + coveredBranches.size() + "/14");
//        System.out.println("Covered Branch IDs: " + coveredBranches);
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");


    }

    public static void searchBasedGeneration() {

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

}
