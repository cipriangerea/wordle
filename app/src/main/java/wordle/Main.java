package wordle;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        // write your code here

//        printSolution("steal", "jaunt");
//        printSolution("steal", "homer");
//        printSolution("stare", "spill");

    //     String startWord = "stare";
    //    printReport(Scorer.score(Solver.makeNaiveSolver(), startWord), "Naive");
    //    printReport(Scorer.score(Solver.makeLetterCountSolver(), startWord), "LetterCount");
    //    printReport(Scorer.score(Solver.makeWordCountSolver(), startWord), "WordCount");
    //    printReport(Scorer.score(Solver.makeTurboSolver(), startWord), "WordCount");

        solveInteractive();
    }

    private static void solveInteractive() throws IOException {
        try (Scanner scanner = new Scanner(System.in)) {
            SolverSession session = new SolverSession(5);
            while (true) {
                System.out.println(session.getCandidatesList(10));

                System.out.println("Input word");
                String input = scanner.nextLine();
                System.out.println("Input score");
                String score = scanner.nextLine();
                Verify.MatchType[] scoreMt = Arrays.stream(score.split(","))
                        .map(s -> Verify.MatchType.valueOf(s))
                        .toArray(Verify.MatchType[]::new);

                if (Verify.isFinalSolution(scoreMt)) {
                    System.out.println("My work is done! Such satisfaction!");
                    System.exit(0);
                }

                session.updateMatchMatrix(input.toCharArray(), scoreMt);
            }
        }
    }

    private static void printReport(Map<Integer, Integer> naiveSolverHistogram, String solverName) {
        Double algoScore = 0.0;
        for (Map.Entry<Integer, Integer> e : naiveSolverHistogram
                .entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList())) {
            algoScore += e.getValue() * Math.pow(2, -(e.getKey()));
        }

        System.out.println(String.format("%s solver performance. Total Score %f", solverName, algoScore));

        System.out.println(
                naiveSolverHistogram
                        .entrySet().stream().sorted((e1, e2) -> e1.getKey().compareTo(e2.getKey()))
                        .map(e -> String.format("%d=%d", e.getKey(), e.getValue()))
                        .collect(Collectors.joining(",")));
    }

    public static void printSolution(String start, String end) throws IOException {
        printSolution(Solver.makeNaiveSolver(), start, end);
        System.out.println("");

        printSolution(Solver.makeLetterCountSolver(), start, end);
        System.out.println("");

        printSolution(Solver.makeWordCountSolver(), start, end);
        System.out.println("");

        printSolution(Solver.makeTurboSolver(), start, end);
        System.out.println("");
    }


    public static void printSolution(Solver solver, String start, String end) throws IOException {
        System.out.println(String.format("Start=%s, End=%s, Match=\n%s", start, end,
                solver.solve(start.toCharArray(), end).stream()
                        .map(ScoredWord::toColoredString)
                        .collect(Collectors.joining("\n"))));
    }

    public static void printMatch(String start, String end) {
        System.out.println(String.format("Start=%s, End=%s, Match=%s", start, end,
                Arrays.stream(Verify.verify(start.toCharArray(), end.toCharArray()))
                        .map(Verify.MatchType::toString)
                        .collect(Collectors.joining(", "))));
    }
}
