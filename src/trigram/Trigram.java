package trigram;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Trigram {

    /*
    ("tokenA", "tokenB") → {
        "tokenC": count,
        "tokenD": count,
        "tokenE": count
    }
    */
    Map<String, Map<String, Integer>> trigramCounts = new HashMap<>();

    public Trigram() {

        String corpus = readFile("corpus.txt");
        String[] words = corpus.split("\\s+");

        for (int i = 0; i < words.length - 2; i++) {

            String twoWords = words[i] + "$" + words[i+1];
            String nextWord = words[i+2];

            Map<String, Integer> tokenCountMap;

            if (trigramCounts.containsKey(twoWords)){

                tokenCountMap = trigramCounts.get(twoWords);

                if (tokenCountMap.containsKey(nextWord)) {
                    tokenCountMap.put(nextWord, tokenCountMap.get(nextWord) + 1);
                } else {
                    tokenCountMap.put(nextWord, 1);
                }

            } else {

                tokenCountMap = new HashMap<>();
                tokenCountMap.put(nextWord, 1);
                trigramCounts.put(twoWords, tokenCountMap);

            }

            System.out.println(twoWords + " -> " + tokenCountMap);

        }

        List<String> keys = new ArrayList<>(trigramCounts.keySet());
        Collections.sort(keys);

        for (String key : keys) {

        }

    }

    private String readFile(String fileName) {
        Path path = Paths.get("src", "trigram", fileName);
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
