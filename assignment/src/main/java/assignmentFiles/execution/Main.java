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

        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH[0]));

        Instrument classMethods = Instrument.parseClass(cu);

//        @todo 4.2: generate test requirements (branch coverage/MCDC)

        TestDataGenerator generator = new TestDataGenerator(args[0],args[1]);
        generator.testGeneration(classMethods);
//        TestDataGenerator.searchBasedGeneration(classMethods);


//        @todo 4.4 generate test data.





    }

}
