package assignmentFiles.execution;

import java.io.FileWriter;
import java.io.IOException;



public class WriteToFile {

    public static String writeClass(String myClass, String fileName, String filePath) {
        String path = filePath + fileName + ".java";

        try {
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(myClass);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return path;

    }
}
