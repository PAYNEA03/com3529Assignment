package assignmentFiles.execution;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.LineComment;


import java.io.FileWriter;
import java.io.IOException;


public class WriteToFile {
    public static void main(String[] args) {

        ClassOrInterfaceDeclaration myClass = new ClassOrInterfaceDeclaration();
        myClass.setComment(new LineComment("package assignmentFiles.execution;"));
        myClass.setName("MyClassTest");
        myClass.addField("String", "foo");

        try {
            FileWriter myWriter = new FileWriter("src/test/autoGeneratedTests/MyClassTest.java");
            myWriter.write(myClass.toString());
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }



    }
}
