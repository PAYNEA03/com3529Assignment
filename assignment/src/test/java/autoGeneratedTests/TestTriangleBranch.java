package autoGeneratedTests;

import org.junit.jupiter.api.Test;
import assignmentFiles.subjectFiles.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTriangleBranch {

    @Test()
    public void classify1() {
        Triangle.Type var = Triangle.classify(-4, -1, -6);
        assertEquals(Triangle.Type.INVALID, var);
    }

    @Test()
    public void classify2() {
        Triangle.Type var = Triangle.classify(-7, 15, 0);
        assertEquals(Triangle.Type.INVALID, var);
    }

    @Test()
    public void classify3() {
        Triangle.Type var = Triangle.classify(6, 2, -6);
        assertEquals(Triangle.Type.INVALID, var);
    }

    @Test()
    public void classify4() {
        Triangle.Type var = Triangle.classify(-5, -1, 5);
        assertEquals(Triangle.Type.INVALID, var);
    }

    @Test()
    public void classify5() {
        Triangle.Type var = Triangle.classify(9, 8, 12);
        assertEquals(Triangle.Type.SCALENE, var);
    }

    @Test()
    public void classify6() {
        Triangle.Type var = Triangle.classify(2, 5, 5);
        assertEquals(Triangle.Type.ISOSCELES, var);
    }

    @Test()
    public void classify7() {
        Triangle.Type var = Triangle.classify(10, 12, 10);
        assertEquals(Triangle.Type.SCALENE, var);
    }
}