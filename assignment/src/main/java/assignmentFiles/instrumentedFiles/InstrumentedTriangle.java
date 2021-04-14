package assignmentFiles.instrumentedFiles;

import java.util.TreeSet;
import java.util.Set;
import assignmentFiles.execution.*;

public class InstrumentedTriangle {

    public enum Type {

        INVALID, SCALENE, EQUILATERAL, ISOSCELES
    }

    public static Type classify(int side1, int side2, int side3, Set<Integer> coveredBranches) {
        Type type;
        if (TestDataGenerator.logCondition(1, side1 > side2)) {
            TestDataGenerator.coveredBranch(1, coveredBranches);
            int temp = side1;
            side1 = side2;
            side2 = temp;
        }
        if (TestDataGenerator.logCondition(3, side1 > side3)) {
            TestDataGenerator.coveredBranch(2, coveredBranches);
            int temp = side1;
            side1 = side3;
            side3 = temp;
        }
        if (TestDataGenerator.logCondition(5, side2 > side3)) {
            TestDataGenerator.coveredBranch(3, coveredBranches);
            int temp = side2;
            side2 = side3;
            side3 = temp;
        }
        if (TestDataGenerator.logCondition(13, side1 + side2 <= side3)) {
            TestDataGenerator.coveredBranch(8, coveredBranches);
            type = Type.INVALID;
        } else {
            TestDataGenerator.coveredBranch(9, coveredBranches);
            type = Type.SCALENE;
            if (TestDataGenerator.logCondition(11, side1 == side2)) {
                TestDataGenerator.coveredBranch(6, coveredBranches);
                if (TestDataGenerator.logCondition(9, side2 == side3)) {
                    TestDataGenerator.coveredBranch(5, coveredBranches);
                    type = Type.EQUILATERAL;
                }
            } else {
                TestDataGenerator.coveredBranch(7, coveredBranches);
                if (TestDataGenerator.logCondition(7, side2 == side3)) {
                    TestDataGenerator.coveredBranch(4, coveredBranches);
                    type = Type.ISOSCELES;
                }
            }
        }
        return type;
    }

    public static void testingGetterAndSetter(String[] paramList, Set<Integer> coveredBranches) {
        String out;
        String out2;
    }
}
