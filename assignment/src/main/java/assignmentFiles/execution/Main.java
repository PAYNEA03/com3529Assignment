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

//        @todo 4.1: analyse methods, obtain predicates and conditions.

//        @todo 4.1.2/4.3: using obtained methods and predicates, create an instrumented file with
//          logging statements

        Instrument.parseClass(cu);

//        @todo 4.2: generate test requirements (branch coverage/MCDC)

//        TestDataGenerator.randomBranchGeneration();

//        @todo 4.4 generate test data.





    }

    private static void parseAllFiles() throws FileNotFoundException {
        for (String file: FILE_PATH) {
            System.out.println("");
            System.out.println("File to be parsed: " + file);

            // Parse File using Java Parser
            CompilationUnit cu = StaticJavaParser.parse(new File(file));


//            /**
//             * Javaparser visitor for if and else statements, returns a hasmap with details of the conditions
//             * */
//            List<HashMap> ifStmt = new ArrayList<>();
//            VoidVisitor<List<HashMap>> ifStmtCollector = new Parser.IfStmtCollector();
//
//            ifStmtCollector.visit(cu, ifStmt);
//            ifStmt.forEach(n ->
//                    System.out.println(n));

//            for (HashMap h:ifStmt) {
//                IfStmt i = (IfStmt) h.get("object");
//
//                System.out.println(i);
//            }

            /**
             * Javaparser visitor for method declarations, returns a hashmap with method names and their params
             * */

            List<HashMap> methodNames = new ArrayList<>();
            VoidVisitor<List<HashMap>> methodCollector = new Parser.MethodCollector();

            methodCollector.visit(cu, methodNames);
            methodNames.forEach(n ->
                    System.out.println(n));

            System.out.println(cu);
        }
    }

    private static void writeFile(String file, String fileName) throws IOException {
//        String[] file = WriteToFile.fileInstrument(filePath);

//        String instrumentedFile = WriteToFile.writeInstrumentedFile(file, fileName);

        // checks to see if created file exists (https://alvinalexander.com/java/java-file-exists-directory-exists/)
//        File tmpDir = new File(instrumentedFile);
//        boolean exists = tmpDir.exists();
//
//        System.out.println("has file been created? " + exists + " @ " + instrumentedFile);
    }


}
