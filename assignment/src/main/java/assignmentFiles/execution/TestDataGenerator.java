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


    HashMap<String, HashMap<String,Type>> methodList;

    //the constructor is called and set up the parameters for the coverage
    // (i.e. used when checking if an input was a new successful test to be added to the test suite)
    public TestDataGenerator(String coverageCriteria, String generationMethod, HashMap<String, HashMap<String, Type>> methodList){
        this.methodList = methodList;
        if (coverageCriteria.equals("condition")){

        }
        else { // for now lets just assume that otherwise it was (coverageCriteria.equals("correlatedMCDC"))

        }
        if (generationMethod == "random"){

        }
        else { //for now lets just assume if not random then its (generationMethod == "search")

        }
    }

    public void testCaseGeneration(CompilationUnit cu) {


        //get an instance of the class
        Class clazz = cu.getClass();
        Method [] allMethods = clazz.getMethods();

    public static void randomBranchGeneration(Instrument classMethods) throws Exception {

        //iterate through each method
        for (HashMap.Entry<String,HashMap<String,Type>> mapElement : methodList.entrySet()) {
            String methodName = mapElement.getKey();
            HashMap<String,Type> parameterList = mapElement.getValue();


        }   //TODO come back to or delete

        for (Method method:allMethods){
            
        }


        //set up how the inputs are generated
        Random r = new Random();
        Set<Integer> coveredBranches = new TreeSet<>();

        //invoke the method once to get the type of the output and record it

        for (int i=0; i < ITERATIONS; i ++) {
            //generate inputs

            //get result from said inputs

            //is this input wanted?
            //here check all the coverage business relative to what was set up

            //check if full/enough coverage has been reached if so break
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

        //output the inputs for each test-case in form HashMap<Method Name,HashMap<parameter input, input type>>

        //next
        //https://stackoverflow.com/questions/160970/how-do-i-invoke-a-java-method-when-given-the-method-name-as-a-string
        //https://docs.oracle.com/javase/tutorial/java/generics/types.html
        //https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwid3N7GjP7vAhViWhUIHRvpDBwQFjAAegQIBhAD&url=http%3A%2F%2Fcrest.cs.ucl.ac.uk%2Ffileadmin%2Fcrest%2Fsebasepaper%2FGhaniC09b.pdf&usg=AOvVaw1kA1DwCCIHOMPkwjVTtfXV

    }
        System.out.println("");



    //this uses the url to get the class instance although not sure if it works
    private Class getClassInstance(String className) throws MalformedURLException, ClassNotFoundException{
        String classPath = System.getProperty("user.dir")+"src/main/java/assignmentFiles/instrumentedFiles/"+className+".java";
        File f = new File(classPath);
        URL[] cp = {f.toURI().toURL()};
        URLClassLoader urlcl = new URLClassLoader(cp);
        Class clazz = urlcl.loadClass("instrumentedFiles.Instrumented"+className);
        return clazz;
    }

    public static void searchBasedGeneration(Instrument classMethods) {
//            Instrument Instances:
//            this.path = path;
//            this.methodDetails = methodDetail;
//            this.ifStmts = ifStmtLogs;
//            this.branchTotal = branchCount;
//            this.conditionTotal = conditionCount;


        ////        example code to parse hashmaps
//        for (Map.Entry<String, List> entry : classMethods.methodDetails.entrySet()) {
//            String key = entry.getKey();
//            List value = entry.getValue();
//            System.out.println("Key: "+ key);
//            System.out.println("Vals: " + value);
//        }
//
//        System.out.println("");
//
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

        //return method, each parameter - n test cases
        //HashMap<String,HashMap<String,String>>
        // or
        //HashMap<String,HashMap<Class<?>,String>>

        //TODO look into Class<?>


        //no coveredBranches in method means that theres no branches so trivially can only make one test case
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
