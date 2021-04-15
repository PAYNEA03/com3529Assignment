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

        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH[1]));

        Instrument classMethods = Instrument.parseClass(cu);

//        @todo 4.2: generate test requirements (branch coverage/MCDC)

//        TestDataGenerator.randomBranchGeneration(classMethods);
        TestDataGenerator.searchBasedGeneration(classMethods);


//        @todo 4.4 generate test data.





    }

}
