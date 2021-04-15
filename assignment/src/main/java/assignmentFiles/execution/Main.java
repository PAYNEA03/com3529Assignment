package assignmentFiles.execution;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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

        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH[2]));

        Instrument classMethods = Instrument.parseClass(cu);

//        @todo 4.2: generate test requirements (branch coverage/MCDC)

        TestDataGenerator.randomBranchGeneration(classMethods);

//        @todo 4.4 generate test data.





    }

}
