package wordle;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        testVerifyHelper("abc", "abcde", 
            new MatchType[] { MatchType.Exact, MatchType.Exact, MatchType.Exact });
    }

    private void testVerifyHelper(String input, String word, MatchType[] expected) {
        MatchType[] actual = Verify.verify(input.toCharArray(), word.toCharArray());
        assertArrayEquals(expected, actual);
    }
    
    // test isFinalSolution
    @Test public void testIsFinalSolution() {
        MatchType[] solution = new MatchType[] { MatchType.Exact, MatchType.Exact, MatchType.Exact };
        boolean expected = true;
        boolean actual = Verify.isFinalSolution(solution);
        assertEquals(expected, actual);
    }    
}
