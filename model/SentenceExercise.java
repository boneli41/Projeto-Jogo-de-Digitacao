package model;

public class SentenceExercise extends Exercise {

    public SentenceExercise(String sentence, String description, String category, int difficulty) {
        // Tempo generoso: nivel 1 = 150s, nivel 5 = 330s
        super(sentence, description, category, 60 * difficulty, difficulty, 120 + difficulty * 42);
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getInstructions() {
        return "Digite a frase completa. Use espaço entre as palavras.";
    }
}
