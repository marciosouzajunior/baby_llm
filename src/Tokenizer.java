import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tokenizer {

    private final Map<String, Integer> wordToToken = new HashMap<>();
    private final List<String> tokenToWord = new ArrayList<>();


    public Tokenizer() {

        wordToToken.put("<unk>", 0);
        tokenToWord.add("<unk>");

        String trainingData = "hello how are you";

        String[] words = trainingData.split("\\s+");
        for (String word : words) {
            if (wordToToken.containsKey(word))
                continue;
            wordToToken.put(word, wordToToken.size());
            tokenToWord.add(word);
        }

    }

    public List<Integer> encode(String text) {
        List<Integer> result = new ArrayList<>();
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (wordToToken.containsKey(word)){
                result.add(wordToToken.get(word));
            } else {
                result.add(wordToToken.get("<unk>"));
            }
        }
        return result;
    }

    public String decode(List<Integer> tokens) {
        StringBuilder result = new StringBuilder();
        for (Integer index : tokens){
            result.append(tokenToWord.get(index));
            result.append(" ");
        }
        return result.toString();
    }

}
