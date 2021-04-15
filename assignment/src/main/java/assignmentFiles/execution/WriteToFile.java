package assignmentFiles.execution;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


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

    public static String writeInstrumentedFile(CompilationUnit cu, List<String> className, HashMap<String, List> methodDetail) {
        String newName = "Instrumented";
        String filePath = "src/main/java/assignmentFiles/instrumentedFiles/";
        ClassOrInterfaceDeclaration myClass = cu.getClassByName(className.get(0)).get();
        Instrument.createMethod(myClass, methodDetail);

        myClass.setName(newName);
        cu.setPackageDeclaration("assignmentFiles.instrumentedFiles");

        cu.addImport(new ImportDeclaration("java.util.TreeSet", false, false));
        cu.addImport(new ImportDeclaration("java.util.Set", false, false));
        cu.addImport(new ImportDeclaration("java.util.HashMap", false, false));
        cu.addImport(new ImportDeclaration("java.util.List", false, false));
        cu.addImport(new ImportDeclaration("java.util.Map", false, false));
        cu.addImport(new ImportDeclaration("assignmentFiles.execution", false, true));


        writeClass(cu.toString(),newName,filePath);
        String createFile = filePath + newName + ".java";

        return createFile;

    }
}
