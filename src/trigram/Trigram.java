package trigram;

import java.util.*;

public class Trigram {

    /*
     Trains the trigram language model on a raw text corpus.

     The corpus is split into a sequence of words. For each sliding window
     of three consecutive words:

         (word[i], word[i+1]) -> word[i+2]

     we treat the first two words as the context (a bigram) and the third
     word as the target to be predicted.

     Internally, the model builds a nested map structure:

         "wordA$wordB" -> {
             "wordC": count,
             "wordD": count,
             ...
         }

     Each time a particular third word follows the same two-word context,
     its count is incremented. These counts later serve as weights during
     generation, allowing the model to sample more frequent continuations
     with higher probability.

     This method performs no learning in the machine-learning sense
     (no gradients, no optimization). It simply collects frequency
     statistics from the corpus, which is the core idea behind classic
     n-gram language models.
    */
    Map<String, Map<String, Integer>> trigramCounts = new HashMap<>();
    Random rand = new Random();

    public void train(String corpus) {

        String[] words = corpus.split("\\s+");

        for (int i = 0; i < words.length - 2; i++) {

            String twoWords = words[i] + "$" + words[i+1];
            String nextWord = words[i+2];
            twoWords = twoWords.toLowerCase();
            nextWord = nextWord.toLowerCase();

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

        }

    }

    /*
     This function selects the next word using weighted random sampling.

     Each possible next word has an associated weight (its frequency count).
     Conceptually, all words are laid out on a number line, where each word
     occupies a contiguous interval whose size is proportional to its count.

     Example (counts):

            A       B   C
      _____________________
      |     3     | 1 | 1 |
      ~~~~~~~~~~~~~~~~~~~~~
       0    1     2   3   4   (total = 5)

     A random integer r is drawn uniformly from the range [0, total).
     The goal is to determine which interval contains r.

     ------------------------------------------------------------------
     Two equivalent ways to find the selected word:
     ------------------------------------------------------------------

     1) CUMULATIVE (GROWING CURSOR) APPROACH
        We iterate from left to right, accumulating the counts.
        When the accumulated sum becomes greater than r, we have found
        the interval that contains r, and the corresponding word is returned.

            cursor = 0
            cursor += count(word)
            if cursor > r → select word

     2) SUBTRACTION (SHRINKING RANDOM) APPROACH
        We iterate from left to right, subtracting each word's count from r.
        When r becomes negative, it means the original random value fell
        inside the current word's interval, so that word is selected.

            r -= count(word)
            if r < 0 → select word

     Although these approaches look different, they are mathematically
     identical. In both cases, the iteration order is left-to-right;
     what changes is the frame of reference: either the cursor grows
     upward to reach r, or r shrinks downward to cross zero.

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

        for (Map.Entry<String, Integer> entry : tokenCountMap.entrySet()) {

            cumulativeCount += entry.getValue();

            /*
            visualizeSampling(
                    tokenCountMap,
                    total,
                    randomInt,
                    cumulativeCount,
                    entry.getKey(),
                    entry.getValue()
            );
            */

            if (cumulativeCount > randomInt) {
                // System.out.println("➡ SELECTED: " + entry.getKey());
                return entry.getKey();
            }

        }

        return "";
    }

    /*
     Generates a sentence starting from two initial words.

     At each step, the model predicts the next word based on the previous
     two words using the learned trigram statistics. The generated word
     becomes part of the context for the next prediction, allowing the
     sentence to grow one word at a time.

     Generation continues until the special end-of-sentence token (<eos>)
     is produced, which signals that the sentence should stop.

     This method demonstrates how the trained trigram model can be used
     to produce coherent text by chaining local word predictions.
    */
    public void generateSentence(String word1, String word2) {

        System.out.print(word1.equals("<eos>") ? "" : word1 + " ");
        System.out.print(word2.equals("<eos>") ? "" : word2 + " ");
        String nextWord;

        while (true) {

            nextWord = getNextWord(word1.toLowerCase(), word2.toLowerCase());

            if (nextWord.equals("<eos>")){
                break;
            }

            System.out.print(nextWord + " ");
            word1 = word2;
            word2 = nextWord;

        }

        System.out.print("\n---\n");
    }

    /*
     Helper method used for debugging and learning purposes.

     It visually represents the weighted sampling process by printing
     a horizontal "line" of word slots, showing:
     - the original random value (r)
     - the cumulative progress during iteration
     - the current word being evaluated

     This makes it easier to understand how the iteration over word counts
     and the comparison logic lead to the selection of a specific word.
     The method does not affect the model behavior and is meant purely
     as a visualization aid.
    */
    private void visualizeSampling(
            Map<String, Integer> map,
            int total,
            int randomInt,
            int cumulativeCount,
            String currentWord,
            int currentWordCount
    ) {
        int cellWidth = 16;
        int slotWidth = cellWidth + 1;
        int totalWidth = total * slotWidth;

        StringBuilder arrowOriginal = new StringBuilder();
        StringBuilder valueOriginal = new StringBuilder();
        StringBuilder arrowCurrent  = new StringBuilder();
        StringBuilder valueCurrent  = new StringBuilder();

        StringBuilder border    = new StringBuilder();
        StringBuilder indexLine = new StringBuilder();
        StringBuilder wordLine  = new StringBuilder();

        // --- Initialize arrow lines with spaces ---
        for (int i = 0; i < totalWidth; i++) {
            arrowOriginal.append(' ');
            valueOriginal.append(' ');
            arrowCurrent.append(' ');
            valueCurrent.append(' ');
        }

        // --- Place original randomInt arrow ---
        int rPos = randomInt * slotWidth + cellWidth / 2;
        if (rPos >= 0 && rPos < totalWidth) {
            arrowOriginal.setCharAt(rPos, '↓');
            String rLabel = "r=" + randomInt;
            int start = Math.max(0, rPos - rLabel.length() / 2);
            for (int i = 0; i < rLabel.length() && start + i < totalWidth; i++) {
                valueOriginal.setCharAt(start + i, rLabel.charAt(i));
            }
        }

        // --- Place cumulative arrow (last covered index) ---
        if (cumulativeCount > 0) {
            int cPos = (cumulativeCount - 1) * slotWidth + cellWidth / 2;
            if (cPos >= 0 && cPos < totalWidth) {
                arrowCurrent.setCharAt(cPos, '↓');
                String cLabel = "cum=" + cumulativeCount;
                int start = Math.max(0, cPos - cLabel.length() / 2);
                for (int i = 0; i < cLabel.length() && start + i < totalWidth; i++) {
                    valueCurrent.setCharAt(start + i, cLabel.charAt(i));
                }
            }
        }

        // --- Border ---
        String cellBorder = "+" + "-".repeat(cellWidth);
        border.append(cellBorder.repeat(Math.max(0, total)));
        border.append("+");

        // --- Slots ---
        int index = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                indexLine.append(String.format("|%-" + cellWidth + "d", index));
                wordLine.append(String.format("|%-" + cellWidth + "s", entry.getKey()));
                index++;
            }
        }
        indexLine.append("|");
        wordLine.append("|");

        // --- Print ---
        System.out.println(arrowOriginal);
        System.out.println(valueOriginal);
        System.out.println(arrowCurrent);
        System.out.println(valueCurrent);
        System.out.println(border);
        System.out.println(indexLine);
        System.out.println(border);
        System.out.println(wordLine);
        System.out.println(border);

        boolean condition = cumulativeCount > randomInt;

        System.out.println("Checking word: \"" + currentWord + "\"");
        System.out.println("Word count: " + currentWordCount);
        System.out.println("Condition (cum > r): " + (condition ? "YES ✅" : "NO ❌"));
        System.out.println();
    }

}
