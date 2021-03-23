package uk.ac.shef.com3529.lectures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTriangle {

    @Test
    public void testInvalid() {
        assertEquals(Triangle.Type.INVALID, Triangle.classify(0,0,0));
    }

    //no equal sides
    @Test
    public void testScalene() {
        assertEquals(Triangle.Type.SCALENE, Triangle.classify(2,4,5));
    }

    @Test
    public void testEquilateral() {
        assertEquals(Triangle.Type.EQUILATERAL, Triangle.classify(2,2,2));
    }

    //This one is equivalent to branch 36F45
    @Test
    public void testScaleneWithEqualSides12ButNot23() {
        assertEquals(Triangle.Type.SCALENE, Triangle.classify(2,2,3));
    }

    @Test
    public void testIsosceles() {
        assertEquals(Triangle.Type.ISOSCELES, Triangle.classify(1,2,2));
    }

    //here tests the early part branches, this checks all of them as it rearranges it back to front
    @Test
    public void testScalene1Biggest3Smallest() {
        assertEquals(Triangle.Type.SCALENE, Triangle.classify(5,4,2));
    }

    //restricted MCDC test suite
    @Test
    public void testCondition1T() {
        assertEquals(Triangle.Type.ISOSCELES, Triangle.classify(1,2,2));
    }

    //infeasible because side1+side2 cannot be <= side3 and side2 == side3 unless side1 == 0
    @Test
    public void testCondition1F() {assertEquals(Triangle.Type.INVALID, Triangle.classify(0,2,2));}

    //testCondition2T is infeasible as side1 == side2 and side1 != side2 are incompatible

    //testCondition2F is infeasible because side2 == side3 and side2 != side3 cant both be false

    @Test
    public void testCondition3T() {
        assertEquals(Triangle.Type.ISOSCELES, Triangle.classify(1,2,2));
    }

    //testCondition3F is infeasible because side2 == side3 and side2 != side3 cant both be false


    //Input Domain Analysis - functionality-based input domain modelling
    //                        Equivalence Classes
    //    Characteristic          e1      e2     Base Choice
    // 1 Triangle is Scalene     true    false       e1
    // 2 Triangle is Isosceles   true    false       e2
    // 3 Triangle is Equilateral true    false       e2
    // 4 Triangle is Invalid     true    false       e2

    //Constraints
    // - Invalid cannot be combined with any e1 class for any other characteristic
    // - Isosceles e1 must be combined with equilateral e2
    // - Equilateral e1 and Isosceles e1 must both be independently combined with Scalene e1

}
