package wordle;

public class Verify {
    public static MatchType[] verify(char[] input, char[] word) {
        return verify(input, word, input.length);
    }

    public static MatchType[] verify(char[] input, char[] word, int len) {
        if (len > input.length) {
            throw new IllegalArgumentException("len must be greater than input.length");
        }

        if (input.length != word.length) {
            throw new IllegalArgumentException("input and word must be the same length");
        }

        assert (len <= input.length);
        assert (word.length == input.length);
        MatchType[] result = new MatchType[input.length];
        for (int i = 0; i < len; i++) {
            result[i] = MatchType.Missing;
            if (input[i] == word[i]) {
                result[i] = MatchType.Exact;
            } else {
                for (int j = 0; j < word.length; j++) {
                    if ((input[i] == word[j]) && (input[j] != word[j])) {
                        result[i] = MatchType.Present;
                        continue;
                    }
                }
            }
        }

        return result;
    }

    public static boolean isFinalSolution(MatchType[] solution) {
        for (MatchType matchType : solution) {
            if (matchType != MatchType.Exact) {
                return false;
            }
        }

        return true;
    }

    public enum MatchType {
        Present, Exact, Missing, Unknown
    }
}
