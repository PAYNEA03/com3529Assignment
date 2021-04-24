package assignmentFiles.instrumentedFiles;

import java.util.TreeSet;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import assignmentFiles.execution.*;

public class Instrumented {

    public enum Type {

        INVALID, SCALENE, EQUILATERAL, ISOSCELES
    }

    public static Type classify(int side1, int side2, int side3, Set<Integer> coveredBranches, HashMap<Integer, Boolean> coveredConditions) {
        Type type;
        if (TestDataGenerator.logCondition(1, side1 > side2, coveredConditions)) {
            TestDataGenerator.coveredBranch(1, coveredBranches);
            int temp = side1;
            side1 = side2;
            side2 = temp;
        } else {
            TestDataGenerator.coveredBranch(2, coveredBranches);
        }
        if (TestDataGenerator.logCondition(2, side1 > side3, coveredConditions)) {
            TestDataGenerator.coveredBranch(3, coveredBranches);
            int temp = side1;
            side1 = side3;
            side3 = temp;
        } else {
            TestDataGenerator.coveredBranch(4, coveredBranches);
        }
        if (TestDataGenerator.logCondition(3, side2 > side3, coveredConditions)) {
            TestDataGenerator.coveredBranch(5, coveredBranches);
            int temp = side2;
            side2 = side3;
            side3 = temp;
        } else {
            TestDataGenerator.coveredBranch(6, coveredBranches);
        }
        if (TestDataGenerator.logCondition(7, side1 + side2 <= side3, coveredConditions)) {
            TestDataGenerator.coveredBranch(13, coveredBranches);
            type = Type.INVALID;
        } else {
            TestDataGenerator.coveredBranch(14, coveredBranches);
            type = Type.SCALENE;
            if (TestDataGenerator.logCondition(6, side1 == side2, coveredConditions)) {
                TestDataGenerator.coveredBranch(11, coveredBranches);
                if (TestDataGenerator.logCondition(5, side2 == side3, coveredConditions)) {
                    TestDataGenerator.coveredBranch(9, coveredBranches);
                    type = Type.EQUILATERAL;
                } else {
                    TestDataGenerator.coveredBranch(10, coveredBranches);
                }
            } else {
                TestDataGenerator.coveredBranch(12, coveredBranches);
                if (TestDataGenerator.logCondition(4, side2 == side3, coveredConditions)) {
                    TestDataGenerator.coveredBranch(7, coveredBranches);
                    type = Type.ISOSCELES;
                } else {
                    TestDataGenerator.coveredBranch(8, coveredBranches);
                }
            }
        }
        return type;
    }

    public static Object assignVariables(Map.Entry<String, List> paramList, Set<Integer> coveredBranches, HashMap<Integer, Boolean> coveredConditions) {
        Object result = "empty";
        String methodName = paramList.getKey();
        List methodParams = paramList.getValue();
        if (methodName.equals("classify")) {;
        System.out.println("********Parsing Method: classify ****");;
        int side1 = (int) TestDataGenerator.assignValues("side1", methodParams);
        int side2 = (int) TestDataGenerator.assignValues("side2", methodParams);
        int side3 = (int) TestDataGenerator.assignValues("side3", methodParams);
        try {;
        result = classify(side1, side2, side3, coveredBranches, coveredConditions);
        } catch (Exception e) {;
        System.out.println(e);
        System.out.println("Something went wrong passing values to function");
        };
        };
        return result;
    }
}
