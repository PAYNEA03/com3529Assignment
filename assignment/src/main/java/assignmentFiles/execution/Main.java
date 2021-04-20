package assignmentFiles.execution;

import assignmentFiles.instrumentedFiles.Instrumented;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;



public class Main {

    private static final String[] FILE_PATH = {
            "src/main/java/assignmentFiles/subjectFiles/Triangle.java",
            "src/main/java/assignmentFiles/subjectFiles/BMICalculator.java",
            "src/main/java/assignmentFiles/subjectFiles/Calendar.java",
            "src/main/java/assignmentFiles/subjectFiles/VendingMachine.java",
            "src/main/java/assignmentFiles/subjectFiles/SignUtils.java"
    };

    public static void main(String[] args) throws Exception {
        // args[0] - coverage criteria out of branch and MCDC
        // args[1] - type of search
        // args[2] - will presumably be the java file they want to instrument

        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH[2]));

        Instrument classMethods = Instrument.parseClass(cu);

//        @todo as requested, the following booleans with ids to the method
//          also previously implemented, if stmts with the condition and id.
//          object saved in javaparser format, can use getLeft, getRight, getOperator
//          to get details - could be helpful for mcdc/condition coverage?
//        if statements contains all ids, plus the condition for that id
        System.out.println(classMethods.ifStmts);
//        LISTS ids for conditions associated with method
        System.out.println(classMethods.methodConditions);
//        lists ids for branches associated with method
        System.out.println(classMethods.methodBranchBooleans);

        String coverage = "branch"; // args[0];
        String search = "random"; // args[1]

//        TestDataGenerator generator = new TestDataGenerator(coverage,search);
//        generator.testGeneration(classMethods);



//        TestDataGenerator.searchBasedGeneration(classMethods);


//        @todo 4.4 generate test data.





    }

}
