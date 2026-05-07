package model;

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
        return (int) (xpReward * accuracy * (1.0 + timeBonus));
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
