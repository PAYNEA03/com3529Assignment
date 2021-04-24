package assignmentFiles.execution;

import assignmentFiles.instrumentedFiles.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.type.Type;
import assignmentFiles.utils.Pair;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


public class TestDataGenerator {
    private CompilationUnit compilationUnit;

    int ITERATIONS = 1250;
    int MIN_INT = -15;
    int MAX_INT = 15;
    double MIN_DOUBLE = -15.;
    double MAX_DOUBLE = 15.;
    int MIN_STRING_LEN = 5;
    int MAX_STRING_LEN = 30;

    //alphanumeric is the type of strings generated, alphabetic and numeric if true, only alphabetic if false
    boolean alphanumeric = true;

    //MAX_ATTEMPTS is the amount of times a method will have inputs generated for it and fail before the method is removed
    // i.e. the point at which the program deems it too difficult to generate input object for it
    static final int MAX_ATTEMPTS = 30;

    private String coverageCriteria;

    private Random rand;

    //make it so that in isNewMCDCTestCase this is filled out to True when that condition has a pair added for it,
    // can use for evaluation of coverage because once all conditions have a pair coverage is 100% complete
    private HashMap<String, HashMap<Integer,Boolean>> methodConditionsWithPairs = new HashMap<>();

    //left true right false
    private HashMap<String, HashMap<Integer,Boolean>> methodBranchesCovered;
    private HashMap<String, HashMap<Integer,Pair<Boolean,Boolean>>> methodConditionsCovered;

    //MCDCoverage is a map from a conditionSequence (a string denoting the conditions truth values for an input)
    // - said input is given as {"parameters":xyz} in the hashmap the conditionSequence maps to
    // - the inputs has a corresponding branchSequence as a string and is given in the hashMap as {"branchSequence":xyz}
    // - other inputs that have different yet matching conditionSequences (hamming distance of 1, 1 element different) are also in the hashmap
    //    these other inputs also map to a hashmap with "parameters":xyz and "branchSequence":xyz
    private HashMap<String,HashMap<String,Object>> MCDCoverage = new HashMap<>();

    //use currentMethod to know which one is currently being tested so that when logConditions and coveredBranch is called
    //we can store the name of the method that it resides in
    private String currentMethod;
    //TODO map the name of the method to the condition and the branch ids that appear in them to know the size of the necessary test suites
    private HashMap<String,List<Integer>> conditionRecords;
    private HashMap<String,List<Integer>> branchRecords;
    //TODO maybe get logConditions and coveredBranch to pass the methodName of the method they're called from
    // but its VERY IMPORTANT that all of them are gotten so we may need to put it deeper inside the parser!

    private List<Object> nextParameterSet;

    public TestDataGenerator(CompilationUnit cu, String cc, HashMap<String, HashMap<Integer, Pair<Boolean, Boolean>>> methodConditions,
                             HashMap<String, HashMap<Integer, Pair<Boolean, Boolean>>> methodBranches){
        compilationUnit = cu;
        coverageCriteria = cc;

        //create conditionRecords and branchRecords from methodConditions and methodBranches

        this.conditionRecords = toRecords(methodConditions);
        this.branchRecords = toRecords(methodBranches);

        rand = new Random();

        if (coverageCriteria.equals("MCDC")){

            //populate methodConditionsWithPairs for tracking coverage

            for (Map.Entry<String,List<Integer>> methodCons : conditionRecords.entrySet()){
                methodConditionsWithPairs.put(methodCons.getKey(), new HashMap<>());
                for (int conditionId : methodCons.getValue()){
                    methodConditionsWithPairs.get(methodCons.getKey()).put(conditionId,false);
                }
            }
        }
        else if (coverageCriteria.equals("condition")){
            //populate methodConditionsCovered for tracking coverage
            methodConditionsCovered = new HashMap<>();
            for (Map.Entry<String,List<Integer>> methodCons : conditionRecords.entrySet()){
                methodConditionsCovered.put(methodCons.getKey(), new HashMap<>());
                for (int conditionId : methodCons.getValue()){
                    methodConditionsCovered.get(methodCons.getKey()).put(conditionId,new Pair<>(false,false));
                }
            }
        }
        else if (coverageCriteria.equals("branch")){
            //populate methodBranchesCovered for tracking coverage
            methodBranchesCovered = new HashMap<>();
            for (Map.Entry<String,List<Integer>> methodBrans : branchRecords.entrySet()){
                methodBranchesCovered.put(methodBrans.getKey(), new HashMap<>());
                for (int branchId : methodBrans.getValue()){
                    methodBranchesCovered.get(methodBrans.getKey()).put(branchId,false);
                }
            }
        }
    }

    //Constructor where the user inputs configurable maximum and minimum values for generated int and double inputs
    // and min and max lengths for generated string inputs
    public TestDataGenerator(CompilationUnit cu, String cc, HashMap<String, HashMap<Integer, Pair<Boolean, Boolean>>> methodConditions,
                             HashMap<String, HashMap<Integer, Pair<Boolean, Boolean>>> methodBranches, Double min_doub,
                             Double max_doub, Integer min_int, Integer max_int, Integer min_str_len, Integer max_str_len,
                             Boolean alphanumeric){
        this(cu, cc, methodConditions, methodBranches);
        if (min_doub != null){
            MIN_DOUBLE = min_doub;
        }

        MAX_DOUBLE = max_doub;
        MIN_INT = min_int;
        MAX_INT = max_int;
        MIN_STRING_LEN = min_str_len;
        MAX_STRING_LEN = max_str_len;
        this.alphanumeric = alphanumeric;
    }

    /** testGeneration takes the Instrument instance that successfully
     *
     * @param classMethods
     * @return
     * @throws Exception
     */
    public HashMap<String,List<List<Object>>> testGeneration(Instrument classMethods) throws Exception {
        Set<Integer> coveredBranches = new TreeSet<>();
        HashMap<Integer,Boolean> coveredConditions = new HashMap<>();
        Set<Integer> definitiveCoveredBranches = new TreeSet<>();



        //the test case output file
        //format MethodName - List of (each element is a test case) of Lists (each element is a parameter for that method)
        HashMap<String,List<List<Object>>> testCases = new HashMap<>();
        //failed attempts catalogues all the times each method has failed to have its inputs generated,
        // reach more than MAX_ATTEMPTS and the program gives up with test cases for that method
        HashMap<String,Integer> failedAttempts = new HashMap<>();
        for (String classMethod : classMethods.methodDetails.keySet()){
            testCases.put(classMethod,new ArrayList<>());
            failedAttempts.put(classMethod,0);
        }

        HashMap<String, List> methodDetailsX = classMethods.methodDetails;

        for (int i=0; i < ITERATIONS; i ++) {

            //removedMethods is the keys of any methods that have reached maximum coverage for the given criteria
            Set<String> removedMethods = new TreeSet<>();
            // iterate through all the class methods
            for (Map.Entry<String,List> methodEntry: methodDetailsX.entrySet()) {
                //get the current method
                currentMethod = methodEntry.getKey();

                /**GENERATE THE TEST CASE INPUTS**/

                boolean successfulInputGeneration = true;
                //assign values to each parameter variable
                failedAttempt:
                for (Object h : methodEntry.getValue()) {
                    HashMap t = (HashMap) h;
                    String parameterType = ((Type)t.get("paramType")).asString();
                    //System.out.println(parameterType); getting the parameter type works :)
                    switch (parameterType){

                    case "double":
                        t.put("value", generateDouble());
                        break;
                    case "int":
                        t.put("value", generateInt());
                        break;
                    case "boolean":
                        t.put("value", generateBoolean());
                        break;
                    case "String":
                        t.put("value", generateString());
                        break;
                    default:
                        Optional<Object> newOther = generateOther((Type)t.get("paramType"));
                        if (newOther.isPresent()){
                            t.put("value", newOther.get());
                        }
                        else {
                            System.out.println("Unable to initialise an input for: "+methodEntry.getKey());
                            successfulInputGeneration = false;
                            failedAttempts.replace(currentMethod,failedAttempts.get(currentMethod)+1);
                            //if its failed to load the inputs more than MAX_ATTEMPTS add its name to removedMethods
                            // to be removed next iteration
                            if (failedAttempts.get(currentMethod)>MAX_ATTEMPTS){
                                removedMethods.add(currentMethod);
                            }
                            break failedAttempt;
                        }
                    }
                }
                if (successfulInputGeneration) {
                    //if its MCDC we want to reset coveredBranches and coveredConditions so we can check their connection
                    if (coverageCriteria.equals("MCDC")) {
                        coveredBranches = new TreeSet<>();
                        coveredConditions = new HashMap<>();
                    }

                    // print iteration progress and pass updated hashmap with correctly generated values attached
                    System.out.println("~~~~~~~~~~~~Call " + (i + 1) + "~~~~~~~~~");
                    Object result = Instrumented.assignVariables(methodEntry, coveredBranches, coveredConditions);
                    System.out.println("-> " + result);
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

                    //evaluate whether these inputs are worth making into a test case
                    boolean success = false;
                    //for MCDC
                    String testCasePartner = null;

                    /** COVERAGE CHECKING**/ //- are these inputs worth making a test case out of?
                    switch (coverageCriteria) {
                        case "MCDC":
                            //check coveredBranches against MCDCoverage list, non-appearing conditions are considered false
                            Pair<Boolean, String> results = isNewMCDCTestCase(methodEntry, coveredBranches, coveredConditions);
                            if (results.getRight() != null) {
                                testCasePartner = results.getRight();
                            }
                            break;

                        //Branch Coverage
                        //we check what branches are now covered (i.e. aren't in the definitiveCoveredBranches)
                        //and if new branches were found then add these inputs as a test case
                        case "branch":
                            success = isNewBranchTestCase(coveredBranches, definitiveCoveredBranches);
                            break;

                        //Condition Coverage
                        //we check what conditions have had which truth values assigned  (i.e. aren't in the definitiveCoveredConditions)
                        //and if new conditions were found then add these inputs as a test case
                        case "condition":
                            success = isNewConditionTestCase(coveredConditions);
                    }

                    //if the test case was a success add it to the testCases
                    if (success) {
                        System.out.println("*** new test case: " + methodEntry.getValue().toString());
                        //                    loop adds result to methodEntry to allow asserting in testcases
                        for (Object t : methodEntry.getValue()) {
                            HashMap h = (HashMap) t;
                            h.put("result", result);
                        }
                        testCases.get(methodEntry.getKey()).add(methodEntry.getValue());
                        //also don't forget to add this test cases partner from MCDCoverage list if its MCDC coverage
                        if (testCasePartner != null) {
                            //then the test case partner is real so use testCasePartner as the key to get it as the partner
                            // of the current testcase

                            //find testCasePartner in MCDCoverage and add it as a test case where tsetCasePartner is the
                            //conditionSequence key to MCDCoverage
                            HashMap<String, Object> partnerInfo = (HashMap) MCDCoverage.get(methodEntry.getKey()).get(testCasePartner);
                            testCases.get(methodEntry.getKey()).add((List) partnerInfo.get("parameters"));
                        }
                    }

                    /** METHOD COVERAGE EVALUATION**/
                    //evaluate whether the target coverage criteria have been reached for this method
                    boolean completelyCovered = true;
                    switch (coverageCriteria) {
                        //MCDC
                        //this evaluation will be whether all the conditions for that class have a test case pair
                        // where all other conditions are minor and stay the same and the major condition flips with the
                        // predicate/branch
                        case "MCDC":
                            for (Map.Entry<Integer, Boolean> coverage : methodConditionsWithPairs.get(currentMethod).entrySet()) {
                                if (!coverage.getValue()) {
                                    completelyCovered = false;
                                    break;
                                }
                            }
                            break;

                        //Branch Coverage
                        //has this method got any more branches in methodBranchesCovered that aren't true/covered
                        case "branch":
                            for (Map.Entry<Integer, Boolean> coverage : methodBranchesCovered.get(currentMethod).entrySet()) {
                                if (!coverage.getValue()) {
                                    completelyCovered = false;
                                    break;
                                }
                            }
                            break;

                        //Condition Coverage
                        //has this method got any more conditions in methodConditionsCovered that aren't both true
                        // (i.e. has been evaluated to true and false in each)
                        case "condition":
                            for (Map.Entry<Integer, Pair<Boolean, Boolean>> coverage : methodConditionsCovered.get(currentMethod).entrySet()) {
                                //check that left or right is not false (i.e. that true and false for that condition are covered)
                                if (!coverage.getValue().getLeft() || !coverage.getValue().getRight()) {
                                    completelyCovered = false;
                                    break;
                                }
                            }
                            break;
                    }
                    //if its deemed that the current method is now fully covered add its name to removedMethods
                    if (completelyCovered) {
                        System.out.println(currentMethod + " has 100% coverage");
                        System.out.println(methodDetailsX.size() - 1 + " methods left to cover");
                        removedMethods.add(currentMethod);
                    }
                }
            }
            //before the next iteration begins - remove all methods in removedMethods from classMethods.methodDetails - i.e. are fully covered
            // so that they aren't in methodDetailsX and have any check next iteration
            for (String removedMethod : removedMethods) {
                methodDetailsX.remove(removedMethod);
            }

            //if no more methods then break the iterations
            if (methodDetailsX.size()<1){
                break;
            }
        }

        /** TEST GENERATION RESULTS SUMMARY **/
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        switch (coverageCriteria) {
            case "branch":
                System.out.println("Branch Coverage: " + definitiveCoveredBranches.size() + "/" + classMethods.branchTotal);
                System.out.println("Covered Branch IDs: " + definitiveCoveredBranches);
                break;
            case "condition":
                System.out.println("Condition Coverage: " + getFinalConditionCoverage() + "/" + 2 * classMethods.conditionTotal);
                break;
            case "MCDC":
                printMCDCoverage();
                break;
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        System.out.println();

        //print test cases
        System.out.println(testCases.toString());

        return testCases;
    }


    /******* MCDC *******/
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
                for (Map.Entry<String,Object> partnerRecord: MCDCRecord.getValue().entrySet()) {
                    //instances inside the MCDCRecords that are HashMaps will be partners,
                    // the rest will be info on this current test case (Strings)
                    if (partnerRecord.getValue() instanceof HashMap){
                        HashMap<String, Object> partnerMap = (HashMap) partnerRecord.getValue();
                        majorCondition = getIndexOfMajorCondition(conditionSequence, partnerRecord.getKey());
                        if (!(majorCondition < 0) && partnerMap.get("branchSequence") != branchSequence &&
                                !majorConditionAlreadyCovered(majorCondition)) {
                            //make the found matching partner the key in MCDCoverage and add the new test case to it as a partner

                            //if the partner record isn't already a key make it into one
                            if (!MCDCoverage.containsKey(partnerRecord.getKey())) {
                                MCDCoverage.put(partnerRecord.getKey(), partnerMap);
                            }

                            //then add the current test case to it as a partner
                            MCDCoverage.get(partnerRecord.getKey()).put(methodInfo.getKey(), methodInfo.getValue());

                            //partner is not set because that test case is already in the test suite from being added before

                            conditionSequenceInMap = true;
                            success = true;
                            break outer;//do a double break
                        }
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

    /** majorConditionAlreadyCovered returns true if said condition in said method has been covered yet or if that method
     *  doesn't have that condition, otherwise it returns false
     *
     * @param majorCondition the id of the current condtion thats been covered
     * @return has it been covered already in another test case?
     */
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
        System.out.println("Total coverage of: " + totalCoverage + "/" + totalConditions);
    }

    /******* BRANCH *******/
    private boolean isNewBranchTestCase(Set<Integer> coveredBranches, Set<Integer> definitiveCoveredBranches){
        boolean success = false;
        for (Integer covered : coveredBranches){
            if (!methodBranchesCovered.get(currentMethod).getOrDefault(covered,true)){
                success = true;
                methodBranchesCovered.get(currentMethod).replace(covered,true);
                System.out.println(" ** new branch covered: "+covered);
                definitiveCoveredBranches.add(covered);
            }
        }
        return success;
    }

    /******* CONDITION COVERAGE *******/ //BROKENNNNNNNN
    private boolean isNewConditionTestCase(HashMap<Integer,Boolean> coveredConditions){
        boolean success = false;
        HashMap<Integer,Pair<Boolean,Boolean>> currentMethodCoverage = methodConditionsCovered.get(currentMethod);
        //iterate through coveredConditions and check if methodConditionWithPairs has true
        for (Map.Entry<Integer,Boolean> justCovered : coveredConditions.entrySet()){
            //check if the condition being true (currentMethodCoverage's left boolean) hasn't been covered already
            // and the condition has been found as true with this input
            if (!methodConditionsCovered.get(currentMethod).get(justCovered.getKey()).getLeft() &&
                    justCovered.getValue()){
                success = true;
                methodConditionsCovered.get(currentMethod).get(justCovered.getKey()).setLeft(true);
                System.out.println(" ** new condition covered as "+ justCovered.getValue()+": "+justCovered.getKey());
            }

            //check if the condition being false (currentMethodCoverage's right boolean) hasn't been covered already
            // and the condition has been found as false with this input
            else if (!methodConditionsCovered.get(currentMethod).get(justCovered.getKey()).getRight() &&
                    !justCovered.getValue()){
                //so if not covered and this test case makes the condition false then add these inputs as a test case
                success = true;
                methodConditionsCovered.get(currentMethod).get(justCovered.getKey()).setRight(true);
                System.out.println(" ** new condition covered as "+ justCovered.getValue()+": "+justCovered.getKey());
            }
        }
        return success;
    }

    private int getFinalConditionCoverage(){
        //conditions the number of covered condition truth values successfully covered
        int conditionsCovered = 0;
        //iterate through all the methods and all the conditions and get a total number of covered condition polarities
        for (Map.Entry<String, HashMap<Integer,Pair<Boolean,Boolean>>> coverage : methodConditionsCovered.entrySet()){
            for (Map.Entry<Integer,Pair<Boolean,Boolean>> methodCoverage : coverage.getValue().entrySet()){
                if (methodCoverage.getValue().getLeft()){
                    conditionsCovered++;
                }
                if (methodCoverage.getValue().getRight()){
                    conditionsCovered++;
                }
            }
        }
        return conditionsCovered;
    }


    /**TEST DATA GENERATION*/
    private int generateInt(){
        if (MIN_INT == Integer.MIN_VALUE && MAX_INT == Integer.MAX_VALUE) {
            return rand.nextInt();
        } else {
            return rand.nextInt((MAX_INT - MIN_INT + 1)) + MIN_INT;
        }
    }

    private double generateDouble(){
        if (MIN_DOUBLE == Double.MIN_VALUE && MAX_DOUBLE == Double.MAX_VALUE) {
            return rand.nextDouble();
        } else {
            return (MAX_DOUBLE - MIN_DOUBLE + 1.)*rand.nextDouble() + MIN_DOUBLE;
        }
    }

    private boolean generateBoolean(){
        if (rand.nextDouble() >= 0.5){
            return true;
        }
        else {
            return false;
        }
    }

    /** try to discern the type of this other input from its javaparser Type and generate an object for it
     *
     * @param parameterType javaparser Type variable for this parameter
     * @return an Optional Object which will only exist if it successfully generated the input object
     */
    private Optional<Object> generateOther(Type parameterType){
        Class<?> cls = null;
        //if its a primitive type then the type will be a part of java.lang and so getting its class will be quite easy
        if (parameterType.isPrimitiveType()) {
            try {
                cls = Class.forName("java.lang." + parameterType.toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("The program found a parameter type that it is not able to generate input for " + parameterType.toString());
                System.exit(0);
            }
        } else {
            //try and get it through the compilation unit
            if (compilationUnit.getClassByName(parameterType.toString()).isPresent()) {
                if (compilationUnit.getClassByName(parameterType.toString()).get().getFullyQualifiedName().isPresent()) {
                    String className = compilationUnit.getClassByName(parameterType.toString()).get().getFullyQualifiedName().get();
                    try {
                        cls = Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        System.out.println("The program found a parameter type that it is not able to generate input for " + parameterType.toString());
                        System.exit(0);
                    }
                }
            }
        }

        return constructOther(cls);
    }

    /** This the class that actually takes the class of the object to be instantiated and attempts to make an instance
     *   for the test case. Can be called recursively and will have the basecase of either managing to create the instance
     *   or being unable to make any of the instances needed
     *
     * @param clazz the class of the object that is to have an instance created
     * @return an Optional Object which will only exist if a version of the object was successfully created
     */
    private Optional<Object> constructOther(Class<?> clazz){
        //if we successfully found the class lets see if we can manage to create on via one of its constructors
        if (clazz != null){
            Constructor[] cs = clazz.getConstructors();
            //iterate through constructors until you find one that can be completed
            for (Constructor<?> cons : cs){
                boolean success = true;
                Object[] consInputs = new Object[cons.getParameterCount()];
                //iterate through all the constructor's parameters and try to generate inputs for each
                Class<?>[] params = cons.getParameterTypes();
                outer:
                for (int i = 0; i < params.length; i++){
                    switch (params[i].getTypeName()){
                        case "String":
                            consInputs[i] = generateString();
                            break;
                        case "int":
                        case "Integer":
                            consInputs[i] = generateInt();
                            break;
                        case "double":
                        case "Double":
                            consInputs[i] = generateDouble();
                            break;
                        case "boolean":
                        case "Boolean":
                            consInputs[i] = generateBoolean();
                            break;
                        //the default here is to start a recursion where the generateOther is called and if it returns nothing
                        //then there was something it could not generate
                        default:
                            Optional<Object> newOther = constructOther(params[i]);
                            if (newOther.isPresent()){
                                consInputs[i] = newOther;
                            }
                            else {
                                success = false;
                                break outer;//do a double break as this constructor is a dud
                            }
                    }
                }

                //if a completable set of inputs was found then construct the object and return it, if it doesnt work carry on
                // with the other constructors
                if (success){
                    try {
                        Object finalObject = cons.newInstance(consInputs);
                        return Optional.of(finalObject);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        System.out.println("System attempted to instantiate test data it did not have access to");
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }


        }
        //failed to create the object return an empty Optional
        return Optional.empty();
    }

    /** generates a random string of a random length in the range max and min string length that is either alphanumeric
     *   or just alphabetic based on user passed inputs, by default alphanumeric of range 5 to 30
     *
     * @return random string
     */
    private String generateString(){
        // use min and max string length to get the string length of this particular input
        int targetStringLength = rand.nextInt((MAX_STRING_LEN - MIN_STRING_LEN + 1)) + MIN_STRING_LEN;

        //set the limits of which characters to include in the random generation to alphabetic or alphanumeric characters only
        int leftLimit = 97;
        if (alphanumeric) {
            leftLimit = 48; // numeral '0'
        }
        int rightLimit = 122; // letter 'z'

        //generate alphanumeric string
        String generatedString = rand.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    /** Instrumentation Utils */

    public static Object assignValues(String name, List value) {
        Object assignValue = 0;
        //searches hashmap for matching variable name and assigns value to it
        for (Object i: value) {
            HashMap h = (HashMap) i;

            if (h.get("paramName").toString().equals(name)) {
                assignValue = h.get("value");
            }
        }
        System.out.println("Var: " + name + ", value: " + assignValue);
        return assignValue;
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

    public static boolean logCondition(int id, Boolean condition, HashMap<Integer,Boolean> coveredConditions ) {
        boolean result = condition;
        coveredConditions.put(id, result);
        return result;
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

    /** toRecords takes the method and their integer condition or branch ids and sorts them to make a consistent
     *   ordered sequence for the creation of conditionSequences and branchSequences for MCDC coverage checking purposes
     *
     * @param methods the hashmap from methodName to that methods contained branch/condition ids and truth values
     * @return hashmap from methodName to sorted list of their contained branch/condition ids
     */
    private HashMap<String,List<Integer>> toRecords(HashMap<String, HashMap<Integer, Pair<Boolean, Boolean>>> methods){
        HashMap<String,List<Integer>> output = new HashMap<>();
        for (Map.Entry<String,HashMap<Integer,Pair<Boolean,Boolean>>> mC : methods.entrySet()){
            List<Integer> ids = new ArrayList<>(mC.getValue().keySet());
            Collections.sort(ids);
            output.put(mC.getKey(),ids);
        }
        return output;
    }
}
