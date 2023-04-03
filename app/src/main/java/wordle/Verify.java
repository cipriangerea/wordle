package wordle;

public class Verify {
    public static MatchType[] verify(char[] input, char[] word) {
        return verify(input, word, input.length);
    }

    public static MatchType[] verify(char[] input, char[] word, int len) {
        assert (len <= input.length);
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
