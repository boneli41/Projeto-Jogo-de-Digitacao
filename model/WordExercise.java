package model;

public class WordExercise extends Exercise {

    public WordExercise(String word, String description, int difficulty) {
        super( word, description, "Palavras", 30 * difficulty, difficulty, 60 + difficulty * 15);
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getInstructions() {
        return "Digite a palavra exatamente como aparece na tela.";
    }
}
