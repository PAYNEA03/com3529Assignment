package assignmentFiles.execution;

import assignmentFiles.instrumentedFiles.*;
import com.github.javaparser.ast.expr.Expression;

import java.io.File;
import java.util.*;
import java.lang.reflect.Method;


public class TestDataGenerator {

    static final int ITERATIONS = 1250;
    static final int MIN_INT = -15;
    static final int MAX_INT = 15;


    public static void randomBranchGeneration(Instrument classMethods) throws Exception {

        Random r = new Random();
        Set<Integer> coveredBranches = new TreeSet<>();

        for (int i=0; i < ITERATIONS; i ++) {

            // iterate through hashmap parameters and assign a value to each variable
            for (List li : classMethods.methodDetails.values()) {
                for (Object h : li) {
                    HashMap t = (HashMap) h;
                    t.put("value", randomInt(r));
                }
            }

            // print iteration progress and pass updated hashmap with random values attached
            System.out.println("~~~~~~~~~~~~Call " + (i+1) + "~~~~~~~~~");
            Object result = Instrumented.assignVariables(classMethods.methodDetails, coveredBranches);
            System.out.println("-> " + result);
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            System.out.println("");

        }

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Branch Coverage: " + coveredBranches.size() + "/" + classMethods.branchTotal);
        System.out.println("Covered Branch IDs: " + coveredBranches);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        System.out.println("");



    }

    public static void searchBasedGeneration(Instrument classMethods) {
//            Instrument Instances:
//            this.path = path;
//            this.methodDetails = methodDetail;
//            this.ifStmts = ifStmtLogs;
//            this.branchTotal = branchCount;
//            this.conditionTotal = conditionCount;


        ////        example code to parse hashmaps
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

    }

    public static int assignValues(String name, List value) {
        int assignValue = 0;
        //searches hashmap for matching variable name and assigns value to it
        for (Object i: value) {
            HashMap h = (HashMap) i;
            if (h.get("paramName").toString().equals(name)) {
                assignValue = (int) h.get("value");
            }
        }
        System.out.println("Var: " + name + ", value: " + assignValue);
        return assignValue;
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
//        System.out.println(condition);
        boolean result = condition;
        // ... log the id somewhere, along with the result,
        // thereby storing whether the condition was executed
        // as true or false, for computing coverage later on...
        return result;
    }


}
