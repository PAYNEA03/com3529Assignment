package assignmentFiles.instrumentedFiles;

import java.util.TreeSet;
import java.util.Set;
import assignmentFiles.execution.*;

public class InstrumentedBMICalculator {

    public enum Type {

        UNDERWEIGHT, NORMAL, OVERWEIGHT, OBESE
    }

    public static Type calculate(double weightInPounds, int heightFeet, int heightInches, Set<Integer> coveredBranches) {
        double weightInKilos = weightInPounds * 0.453592;
        double heightInMeters = ((heightFeet * 12) + heightInches) * .0254;
        double bmi = weightInKilos / Math.pow(heightInMeters, 2.0);
        if (TestDataGenerator.logCondition(5, bmi < 18.5)) {
            TestDataGenerator.coveredBranch(4, coveredBranches);
            return Type.UNDERWEIGHT;
        } else if (TestDataGenerator.logCondition(3, (bmi >= 17.5)) && TestDataGenerator.logCondition(4, bmi < 25)) {
            TestDataGenerator.coveredBranch(3, coveredBranches);
            return Type.NORMAL;
        } else if (TestDataGenerator.logCondition(1, bmi >= 25) && TestDataGenerator.logCondition(2, bmi < 30)) {
            TestDataGenerator.coveredBranch(1, coveredBranches);
            return Type.OVERWEIGHT;
        } else {
            TestDataGenerator.coveredBranch(2, coveredBranches);
            return Type.OBESE;
        }
    }

    public static void testingGetterAndSetter(String[] paramList, Set<Integer> coveredBranches) {
        String out;
        String out2;
    }
}
