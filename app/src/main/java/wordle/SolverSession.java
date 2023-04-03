package wordle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SolverSession {

    final List<Character> present;
    private final Verify.MatchType[][] matches;
    private final Map<Character, Integer> priority;
    private final DictionaryNode root;


    public SolverSession(int length) throws IOException {
        matches = initMatches(length);

        priority = DictionaryNode.computeLetterCount();

        root = DictionaryNode.makeNaive();

        present = new ArrayList<>();
    }

    private Verify.MatchType[][] initMatches(int length) {
        Verify.MatchType[][] matches = new Verify.MatchType[Constants.CountLetters][];
        for (int i = 0; i < Constants.CountLetters; i++) {
            matches[i] = new Verify.MatchType[length];
            for (int j = 0; j < length; j++) {
                matches[i][j] = Verify.MatchType.Unknown;
            }
        }

        return matches;
    }

    public void updateMatchMatrix(char[] guess, Verify.MatchType[] score) {
        // update matches
        for (int i = 0; i < guess.length; i++) {
            int letterId = guess[i] - 'a';
            matches[letterId][i] = score[i];

            // If it's missing in one spot it's missing in all spots
            if (score[i] == Verify.MatchType.Missing) {
                for (int j = 0; j < guess.length; j++) {
                    if (matches[letterId][j] == Verify.MatchType.Unknown) {
                        matches[letterId][j] = Verify.MatchType.Missing;
                    }
                }
            }

            // update present list to be the intersection between old & new
            if (score[i] == Verify.MatchType.Present) {
                if (!present.contains(guess[i])) {
                    present.add(guess[i]);
                }
            } else if (score[i] == Verify.MatchType.Exact) {
                int idx = present.indexOf(guess[i]);
                if (idx >= 0) {
                    present.remove(idx);
                }
            }
        }

    }

    public char[] getNewCandidate() {
        char[] candidate = new char[matches[0].length];
        getNewCandidateRec(root, candidate, 0);
        return candidate;
    }

    public char[] getNewCandidateTurbo() {

        List<char[]> candidates = getAllCandidatesTurbo().stream()
                .filter(c -> hasPresent(c))
                .collect(Collectors.toList());

        Map<Character, Integer> letterCount = new HashMap<>();
        for (char[] candidate : candidates) {
            for (char c : candidate) {
                letterCount.compute(c, (c1, o) -> o == null ? 1 : o + 1);
            }
        }

        candidates.sort((c1, c2) -> getStrength(c2, letterCount) - getStrength(c1, letterCount));
        return candidates.get(0);
    }

    private String getCandidatesList(int count, int direction) {
        return getAllCandidatesTurbo().stream()
                .filter(this::hasPresent)
                .sorted((c1, c2) -> direction * (getStrength(c2, priority) - getStrength(c1, priority)))
                .limit(count)
                .map(String::new)
                .collect(Collectors.joining(","));
    }

    public String getCandidatesList(int count) {
        return "head: " + getCandidatesList(count, 1) + " tail: " + getCandidatesList(count, -1);
    }

    private int getStrength(char[] word, Map<Character, Integer> priority) {
        int strength = 0;
        for (int i = 0; i < word.length; i++) {
            // don't count if we already have an exact match
            if (matches[word[i] - 'a'][i] == Verify.MatchType.Exact) {
                continue;
            }

            if (matches[word[i] - 'a'][i] == Verify.MatchType.Present) {
                return 0;
            }

            if (present.contains(word[i])) {
                continue;
            }

            // duplicates will be treated differently
            boolean isDuplicate = false;
            for (int jj = 0; jj < i; jj++) {
                if (word[jj] == word[i]) {
                    isDuplicate = true;
                }
            }

            if (!isDuplicate) {
                strength += priority.get(word[i]);
            } else {
                // duplicate letters get 1/3 strength
//                strength += 9;
            }
        }

        return strength;
    }

    private boolean hasPresent(char[] word) {
        return present.stream().allMatch(c -> {
            for (int i = 0; i < word.length; i++) {
                if (word[i] == c && matches[word[i] - 'a'][i] == Verify.MatchType.Unknown) {
                    return true;
                }
            }
            return false;
        });
    }

    private List<char[]> getAllCandidatesTurbo() {
        List<char[]> candidates = new ArrayList<>();
        char[] candidate = new char[matches[0].length];
        getAllCandidatesRec(root, candidate, 0, candidates);

        return candidates;
    }

    private void getAllCandidatesRec(DictionaryNode root, char[] candidate, int depth, List<char[]> candidates) {
        if (depth == candidate.length) {
            if (hasPresent(candidate)) {
                candidates.add(Arrays.copyOf(candidate, candidate.length));
            }
            return;
        }

        // try with the exact match
        boolean isExactMatch = false;
        for (int i = 0; i < Constants.CountLetters; i++) {
            if (matches[i][depth] == Verify.MatchType.Exact) {
                isExactMatch = true;
            }
        }

        if (isExactMatch) {
            for (int i = 0; i < Constants.CountLetters; i++) {
                if (matches[i][depth] == Verify.MatchType.Exact) {
                    if (root.getLinks()[i] != null) {
                        candidate[depth] = (char) ('a' + i);
                        getAllCandidatesRec(root.getLinks()[i], candidate, depth + 1, candidates);
                    } else {
                        return;
                    }
                }
            }
        } else {
            // pick one of the remaining characters
            for (Character cc : root.getLetterCount().entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList())) {
                if (root.getLinks()[cc-'a'] != null) {
                    if (matches[cc-'a'][depth] == Verify.MatchType.Unknown) {
                        candidate[depth] = cc;
                        getAllCandidatesRec(root.getLinks()[cc-'a'], candidate, depth + 1, candidates);
                    }
                }
            }
        }
    }

    private boolean getNewCandidateRec(DictionaryNode root, char[] candidate, int depth) {
        if (depth == candidate.length) {
            return true;
        }

        // we want to do duplicates last
        Map<Character, DictionaryNode> duplicates = new HashMap<>();

        // try with the exact match
        for (int i = 0; i < Constants.CountLetters; i++) {
            if (matches[i][depth] == Verify.MatchType.Exact) {
                if (root.getLinks()[i] != null) {
                    candidate[depth] = (char) ('a' + i);
                    return getNewCandidateRec(root.getLinks()[i], candidate, depth + 1);
                } else {
                    return false;
                }
            }
        }

        // try matching one of the characters that are in the present set
        for (int j = 0; j < present.size(); j++) {
            int cc = present.get(j);
            int ccIdx = cc - 'a';
            if (matches[ccIdx][depth] == Verify.MatchType.Unknown &&
                    root.getLinks()[ccIdx] != null) {
                boolean isDuplicate = false;
                for (int jj = 0; jj < depth; jj++) {
                    if (candidate[jj] == (char) cc) {
                        duplicates.put((char) cc, root.getLinks()[ccIdx]);
                        isDuplicate = true;
                    }
                }

                if (!isDuplicate) {
                    candidate[depth] = (char) cc;
                    if (getNewCandidateRec(root.getLinks()[ccIdx], candidate, depth + 1)) {
                        return true;
                    }
                }
            }
        }

        // pick one of the remaining characters
        for (int jj = 0; jj < root.getLetterCount().size(); jj++) {
            int jt = root.getLetterCount().get((char) ('a' + jj));
            if (root.getLinks()[jt] != null) {
                if (matches[jt][depth] == Verify.MatchType.Unknown) {
                    char cc = (char) ('a' + jt);

                    boolean isDuplicate = false;
                    for (int jjj = 0; jjj < depth; jjj++) {
                        if (candidate[jjj] == cc) {
                            duplicates.put(cc, root.getLinks()[jt]);
                            isDuplicate = true;
                        }
                    }

                    if (!isDuplicate) {
                        candidate[depth] = (char) ('a' + jt);
                        if (getNewCandidateRec(root.getLinks()[jt], candidate, depth + 1)) {
                            return true;
                        }
                    }
                }
            }
        }

        for (Map.Entry<Character, DictionaryNode> duplicate : duplicates.entrySet()) {
            candidate[depth] = duplicate.getKey();
            if (getNewCandidateRec(duplicate.getValue(), candidate, depth + 1)) {
                return true;
            }
        }

        return false;
    }

}
