import trigram.Trigram;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 Entry point for the BabyLLM demo.
 Loads the corpus, trains the trigram model,
 and generates multiple example sentences
 from a fixed starting context.
*/
public class BabyLLM {

    Trigram trigram;

    public BabyLLM() {
        trigram = new Trigram();
        String corpus = readFile("corpus_en.txt");
        trigram.train(corpus);
    }

    private String readFile(String fileName) {
        Path path = Paths.get("src", "trigram", fileName);
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        BabyLLM babyLLM = new BabyLLM();

        for (int i = 0; i < 10; i++){
            // Try different starting seeds:
            // "the man", "i am", "he will"
            babyLLM.trigram.generateSentence("i", "can");
        }
    }

}