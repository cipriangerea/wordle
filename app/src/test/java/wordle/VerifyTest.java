package wordle;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import wordle.Verify.MatchType;

public class VerifyTest {
    // test verify
    @Test public void testVerify() {
        testVerifyHelper("abc", "abc", 
            new MatchType[] { MatchType.Exact, MatchType.Exact, MatchType.Exact });

        testVerifyHelper("abc", "acb", 
            new MatchType[] { MatchType.Exact, MatchType.Present, MatchType.Present });

        // test case with 5 letter word
        testVerifyHelper("abcde", "abcde", 
            new MatchType[] { MatchType.Exact, MatchType.Exact, MatchType.Exact, MatchType.Exact, MatchType.Exact });   

        // test case with 5 letter word and 3 letter input
        testVerifyHelperIllegalArgumentException("abc", "abcde");

        // test case with 3 letter word and 5 letter input
        testVerifyHelperIllegalArgumentException("abcde", "abc");   
    }

    private void testVerifyHelper(String input, String word, MatchType[] expected) {
        MatchType[] actual = Verify.verify(input.toCharArray(), word.toCharArray());
        assertArrayEquals(expected, actual);
    }

    // test verify helper for IllegalArgumentException
    private void testVerifyHelperIllegalArgumentException(String input, String word) {
        try {
            Verify.verify(input.toCharArray(), word.toCharArray());
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    
    // test isFinalSolution
    @Test public void testIsFinalSolution() {
        MatchType[] solution = new MatchType[] { MatchType.Exact, MatchType.Exact, MatchType.Exact };
        boolean expected = true;
        boolean actual = Verify.isFinalSolution(solution);
        assertEquals(expected, actual);
    }    
}
