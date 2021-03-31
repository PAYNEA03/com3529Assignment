package uk.ac.shef.com3529.lectures.execution;

import com.sun.jdi.ClassType;
import uk.ac.shef.com3529.lectures.Triangle.Type;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.lang.reflect.*;

public class RandomlyTestTriangle {

    static final int ITERATIONS = 10000;

    // static final int MIN_INT = Integer.MIN_VALUE;
    // static final int MAX_INT = Integer.MAX_VALUE;
    static final int MIN_INT = -10;
    static final int MAX_INT = 10;

    public static void main(String[] args) {
        reflectClasses("C:\\Users\\ambro\\Documents\\COMThirdYearUniWork\\COM3523SoftwareRe-engineering\\commons-text\\target\\classes\\");
        //randomlyTestClassify();
    }

    //the method declaration regex can extract a method from
    String methodRegex = "(public|protected|private|static|\\s) +[\\w\\<\\>\\[\\]]+\\s+(\\w+) *\\([^\\)]*\\) *(\\{?|[^;])";

    //these maps store the inputs and outputs of all the methods for the class, the string key is the method name
    Map<String, Class<?>> returns = new HashMap<String, Class<?>>();
    Map<String, Class<?>[]> parameters = new HashMap<String, Class<?>[]>();
    Map<String, Class<?>[]> fields = new HashMap<String, Class<?>[]>();
    public void reflectClasses(String className){
        //use Package java.lang.reflect to get the class as a string classString
        Class clazz;
        try{
            clazz = Class.forName(className); //get the class
        }
        catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        //now get that class as a string
        String path = clazz.getPackage().getName().replaceAll("\\.","/");
        path += clazz.getName() + ".java";

        try{
            String classContent = Files.readString(Paths.get(path));
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        //get all the fields
        Field[] fields = clazz.getFields();
        String[] fieldNames = new String[fields.length];
        //get all the field names as Strings
        for (int i = 0;i < fields.length; i++){
            fieldNames[i] = fields[i].getName();
        }

        Method[] methods = clazz.getMethods();
        //TODO get all methods as strings using regex

        for (Method m : methods) {
            String methodName = m.getName();
            returns.put(methodName,m.getReturnType());
            parameters.put(methodName, m.getParameterTypes());
            fields.put(methodName,clazz.)
            //TODO check if field is used within methodString for this method, if yes then fields.put(thismethodName,thisField.type

            //TODO how to match these maps up to the specific input?

            //TODO use regex on methods-as-strings to instrument method string (i.e. for each match on regex matcher)
        }

        //iterate through each Method object
        //  get the parameter types and types of each field attribute used
        //  get the return type
        //  store all these in some set of data structures for use in generating the test cases
        //  perhaps a map like Map<String (method name),object type> returns
        //  and Map<String (method name),Arraylist<T> object types> parameters/fieldsUsed
        //
        //  then get the method as a string and instrument it
        //  i.e. find all if statements and put a coveredBranch(id) after each
        //    or find all if statements and put a coveredCondition(id,condition) around each condition in each if and else if statement

        //    where id is incremented for each of these instrumented in turn
        //    and coveredCondition also returns the value of said condition so that the program still works

        //    An importing of our coverage counting package or whatever may need to be used to allow for the use of
        //    coveredBranch and coveredCondition but very unsure about this part

        //  replace the method in the classString with this instrumented method string we've just created

        //  a lot of this will be bracket matching to find the start and end of if statements, conditions, methods, etc.
        //  perhaps use a helper class like StringBuilder and use of Regex

        //can then turn the string into a class that can be executed into an object that can then be ran to generate
        // test cases
        // option for string to class are:
        // - https://www.edureka.co/community/66796/how-to-generate-a-class-at-runtime using bytecode
        // - https://github.com/burningwave/core using BodySourceGenerator
        //neither is ideal would be nice to have other options too


        //we have the inputs and outputs needed for each method is there with their respective object type
        //here is andy stuff with input generation and output expectation and what not


        //potential limitations:
        // - only create inputs for methods that have inputs (fields/parameters) that are

        //all code used taken from: (to refer back to)
        //https://stackoverflow.com/questions/326390/how-do-i-create-a-java-string-from-the-contents-of-a-file
        //https://stackoverflow.com/questions/2320404/creating-classes-dynamically-with-java
        //http://jessezhuang.github.io/article/java-reflection-test/
    }

    static void randomlyTestClassify() {
        Random r = new Random();
        Set<Integer> coveredBranches = new TreeSet<>();

        for (int i=0; i < ITERATIONS; i ++) {
            int side1 = randomInt(r);
            int side2 = randomInt(r);
            int side3 = randomInt(r);
            System.out.println((i+1) + ": [" + side1 + ", " + side2 + ", " + side3 + "]");
            Type result = instrumentedClassify(side1, side2, side3, coveredBranches);
            System.out.println("-> " + result);

        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("Branch Coverage: " + coveredBranches.size() + "/14");
        System.out.println("Covered Branch IDs: " + coveredBranches);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    static int randomInt(Random r) {
        if (MIN_INT == Integer.MIN_VALUE && MAX_INT == Integer.MAX_VALUE) {
            return r.nextInt();
        } else {
            return r.nextInt((MAX_INT - MIN_INT + 1)) + MIN_INT;
        }
    }

    static void coveredBranch(int id, Set<Integer> coveredBranches) {
        if (!coveredBranches.contains(id)) {
            System.out.println("* covered new branch: " + id);
            coveredBranches.add(id);
        }
    }

    static Type instrumentedClassify(int side1, int side2, int side3, Set<Integer> coveredBranches) {
        Type type;

        if (side1 > side2) {
            coveredBranch(1, coveredBranches);
            int temp = side1;
            side1 = side2;
            side2 = temp;
        } else {
            coveredBranch(2, coveredBranches);
        }

        if (side1 > side3) {
            coveredBranch(3, coveredBranches);
            int temp = side1;
            side1 = side3;
            side3 = temp;
        } else {
            coveredBranch(4, coveredBranches);
        }

        if (side2 > side3) {
            coveredBranch(5, coveredBranches);
            int temp = side2;
            side2 = side3;
            side3 = temp;
        } else {
            coveredBranch(6, coveredBranches);
        }

        if (side1 + side2 <= side3) {
            coveredBranch(7, coveredBranches);
            type = Type.INVALID;
        } else {
            coveredBranch(8, coveredBranches);
            type = Type.SCALENE;
            if (side1 == side2) {
                coveredBranch(9, coveredBranches);
                if (side2 == side3) {
                    coveredBranch(10, coveredBranches);
                    type = Type.EQUILATERAL;
                } else {
                    coveredBranch(11, coveredBranches);
                }
            } else {
                coveredBranch(12, coveredBranches);
                if (side2 == side3) {
                    coveredBranch(13, coveredBranches);
                    type = Type.ISOSCELES;
                } else {
                    coveredBranch(14, coveredBranches);
                }
            }
        }
        return type;
    }
}
