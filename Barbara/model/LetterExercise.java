package model;

public class LetterExercise extends Exercise {

    public LetterExercise(String letters, String description) {
        super(letters, description, 20, 1, 90);
    }

    @Override
    public String getCategory() {
        return "Letras Basicas";
    }

    @Override
    public String getInstructions() {
        return "Digite as letras exatamente como aparecem, uma por uma.";
    }
}
