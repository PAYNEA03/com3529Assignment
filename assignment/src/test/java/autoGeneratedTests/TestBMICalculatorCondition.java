package autoGeneratedTests;

import org.junit.jupiter.api.Test;
import assignmentFiles.subjectFiles.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBMICalculatorCondition {

    @Test()
    public void calculateSequence00001() // Sequence of 1 & 0 in method name denote order each branch/condition in test is executed as true or false
    {
        BMICalculator.Type var = BMICalculator.calculate(-5.886664494689667, -4, 7);
        assertEquals(BMICalculator.Type.UNDERWEIGHT, var);
    }

    @Test()
    public void calculateSequence10100() // Sequence of 1 & 0 in method name denote order each branch/condition in test is executed as true or false
    {
        BMICalculator.Type var = BMICalculator.calculate(15.947204473476528, -1, 0);
        assertEquals(BMICalculator.Type.OBESE, var);
    }

    @Test()
    public void calculateSequence11100() // Sequence of 1 & 0 in method name denote order each branch/condition in test is executed as true or false
    {
        BMICalculator.Type var = BMICalculator.calculate(6.503858718869562, 0, 13);
        assertEquals(BMICalculator.Type.OVERWEIGHT, var);
    }

    @Test()
    public void calculateSequence10110() // Sequence of 1 & 0 in method name denote order each branch/condition in test is executed as true or false
    {
        BMICalculator.Type var = BMICalculator.calculate(11.112343108074366, 2, -6);
        assertEquals(BMICalculator.Type.NORMAL, var);
    }
}
