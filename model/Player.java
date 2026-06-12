package model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name;
    private int lives;
    private int totalScore;
    private int gameLevel;
    private int xp;
    private int exercisesCompleted;
    private int streak;


    private List<String> achievements;

    public static final int MAX_LIVES = 3;

    private static final int[] XP_THRESHOLD = {
            0, 100, 300, 600, 1000, 1500
    };

    public Player(String name) {
        this.name = name;
        this.lives = MAX_LIVES;
        this.totalScore = 0;
        this.gameLevel = 1;
        this.xp = 0;
        this.exercisesCompleted = 0;
        this.streak = 0;


        this.achievements = new ArrayList<>();
    }

    public void addXP(int amount) {
        this.xp += amount;
        this.totalScore += amount;

        checkLevelUp();
        checkAchievements();
    }

    private void checkLevelUp() {
        while (gameLevel < 5 && xp >= XP_THRESHOLD[gameLevel]) {
            gameLevel++;
        }
    }

    private void checkAchievements() {
        if (exercisesCompleted >= 1 && !achievements.contains("Primeiro Passo")) {
            achievements.add("Primeiro Passo");
        }

        if (exercisesCompleted >= 5 && !achievements.contains("Praticante")) {
            achievements.add("Praticante");
        }

        if (exercisesCompleted >= 10 && !achievements.contains("Dedicado")) {
            achievements.add("Dedicado");
        }

        if (streak >= 3 && !achievements.contains("Em Sequência")) {
            achievements.add("Em Sequência");
        }

        if (streak >= 5 && !achievements.contains("Imparável")) {
            achievements.add("Imparável");
        }

        if (totalScore >= 500 && !achievements.contains("500 Pontos")) {
            achievements.add("500 Pontos");
        }
    }

    public void loseLife() {
        if (lives > 0) {
            lives--;
        }

        streak = 0;
    }

    public void restoreLives() {
        lives = MAX_LIVES;
    }

    /**
     * Chamado quando uma frase é concluída.
     */
    public void completeExercise() {
        exercisesCompleted++;
        streak++;

        checkAchievements();
    }


    public boolean isGameOver() {
        return lives <= 0;
    }

    public String getLevelName() {
        switch (gameLevel) {
            case 1:
                return "Iniciante";
            case 2:
                return "Aprendiz";
            case 3:
                return "Intermediário";
            case 4:
                return "Avançado";
            case 5:
                return "Especialista";
            default:
                return "Iniciante";
        }
    }

    public int getXpForNextLevel() {
        if (gameLevel >= 5) {
            return XP_THRESHOLD[5];
        }

        return XP_THRESHOLD[gameLevel];
    }

    public int getXpProgress() {
        int prevThreshold = gameLevel > 1
                ? XP_THRESHOLD[gameLevel - 1]
                : 0;

        int nextThreshold = getXpForNextLevel();

        if (nextThreshold == prevThreshold) {
            return 100;
        }

        return (int) (((double) (xp - prevThreshold)
                / (nextThreshold - prevThreshold)) * 100);
    }

    public String getName() {
        return name;
    }

    public int getLives() {
        return lives;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public int getXp() {
        return xp;
    }

    public int getExercisesCompleted() {
        return exercisesCompleted;
    }

    public int getStreak() {
        return streak;
    }

    public List<String> getAchievements() {
        return new ArrayList<>(achievements);
    }


}