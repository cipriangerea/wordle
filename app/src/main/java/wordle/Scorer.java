package wordle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scorer {
    public static Map<Integer, Integer> score(Solver solver, String start) throws IOException {
        Map<Integer, Integer> histogram = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.WORDS_LOC))) {
            for (String line; (line = br.readLine()) != null; ) {
                List<ScoredWord> solution = solver.solve(start.toCharArray(), line.toLowerCase());
                histogram.compute(solution.size(), (i1, i2) -> i2 != null ? i2 + 1 : 1);
                if (solution.size() > 7) {
                    System.out.println(String.format("%s %s", start, line));
                }
            }
        }

        return histogram;
    }
}
