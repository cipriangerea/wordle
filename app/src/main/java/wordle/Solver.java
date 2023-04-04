package wordle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Solver {
    private boolean isTurbo;

    private Solver(DictionaryNode root) throws IOException {
        DictionaryNode.computeLetterCount();
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
        List<ScoredWord> solution = new ArrayList<>();

        Verify.MatchType[] score = Verify.verify(guess, end.toCharArray());
        solution.add(new ScoredWord(new String(guess), score));

        SolverSession session = new SolverSession(end.length());
        session.updateMatchMatrix(guess, score);

        while (!Verify.isFinalSolution(score)) {
            guess = isTurbo ? session.getNewCandidateTurbo() : session.getNewCandidate();
            score = Verify.verify(guess, end.toCharArray());
            solution.add(new ScoredWord(new String(guess), score));
            session.updateMatchMatrix(guess, score);
        }

        return solution;
    }


}
