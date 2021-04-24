package assignmentFiles.execution;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WriteToFile {

    public static String writeClass(String myClass, String fileName, String filePath) {
        String path = filePath + fileName + ".java";

        try {
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(myClass);
            myWriter.close();
            System.out.println("Successfully wrote " + path);
            System.out.println("");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return path;

    }

    public static void writeTestSuite(String coverageType, HashMap<String, List<List<HashMap<String,
            Object>>>> testCases, HashMap<String, List> methodDetails, String className, EnumDeclaration parsedEnum) {

        String prepend = "Test";
        String filePath = "src/test/java/autoGeneratedTests/";

        String coverage = coverageType.substring(0, 1).toUpperCase() + coverageType.substring(1);

        CompilationUnit cu = new CompilationUnit();

        cu.addClass(prepend+className+coverage);
        cu.setPackageDeclaration("autoGeneratedTests");
        cu.addImport(new ImportDeclaration("org.junit.jupiter.api.Test", false, false));
        cu.addImport(new ImportDeclaration("assignmentFiles.subjectFiles", false, true));
        cu.addImport(new ImportDeclaration("org.junit.jupiter.api.Assertions.assertEquals", true, false));

        ClassOrInterfaceDeclaration myClass = cu.getClassByName(prepend+className+coverage).get();

        for (Map.Entry<String, List<List<HashMap<String, Object>>>> entry : testCases.entrySet()) {

            int i = 1;
            for (List item:entry.getValue()) {

                Instrument.createTestCase(item,entry.getKey(),myClass,i,methodDetails,className, parsedEnum);
                i++;
            }
        }


        writeClass(cu.toString(), myClass.getNameAsString(), filePath);

    }

    public static String writeInstrumentedFile(CompilationUnit cu, List<String> className, HashMap<String, List> methodDetail) {
        String newName = "Instrumented";
        String filePath = "src/main/java/assignmentFiles/instrumentedFiles/";
        //        set class name
        ClassOrInterfaceDeclaration myClass = cu.getClassByName(className.get(0)).get();
        myClass.setName(newName);

//        choose additional methods to add
        //Instrument.createVariableAssignMethod(myClass, methodDetail);
        Instrument.createVariableAssignMethod2(myClass, methodDetail);

//    set package details and imports
        cu.setPackageDeclaration("assignmentFiles.instrumentedFiles");

        cu.addImport(new ImportDeclaration("java.util.TreeSet", false, false));
        cu.addImport(new ImportDeclaration("java.util.Set", false, false));
        cu.addImport(new ImportDeclaration("java.util.HashMap", false, false));
        cu.addImport(new ImportDeclaration("java.util.List", false, false));
        cu.addImport(new ImportDeclaration("java.util.Map", false, false));
        cu.addImport(new ImportDeclaration("assignmentFiles.execution", false, true));


//        change to string so can be written to a file
        writeClass(cu.toString(),newName,filePath);
        String createFile = filePath + newName + ".java";

        return createFile;

    }
}
