package uk.ac.shef.com3529.lectures;

import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestStringUtilsBuggy1 {

    @Test
    public void testRepeatedChar() {
        Set<Character> resultSet = StringUtilsBuggy1.duplicateLetters("software testing");
        assertTrue(resultSet.contains('t'));
    }

    @Test
    public void testNonRepeatedChar() {
        Set<Character> resultSet = StringUtilsBuggy1.duplicateLetters("software debugging");
        assertFalse(resultSet.contains('t'));
    }
}
