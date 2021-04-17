package assignmentFiles.execution;

import assignmentFiles.instrumentedFiles.*;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.type.Type;
import assignmentFiles.utils.Pair;

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

    private HashMap<Integer,Boolean> coveredConditions;
    //TODO make it so that in isNewMCDCTestCase this is filled out to True
    // when that condition has a pair added for it, can use for evaluation of coverage because once all conditions have
    // a pair coverage is 100% complete

    //MCDCoverage is a map from a conditionSequence (a string denoting the conditions truth values for an input)
    // - said input is given as {"parameters":xyz} in the hashmap the conditionSequence maps to
    // - the inputs has a corresponding branchSequence as a string and is given in the hashMap as {"branchSequence":xyz}
    // - other inputs that have different yet matching conditionSequences (hamming distance of 1, 1 element different) are also in the hashmap
    //    these other inputs also map to a hashmap with "parameters":xyz and "branchSequence":xyz
    private HashMap<String,HashMap<String,Object>> MCDCoverage;

    //use currentMethod to know which one is currently being tested so that when logConditions and coveredBranch is called
    //we can store the name of the method that it resides in
    private String currentMethod;
    //TODO map the name of the method to the condition and the branch ids that appear in them to know the size of the necessary test suites
    private HashMap<String,List<Integer>> conditionRecords = new HashMap<>();
    private HashMap<String,List<Integer>> branchRecords = new HashMap<>();
    //TODO maybe get logConditions and coveredBranch to pass the methodName of the method they're called from
    // but its VERY IMPORTANT that all of them are gotten so we may need to put it deeper inside the parser!

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
                currentMethod = methodEntry.getKey();
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
                String testCasePartner = null;
                if (coverageCriteria == "MCDC") {
                    //check coveredBranches against MCDCoverage list, non-appearing conditions are considered false
                    //String[] results = isNewMCDCTestCase(methodEntry);
                    //if (results[0]=="success"){
                    //    success = true;
                    //    if (results[1] != null){
                    //        testCasePartner = results[1];
                    //    }
                    //}

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
                    if (testCasePartner != null){
                        //then the test case partner is real so use testCasePartner as the key or get it as the partner
                        // of the current testcase

                        //find testCasePartner in MCDCoverage and add it as a test case where tsetCasePartner is the
                        //conditionSequence key to MCDCoverage
                    }
                }


                //evaluate whether the target coverage criteria have been reached for this method
                //if so then somehow delete this methodEntry from the entrySet and carry on until all of them are met
                    //TODO to do this with branch coverage will need a HashMap from method to number of branches
                    // (somehow link with Instrument.addBranchLogger())

                    //TODO for MCDC this evaluation will be whether all the conditions for that class have a test case pair
                    // where all other conditions are minor and stay the same and the major condition flips with the
                    // predicate/branch
            }
        }

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Branch Coverage: " + definitiveCoveredBranches.size() + "/" + classMethods.branchTotal);
        System.out.println("Covered Branch IDs: " + definitiveCoveredBranches);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        System.out.println("");

        System.out.println(testCases.toString());

    }

    /** isNewMCDCTestCase uses the MCDCoverage data structure to find if the new test case given the conditions and
     *   branches it covered whether it should be added into the test suite
     *
     * @param methodInfo the current method under test including the parameters of the current input / potential test case
     * @param coveredConditions hashmap of all covered conditions and their truth value
     * @param coveredBranches integer set of all branches that were covered
     * @return a Pair object where the left hand side is whether it was a success and the current is to be added as a test case
     *          and the string is the conditionSequence of the partner to it in the MCDCoverage data structure
     */
    private Pair<Boolean,String> isNewMCDCTestCase(Map.Entry<String,List> methodInfo, HashMap<Integer,Boolean> coveredConditions, Set<Integer> coveredBranches){
        //remember - HashMap<String,HashMap<String,Object>> MCDCoverage - more at top of class

        //turn the results (coveredConditions and coveredBranches) into a strings that can be compared
        String conditionSequence = coveredConditionsIntoConditionSequence(coveredConditions);
        String branchSequence = coveredBranchesIntoBranchSequence(coveredBranches);
        boolean success = false;
        String partner = null;

        //check if new test case's conditionSequence is already a key
        if (!MCDCoverage.containsKey(conditionSequence)){

            //make InMap true if its found as a partner at any time or put as a partner into the hashmap
            boolean conditionSequenceInMap = false;

            //TODO get where the different condition is so that if you find it to be a new pair test case that should be
            // put in the test suite you can record which conditions are done

            // iterate through all the records in MCDCoverage to check all the children and the hamming distance to those keys
            for (Map.Entry<String,HashMap<String,Object>> MCDCRecord: MCDCoverage.entrySet()){
                //check if its already in as a partner to this key if so break from the loop and nothing is added
                if (MCDCRecord.getValue().containsKey(conditionSequence)){
                    conditionSequenceInMap = true;
                    break;
                }
                // check if the conditionSequence has a hamming distance of 1 to the current key (i.e. only 1 condition different)
                // and their its branchSequences are different
                if (hammingDist(conditionSequence,MCDCRecord.getKey())==1 &&
                        MCDCRecord.getValue().get("branchSequence")!=branchSequence){
                    //key will also be added if this is the first partner
                    if (MCDCRecord.getValue().isEmpty()){
                        partner = MCDCRecord.getKey();
                    }
                    //then add it to that key's hashmap as a partner and it'll be added as a test case
                    HashMap<String,Object> contents = new HashMap<>();
                    contents.put("parameters",methodInfo.getValue());
                    contents.put("branchSequence",branchSequence);
                    MCDCRecord.getValue().put(conditionSequence,contents);

                    success = true;
                    break;
                }

                //check if the conditionSequence has a hamming distance of 1 to any of this key's partners
                for (Map.Entry<String,Object> partnerRecord: MCDCRecord.getValue().entrySet()){
                    HashMap<String,Object> partnerMap = (HashMap)partnerRecord.getValue();
                    if (hammingDist(conditionSequence,partnerRecord.getKey())==1 &&
                            partnerMap.get("branchSequence")!=branchSequence){
                        //TODO
                        //make the partner the key in MCDCoverage and add the new test case to it as a partner

                        //partner is not set because that test case is already in the test suite from being added before

                        success = true;
                        break;//TODO double break?
                    }
                }
            }


            // if no key is close make sure to check distance of all key partners as you go - if distance to any key is 1 + branchSequence different
            //  then take the found partner and make it into a key, add new to it and make into a new test case but don't add the found partner as it'll already have one

            // if not got a distance of 1 to any key in the current coverage add it as a new key
            if (!conditionSequenceInMap){
                success = true;
                HashMap<String,Object> contents = new HashMap<>();
                contents.put("parameters",methodInfo.getValue());
                contents.put("branchSequence",branchSequence);
                MCDCoverage.put(conditionSequence,contents);
            }
            //otherwise if you find the conditionSequence anywhere in the hashmap then just move on, nothing is added
        }


        return new Pair<>(success,partner);
    }

    private String coveredConditionsIntoConditionSequence(HashMap<Integer,Boolean> coveredConditions){
        List<Integer> currentMethodsConditions = conditionRecords.get(currentMethod);
        String conditionSequence = currentMethod;
        //iterate through all the conditions for the current method
        for (int i : currentMethodsConditions){
            //add on the condition - if its present in coveredConditions check if true or not
            // add 1 if true 0 if false
            if (coveredConditions.containsKey(i)) {
                if (coveredConditions.get(i)){
                    conditionSequence += "1";
                }
                else {
                    conditionSequence += "0";
                }
            }
            //if absent then set as false
            else {
                conditionSequence += "0";
            }
        }
        return conditionSequence;
    }

    private String coveredBranchesIntoBranchSequence(Set<Integer> coveredBranches){
        List<Integer> currentMethodsBranches = branchRecords.get(currentMethod);
        String branchSequence = "";
        //iterate through all the branches for the current method
        for (int i : currentMethodsBranches){
            //add on the branch - if its present then set as 1 (i.e. the predicate was made true)
            if (coveredBranches.contains(i)) {
                branchSequence += "1";
            }
            //if absent then set as false
            else {
                branchSequence += "0";
            }
        }
        return branchSequence;
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
        //add the branch to branchRecords if it doesn't already exist
        //if (!branchRecords.containsKey(id)){

        //}
    }

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
