package assignmentFiles.execution;

import assignmentFiles.subjectFiles.Triangle;

import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class TestDataGenerator {

    static final int ITERATIONS = 750;
    static final int MIN_INT = -10;
    static final int MAX_INT = 10;

    public static void randomGeneration() {

//        Random r = new Random();
//        Set<Integer> coveredBranches = new TreeSet<>();
//
//        for (int i=0; i < ITERATIONS; i ++) {
//            int side1 = randomInt(r);
//            int side2 = randomInt(r);
//            int side3 = randomInt(r);
//            System.out.println((i+1) + ": [" + side1 + ", " + side2 + ", " + side3 + "]");
//            Triangle.Type result = instrumentedClassify(side1, side2, side3, coveredBranches);
//            System.out.println("-> " + result);
//
//        }
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        System.out.println("Branch Coverage: " + coveredBranches.size() + "/14");
//        System.out.println("Covered Branch IDs: " + coveredBranches);
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//

    }



    /** randomInt is taken from the week5 lectures of the RandomlyTestTriangle class and
     * has not been changed in any form
     * */
    static int randomInt(Random r) {
        if (MIN_INT == Integer.MIN_VALUE && MAX_INT == Integer.MAX_VALUE) {
            return r.nextInt();
        } else {
            return r.nextInt((MAX_INT - MIN_INT + 1)) + MIN_INT;
        }
    }

    /** coveredBranch is taken from the week5 lectures of the RandomlyTestTriangle class and
     * has not been changed in any form
     * */
    static void coveredBranch(int id, Set<Integer> coveredBranches) {
        if (!coveredBranches.contains(id)) {
            System.out.println("* covered new branch: " + id);
            coveredBranches.add(id);
        }
    }

    public static void searchBasedGeneration() {

    }

}
