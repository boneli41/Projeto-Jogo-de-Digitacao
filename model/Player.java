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
    private int gameLevel;      // nível de XP: 1=Iniciante … 5=Especialista
    private int xp;
    private int exercisesCompleted;
    private int streak;

    private List<String> achievements;

    public static final int MAX_LIVES = 3;

    // ----------------------------------------------------------------
    // XP necessário para subir de nível de campanha.
    // Com 8 exercícios por nível e XP base de 60*dificuldade por acerto
    // (+ bônus de velocidade), cada nível rende ~600–900 XP.
    // ----------------------------------------------------------------
    private static final int[] XP_THRESHOLD = {
            0,     // índice 0 – não usado
            800,   // nível 1 → 2
            1700,  // nível 2 → 3
            2700,  // nível 3 → 4
            3800,  // nível 4 → 5
            5000   // nível 5 máximo
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


    public void saveToFile(String filePath) {
        List<String> lines = new ArrayList<>();
        Path path = Paths.get(filePath);

        if (Files.exists(path)) {
            try {
                lines = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
            } catch (IOException e) {
                System.out.println("Aviso: não foi possível ler " + filePath);
            }
        }

        String entry = name + "|" + totalScore + "|" + gameLevel + "|" + exercisesCompleted;
        lines.add(entry); // sempre adiciona — nunca procura/substitui por nome

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
     * Retorna lista de arrays [nome, score, nível, exercícios] ordenada,
     * representando o histórico completo de partidas jogadas.
     */
    public static List<String[]> loadRanking(String filePath) {
        List<String[]> ranking = new ArrayList<>();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) return ranking;
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length >= 2) {
                    String[] full = new String[]{
                            parts[0],
                            parts.length > 1 ? parts[1] : "0",
                            parts.length > 2 ? parts[2] : "1",
                            parts.length > 3 ? parts[3] : "0"
                    };
                    ranking.add(full);
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
        if (exercisesCompleted >= 1 && !achievements.contains("Primeiro Passo")) achievements.add("Primeiro Passo");
        if (exercisesCompleted >= 5 && !achievements.contains("Praticante")) achievements.add("Praticante");
        if (exercisesCompleted >= 10 && !achievements.contains("Dedicado")) achievements.add("Dedicado");
        if (streak >= 3 && !achievements.contains("Em Sequência")) achievements.add("Em Sequência");
        if (streak >= 5 && !achievements.contains("Imparável")) achievements.add("Imparável");
        if (totalScore >= 500 && !achievements.contains("500 Pontos")) achievements.add("500 Pontos");
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
    public String getCampaignLevelName(int campaignLevel) {
        switch (campaignLevel) {
            case 1: return "Minúsculas";
            case 2: return "Maiúsculas";
            case 3: return "Números";
            case 4: return "Acentos";
            case 5: return "Pontuação";
            default: return "Minúsculas";
        }
    }

    public String getLevelName() {
        switch (gameLevel) {
            case 1: return "Iniciante";
            case 2: return "Aprendiz";
            case 3: return "Intermediário";
            case 4: return "Avançado";
            case 5: return "Especialista";
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
        return (int) (((double) (xp - prev) / (next - prev)) * 100);
    }


    public String getName() { return name; }
    public int getLives() { return lives; }
    public int getTotalScore() { return totalScore; }
    public int getGameLevel() { return gameLevel; }
    public int getXp() { return xp; }
    public int getExercisesCompleted() { return exercisesCompleted; }
    public int getStreak() { return streak; }
    public List<String> getAchievements() { return new ArrayList<>(achievements); }
}