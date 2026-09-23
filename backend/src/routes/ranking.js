import { Router } from "express";
import { db } from "../db.js";

export const rankingRouter = Router();

const selectTop = db.prepare(
  "SELECT name, total_score AS totalScore, game_level AS gameLevel, streak, exercises_completed AS exercisesCompleted, created_at AS createdAt FROM scores ORDER BY total_score DESC LIMIT ?"
);

const insertScore = db.prepare(
  "INSERT INTO scores (name, total_score, game_level, streak, exercises_completed) VALUES ($name, $totalScore, $gameLevel, $streak, $exercisesCompleted)"
);

rankingRouter.get("/", (req, res) => {
  const limit = Math.min(parseInt(req.query.limit, 10) || 3, 100);
  res.json(selectTop.all(limit));
});

rankingRouter.get("/full", (req, res) => {
  res.json(selectTop.all(100));
});

rankingRouter.post("/", (req, res) => {
  const { name, totalScore, gameLevel, streak, exercisesCompleted } = req.body || {};

  if (!name || typeof name !== "string" || !name.trim()) {
    return res.status(400).json({ error: "name é obrigatório" });
  }
  if (
    !Number.isFinite(totalScore) ||
    !Number.isFinite(gameLevel) ||
    !Number.isFinite(streak) ||
    !Number.isFinite(exercisesCompleted)
  ) {
    return res.status(400).json({ error: "campos numéricos inválidos" });
  }

  insertScore.run({
    $name: name.trim().slice(0, 60),
    $totalScore: Math.max(0, Math.trunc(totalScore)),
    $gameLevel: Math.max(1, Math.trunc(gameLevel)),
    $streak: Math.max(0, Math.trunc(streak)),
    $exercisesCompleted: Math.max(0, Math.trunc(exercisesCompleted)),
  });

  res.status(201).json({ ok: true });
});
