package utils;

import model.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ResultSaver {

    public static void saveFinalResult(
            Player player,
            String difficulty
    ) {

        try {

            PrintWriter writer =
                    new PrintWriter(
                            new FileWriter(
                                    "resultado_final.txt",
                                    true
                            )
                    );

            writer.println(
                    "=================================="
            );

            writer.println("RESULTADO FINAL");

            writer.println(
                    "Nome: "
                            + player.getName()
            );

            writer.println(
                    "Dificuldade: "
                            + difficulty
            );

            writer.println(
                    "Nível alcançado: "
                            + player.getGameLevel()
            );

            writer.println(
                    "Pontuação total: "
                            + player.getTotalScore()
            );

            writer.println(
                    "Exercícios completos: "
                            + player.getExercisesCompleted()
            );

            writer.println(
                    "Sequência máxima: "
                            + player.getStreak()
            );

            writer.println(
                    "Vidas restantes: "
                            + player.getLives()
            );

            writer.println("");

            writer.println("Conquistas:");

            for (String achievement :
                    player.getAchievements()) {

                writer.println(
                        "- " + achievement
                );
            }

            writer.println(
                    "=================================="
            );

            writer.println("");

            writer.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}