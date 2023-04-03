package wordle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DictionaryNode {
    final char letter;
    final DictionaryNode[] links;
    Map<Character, Integer> letterCount;
    int subtreeSize;

    public DictionaryNode(char letter) {
        this.letter = letter;
        links = new DictionaryNode[Constants.CountLetters];
        letterCount = new HashMap<>();
        for (Character c = 'a'; c < 'z'; c++) {
            letterCount.put(c, 0);
        }
        subtreeSize = 0;
    }

    public static void addWord(DictionaryNode root, String word, Map<Character, Integer> letterCount) {
        for (char c : word.toCharArray()) {
            if (root.getLinks()[c - 'a'] == null) {
                root.getLinks()[c - 'a'] = new DictionaryNode(c);
                if (letterCount != null) {
                    root.getLinks()[c - 'a'].setLetterCount(letterCount);
                }
            }
            root = root.getLinks()[c - 'a'];
        }
    }

    public static DictionaryNode makeNaive() throws IOException {
        return makeNaiveDictionary(Constants.WORDS_LOC, null);
    }

    public static DictionaryNode makeLetterCount() throws IOException {
        return makeNaiveDictionary(Constants.WORDS_LOC, computeLetterCount());
    }

    public static DictionaryNode makeWordCount() throws IOException {
        DictionaryNode root = makeNaiveDictionary(Constants.WORDS_LOC, null);
        updateSubtreeSize(root);
        updatePriority(root);

        return root;
    }

    private static int updateSubtreeSize(DictionaryNode root) {
        if (root.getLinks() == null) {
            root.subtreeSize = 1;
        } else {
            root.subtreeSize = Arrays.stream(root.getLinks())
                    .map(n -> n != null ? updateSubtreeSize(n) : 0).reduce(0, Integer::sum) + 1;
        }
        return root.subtreeSize;
    }

    private static void updatePriority(DictionaryNode root) {
        if (root == null) {
            return;
        }

        Arrays.stream(root.getLinks()).filter(n -> n != null).forEach(DictionaryNode::updatePriority);

        root.setLetterCount(Arrays.stream(root.getLinks())
                .filter(n -> n != null)
                .map(r -> r.getLetterCount())
                .reduce(root.getLetterCount(), (lc1, lc2) -> {
                            lc2.forEach((c2, v2) -> lc1.compute(c2, (c, v) -> v + c2));
                            return lc1;
                        }
                ));
    }

    private static DictionaryNode makeNaiveDictionary(String file, Map<Character, Integer> letterCount)
            throws IOException {
        DictionaryNode root = new DictionaryNode('*');
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                addWord(root, line.toLowerCase(), letterCount);
            }
        }

        if (letterCount != null) {
            root.setLetterCount(letterCount);
        }
        return root;
    }

    public static Map<Character, Integer> computeLetterCount() throws IOException {
        Map<Character, Integer> letterCount = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(Constants.WORDS_LOC))) {
            for (String line; (line = br.readLine()) != null; ) {
                for (char c : line.toLowerCase().toCharArray()) {
                    letterCount.compute(c, (c1, o) -> o == null ? 1 : o + 1);
                }
            }
        }

        return letterCount;

//        .entrySet().stream().sorted((e1, e2) -> -e1.getValue().compareTo(e2.getValue()))
//                .map(e -> e.getKey() - 'a')
//                .collect(Collectors.toList()).toArray(new Integer[0]);
    }

    public char getLetter() {
        return letter;
    }

    public Map<Character, Integer> getLetterCount() {
        return letterCount;
    }

    public void setLetterCount(Map<Character, Integer> letterCount) {
        this.letterCount = letterCount;
    }

    public DictionaryNode[] getLinks() {
        return links;
    }
}
