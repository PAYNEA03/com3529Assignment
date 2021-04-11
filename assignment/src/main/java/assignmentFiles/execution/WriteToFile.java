package assignmentFiles.execution;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class WriteToFile {

    private static final String[] FILE_PATH = {
            "src/main/java/assignmentFiles/subjectFiles/Triangle.java",
            "src/main/java/assignmentFiles/lectureExamples/RandomlyTestTriangle.java",
            "src/main/java/assignmentFiles/subjectFiles/BMICalculator.java",
            "src/main/java/assignmentFiles/subjectFiles/Calendar.java",
            "src/main/java/assignmentFiles/subjectFiles/VendingMachine.java",
            "src/main/java/assignmentFiles/subjectFiles/SignUtils.java"
    };

    public static void main(String[] args) throws IOException {

//        javaParser();
        String[] file = fileInstrument(FILE_PATH[1]);

        writeInstrumentedFile(file, "RandomlyTestTriangle.java");

    }

   /**
    * fileInstrumenter is a file reader and its base code was implemented from the following url:
    * https://stackoverflow.com/questions/16100175/store-text-file-content-line-by-line-into-array
    *
   */
    private static String[] fileInstrument(String filePath) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(filePath));
        String str;

        List<String> list = new ArrayList<String>();


        while((str = in.readLine()) != null){
            list.add(str);
        }

        String[] stringArr = list.toArray(new String[0]);

        String testLine = "String test = \"Hello World\";";
        int testLineNumber = 43;


        String[] instrumentedArray = addElement(stringArr,testLine,testLineNumber);


        return instrumentedArray;
    }

    /**
     * addElement is a method that can be found at https://www.geeksforgeeks.org/how-to-insert-an-element-at-a-specific-position-in-an-array-in-java/
     * changes have been made to make the method more applicable to this project but is implemented from the approach found at the url above.
    * */
    private static String[] addElement(
            String[] arr, String element,
            int position)
    {
        // Coverting array to ArrayList
        List<String> list = new ArrayList<>(
                Arrays.asList(arr));

        // Adding the element at position
        list.add(position - 1, element);

        // replaces package location at line 1
        list.set(0, "package assignmentFiles.instrumentedFiles;");

        // Converting the list back to array
        arr = list.toArray(arr);


        return arr;

    }

    private static void writeInstrumentedFile(String[] myClass, String fileName) {

        try {
            FileWriter myWriter = new FileWriter("src/main/java/assignmentFiles/instrumentedFiles/" + fileName);
            for (String line:myClass) {
                myWriter.write(line);
                myWriter.write(System.getProperty( "line.separator" ));
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
