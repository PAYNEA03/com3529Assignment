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
            "src/main/java/assignmentFiles/subjectFiles/SignUtils.java",
            "src/main/java/assignmentFiles/subjectFiles/CompareShapes.java"
    };

    public static void main(String[] args) throws Exception {
        // args[0] - coverage criteria out of branch and MCDC
        // args[1] - will be the java file the user wants to instrument
        // IF THE USER WANTS TO CONFIGURE INPUT PARAMETERS THEN ADD ADDITIONAL args[3+]
        // args[2] - min_doub = minimum value for double generation
        // args[3] - max_doub = maximum value for double generation
        // args[4] - min_int = minimum value for integer generation
        // args[5] - max_int = maximum value for integer generation
        // args[6] - min_str_len = minimum length of strings generated in string generation
        // args[7] - max_str_len = maximum length of strings generated in string generation
        // args[8] - alphanumeric = boolean true if strings generated should be alphanumeric else alphabetic only

        CompilationUnit cu = StaticJavaParser.parse(new File(args[1]));


        Instrument classMethods = Instrument.parseClass(cu);

        String coverage = args[0];

        TestDataGenerator generator;
        if (args.length == 9){
            generator = new TestDataGenerator(cu, coverage, classMethods.methodConditions, classMethods.methodBranchBooleans,
                    Double.parseDouble(args[2]), Double.parseDouble(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]),
                    Integer.parseInt(args[6]), Integer.parseInt(args[7]), Boolean.getBoolean(args[8]));
        }
        else {
            generator = new TestDataGenerator(cu, coverage, classMethods.methodConditions, classMethods.methodBranchBooleans);
        }
        HashMap<String,List<List<HashMap<String,Object>>>> testCases = generator.testGeneration(classMethods);


        WriteToFile.writeTestSuite(args[0],testCases,classMethods.methodDetails,classMethods.className,classMethods.parsedEnum);


    }

}
