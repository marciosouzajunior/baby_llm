import trigram.Trigram;

import java.util.List;

public class BabyLLM {

    Tokenizer tokenizer = new Tokenizer();
    Trigram trigram = new Trigram();

    public static void main(String[] args) {

        BabyLLM babyLLM = new BabyLLM();

//        String sampleText = "hello how are you asdklh";
//        List<Integer> encoded = babyLLM.tokenizer.encode(sampleText);
//        System.out.println("encoded: " + encoded);
//
//        String decoded = babyLLM.tokenizer.decode(encoded);
//        System.out.println("decoded: " + decoded);

        //babyLLM.trigram;

        for (int i = 0; i < 20; i++) {
            String nextWord = babyLLM.trigram.getNextWord("<eos>", "os");
            System.out.println("next word: " + nextWord);
        }
    }

}