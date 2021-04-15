package assignmentFiles.execution;

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

        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH[0]));

        Instrument classMethods = Instrument.parseClass(cu);

//        @todo 4.2: generate test requirements (branch coverage/MCDC)

//        wait before calling to ensure time for file to write
        System.out.println("Writing File");
        for (int i = 3; i > 0 ; i--) {
            System.out.println(i + " second wait before calling TestDataGenerator to ensure instrumentation is completed");
            Thread.sleep(1000);
        }
        TestDataGenerator.randomBranchGeneration(classMethods);

//        @todo 4.4 generate test data.





    }

}
