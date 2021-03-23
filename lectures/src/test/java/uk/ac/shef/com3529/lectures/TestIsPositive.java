package uk.ac.shef.com3529.lectures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestIsPositive {

    @Test
    public void testGreaterThan1() {
        assertTrue(SignUtils.isPositive(2));
    }

    @Test
    public void test1() {
        assertTrue(SignUtils.isPositive(1));
    }

    @Test
    public void test0() {
        assertFalse(SignUtils.isPositive(0));
    }

    @Test
    public void testLessThan0() {
        assertFalse(SignUtils.isPositive(-1));
    }

}
