package assignmentFiles.instrumentedFiles;

import assignmentFiles.subjectFiles.Cuboid;
import assignmentFiles.subjectFiles.Rectangle;
import java.util.TreeSet;
import java.util.Set;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import assignmentFiles.execution.*;

public class Instrumented {

    public enum Similarity {

        COMPLETELY_DIFFERENT,
        ONE_SIDE_SAME,
        TWO_SIDES_SAME,
        SAME_AREA,
        SAME_VOLUME,
        SAME_CUBOID
    }

    public static Similarity compareCuboids(Cuboid cuboid1, Cuboid cuboid2, Set<Integer> coveredBranches, HashMap<Integer, Boolean> coveredConditions) {
        if (cuboid1.cube && cuboid2.cube) {
            TestDataGenerator.coveredBranch(1, coveredBranches);
            return Similarity.SAME_CUBOID;
        } else {
            TestDataGenerator.coveredBranch(2, coveredBranches);
        }
        int sides_same = 0;
        if (cuboid1.length == cuboid2.length) {
            TestDataGenerator.coveredBranch(3, coveredBranches);
            sides_same++;
        } else {
            TestDataGenerator.coveredBranch(4, coveredBranches);
        }
        if (cuboid1.width == cuboid2.width) {
            TestDataGenerator.coveredBranch(5, coveredBranches);
            sides_same++;
        } else {
            TestDataGenerator.coveredBranch(6, coveredBranches);
        }
        if (cuboid1.height == cuboid2.height) {
            TestDataGenerator.coveredBranch(7, coveredBranches);
            sides_same++;
        } else {
            TestDataGenerator.coveredBranch(8, coveredBranches);
        }
        if (cuboid1.width == cuboid2.height) {
            TestDataGenerator.coveredBranch(9, coveredBranches);
            sides_same++;
        } else {
            TestDataGenerator.coveredBranch(10, coveredBranches);
        }
        if (cuboid1.height == cuboid2.width) {
            TestDataGenerator.coveredBranch(11, coveredBranches);
            sides_same++;
        } else {
            TestDataGenerator.coveredBranch(12, coveredBranches);
        }
        if (cuboid1.length == cuboid2.width) {
            TestDataGenerator.coveredBranch(13, coveredBranches);
            sides_same++;
        } else {
            TestDataGenerator.coveredBranch(14, coveredBranches);
        }
        if (cuboid1.length == cuboid2.height) {
            TestDataGenerator.coveredBranch(15, coveredBranches);
            sides_same++;
        } else {
            TestDataGenerator.coveredBranch(16, coveredBranches);
        }
        if (cuboid1.width == cuboid2.length) {
            TestDataGenerator.coveredBranch(17, coveredBranches);
            sides_same++;
        } else {
            TestDataGenerator.coveredBranch(18, coveredBranches);
        }
        if (cuboid1.height == cuboid2.length) {
            TestDataGenerator.coveredBranch(19, coveredBranches);
            sides_same++;
        } else {
            TestDataGenerator.coveredBranch(20, coveredBranches);
        }
        if (TestDataGenerator.logCondition(3, sides_same >= 3, coveredConditions)) {
            TestDataGenerator.coveredBranch(25, coveredBranches);
            return Similarity.SAME_CUBOID;
        } else if (TestDataGenerator.logCondition(2, sides_same == 2, coveredConditions)) {
            TestDataGenerator.coveredBranch(24, coveredBranches);
            return Similarity.TWO_SIDES_SAME;
        } else if (TestDataGenerator.logCondition(1, sides_same == 1, coveredConditions)) {
            TestDataGenerator.coveredBranch(23, coveredBranches);
            return Similarity.ONE_SIDE_SAME;
        } else if (cuboid1.length * cuboid1.width * cuboid1.height == cuboid2.length * cuboid2.width * cuboid2.height) {
            TestDataGenerator.coveredBranch(21, coveredBranches);
            return Similarity.SAME_VOLUME;
        } else {
            TestDataGenerator.coveredBranch(22, coveredBranches);
        }
        return Similarity.COMPLETELY_DIFFERENT;
    }

    public static Similarity compareRectangles(Rectangle rect1, Rectangle rect2, Set<Integer> coveredBranches, HashMap<Integer, Boolean> coveredConditions) {
        int sides_same = 0;
        if (rect1.side1 == rect2.side1) {
            TestDataGenerator.coveredBranch(33, coveredBranches);
            sides_same++;
            if (rect1.side2 == rect2.side2) {
                TestDataGenerator.coveredBranch(31, coveredBranches);
                sides_same++;
            } else {
                TestDataGenerator.coveredBranch(32, coveredBranches);
            }
        } else if (rect1.side1 == rect2.side2) {
            TestDataGenerator.coveredBranch(30, coveredBranches);
            sides_same++;
            if (rect1.side2 == rect2.side1) {
                TestDataGenerator.coveredBranch(28, coveredBranches);
                sides_same++;
            } else {
                TestDataGenerator.coveredBranch(29, coveredBranches);
            }
        } else if (rect1.area == rect2.area) {
            TestDataGenerator.coveredBranch(26, coveredBranches);
            return Similarity.SAME_AREA;
        } else {
            TestDataGenerator.coveredBranch(27, coveredBranches);
        }
        if (TestDataGenerator.logCondition(5, sides_same == 1, coveredConditions)) {
            TestDataGenerator.coveredBranch(36, coveredBranches);
            return Similarity.ONE_SIDE_SAME;
        } else if (TestDataGenerator.logCondition(4, sides_same == 2, coveredConditions)) {
            TestDataGenerator.coveredBranch(34, coveredBranches);
            return Similarity.TWO_SIDES_SAME;
        } else {
            TestDataGenerator.coveredBranch(35, coveredBranches);
        }
        return Similarity.COMPLETELY_DIFFERENT;
    }

    static int hammingDist(String str1, String str2, Set<Integer> coveredBranches, HashMap<Integer, Boolean> coveredConditions) {
        int i = 0, count = 0;
        while (TestDataGenerator.logCondition(6, i < str1.length(), coveredConditions)) {
            TestDataGenerator.coveredBranch(39, coveredBranches);
            if (str1.charAt(i) != str2.charAt(i)) {
                TestDataGenerator.coveredBranch(37, coveredBranches);
                count++;
            } else {
                TestDataGenerator.coveredBranch(38, coveredBranches);
            }
            i++;
        }
        return count;
    }

    public static Object assignVariables(Map.Entry<String, List> paramList, Set<Integer> coveredBranches, HashMap<Integer, Boolean> coveredConditions) {
        Object result = "empty";
        String methodName = paramList.getKey();
        List methodParams = paramList.getValue();
        if (methodName.equals("hammingDist")) {;
        System.out.println("********Parsing Method: hammingDist ****");;
        String str1 = (String) TestDataGenerator.assignValues("str1", methodParams);
        String str2 = (String) TestDataGenerator.assignValues("str2", methodParams);
        try {;
        result = hammingDist(str1, str2, coveredBranches, coveredConditions);
        } catch (Exception e) {;
        System.out.println(e);
        System.out.println("Something went wrong passing values to function");
        };
        };
        if (methodName.equals("compareRectangles")) {;
        System.out.println("********Parsing Method: compareRectangles ****");;
        Rectangle rect1 = (Rectangle) TestDataGenerator.assignValues("rect1", methodParams);
        Rectangle rect2 = (Rectangle) TestDataGenerator.assignValues("rect2", methodParams);
        try {;
        result = compareRectangles(rect1, rect2, coveredBranches, coveredConditions);
        } catch (Exception e) {;
        System.out.println(e);
        System.out.println("Something went wrong passing values to function");
        };
        };
        if (methodName.equals("compareCuboids")) {;
        System.out.println("********Parsing Method: compareCuboids ****");;
        Cuboid cuboid1 = (Cuboid) TestDataGenerator.assignValues("cuboid1", methodParams);
        Cuboid cuboid2 = (Cuboid) TestDataGenerator.assignValues("cuboid2", methodParams);
        try {;
        result = compareCuboids(cuboid1, cuboid2, coveredBranches, coveredConditions);
        } catch (Exception e) {;
        System.out.println(e);
        System.out.println("Something went wrong passing values to function");
        };
        };
        return result;
    }
}
