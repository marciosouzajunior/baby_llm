# BabyLLM

A tiny language model built **from scratch in Java**, created for **educational purposes**.

With ~200 lines of code, this project trains a simple **trigram (n-gram) language model** on a text corpus and learns how to **predict the next word in a sentence** using weighted random sampling.

This is **not** a neural network and **not** a replacement for modern LLMs. The goal is to understand the *core ideas* behind language modeling before jumping into embeddings, matrices, and deep learning frameworks.
<br><br>

## What this project does

- Trains a **trigram model**:  
  `(word₁, word₂) → possible next words + frequency`
- Learns probabilities directly from raw text
- Generates sentences word-by-word
- Uses **weighted random sampling** to choose the next word
- Supports sentence boundaries using a special `<eos>` token
- Includes a **visualization tool** to debug and understand how sampling works internally
<br><br>

## How it works

1. **Training**
   - The corpus is split into tokens (words)
   - For every sequence of three words `(A B C)`:
     - `(A, B)` becomes the key
     - `C` is counted as a possible next word
   - Example structure:
     ```text
     "i$am" → { "happy": 3, "tired": 1 }
     ```

2. **Generation**
   - Start with two seed words (e.g. `"i am"`)
   - Predict the next word based on learned frequencies
   - Slide the window forward and repeat
   - Stop when `<eos>` is generated

3. **Sampling**
   - Words with higher counts are **more likely**, but not guaranteed
   - This keeps generation varied instead of deterministic
<br><br>

## Example output

Generated sentences trained on Stoic philosophy–inspired text:

"i am dead and gone long since forgotten"  
"i am not bound to embrace whatsoever shall happen"  
"he will soon be dust and ashes"  

Every run produces slightly different results.
<br><br>

## How to run

### Prepare a corpus
Put your training text in: `src/trigram/corpus_en.txt`  
Use any plain text:
- philosophy
- poetry
- song lyrics
- books (public domain recommended)

### Run the project

```java
public static void main(String[] args) {
    BabyLLM babyLLM = new BabyLLM();

    for (int i = 0; i < 100; i++){
        babyLLM.trigram.generateSentence("i", "am");
    }
}
```

Try different starting words:  
- "the man"  
- "he will"  
- "i can"  
<br>

## Debugging & Visualization
There is an optional method (visualizeSampling) that prints a horizontal probability line, showing:
- Each word’s interval
- The random number (r)
- The cumulative cursor
- Why a specific word was selected

This is extremely useful to understand weighted sampling intuitively.
<br><br>

## Limitations
- No understanding of meaning
- No memory beyond two previous words
- No grammar rules
- No embeddings or neural networks
- Can’t answer questions reliably

This is a statistical language model, not an intelligent agent.
<br><br>

## Why this exists
Modern LLMs can feel like magic.  
This project removes the magic and answers:
- How can text be generated at all?
- What does “predicting the next word” really mean?
- How does probability influence language generation?

It’s a great stepping stone before diving into:
- Embeddings
- Matrix math
- Transformers
- Real LLM frameworks
<br><br>

## Possible extensions
- Add bigram / unigram fallback
- Temperature control
- Top-K sampling
- Tokenization improvements
- Transition to embeddings
- Replace maps with matrices
- Move toward a neural model
<br><br>

## Tech stack
- Java
- Standard library only
- No ML frameworks
- No external dependencies
<br><br>

## License
Educational / experimental use.
Feel free to fork, modify, and learn.
<br><br>

## Final note
If you’re curious about how large language models actually work under the hood, building something small like this is one of the best ways to start.

Have fun experimenting 🚀
