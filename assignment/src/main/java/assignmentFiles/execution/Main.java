package assignmentFiles.execution;

import assignmentFiles.instrumentedFiles.Instrumented;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.util.HashMap;
import java.util.List;


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
        // args[2] - will be the java file the user wants to instrument

        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH[1]));

        Instrument classMethods = Instrument.parseClass(cu);

        String coverage = args[0];
        String search =  args[1];

        TestDataGenerator generator = new TestDataGenerator(coverage,search, classMethods.methodConditions, classMethods.methodBranchBooleans);
        HashMap<String,List<List<Object>>> testCases = generator.testGeneration(classMethods);

    }

}
