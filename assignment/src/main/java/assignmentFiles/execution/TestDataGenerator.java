package assignmentFiles.execution;

import assignmentFiles.instrumentedFiles.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.type.Type;


import java.io.File;
import java.util.*;


public class TestDataGenerator {

    static final int ITERATIONS = 1250;
    static final int MIN_INT = -15;
    static final int MAX_INT = 15;
    static final double MIN_DOUBLE = -15.;
    static final double MAX_DOUBLE = 15.;

    private String generationType;
    private String coverageCriteria;

    private Random rand;

    //MCDCoverage is a map from a conditionSequence (a string denoting the conditions truth values for an input)
    // - said input is given as {"parameters":xyz} in the hashmap the conditionSequence maps to
    // - the inputs has a corresponding branchSequence as a string and is given in the hashMap as {"branchSequence":xyz}
    // - other inputs that have different yet matching conditionSequences (hamming distance of 1, 1 element different) are also in the hashmap
    //    these other inputs also map to a hashmap with "parameters":xyz and "branchSequence":xyz and "conditionSequence":xyz
    private HashMap<String,HashMap<String,Object>> MCDCoverage;
    //when iterating through it check if new is already a key
    // if not check if its got a hamming distance of 1 to any key
    //  then add it to that key's hashmap only if its branchSequence is different - make test case (if first pairing add key parameters as one too)
    // if no key is close make sure to check distance of all key partners as you go - if distance to any key is 1 + branchSequence different
    //  then take the found partner and make it into a key, add new to it and make into a new test case but don't add the found partner as it'll already have one
    // if not got a distance of 1 to any key in the current coverage add it as a new key
    //otherwise if you find the conditionSequence anywhere in the hashmap then just move on, nothing is added

    private List<Object> nextParameterSet;

    public TestDataGenerator(String cc,String gt){
        coverageCriteria = cc;
        generationType = gt;
        if (generationType == "search"){
            //
        }
        else {
            rand = new Random();
        }
        if (coverageCriteria == "MCDC"){
            //set up MCDC list of pairs of boolean lists,
            // elements of the greater list is a pair of correlated MCDC conditions with matching major and minor conditions
            // where the major term decides the predicate of a branch (can be any branch as every other minor condition is
            // the same)
            MCDCoverage = new HashMap<>();
            //every index is a different coverage pair
            //every index in the pair lists is the condition id
        }
    }

    public void testGeneration(Instrument classMethods) throws Exception {
        Set<Integer> coveredBranches = new TreeSet<>();
        HashMap<Integer,Boolean> coveredConditions = new HashMap<>();
        Set<Integer> oldCoveredBranches = new TreeSet<>();
        Set<Integer> definitiveCoveredBranches = new TreeSet<>();

        //the test case output file
        //format MethodName - List of (each element is a test case) of Lists (each element is a parameter for that method)
        HashMap<String,List<List<Object>>> testCases = new HashMap<>();
        for (String classMethod : classMethods.methodDetails.keySet()){
            testCases.put(classMethod,new ArrayList<>());
        }

        for (int i=0; i < ITERATIONS; i ++) {

            // iterate through all the class methods
            for (Map.Entry<String,List> methodEntry: classMethods.methodDetails.entrySet()) {
                //if its a search based technique calculate the next set of parameters in the search for every method
                if (generationType == "search"){
                    generateInputsBySearch();
                }

                int paramNum = 0;
                //assign values to each parameter variable
                for (Object h : methodEntry.getValue()) {
                    HashMap t = (HashMap) h;
                    String parameterType = ((Type)t.get("paramType")).asString();
                    //System.out.println(parameterType); getting the parameter type works :)
                    if (parameterType == "double"){
                        t.put("value", generateDouble(paramNum));
                    }
                    else { //if (parameterType == "int"){
                        t.put("value", generateInt(paramNum));
                    }

                    paramNum++;
                }

                //if its MCDC we want to reset coveredBranches and coveredConditions so we can check their connection
                if (coverageCriteria == "MCDC"){
                    coveredBranches = new TreeSet<>();
                    coveredConditions = new HashMap<>();
                }

                // print iteration progress and pass updated hashmap with correctly generated values attached
                System.out.println("~~~~~~~~~~~~Call " + (i+1) + "~~~~~~~~~");
                Object result = Instrumented.assignVariables(methodEntry, coveredBranches, coveredConditions);
                System.out.println("-> " + result);
                System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

                //evaluate whether these inputs are worth making into a test case
                boolean success = false;
                if (coverageCriteria == "MCDC"){
                    //check coveredBranches against MCDCoverage list, non-appearing conditions are considered false

                    //add everything to MCDCoverage but only add it to the testCases if a second pair is found for that major condition
                    //i.e. search through to find if
                    // 1. its already in the MCDCoverage
                    // 2. its got a partner test case that is
                    //      - this test case with 1 condition flipped and everything else the same
                    //      - any associated conditions are flipped too
                    //      TODO perhaps you could be sure the condition-predicate linkage was confirmed if coveredBranch()
                    //       went around the if statement and stored whether it was true or false like logCondition
                }
                //otherwise we check what branches are now covered (i.e. aren't in the definitiveCoveredBranches)
                //and if new branches were found then add these inputs as a test case
                else if (coverageCriteria == "branch") {
                    //remove all the old covered branches
                    coveredBranches.removeAll(definitiveCoveredBranches);
                    //if theres any left then there was newly covered branches
                    if (coveredBranches.size() > 0){
                        success = true;
                        for (int newBranch : coveredBranches){
                            System.out.println(" ** new branch covered: "+newBranch);
                            definitiveCoveredBranches.add(newBranch);
                        }

                    }
                }

                //if the test case was a success add it to the testCases
                if (success){
                    System.out.println("*** new test case: "+methodEntry.getValue().toString());
                    testCases.get(methodEntry.getKey()).add(methodEntry.getValue());
                    //also don't forget to add this test cases partner from MCDCoverage list if its MCDC coverage
                }


                //evaluate whether the target coverage criteria have been reached for this method
                //if so then somehow delete this methodEntry from the entrySet and carry on until all of them are met
                //TODO to do this with branch coverage will need a HashMap from method to number of branches
                // (somehow link with Instrument.addBranchLogger())
            }
        }

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Branch Coverage: " + definitiveCoveredBranches.size() + "/" + classMethods.branchTotal);
        System.out.println("Covered Branch IDs: " + definitiveCoveredBranches);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        System.out.println("");

        System.out.println(testCases.toString());

    }

    private int generateInt(int i){
        if (generationType == "search"){
            return (int)nextParameterSet.get(i);
        }
        else { //if (generationType == "random"){
            return randomInt(rand);
        }
    }

    private double generateDouble(int i){
        if (generationType == "search"){
            return (double)nextParameterSet.get(i);
        }
        else {//if (generationType == "random"){
            return randomDouble(rand);
        }
    }

    private List generateInputsBySearch(){
        nextParameterSet = new ArrayList<Object>();
        return nextParameterSet;
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

    /** randomDouble is an altered version of randomInt from
     * the week5 lectures in the RandomlyTestTriangle class
     * */
    static double randomDouble(Random r) {
        if (MIN_DOUBLE == Double.MIN_VALUE && MAX_DOUBLE == Double.MAX_VALUE) {
            return r.nextDouble();
        } else {
            return (MAX_DOUBLE - MIN_DOUBLE + 1.)*r.nextDouble() + MIN_DOUBLE;
        }
    }

    /** coveredBranch is taken from the week5 lectures of the RandomlyTestTriangle class and
     * has not been changed in any form
     * */
    public static void coveredBranch(int id, Set<Integer> coveredBranches) {
        if (!coveredBranches.contains(id)) {
            System.out.println("* branch covered: " + id);
            coveredBranches.add(id);
        }
    }

//    @todo needed for MCDC - can we change Set<Integer> to HashMap<Integer,Boolean> so that we know if the loggedCondition was true or not
    public static boolean logCondition(int id, Boolean condition, HashMap<Integer,Boolean> coveredConditions ) {
//        System.out.println(condition);
        boolean result = condition;
        coveredConditions.put(id, result);
        // ... log the id somewhere, along with the result,
        // thereby storing whether the condition was executed
        // as true or false, for computing coverage later on...
        return result;
    }

    static int hammingDist(String str1, String str2)
    {
        int i = 0, count = 0;
        while (i < str1.length())
        {
            if (str1.charAt(i) != str2.charAt(i))
                count++;
            i++;
        }
        return count;
    }
}
