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

    //make it so that in isNewMCDCTestCase this is filled out to True when that condition has a pair added for it,
    // can use for evaluation of coverage because once all conditions have a pair coverage is 100% complete
    private HashMap<String, HashMap<Integer,Boolean>> methodConditionsWithPairs;
    //TODO populate at the start somehow (from conditionRecords?)

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
                //MCDC
                String testCasePartner = null;
                if (coverageCriteria == "MCDC") {
                    //check coveredBranches against MCDCoverage list, non-appearing conditions are considered false
                    Pair<Boolean,String> results = isNewMCDCTestCase(methodEntry,coveredBranches, coveredConditions);
                    if (results.getRight() != null){
                        testCasePartner = results.getRight();
                    }

                    //add everything to MCDCoverage but only add it to the testCases if a second pair is found for that major condition
                    //i.e. search through to find if
                    // 1. its already in the MCDCoverage
                    // 2. its got a partner test case that is
                    //      - this test case with 1 condition flipped and everything else the same
                    //      - any associated conditions are flipped too
                }
                //Branch Coverage
                //we check what branches are now covered (i.e. aren't in the definitiveCoveredBranches)
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
//                    loop adds result to methodEntry to allow asserting in testcases
                    for (Object t: methodEntry.getValue()
                         ) {
                        HashMap h = (HashMap) t;
                        h.put("result",result);
                    }
                    testCases.get(methodEntry.getKey()).add(methodEntry.getValue());
                    //also don't forget to add this test cases partner from MCDCoverage list if its MCDC coverage
                    if (testCasePartner != null){
                        //then the test case partner is real so use testCasePartner as the key to get it as the partner
                        // of the current testcase

                        //find testCasePartner in MCDCoverage and add it as a test case where tsetCasePartner is the
                        //conditionSequence key to MCDCoverage
                        HashMap<String,Object> partnerInfo = (HashMap)MCDCoverage.get(methodEntry.getKey()).get(testCasePartner);
                        testCases.get(methodEntry.getKey()).add((List)partnerInfo.get("parameters"));
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
        if (coverageCriteria == "branch") {
            System.out.println("Branch Coverage: " + definitiveCoveredBranches.size() + "/" + classMethods.branchTotal);
            System.out.println("Covered Branch IDs: " + definitiveCoveredBranches);
        }
        else if (coverageCriteria == "MCDC"){
            printMCDCoverage();
        }
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
    private Pair<Boolean,String> isNewMCDCTestCase(Map.Entry<String,List> methodInfo,Set<Integer> coveredBranches,HashMap<Integer,Boolean> coveredConditions){
        //remember - HashMap<String,HashMap<String,Object>> MCDCoverage - more at top of class

        //turn the results (coveredConditions and coveredBranches) into a strings that can be compared
        String conditionSequence = coveredConditionsIntoConditionSequence(coveredConditions);
        String branchSequence = coveredBranchesIntoBranchSequence(coveredBranches);
        boolean success = false;
        String partner = null;
        int majorCondition = -1;

        //check if new test case's conditionSequence is already a key
        if (!MCDCoverage.containsKey(conditionSequence)){

            //make InMap true if its found as a partner at any time or put as a partner into the hashmap
            boolean conditionSequenceInMap = false;

            outer:
            // iterate through all the records in MCDCoverage to check all the children and the hamming distance to those keys
            for (Map.Entry<String,HashMap<String,Object>> MCDCRecord: MCDCoverage.entrySet()){
                //check if its already in as a partner to this key if so break from the loop and nothing is added
                if (MCDCRecord.getValue().containsKey(conditionSequence)){
                    conditionSequenceInMap = true;
                    break;
                }

                //TODO split these next two sections into separate functions?

                // check if the conditionSequence has a hamming distance of 1 to the current key (i.e. only 1 condition different),
                // their branchSequences are different and that major condition doesn't already have a pair
                majorCondition = getIndexOfMajorCondition(conditionSequence,MCDCRecord.getKey());
                if (!(majorCondition < 0) && MCDCRecord.getValue().get("branchSequence")!=branchSequence &&
                        !majorConditionAlreadyCovered(majorCondition)){
                    //key will also be added if this is the first partner
                    if (MCDCRecord.getValue().isEmpty()){
                        partner = MCDCRecord.getKey();
                    }
                    //then add it to that key's hashmap as a partner and it'll be added as a test case
                    HashMap<String,Object> contents = new HashMap<>();
                    contents.put("parameters",methodInfo.getValue());
                    contents.put("branchSequence",branchSequence);
                    MCDCRecord.getValue().put(conditionSequence,contents);

                    conditionSequenceInMap = true;
                    success = true;
                    break;
                }

                //check if the conditionSequence has a hamming distance of 1 to any of this key's partners
                for (Map.Entry<String,Object> partnerRecord: MCDCRecord.getValue().entrySet()){
                    HashMap<String,Object> partnerMap = (HashMap)partnerRecord.getValue();
                    majorCondition = getIndexOfMajorCondition(conditionSequence,partnerRecord.getKey());
                    if (!(majorCondition < 0) && partnerMap.get("branchSequence")!=branchSequence &&
                            !majorConditionAlreadyCovered(majorCondition)){
                        //make the found matching partner the key in MCDCoverage and add the new test case to it as a partner

                        //if the partner record isn't already a key make it into one
                        if (!MCDCoverage.containsKey(partnerRecord.getKey())){
                            MCDCoverage.put(partnerRecord.getKey(),partnerMap);
                        }

                        //then add the current test case to it as a partner
                        MCDCoverage.get(partnerRecord.getKey()).put(methodInfo.getKey(),methodInfo.getValue());

                        //partner is not set because that test case is already in the test suite from being added before

                        conditionSequenceInMap = true;
                        success = true;
                        break outer;//do a double break
                    }
                }
            }

            // if not got a distance of 1 to any key in the current coverage add it as a new key
            if (!conditionSequenceInMap){
                //success is not set to true as it does not have a partner yet
                HashMap<String,Object> contents = new HashMap<>();
                contents.put("parameters",methodInfo.getValue());
                contents.put("branchSequence",branchSequence);
                MCDCoverage.put(conditionSequence,contents);
            }
            //otherwise if you find the conditionSequence anywhere in the hashmap then just move on, nothing is added
        }

        //if success is true then we can fill in coveredConditions for this method and this majorCondition
        if (success){
            methodConditionsWithPairs.get(currentMethod).replace(majorCondition,true);
        }

        return new Pair<>(success,partner);
    }

    private boolean majorConditionAlreadyCovered(int majorCondition){
        return methodConditionsWithPairs.get(currentMethod).getOrDefault(majorCondition, true);
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

    private void printMCDCoverage(){
        int totalCoverage = 0;
        int totalConditions = 0;
        for (Map.Entry<String,HashMap<Integer,Boolean>> method: methodConditionsWithPairs.entrySet()){
            HashMap<Integer,Boolean> methodCoverage = method.getValue();
            int thisMethodsConditions = conditionRecords.get(method.getKey()).size();
            int thisMethodsCoverage = 0;
            for (Map.Entry<Integer,Boolean> conditions:methodCoverage.entrySet()){
                if (conditions.getValue()){
                    thisMethodsCoverage++;
                }
            }
            System.out.println("Method "+ method.getKey() +" has coverage of: " + thisMethodsCoverage + "/" + thisMethodsConditions);
            System.out.println("");
            totalCoverage += thisMethodsCoverage;
            totalConditions += thisMethodsConditions;
        }
        System.out.println("Total class coverage of: " + totalCoverage + "/" + totalConditions);
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

    /** when passed two conditionSequences it will return the index at which the condition is flipped (i.e. the major
     * condition), it will not count if the differing single flip is not a 1 or 0 as that means it is not a real
     * test case pair and will return -1
     *
     * @param str1
     * @param str2
     * @return index of the major condition or -1 if hamming distance is greater than 1 or the 1 different character
     *          is a letter rather than a 0 or 1
     */
    private int getIndexOfMajorCondition(String str1, String str2)
    {
        int i = 0, count = 0, index = -1;
        while (i < str1.length())
        {
            if (str1.charAt(i) != str2.charAt(i)) {
                count++;
                index = i;
            }
            if (count > 1){
                break;
            }
            i++;
        }
        if (count==1){
            if (str1.charAt(index)=='0'||str1.charAt(index)=='1'||str2.charAt(index)=='0'||str2.charAt(index)=='1'){
                //here we have a real condition pair so we get the index and we take away the length of the currentMethod
                return index - currentMethod.length();
            }
        }
        return -1;
    }
}
