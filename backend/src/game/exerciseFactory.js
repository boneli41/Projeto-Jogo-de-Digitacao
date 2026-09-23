// Porta de factory/ExerciseFactory.java — mesma lógica de leitura, embaralhamento
// e montagem da campanha (5 módulos x 8 frases, todas como "SentenceExercise").

import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const DATA_DIR = path.join(__dirname, "..", "..", "data", "exercises");

const EXERCISES_PER_SESSION = 8;

const MODULES = [
  { file: "1-minusculas.txt", category: "Minúsculas", difficulty: 1 },
  { file: "2-maiusculas.txt", category: "Maiúsculas", difficulty: 2 },
  { file: "3-numeros.txt", category: "Números", difficulty: 3 },
  { file: "4-acentos.txt", category: "Acentos", difficulty: 4 },
  { file: "5-pontuacao.txt", category: "Pontuação", difficulty: 5 },
];

function loadPhrases(fileName) {
  const filePath = path.join(DATA_DIR, fileName);
  try {
    return fs
      .readFileSync(filePath, "utf-8")
      .split(/\r?\n/)
      .filter((line) => line.length > 0);
  } catch (err) {
    console.error(`Erro ao ler arquivo: ${filePath}`, err);
    return [];
  }
}

function shuffle(array) {
  const arr = [...array];
  for (let i = arr.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [arr[i], arr[j]] = [arr[j], arr[i]];
  }
  return arr;
}

// Mesma fórmula de SentenceExercise.java:
// xpReward = 60 * difficulty; timeLimit = 120 + difficulty * 42
function buildSentenceExercise(sentence, category, difficulty, moduleMeta) {
  return {
    targetText: sentence,
    description: "Pratique a digitação",
    category,
    xpReward: 60 * difficulty,
    difficulty,
    timeLimit: 120 + difficulty * 42,
    instructions: "Digite a frase completa. Use espaço entre as palavras.",
    ...moduleMeta,
  };
}

function createLevel({ file, category, difficulty }, moduleIndex, totalModules) {
  const phrases = shuffle(loadPhrases(file));
  const amount = Math.min(EXERCISES_PER_SESSION, phrases.length);
  const exercises = [];
  for (let i = 0; i < amount; i++) {
    exercises.push(
      buildSentenceExercise(phrases[i], category, difficulty, {
        moduleNumber: difficulty, // 1..5, número absoluto do módulo (= nível de dificuldade)
        moduleIndex, // posição do módulo NESTA campanha (1-based)
        totalModules, // quantos módulos tem esta campanha
        indexInModule: i + 1,
        totalInModule: amount,
      })
    );
  }
  return exercises;
}

// startLevel (1 a 5): a campanha começa nesse módulo e segue até o 5º,
// igual descrito no README ("a campanha segue dele até o final").
export function createCampaign(startLevel = 1) {
  const safeStart = Math.min(Math.max(1, Math.trunc(startLevel) || 1), MODULES.length);
  const selected = MODULES.slice(safeStart - 1);
  const totalModules = selected.length;
  return selected.flatMap((mod, i) => createLevel(mod, i + 1, totalModules));
}

export function getModules() {
  return MODULES.map(({ category, difficulty }) => ({ moduleNumber: difficulty, category }));
}
