package wordle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Solver {
    private final DictionaryNode root;
    private Map<Character, Integer> priority;
    private boolean isTurbo;

    private Solver(DictionaryNode root) throws IOException {
        this.root = root;

        priority = DictionaryNode.computeLetterCount();
    }

    public static Solver makeNaiveSolver() throws IOException {
        return new Solver(DictionaryNode.makeNaive());
    }

    public static Solver makeLetterCountSolver() throws IOException {
        return new Solver(DictionaryNode.makeLetterCount());
    }

    public static Solver makeWordCountSolver() throws IOException {
        return new Solver(DictionaryNode.makeWordCount());
    }

    public static Solver makeTurboSolver() throws IOException {
        Solver s = new Solver(DictionaryNode.makeNaive());
        s.isTurbo = true;
        return s;
    }

    public List<ScoredWord> solve(char[] guess, String end) throws IOException {
        Verify verify = new Verify();
        List<ScoredWord> solution = new ArrayList<>();

        Verify.MatchType[] score = verify.verify(guess, end.toCharArray());
        solution.add(new ScoredWord(new String(guess), score));

        SolverSession session = new SolverSession(end.length());
        session.updateMatchMatrix(guess, score);

        while (!Verify.isFinalSolution(score)) {
            guess = isTurbo ? session.getNewCandidateTurbo() : session.getNewCandidate();
            score = verify.verify(guess, end.toCharArray());
            solution.add(new ScoredWord(new String(guess), score));
            session.updateMatchMatrix(guess, score);
        }

        return solution;
    }


}
