package model;

import java.io.FileWriter;
import java.io.IOException;

public abstract class Exercise {

    protected String targetText;
    protected String description;
    protected int xpReward;
    protected int difficulty;
    protected int timeLimit;

    public Exercise(String targetText, String description, int xpReward, int difficulty, int timeLimit) {
        this.targetText = targetText;
        this.description = description;
        this.xpReward = xpReward;
        this.difficulty = difficulty;
        this.timeLimit = timeLimit;
    }

    public abstract String getCategory();
    public abstract String getInstructions();

    public int calculateScore(int correctChars, int totalChars, long timeUsedMs) {

        if (totalChars == 0) return 0;

        double accuracy = (double) correctChars / totalChars;
        double timeFraction = (double) timeUsedMs / (timeLimit * 1000L);
        double timeBonus = Math.max(0.0, 1.0 - timeFraction) * 0.5;

        int score = (int) (xpReward * accuracy * (1.0 + timeBonus));

        saveScore(score);

        return score;
    }

    private void saveScore(int score) {

        try {

            FileWriter writer = new FileWriter("scores.txt", true);

            writer.write(
                    "Category: " + getCategory() +
                            " | Score: " + score +
                            " | Difficulty: " + difficulty +
                            "\n"
            );

            writer.close();

        } catch (IOException e) {

            System.out.println("Error saving score.");
            e.printStackTrace();

        }
    }

    public int calculateStars(double accuracy, long timeUsedMs) {

        double timeFraction = (double) timeUsedMs / (timeLimit * 1000L);

        if (accuracy >= 0.95 && timeFraction <= 0.70) return 3;
        if (accuracy >= 0.80) return 2;
        if (accuracy >= 0.60) return 1;

        return 0;
    }

    public String getTargetText()  { return targetText; }
    public String getDescription() { return description; }
    public int getXpReward()       { return xpReward; }
    public int getDifficulty()     { return difficulty; }
    public int getTimeLimit()      { return timeLimit; }
}