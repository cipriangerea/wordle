package wordle;

import java.util.Arrays;

public class ScoredWord {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_GRAY = "\u001B[90m";
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    private final String word;
    private final Verify.MatchType[] score;
    public ScoredWord(String word, Verify.MatchType[] score) {
        this.word = word;
        this.score = score;
    }

    public String getWord() {
        return word;
    }

    public Verify.MatchType[] getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "ScoredWord{" +
                "word='" + word +
                ", score=" + Arrays.toString(score) +
                '}';
    }

    public String toColoredString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            switch (score[i]) {
                case Missing:
                    sb.append(ANSI_GRAY);
                    break;
                case Exact:
                    sb.append(ANSI_GREEN);
                    break;
                case Present:
                    sb.append(ANSI_YELLOW);
                    break;
                default:
                    break;
            }
            sb.append(word.charAt(i));
            sb.append(ANSI_RESET);
        }

        return sb.toString();
    }
}
