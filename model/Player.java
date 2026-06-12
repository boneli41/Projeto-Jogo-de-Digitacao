package model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
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

    // ================================================================
    //  Salvar e Carregar Progresso
    // ================================================================

    /**
     * Salva (ou atualiza) a entrada do jogador no arquivo de ranking.
     * Formato de cada linha: NOME|PONTOS|NIVEL|EXERCICIOS
     * Se o jogador já existir, mantém a linha com maior pontuação.
     */
    public void saveToFile(String filePath) {
        List<String> lines = new ArrayList<>();
        Path path = Paths.get(filePath);

        // Lê as linhas existentes
        if (Files.exists(path)) {
            try {
                lines = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
            } catch (IOException e) {
                System.out.println("Aviso: não foi possível ler " + filePath);
            }
        }

        String entry = name + "|" + totalScore + "|" + gameLevel + "|" + exercisesCompleted;
        boolean found = false;

        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split("\\|");
            if (parts.length >= 2 && parts[0].equalsIgnoreCase(name)) {
                // Atualiza somente se a pontuação atual for maior
                int savedScore = 0;
                try { savedScore = Integer.parseInt(parts[1]); } catch (NumberFormatException ignored) {}
                if (totalScore >= savedScore) {
                    lines.set(i, entry);
                }
                found = true;
                break;
            }
        }

        if (!found) {
            lines.add(entry);
        }

        // Ordena do maior para o menor score
        lines.sort((a, b) -> {
            int sa = 0, sb = 0;
            try { sa = Integer.parseInt(a.split("\\|")[1]); } catch (Exception ignored) {}
            try { sb = Integer.parseInt(b.split("\\|")[1]); } catch (Exception ignored) {}
            return Integer.compare(sb, sa);
        });

        try {
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Erro ao salvar ranking: " + e.getMessage());
        }
    }

    /**
     * Lê o ranking completo do arquivo e retorna como lista de strings formatadas.
     * Cada item: "1º  Maria  —  1500 pts  (Nível 3)"
     */
    public static List<String[]> loadRanking(String filePath) {
        List<String[]> ranking = new ArrayList<>();
        Path path = Paths.get(filePath);

        if (!Files.exists(path)) return ranking;

        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    ranking.add(parts); // [nome, score, nível, exercícios]
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar ranking: " + e.getMessage());
        }

        return ranking;
    }

    // ================================================================
    //  Lógica de XP e Nível
    // ================================================================
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
        if (exercisesCompleted >= 1  && !achievements.contains("Primeiro Passo"))  achievements.add("Primeiro Passo");
        if (exercisesCompleted >= 5  && !achievements.contains("Praticante"))       achievements.add("Praticante");
        if (exercisesCompleted >= 10 && !achievements.contains("Dedicado"))         achievements.add("Dedicado");
        if (streak >= 3              && !achievements.contains("Em Sequência"))     achievements.add("Em Sequência");
        if (streak >= 5              && !achievements.contains("Imparável"))        achievements.add("Imparável");
        if (totalScore >= 500        && !achievements.contains("500 Pontos"))       achievements.add("500 Pontos");
    }

    public void loseLife() {
        if (lives > 0) lives--;
        streak = 0;
    }

    public void restoreLives() {
        lives = MAX_LIVES;
    }

    public void completeExercise() {
        exercisesCompleted++;
        streak++;
        checkAchievements();
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    // ================================================================
    //  Getters de Nível
    // ================================================================
    public String getLevelName() {
        switch (gameLevel) {
            case 1:  return "Iniciante";
            case 2:  return "Aprendiz";
            case 3:  return "Intermediário";
            case 4:  return "Avançado";
            case 5:  return "Especialista";
            default: return "Iniciante";
        }
    }

    public int getXpForNextLevel() {
        if (gameLevel >= 5) return XP_THRESHOLD[5];
        return XP_THRESHOLD[gameLevel];
    }

    public int getXpProgress() {
        int prev = gameLevel > 1 ? XP_THRESHOLD[gameLevel - 1] : 0;
        int next = getXpForNextLevel();
        if (next == prev) return 100;
        return (int)(((double)(xp - prev) / (next - prev)) * 100);
    }

    // ================================================================
    //  Getters simples
    // ================================================================
    public String getName()              { return name; }
    public int    getLives()             { return lives; }
    public int    getTotalScore()        { return totalScore; }
    public int    getGameLevel()         { return gameLevel; }
    public int    getXp()                { return xp; }
    public int    getExercisesCompleted(){ return exercisesCompleted; }
    public int    getStreak()            { return streak; }
    public List<String> getAchievements(){ return new ArrayList<>(achievements); }
}