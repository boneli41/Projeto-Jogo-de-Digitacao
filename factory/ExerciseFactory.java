package factory;

import model.Exercise;
import model.SentenceExercise;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExerciseFactory {

    private static final int EXERCISES_PER_SESSION = 8;

    private static List<String> loadPhrases(String filePath) {

        try {
            return Files.readAllLines(
                    Paths.get(filePath),
                    StandardCharsets.UTF_8
            );
        }
        catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + filePath);
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static List<Exercise> createExercisesFromFile(
            String filePath,
            String category,
            int difficulty
    ) {

        List<String> phrases = loadPhrases(filePath);

        Collections.shuffle(phrases);

        List<Exercise> exercises = new ArrayList<>();

        int amount = Math.min(
                EXERCISES_PER_SESSION,
                phrases.size()
        );

        for (int i = 0; i < amount; i++) {

            exercises.add(
                    new SentenceExercise(
                            phrases.get(i),
                            "Pratique a digitação",
                            category,
                            difficulty
                    )
            );
        }

        return exercises;
    }

    private static List<Exercise> createLevel1() {
        return createExercisesFromFile(
                "assets/exercises/1-minusculas.txt",
                "Minúsculas",
                1
        );
    }

    private static List<Exercise> createLevel2() {
        return createExercisesFromFile(
                "assets/exercises/2-maiusculas.txt",
                "Maiúsculas",
                2
        );
    }

    private static List<Exercise> createLevel3() {
        return createExercisesFromFile(
                "assets/exercises/3-numeros.txt",
                "Números",
                3
        );
    }

    private static List<Exercise> createLevel4() {
        return createExercisesFromFile(
                "assets/exercises/4-acentos.txt",
                "Acentos",
                4
        );
    }

    private static List<Exercise> createLevel5() {
        return createExercisesFromFile(
                "assets/exercises/5-pontuacao.txt",
                "Pontuação",
                5
        );
    }

    public static List<Exercise> createExercisesForLevel(int level) {

        switch (level) {

            case 1:
                return createLevel1();

            case 2:
                return createLevel2();

            case 3:
                return createLevel3();

            case 4:
                return createLevel4();

            case 5:
                return createLevel5();

            default:
                return createLevel1();
        }
    }

    public static List<Exercise> createCampaign() {

        List<Exercise> campaign = new ArrayList<>();

        campaign.addAll(createLevel1());
        campaign.addAll(createLevel2());
        campaign.addAll(createLevel3());
        campaign.addAll(createLevel4());
        campaign.addAll(createLevel5());

        return campaign;
    }
}