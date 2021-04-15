package assignmentFiles.execution;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitor;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    //

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
        args[2] = FILE_PATH[2];




        CompilationUnit cu = StaticJavaParser.parse(new File(args[2])); //args[2]

//        @todo 4.1: analyse methods, obtain predicates and conditions.

//        @todo 4.1.2/4.3: using obtained methods and predicates, create an instrumented file with
//          logging statements

        Instrument instrument = new Instrument();
        instrument.parse(cu);
        HashMap<String, HashMap<String, Type>> methods = instrument.getMethodList();

//        @todo 4.2: generate test requirements (branch coverage/MCDC)

        //create the test generator object
        TestDataGenerator generator = new TestDataGenerator(args[0], args[1], methods);


        //get the classes name for invocation in testGeneration
/*        Pattern p = Pattern.compile('([)\w-]+)\.');
        Matcher m = p.matcher(args[2]);

        String className = "";
        if (m.find()){
            className = m.group(0);
        }
        else {
            System.out.println("Error in passed filepath: "+args[2]);
            System.exit(0);
        }*/

        generator.testCaseGeneration(cu);

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
