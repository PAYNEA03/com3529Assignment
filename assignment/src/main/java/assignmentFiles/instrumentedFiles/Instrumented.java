package assignmentFiles.instrumentedFiles;

import java.util.TreeSet;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import assignmentFiles.execution.*;

public class Instrumented {

    public enum Type {

        UNDERWEIGHT, NORMAL, OVERWEIGHT, UNCLASSIFIED, OBESE
    }

    public static Type calculate(double weightInPounds, int heightFeet, int heightInches, Set<Integer> coveredBranches, HashMap<Integer, Boolean> coveredConditions) {
        double weightInKilos = weightInPounds * 0.453592;
        double heightInMeters = ((heightFeet * 12) + heightInches) * .0254;
        double bmi = weightInKilos / Math.pow(heightInMeters, 2.0);
        if (TestDataGenerator.logCondition(5, bmi < 18.5, coveredConditions)) {
            TestDataGenerator.coveredBranch(4, coveredBranches);
            return Type.UNDERWEIGHT;
        } else if (TestDataGenerator.logCondition(3, bmi >= 17.5, coveredConditions) && TestDataGenerator.logCondition(4, bmi < 25, coveredConditions)) {
            TestDataGenerator.coveredBranch(3, coveredBranches);
            return Type.NORMAL;
        } else if (TestDataGenerator.logCondition(1, bmi >= 25, coveredConditions) && TestDataGenerator.logCondition(2, bmi < 30, coveredConditions)) {
            TestDataGenerator.coveredBranch(1, coveredBranches);
            return Type.OVERWEIGHT;
        } else {
            TestDataGenerator.coveredBranch(2, coveredBranches);
            return Type.OBESE;
        }
    }

    public static Object assignVariables(Map.Entry<String, List> paramList, Set<Integer> coveredBranches, HashMap<Integer, Boolean> coveredConditions) {
        Object result = "empty";
        String methodName = paramList.getKey();
        List methodParams = paramList.getValue();
        if (methodName.equals("calculate")) {;
        System.out.println("********Parsing Method: calculate ****");;
        double weightInPounds = TestDataGenerator.assignValues("weightInPounds", methodParams);
        int heightFeet = TestDataGenerator.assignValues("heightFeet", methodParams);
        int heightInches = TestDataGenerator.assignValues("heightInches", methodParams);
        try {;
        result = calculate(weightInPounds, heightFeet, heightInches, coveredBranches, coveredConditions);
        } catch (Exception e) {;
        System.out.println(e);
        System.out.println("Something went wrong passing values to function");
        };
        };
        return result;
    }
}
