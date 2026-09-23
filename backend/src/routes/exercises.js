import { Router } from "express";
import { createCampaign, getModules } from "../game/exerciseFactory.js";

export const exercisesRouter = Router();

exercisesRouter.get("/campaign", (req, res) => {
  const startLevel = parseInt(req.query.startLevel, 10) || 1;
  res.json(createCampaign(startLevel));
});

exercisesRouter.get("/modules", (req, res) => {
  res.json(getModules());
});
