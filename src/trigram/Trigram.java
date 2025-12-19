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
    Random rand = new Random();

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

            // System.out.println(twoWords + " -> " + tokenCountMap);

        }

    }

    /*
     This function selects the next word using weighted random sampling.

     Each possible next word has an associated weight (its frequency count).
     You can imagine all words laid out on a number line, where each word
     occupies an interval whose size is proportional to its count.

     Example:

            A       B   C
      _____________________
      |     3     | 1 | 1 |
      ~~~~~~~~~~~~~~~~~~~~~

     The total weight is the sum of all counts (5 in this example).
     A random integer is drawn uniformly from the range [0, total).

     Then we iterate through the words, accumulating their counts.
     When the accumulated count exceeds the random number, we have found
     the interval in which the random number falls, and we return that word.

     Words with larger counts occupy larger intervals, making them more
     likely to be selected, while still allowing less frequent words
     to appear occasionally.
    */
    public String getNextWord(String word1, String word2){

        String twoWords = word1 + "$" + word2;
        if (!trigramCounts.containsKey(twoWords)){
            return "<unk>";
        }

        Map<String, Integer> tokenCountMap = trigramCounts.get(twoWords);

        int total = 0;
        for (Integer count : tokenCountMap.values()){
            total += count;
        }

        int randomInt = rand.nextInt(total);
        int cumulativeCount = 0;

        for (Map.Entry<String, Integer> entry : tokenCountMap.entrySet()){

            // System.out.println(entry.getKey() + " -> " + entry.getValue() + " (" + (double)entry.getValue() / tokenCountMap.size() + ")");

            cumulativeCount += entry.getValue();

            if (cumulativeCount > randomInt){
                return entry.getKey();
            }

        }

        return "";
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
